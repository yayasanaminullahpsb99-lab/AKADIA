package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.AmberAcademic
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.SiakadCloudTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiakadCloudTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        SiakadMainScreen(
            currentUserId = user.uid,
            userDisplayName = user.displayName ?: "Mahasiswa Cloud",
            userEmail = user.email ?: "mahasiswa@univ-cloud.ac.id"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiakadMainScreen(
    currentUserId: String,
    userDisplayName: String,
    userEmail: String
) {
    val viewModel: SiakadViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                SiakadViewModel(
                    repository = SiakadRepository(db),
                    currentUserId = currentUserId,
                    userDisplayName = userDisplayName,
                    userEmail = userEmail
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val pmbState by viewModel.pmbApplicantsState.collectAsStateWithLifecycle()
    val billingState by viewModel.billingState.collectAsStateWithLifecycle()
    val krsState by viewModel.krsState.collectAsStateWithLifecycle()
    val cbtAttemptsState by viewModel.cbtAttemptsState.collectAsStateWithLifecycle()
    val pddiktiLogsState by viewModel.pddiktiLogsState.collectAsStateWithLifecycle()
    val cbtSession by viewModel.cbtSession.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showRoleDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    val profile = (profileState as? UiState.Success)?.data
    val applicants = (pmbState as? UiState.Success)?.data ?: emptyList()
    val invoices = (billingState as? UiState.Success)?.data ?: emptyList()
    val enrollments = (krsState as? UiState.Success)?.data ?: emptyList()
    val cbtAttempts = (cbtAttemptsState as? UiState.Success)?.data ?: emptyList()
    val pddiktiLogs = (pddiktiLogsState as? UiState.Success)?.data ?: emptyList()

    val maxSksQuota = viewModel.calculateMaxSksQuota(profile?.ipSemesterLalu ?: 3.72)
    val currentTotalSks = enrollments.sumOf { it.sks }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SIAKAD Cloud",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = AmberAcademic.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                onClick = { showRoleDialog = true }
                            ) {
                                Text(
                                    text = profile?.role ?: "MAHASISWA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberAcademic,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = "${profile?.fullName ?: userDisplayName} • NIM: ${profile?.nimOrNidn ?: "2026101042"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showRoleDialog = true },
                        modifier = Modifier.testTag("switch_role_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ManageAccounts,
                            contentDescription = "Ganti Role RBAC"
                        )
                    }
                    IconButton(
                        onClick = {
                            signOutUser(
                                context = context,
                                credentialManager = credentialManager,
                                onSignOutComplete = {},
                                scope = scope
                            )
                        },
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar Akun"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Blueprint & Kode") },
                    label = { Text("Blueprint") },
                    modifier = Modifier.testTag("nav_blueprint")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Payments, contentDescription = "PMB & Billing") },
                    label = { Text("PMB & Bayar") },
                    modifier = Modifier.testTag("nav_pmb_billing")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.School, contentDescription = "KRS & KHS") },
                    label = { Text("KRS & KHS") },
                    modifier = Modifier.testTag("nav_krs_khs")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = "Ujian CBT") },
                    label = { Text("Ujian CBT") },
                    modifier = Modifier.testTag("nav_cbt")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Sync, contentDescription = "PDDIKTI") },
                    label = { Text("PDDIKTI") },
                    modifier = Modifier.testTag("nav_pddikti")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedVisibility(visible = statusBanner != null) {
                statusBanner?.let { msg ->
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearBanner() }) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup Notifikasi")
                            }
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> BlueprintAndCodeTab(
                    onCopyFeedback = { feedback ->
                        viewModel.switchRole(profile?.role ?: "MAHASISWA")
                    }
                )
                1 -> PmbAndBillingTab(
                    profile = profile,
                    applicants = applicants,
                    invoices = invoices,
                    onRegisterPmb = { name, school, p1, p2, ij, ktp, foto ->
                        viewModel.registerPmbApplicant(name, school, p1, p2, ij, ktp, foto)
                    },
                    onAdvancePmb = { app, step ->
                        viewModel.advancePmbWorkflow(app, step)
                    },
                    onCreateInvoice = { title, cat, jenjang, amt, method ->
                        viewModel.createInvoice(title, cat, jenjang, amt, method)
                    },
                    onTogglePaidWebhook = { inv ->
                        viewModel.toggleInvoicePaidWebhook(inv)
                    }
                )
                2 -> AkademikKrsKhsTab(
                    profile = profile,
                    enrollments = enrollments,
                    maxSksQuota = maxSksQuota,
                    onEnrollCourse = { course ->
                        viewModel.enrollKrsCourse(
                            course = course,
                            currentTotalSks = currentTotalSks,
                            maxSks = maxSksQuota,
                            isUktPaid = profile?.isUktPaid ?: true
                        )
                    },
                    onRemoveCourse = { id -> viewModel.removeKrsCourse(id) },
                    onApproveKrsPa = { id, st -> viewModel.approveKrsByDosenPa(id, st) },
                    onRecordAttendance = { krs, m -> viewModel.recordCourseAttendance(krs, m) },
                    onUpdateGrades = { id, t, ut, ua -> viewModel.updateGradesByDosen(id, t, ut, ua) }
                )
                3 -> CbtExamAndAntiCheatTab(
                    profile = profile,
                    session = cbtSession,
                    attempts = cbtAttempts,
                    onStartExam = { kode, nama, jenis, rand ->
                        viewModel.startCbtExam(kode, nama, jenis, rand, profile?.isUktPaid ?: true)
                    },
                    onSelectQuestionIndex = { idx -> viewModel.selectCbtQuestionIndex(idx) },
                    onSelectMcqOption = { qId, optIdx -> viewModel.selectCbtMcqOption(qId, optIdx) },
                    onUpdateEssay = { txt -> viewModel.updateCbtEssayAnswer(txt) },
                    onToggleDoubtful = { qId -> viewModel.toggleCbtDoubtful(qId) },
                    onSimulateTabSwitchCheat = {
                        viewModel.triggerAntiCheatViolation("Perpindahan Tab / Kehilangan Fokus Layar")
                    },
                    onSubmitExam = { viewModel.finishAndSubmitCbtExam(autoSubmittedByCheat = false) },
                    onGradeEssayByDosen = { att, sc -> viewModel.gradeEssayByDosen(att, sc) }
                )
                4 -> PddiktiFeederTab(
                    logs = pddiktiLogs,
                    enrollmentsCount = enrollments.size,
                    onTriggerSync = { act, type, dir, cnt, sum ->
                        viewModel.runPddiktiSync(act, type, dir, cnt, sum)
                    }
                )
            }
        }
    }

    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Pilih Level Akses RBAC (5 Role SIAKAD)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SiakadBlueprintDocs.rolesList.forEach { (roleKey, roleDesc) ->
                        val isSelected = profile?.role == roleKey
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                viewModel.switchRole(roleKey)
                                showRoleDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(roleKey, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    roleDesc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) { Text("Tutup") }
            }
        )
    }
}
