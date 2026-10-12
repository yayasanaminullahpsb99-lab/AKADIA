package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.theme.AmberAcademic
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.RoseAlert
import kotlin.math.roundToInt

@Composable
fun AkademikKrsKhsTab(
    profile: AcademicProfile?,
    enrollments: List<KrsEnrollment>,
    maxSksQuota: Int,
    onEnrollCourse: (CourseCatalogItem) -> Unit,
    onRemoveCourse: (String) -> Unit,
    onApproveKrsPa: (String, String) -> Unit,
    onRecordAttendance: (KrsEnrollment, String) -> Unit,
    onUpdateGrades: (String, Double, Double, Double) -> Unit
) {
    var subView by remember { mutableIntStateOf(0) } // 0 = KRS Online, 1 = Presensi & KHS/Transkrip
    var editingEnrollment by remember { mutableStateOf<KrsEnrollment?>(null) }

    val totalSksTaken = enrollments.sumOf { it.sks }
    val calculatedIpk = remember(enrollments) {
        val graded = enrollments.filter { it.bobotIndeks > 0.0 }
        val sumSks = graded.sumOf { it.sks }
        if (sumSks > 0L) {
            val totalMutu = graded.sumOf { it.sks * it.bobotIndeks }
            ((totalMutu / sumSks) * 100.0).roundToInt() / 100.0
        } else {
            profile?.ipk ?: 3.68
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                        Column {
                            Text(
                                text = "SIM Akademik: KRS, Presensi & KHS",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "IPS Lalu: ${profile?.ipSemesterLalu ?: 3.72} • Kuota SKS: $totalSksTaken / $maxSksQuota SKS • IPK Kumulatif: $calculatedIpk",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AmberAcademic
                            )
                        }
                        Surface(
                            color = if (profile?.isUktPaid == true) EmeraldSuccess.copy(alpha = 0.2f) else RoseAlert.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (profile?.isUktPaid == true) "UKT LUNAS" else "UKT HOLD",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (profile?.isUktPaid == true) EmeraldSuccess else RoseAlert,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = subView == 0,
                            onClick = { subView = 0 },
                            label = { Text("1. Pengambilan KRS & Approval PA") },
                            modifier = Modifier.testTag("chip_krs_view")
                        )
                        FilterChip(
                            selected = subView == 1,
                            onClick = { subView = 1 },
                            label = { Text("2. Presensi QR/Geo & Cetak KHS") },
                            modifier = Modifier.testTag("chip_khs_view")
                        )
                    }
                }
            }
        }

        if (subView == 0) {
            item {
                Text(
                    text = "Katalog Mata Kuliah Semester Ganjil 2026/2027",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(SiakadBlueprintDocs.courseCatalog, key = { it.kodeMk }) { course ->
                val enrolled = enrollments.find { it.kodeMk == course.kodeMk }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (enrolled != null) EmeraldSuccess.copy(alpha = 0.5f) else Color(0xFF1E293B),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${course.kodeMk} — ${course.namaMk} (${course.sks} SKS)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${course.dosenPengampu} • ${course.jadwal}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (course.prasyaratKode != null) {
                                    Text(
                                        text = "Prasyarat Lulus: ${course.prasyaratKode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmberAcademic
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (enrolled == null) {
                                Button(
                                    onClick = { onEnrollCourse(course) },
                                    modifier = Modifier.testTag("enroll_btn_${course.kodeMk}")
                                ) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ambil")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onRemoveCourse(enrolled.id) },
                                    modifier = Modifier.testTag("drop_btn_${course.kodeMk}")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Batal")
                                }
                            }
                        }

                        if (enrolled != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status Dosen PA: ${enrolled.statusApproval}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (enrolled.statusApproval == "DISETUJUI_PA") EmeraldSuccess else AmberAcademic
                                )
                                if (enrolled.statusApproval != "DISETUJUI_PA") {
                                    FilledTonalButton(
                                        onClick = { onApproveKrsPa(enrolled.id, "DISETUJUI_PA") },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve Dosen PA", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            items(enrollments, key = { it.id }) { krs ->
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
                                Text("${krs.kodeMk} • ${krs.namaMk}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${krs.sks} SKS • Kehadiran: ${krs.presensiCount}/16 Pertemuan (${((krs.presensiCount / 16.0) * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Grade: ${krs.gradeHuruf} (${krs.bobotIndeks})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Komponen Nilai: Tugas ${krs.nilaiTugas} (30%) | UTS ${krs.nilaiUts} (30%) | UAS ${krs.nilaiUas} (40%) → Akhir: ${krs.nilaiAkhir}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberAcademic
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onRecordAttendance(krs, "QR Code & Geo-Location") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Presensi QR/Geo")
                            }
                            Button(
                                onClick = { editingEnrollment = krs },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Input Nilai Dosen")
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingEnrollment != null) {
        val target = editingEnrollment!!
        var tugasText by remember(target) { mutableStateOf(target.nilaiTugas.toString()) }
        var utsText by remember(target) { mutableStateOf(target.nilaiUts.toString()) }
        var uasText by remember(target) { mutableStateOf(target.nilaiUas.toString()) }

        AlertDialog(
            onDismissRequest = { editingEnrollment = null },
            title = { Text("Input Nilai Dosen: ${target.namaMk}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Rumus Otomatis KHS: 30% Tugas + 30% UTS + 40% UAS → Konversi Huruf Mutu (A s/d E)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = tugasText,
                        onValueChange = { tugasText = it },
                        label = { Text("Nilai Tugas (0 - 100)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = utsText,
                        onValueChange = { utsText = it },
                        label = { Text("Nilai UTS (0 - 100)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uasText,
                        onValueChange = { uasText = it },
                        label = { Text("Nilai UAS (0 - 100)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val t = tugasText.toDoubleOrNull() ?: 80.0
                    val ut = utsText.toDoubleOrNull() ?: 80.0
                    val ua = uasText.toDoubleOrNull() ?: 85.0
                    onUpdateGrades(target.id, t, ut, ua)
                    editingEnrollment = null
                }) {
                    Text("Simpan & Hitung KHS")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEnrollment = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun CbtExamAndAntiCheatTab(
    profile: AcademicProfile?,
    session: CbtActiveSessionState,
    attempts: List<CbtAttempt>,
    onStartExam: (String, String, String, Boolean) -> Unit,
    onSelectQuestionIndex: (Int) -> Unit,
    onSelectMcqOption: (String, Int) -> Unit,
    onUpdateEssay: (String) -> Unit,
    onToggleDoubtful: (String) -> Unit,
    onSimulateTabSwitchCheat: () -> Unit,
    onSubmitExam: () -> Unit,
    onGradeEssayByDosen: (CbtAttempt, Double) -> Unit
) {
    var randomizeQuestions by remember { mutableStateOf(true) }
    var gradingAttempt by remember { mutableStateOf<CbtAttempt?>(null) }

    if (session.isExamRunning && session.questions.isNotEmpty()) {
        val currentQ = session.questions[session.currentQuestionIndex]
        val hours = session.remainingSeconds / 3600
        val minutes = (session.remainingSeconds % 3600) / 60
        val seconds = session.remainingSeconds % 60
        val formattedTimer = "%02d:%02d:%02d".format(hours, minutes, seconds)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top CBT Header Bar: Exam Title, Anti-Cheat Counter & Countdown Timer
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${session.jenisUjian} • ${session.kodeMk} (FULLSCREEN LOCK)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = session.namaUjian,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Surface(
                                color = AmberAcademic.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "⏱ $formattedTimer",
                                    fontFamily = JetBrainsMonoFamily,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = AmberAcademic,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Anti-Cheat Pelanggaran Tab: ${session.cheatWarnings} / ${session.maxCheatWarnings}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (session.cheatWarnings > 0) RoseAlert else EmeraldSuccess
                            )
                            OutlinedButton(
                                onClick = onSimulateTabSwitchCheat,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseAlert),
                                modifier = Modifier.testTag("simulate_tab_switch_button")
                            ) {
                                Icon(Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Simulasi Pindah Tab")
                            }
                        }

                        if (session.lastWarningMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = RoseAlert.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = session.lastWarningMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = RoseAlert,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Question Number Navigation Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    session.questions.forEachIndexed { idx, q ->
                        val isCurrent = idx == session.currentQuestionIndex
                        val isDoubt = session.doubtfulQuestions.contains(q.id)
                        val isAnswered = if (q.isEssay) {
                            session.essayAnswer.isNotBlank()
                        } else {
                            session.selectedMcqOptions.containsKey(q.id)
                        }
                        val bgColor = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isDoubt -> AmberAcademic
                            isAnswered -> EmeraldSuccess
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Surface(
                            color = bgColor,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { onSelectQuestionIndex(idx) }
                                .testTag("cbt_nav_num_${idx + 1}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Active Question Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Soal Nomor ${currentQ.nomor} dari ${session.questions.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (currentQ.isEssay) "Essay (Koreksi Dosen)" else "Pilihan Ganda (Bobot ${currentQ.bobot.toInt()}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberAcademic
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentQ.pertanyaan,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!currentQ.isEssay) {
                            val selectedIdx = session.selectedMcqOptions[currentQ.id]
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQ.opsi.forEachIndexed { optIdx, optText ->
                                    val isChosen = selectedIdx == optIdx
                                    val optionLetter = listOf("A", "B", "C", "D")[optIdx]
                                    Surface(
                                        color = if (isChosen) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(
                                                1.dp,
                                                if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { onSelectMcqOption(currentQ.id, optIdx) }
                                            .testTag("cbt_option_$optIdx")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = if (isChosen) MaterialTheme.colorScheme.primary else Color(0xFF334155),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = optionLetter,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = optText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = session.essayAnswer,
                                onValueChange = onUpdateEssay,
                                label = { Text("Jawaban Essay Mahasiswa") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 130.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { onSelectQuestionIndex((session.currentQuestionIndex - 1).coerceAtLeast(0)) },
                                enabled = session.currentQuestionIndex > 0
                            ) {
                                Text("← Sebelumnya")
                            }

                            FilterChip(
                                selected = session.doubtfulQuestions.contains(currentQ.id),
                                onClick = { onToggleDoubtful(currentQ.id) },
                                label = { Text("Ragu-Ragu") }
                            )

                            if (session.currentQuestionIndex < session.questions.size - 1) {
                                Button(
                                    onClick = { onSelectQuestionIndex(session.currentQuestionIndex + 1) }
                                ) {
                                    Text("Selanjutnya →")
                                }
                            } else {
                                Button(
                                    onClick = onSubmitExam,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    modifier = Modifier.testTag("submit_cbt_button")
                                ) {
                                    Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Submit Ujian")
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // CBT Lobby & Exam Results History
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Modul Ujian CBT (Computer Based Test) & Anti-Cheat",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mendukung Timer Mundur Otomatis, Acak Soal & Opsi, Deteksi Pindah Tab (Auto-Submit jika >= 3x pelanggaran), Penilaian Pilihan Ganda Otomatis, dan Koreksi Essay Dosen yang langsung masuk ke KHS.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = randomizeQuestions,
                                    onCheckedChange = { randomizeQuestions = it }
                                )
                                Text("Acak Urutan Soal & Opsi PG", style = MaterialTheme.typography.bodyMedium)
                            }
                            Button(
                                onClick = {
                                    onStartExam(
                                        "IF301",
                                        "UTS Arsitektur Cloud & Microservices",
                                        "UTS",
                                        randomizeQuestions
                                    )
                                },
                                modifier = Modifier.testTag("start_cbt_exam_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mulai Ujian CBT")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat Pengerjaan CBT & Halaman Koreksi Essay Dosen (${attempts.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(attempts, key = { it.id }) { att ->
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
                                Text("${att.jenisUjian} • ${att.namaUjian}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Kode: ${att.examCode} • Status: ${att.status}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Skor: ${att.finalCbtScore}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Skor PG Otomatis: ${att.mcqScore} | Skor Essay: ${att.essayScore} | Pelanggaran Tab: ${att.cheatWarnings}x ${if (att.autoSubmittedByAntiCheat) "(AUTO-SUBMIT CHEAT)" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (att.autoSubmittedByAntiCheat) RoseAlert else AmberAcademic
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Jawaban Essay: \"${att.essayAnswer}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FilledTonalButton(
                            onClick = { gradingAttempt = att }
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Koreksi Nilai Essay (Dosen) & Update KHS")
                        }
                    }
                }
            }
        }
    }

    if (gradingAttempt != null) {
        val target = gradingAttempt!!
        var essayInput by remember(target) { mutableStateOf(target.essayScore.toString()) }
        AlertDialog(
            onDismissRequest = { gradingAttempt = null },
            title = { Text("Koreksi Soal Essay CBT oleh Dosen") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Jawaban Mahasiswa:\n\"${target.essayAnswer}\"", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = essayInput,
                        onValueChange = { essayInput = it },
                        label = { Text("Nilai Essay (0 - 100)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val sc = essayInput.toDoubleOrNull() ?: 90.0
                    onGradeEssayByDosen(target, sc)
                    gradingAttempt = null
                }) {
                    Text("Simpan & Sinkron ke KHS")
                }
            },
            dismissButton = {
                TextButton(onClick = { gradingAttempt = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun PddiktiFeederTab(
    logs: List<PddiktiSyncLog>,
    enrollmentsCount: Int,
    onTriggerSync: (String, String, String, Int, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Integrasi PDDIKTI (API Neo Feeder WS 2.1)",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mapping standar Dikti (id_reg_pd, id_sms, id_kelas_kuliah) serta Push & Pull pelaporan semester (Mahasiswa, Dosen, Kelas Kuliah, KRS, Nilai Akhir, dan Validasi Kelulusan/Ijazah PIN).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onTriggerSync(
                                    "InsertNilaiPerkuliahanKelas",
                                    "NILAI_AKHIR",
                                    "PUSH",
                                    enrollmentsCount.coerceAtLeast(1) * 32,
                                    "Push nilai KHS semester 20261 (A-E & Bobot Indeks) ke Neo Feeder PDDIKTI berhasil tanpa error."
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("pddikti_push_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Push KRS & Nilai")
                        }
                        FilledTonalButton(
                            onClick = {
                                onTriggerSync(
                                    "GetMahasiswaLulusDO & Validasi PIN",
                                    "KELULUSAN_IJAZAH",
                                    "PULL",
                                    48,
                                    "Pull data status kelulusan & validasi Penomoran Ijazah Nasional (PIN) PDDIKTI sinkron."
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("pddikti_pull_button")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pull Kelulusan")
                        }
                    }
                }
            }
        }

        items(logs, key = { it.id }) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${log.direction} • ${log.endpointAction}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (log.direction == "PUSH") MaterialTheme.colorScheme.primary else AmberAcademic
                        )
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${log.recordsProcessed} Record",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = log.responseSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
