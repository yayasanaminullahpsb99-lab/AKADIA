package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.*
import com.example.ui.theme.AmberAcademic
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.RoseAlert

@Composable
fun BlueprintAndCodeTab(
    onCopyFeedback: (String) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf(
        "1. ERD & SQL DDL",
        "2. Laravel KRS API",
        "3. Laravel CBT API",
        "4. Blade & Tailwind UI",
        "5. Alur Bisnis"
    )
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Architecture,
                            contentDescription = "Blueprint Arsitektur",
                            tint = AmberAcademic
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Arsitektur & Full-Stack Code Blueprint",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Stack Enterprise: Laravel 11 REST API + Blade Template & Tailwind CSS + PostgreSQL/MySQL Strict Relational Schema + JWT & Spatie RBAC (5 Role Level).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sections.forEachIndexed { idx, label ->
                            FilterChip(
                                selected = selectedSection == idx,
                                onClick = { selectedSection = idx },
                                label = { Text(label) },
                                modifier = Modifier.testTag("blueprint_chip_$idx")
                            )
                        }
                    }
                }
            }
        }

        item {
            val (title, subtitle, codeBlock) = when (selectedSection) {
                0 -> Triple(
                    "1. Struktur Database Relasional (ERD & SQL DDL)",
                    "Mencakup tabel users (5 RBAC), prodi, tahun_akademik, dosen, mahasiswa, pmb_pendaftar, pembayaran, mata_kuliah, kelas_kuliah, krs, krs_detail, cbt_ujian, cbt_soal, cbt_peserta_ujian, dan cbt_jawaban.",
                    SiakadBlueprintDocs.SQL_SCHEMA_DDL
                )
                1 -> Triple(
                    "2A. Backend Laravel: Pengambilan KRS Online (KrsController.php)",
                    "Validasi otomatis status lunas UKT/SPP (Billing Gate), perhitungan kuota maksimal SKS berdasarkan IPS semester lalu, dan penyimpanan transaksional (DB::transaction).",
                    SiakadBlueprintDocs.LARAVEL_KRS_CONTROLLER_CODE
                )
                2 -> Triple(
                    "2B. Backend Laravel: Submit Ujian CBT & Auto-Grading (CbtExamController.php)",
                    "Koreksi otomatis soal Pilihan Ganda, pencatatan log Anti-Cheat (jumlah pindah tab), dan sinkronisasi langsung ke Nilai UTS/UAS serta Huruf Mutu (A-E) pada KHS.",
                    SiakadBlueprintDocs.LARAVEL_CBT_CONTROLLER_CODE
                )
                3 -> Triple(
                    "3. Frontend Blade + Tailwind CSS: Halaman Ujian CBT & Anti-Cheat",
                    "Komponen responsif dengan Countdown Timer otomatis, Peta Navigasi Nomor Soal, penanda Ragu-Ragu, serta deteksi pindah tab (visibilitychange & window blur).",
                    SiakadBlueprintDocs.BLADE_TAILWIND_CBT_VIEW_CODE
                )
                else -> Triple(
                    "4. Alur Kerja & Logika Bisnis Terintegrasi (End-to-End)",
                    "Penjelasan interaksi antar modul: PMB Online -> Auto-NIM -> Billing Gate Midtrans/Xendit -> KRS Kuota IPS -> CBT Anti-Cheat -> KHS & Neo Feeder PDDIKTI.",
                    SiakadBlueprintDocs.BUSINESS_WORKFLOW_EXPLANATION
                )
            }

            CodeShowcaseCard(
                title = title,
                subtitle = subtitle,
                code = codeBlock,
                onCopy = {
                    copyToClipboard(context, title, codeBlock)
                    onCopyFeedback("Kode '$title' berhasil disalin ke Clipboard!")
                }
            )
        }
    }
}

@Composable
private fun CodeShowcaseCard(
    title: String,
    subtitle: String,
    code: String,
    onCopy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = onCopy,
                    modifier = Modifier.testTag("copy_code_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin Kode", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFF060913),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(14.dp)
                ) {
                    Text(
                        text = code.trim(),
                        fontFamily = JetBrainsMonoFamily,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
}

@Composable
fun PmbAndBillingTab(
    profile: AcademicProfile?,
    applicants: List<PmbApplicant>,
    invoices: List<BillingInvoice>,
    onRegisterPmb: (String, String, String, String, Boolean, Boolean, Boolean) -> Unit,
    onAdvancePmb: (PmbApplicant, String) -> Unit,
    onCreateInvoice: (String, String, String, Long, String) -> Unit,
    onTogglePaidWebhook: (BillingInvoice) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0 = PMB Online, 1 = Billing & Payment Gateway
    var showAddPmbDialog by remember { mutableStateOf(false) }
    var showAddInvoiceDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text("A. Modul PMB Online (${applicants.size})") },
                    leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f).testTag("subtab_pmb")
                )
                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text("B. Billing & VA/QRIS (${invoices.size})") },
                    leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f).testTag("subtab_billing")
                )
            }
        }

        if (subTab == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Penerimaan Mahasiswa Baru (PMB)", style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "Alur: Daftar Akun -> Pilihan Prodi 1 & 2 -> Upload Berkas -> Bayar VA Formulir -> Seleksi Panitia -> Daftar Ulang & Generate NIM Otomatis.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showAddPmbDialog = true },
                                modifier = Modifier.testTag("add_pmb_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Daftar PMB")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Daftar Baru")
                            }
                        }
                    }
                }
            }

            items(applicants, key = { it.id }) { item ->
                PmbApplicantCard(applicant = item, onAdvance = { step -> onAdvancePmb(item, step) })
            }
        } else {
            item {
                val isUktLunas = profile?.isUktPaid == true
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUktLunas) EmeraldSuccess.copy(alpha = 0.15f) else RoseAlert.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUktLunas) "STATUS UKT: LUNAS (Akses KRS & CBT Aktif)" else "STATUS UKT: BELUM LUNAS (KRS & CBT Terkunci)",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isUktLunas) EmeraldSuccess else RoseAlert
                            )
                            Text(
                                text = "Simulasikan Webhook Payment Gateway (Midtrans/Xendit) pada kartu tagihan UKT di bawah untuk menguji buka/kunci otomatis hak akses KRS & Ujian CBT.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showAddInvoiceDialog = true },
                            modifier = Modifier.testTag("add_invoice_button")
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Buat Tagihan")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Tagihan")
                        }
                    }
                }
            }

            items(invoices, key = { it.id }) { inv ->
                BillingInvoiceCard(invoice = inv, onTogglePaid = { onTogglePaidWebhook(inv) })
            }
        }
    }

    if (showAddPmbDialog) {
        AddPmbDialog(
            onDismiss = { showAddPmbDialog = false },
            onSubmit = { name, school, p1, p2, ij, ktp, foto ->
                onRegisterPmb(name, school, p1, p2, ij, ktp, foto)
                showAddPmbDialog = false
            }
        )
    }

    if (showAddInvoiceDialog) {
        AddInvoiceDialog(
            onDismiss = { showAddInvoiceDialog = false },
            onSubmit = { title, cat, jenjang, amount, method ->
                onCreateInvoice(title, cat, jenjang, amount, method)
                showAddInvoiceDialog = false
            }
        )
    }
}

@Composable
private fun PmbApplicantCard(
    applicant: PmbApplicant,
    onAdvance: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(applicant.fullName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${applicant.registrationNo} • Asal: ${applicant.asalSekolah}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = when (applicant.statusSeleksi) {
                        "AKTIF_NIM", "LULUS" -> EmeraldSuccess.copy(alpha = 0.2f)
                        "TERVERIFIKASI" -> AmberAcademic.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = applicant.statusSeleksi,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (applicant.statusSeleksi) {
                            "AKTIF_NIM", "LULUS" -> EmeraldSuccess
                            "TERVERIFIKASI" -> AmberAcademic
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Pilihan 1: ${applicant.pilihanProdi1} | Pilihan 2: ${applicant.pilihanProdi2}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "VA Formulir: ${applicant.vaNumber} (${if (applicant.formFeePaid) "LUNAS" else "BELUM BAYAR"}) • Berkas: Ijazah✓ KTP✓ Foto✓",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (applicant.generatedNim != "-") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = EmeraldSuccess.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "NIM Mahasiswa Aktif Terbit: ${applicant.generatedNim}",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!applicant.formFeePaid) {
                    FilledTonalButton(onClick = { onAdvance("BAYAR_FORMULIR") }) {
                        Text("1. Lunasi VA Formulir")
                    }
                } else if (applicant.statusSeleksi == "MENUNGGU_VERIFIKASI") {
                    FilledTonalButton(onClick = { onAdvance("VERIFIKASI_BERKAS") }) {
                        Text("2. Verifikasi Panitia")
                    }
                } else if (applicant.statusSeleksi == "TERVERIFIKASI") {
                    Button(onClick = { onAdvance("LULUS_SELEKSI") }) {
                        Text("3. Umumkan LULUS")
                    }
                } else if (applicant.statusSeleksi == "LULUS") {
                    Button(
                        onClick = { onAdvance("DAFTAR_ULANG_GENERATE_NIM") },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("4. Daftar Ulang & Generate NIM")
                    }
                }
            }
        }
    }
}

@Composable
private fun BillingInvoiceCard(
    invoice: BillingInvoice,
    onTogglePaid: () -> Unit
) {
    val isPaid = invoice.status == "PAID"
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(invoice.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${invoice.invoiceNumber} • Kategori: ${invoice.category} (${invoice.jenjang})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = if (isPaid) EmeraldSuccess.copy(alpha = 0.2f) else RoseAlert.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = invoice.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPaid) EmeraldSuccess else RoseAlert,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rp %,d".format(invoice.amount),
                        style = MaterialTheme.typography.headlineMedium,
                        color = AmberAcademic
                    )
                    Text(
                        text = "${invoice.paymentMethod}: ${invoice.vaOrPaymentCode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onTogglePaid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaid) MaterialTheme.colorScheme.surfaceVariant else EmeraldSuccess
                    ),
                    modifier = Modifier.testTag("webhook_pay_${invoice.id}")
                ) {
                    Icon(
                        imageVector = if (isPaid) Icons.Default.LockReset else Icons.Default.Verified,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isPaid) "Simulasi Unpaid" else "Simulasi Bayar Webhook")
                }
            }
        }
    }
}

@Composable
private fun AddPmbDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Boolean, Boolean, Boolean) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var prodi1 by remember { mutableStateOf("S1 Teknik Informatika") }
    var prodi2 by remember { mutableStateOf("S1 Sistem Informasi") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Formulir Pendaftaran PMB Online") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama Lengkap Calon Mahasiswa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("Asal SMA / SMK / MA") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = prodi1,
                    onValueChange = { prodi1 = it },
                    label = { Text("Pilihan Program Studi 1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = prodi2,
                    onValueChange = { prodi2 = it },
                    label = { Text("Pilihan Program Studi 2") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Berkas Terlampir Otomatis: Ijazah/SKL (✓), KTP/KK (✓), Pas Foto 4x6 (✓)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldSuccess
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(fullName, school, prodi1, prodi2, true, true, true) }) {
                Text("Daftar & Generate VA")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun AddInvoiceDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Long, String) -> Unit
) {
    var title by remember { mutableStateOf("UKT Semester Genap 2026/2027") }
    var category by remember { mutableStateOf("UKT_SPP") }
    var jenjang by remember { mutableStateOf("S1") }
    var amountText by remember { mutableStateOf("5500000") }
    var method by remember { mutableStateOf("VA_BCA") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Tagihan & Channel Payment Gateway") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Deskripsi Tagihan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("UKT_SPP", "SKS", "PEMBANGUNAN").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("VA_BCA", "VA_MANDIRI", "QRIS").forEach { ch ->
                        FilterChip(
                            selected = method == ch,
                            onClick = { method = ch },
                            label = { Text(ch) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amountText.toLongOrNull() ?: 5000000L
                onSubmit(title, category, jenjang, amt, method)
            }) {
                Text("Generate VA / QRIS")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
