package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class CbtActiveSessionState(
    val isExamRunning: Boolean = false,
    val examCode: String = "UTS-IF301-2026",
    val kodeMk: String = "IF301",
    val namaUjian: String = "UTS Arsitektur Cloud & Microservices",
    val jenisUjian: String = "UTS",
    val questions: List<CbtQuestionItem> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedMcqOptions: Map<String, Int> = emptyMap(),
    val essayAnswer: String = "",
    val doubtfulQuestions: Set<String> = emptySet(),
    val remainingSeconds: Int = 5400, // 90 minutes
    val cheatWarnings: Int = 0,
    val maxCheatWarnings: Int = 3,
    val lastWarningMessage: String? = null
)

class SiakadViewModel(
    private val repository: SiakadRepository,
    private val currentUserId: String,
    private val userDisplayName: String,
    private val userEmail: String
) : ViewModel() {

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _cbtSession = MutableStateFlow(CbtActiveSessionState())
    val cbtSession: StateFlow<CbtActiveSessionState> = _cbtSession.asStateFlow()

    private var timerJob: Job? = null

    val profileState: StateFlow<UiState<AcademicProfile?>> = repository.observeProfile(currentUserId)
        .map<AcademicProfile?, UiState<AcademicProfile?>> { UiState.Success(it) }
        .catch { e ->
            Log.w(TAG, "Error observing profile", e)
            emit(UiState.Error(e.message ?: "Gagal memuat profil akademik"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val pmbApplicantsState: StateFlow<UiState<List<PmbApplicant>>> = repository.observePmbApplicants(currentUserId)
        .map<List<PmbApplicant>, UiState<List<PmbApplicant>>> { UiState.Success(it) }
        .catch { e ->
            Log.w(TAG, "Error observing PMB", e)
            emit(UiState.Error(e.message ?: "Gagal memuat data PMB"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val billingState: StateFlow<UiState<List<BillingInvoice>>> = repository.observeBillingInvoices(currentUserId)
        .map<List<BillingInvoice>, UiState<List<BillingInvoice>>> { UiState.Success(it) }
        .catch { e ->
            Log.w(TAG, "Error observing Billing", e)
            emit(UiState.Error(e.message ?: "Gagal memuat tagihan keuangan"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val krsState: StateFlow<UiState<List<KrsEnrollment>>> = repository.observeKrsEnrollments(currentUserId)
        .map<List<KrsEnrollment>, UiState<List<KrsEnrollment>>> { UiState.Success(it) }
        .catch { e ->
            Log.w(TAG, "Error observing KRS", e)
            emit(UiState.Error(e.message ?: "Gagal memuat data KRS & KHS"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val cbtAttemptsState: StateFlow<UiState<List<CbtAttempt>>> = repository.observeCbtAttempts(currentUserId)
        .map<List<CbtAttempt>, UiState<List<CbtAttempt>>> { UiState.Success(it) }
        .catch { e ->
            Log.w(TAG, "Error observing CBT", e)
            emit(UiState.Error(e.message ?: "Gagal memuat riwayat ujian CBT"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val pddiktiLogsState: StateFlow<UiState<List<PddiktiSyncLog>>> = repository.observePddiktiLogs(currentUserId)
        .map<List<PddiktiSyncLog>, UiState<List<PddiktiSyncLog>>> { UiState.Success(it) }
        .catch { e ->
            Log.w(TAG, "Error observing PDDIKTI logs", e)
            emit(UiState.Error(e.message ?: "Gagal memuat log Neo Feeder PDDIKTI"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    init {
        viewModelScope.launch {
            repository.ensureOrCreateProfile(userDisplayName, userEmail)
        }
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun switchRole(newRole: String) {
        viewModelScope.launch {
            repository.switchRole(newRole)
                .onSuccess { _statusBanner.value = "Berhasil beralih ke mode RBAC: $newRole" }
                .onFailure { _statusBanner.value = "Gagal mengubah role: ${it.localizedMessage}" }
        }
    }

    // --- PMB ACTIONS ---
    fun registerPmbApplicant(
        fullName: String,
        asalSekolah: String,
        pilihan1: String,
        pilihan2: String,
        ijazah: Boolean,
        ktp: Boolean,
        foto: Boolean
    ) {
        if (fullName.isBlank() || asalSekolah.isBlank()) {
            _statusBanner.value = "Mohon lengkapi Nama Lengkap & Sekolah Asal pendaftar PMB."
            return
        }
        viewModelScope.launch {
            repository.createPmbApplicant(fullName, asalSekolah, pilihan1, pilihan2, ijazah, ktp, foto)
                .onSuccess { _statusBanner.value = "Pendaftaran PMB Online berhasil dibuat beserta nomor Virtual Account!" }
                .onFailure { _statusBanner.value = "Gagal mendaftar PMB: ${it.localizedMessage}" }
        }
    }

    fun advancePmbWorkflow(applicant: PmbApplicant, step: String) {
        viewModelScope.launch {
            repository.advancePmbStatus(applicant, step)
                .onSuccess {
                    val msg = when (step) {
                        "BAYAR_FORMULIR" -> "Virtual Account Formulir PMB tervalidasi LUNAS!"
                        "VERIFIKASI_BERKAS" -> "Berkas Ijazah, KTP & Pas Foto terverifikasi oleh Panitia!"
                        "LULUS_SELEKSI" -> "Pengumuman Seleksi: Pendaftar dinyatakan LULUS!"
                        "DAFTAR_ULANG_GENERATE_NIM" -> "Daftar Ulang berhasil! NIM Mahasiswa Aktif otomatis diterbitkan."
                        else -> "Status PMB diperbarui."
                    }
                    _statusBanner.value = msg
                }
                .onFailure { _statusBanner.value = "Gagal memproses PMB: ${it.localizedMessage}" }
        }
    }

    // --- BILLING ACTIONS ---
    fun createInvoice(title: String, category: String, jenjang: String, amount: Long, method: String) {
        viewModelScope.launch {
            repository.createBillingInvoice(title, category, jenjang, amount, method)
                .onSuccess { _statusBanner.value = "Tagihan baru & kode pembayaran ($method) berhasil digenerate!" }
                .onFailure { _statusBanner.value = "Gagal membuat tagihan: ${it.localizedMessage}" }
        }
    }

    fun toggleInvoicePaidWebhook(invoice: BillingInvoice) {
        val nextStatus = if (invoice.status == "PAID") "UNPAID" else "PAID"
        viewModelScope.launch {
            repository.reconcileInvoiceWebhook(invoice, nextStatus)
                .onSuccess {
                    _statusBanner.value = if (nextStatus == "PAID") {
                        "Webhook Midtrans/Xendit: Tagihan ${invoice.invoiceNumber} LUNAS! Hak akses KRS & Ujian CBT dibuka."
                    } else {
                        "Status tagihan ${invoice.invoiceNumber} diubah menjadi UNPAID (Simulasi Blokir Akses)."
                    }
                }
                .onFailure { _statusBanner.value = "Gagal rekonsiliasi pembayaran: ${it.localizedMessage}" }
        }
    }

    // --- KRS & KHS ACTIONS ---
    fun calculateMaxSksQuota(ips: Double): Int {
        return when {
            ips >= 3.00 -> 24
            ips >= 2.50 -> 21
            ips >= 2.00 -> 18
            else -> 15
        }
    }

    fun enrollKrsCourse(course: CourseCatalogItem, currentTotalSks: Long, maxSks: Int, isUktPaid: Boolean) {
        if (!isUktPaid) {
            _statusBanner.value = "AKSES DITOLAK: Tagihan UKT/SPP belum lunas! Lunasi di menu Keuangan terlebih dahulu."
            return
        }
        if (currentTotalSks + course.sks > maxSks) {
            _statusBanner.value = "Batas kuota SKS terlampaui! Maksimal $maxSks SKS berdasarkan IPS semester lalu."
            return
        }
        viewModelScope.launch {
            repository.enrollCourseKrs(course)
                .onSuccess { _statusBanner.value = "Mata kuliah ${course.namaMk} (${course.sks} SKS) ditambahkan ke KRS!" }
                .onFailure { _statusBanner.value = "Gagal mengambil KRS: ${it.localizedMessage}" }
        }
    }

    fun removeKrsCourse(enrollmentId: String) {
        viewModelScope.launch {
            repository.removeCourseKrs(enrollmentId)
                .onSuccess { _statusBanner.value = "Mata kuliah berhasil dihapus dari KRS." }
                .onFailure { _statusBanner.value = "Gagal menghapus mata kuliah: ${it.localizedMessage}" }
        }
    }

    fun approveKrsByDosenPa(enrollmentId: String, status: String) {
        viewModelScope.launch {
            repository.updateKrsApproval(enrollmentId, status)
                .onSuccess { _statusBanner.value = "Status KRS diperbarui oleh Dosen PA menjadi: $status" }
                .onFailure { _statusBanner.value = "Gagal update approval KRS: ${it.localizedMessage}" }
        }
    }

    fun recordCourseAttendance(enrollment: KrsEnrollment, method: String) {
        viewModelScope.launch {
            repository.recordAttendance(enrollment)
                .onSuccess { _statusBanner.value = "Presensi ($method) tercatat untuk ${enrollment.namaMk} (Pertemuan ke-${(enrollment.presensiCount + 1).coerceAtMost(16)})" }
                .onFailure { _statusBanner.value = "Gagal mencatat presensi: ${it.localizedMessage}" }
        }
    }

    fun updateGradesByDosen(enrollmentId: String, tugas: Double, uts: Double, uas: Double) {
        viewModelScope.launch {
            repository.submitCourseGrades(enrollmentId, tugas, uts, uas)
                .onSuccess { _statusBanner.value = "Nilai Tugas/UTS/UAS berhasil dihitung otomatis ke Nilai Akhir & Huruf Mutu KHS!" }
                .onFailure { _statusBanner.value = "Gagal menyimpan nilai: ${it.localizedMessage}" }
        }
    }

    // --- CBT EXAM & ANTI-CHEAT ACTIONS ---
    fun startCbtExam(kodeMk: String, namaUjian: String, jenisUjian: String, randomize: Boolean, isUktPaid: Boolean) {
        if (!isUktPaid) {
            _statusBanner.value = "AKSES UJIAN CBT TERKUNCI: Anda memiliki tunggakan UKT/SPP yang belum lunas!"
            return
        }
        val baseQuestions = SiakadBlueprintDocs.defaultCbtQuestions
        val mcqs = baseQuestions.filter { !it.isEssay }.let { if (randomize) it.shuffled() else it }
        val essays = baseQuestions.filter { it.isEssay }
        val ordered = (mcqs + essays).mapIndexed { index, q -> q.copy(nomor = index + 1) }

        _cbtSession.value = CbtActiveSessionState(
            isExamRunning = true,
            examCode = "$jenisUjian-$kodeMk-2026",
            kodeMk = kodeMk,
            namaUjian = namaUjian,
            jenisUjian = jenisUjian,
            questions = ordered,
            currentQuestionIndex = 0,
            selectedMcqOptions = emptyMap(),
            essayAnswer = "Transaksi penyimpanan ujian menggunakan DB::transaction untuk memastikan integritas jawaban PG, log anti-cheat, dan sinkronisasi nilai otomatis ke tabel krs_detail.",
            doubtfulQuestions = emptySet(),
            remainingSeconds = 5400,
            cheatWarnings = 0,
            maxCheatWarnings = 3,
            lastWarningMessage = null
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_cbtSession.value.isExamRunning && _cbtSession.value.remainingSeconds > 0) {
                delay(1000L)
                _cbtSession.update { state ->
                    val nextSec = (state.remainingSeconds - 1).coerceAtLeast(0)
                    state.copy(remainingSeconds = nextSec)
                }
                if (_cbtSession.value.remainingSeconds == 0) {
                    finishAndSubmitCbtExam(autoSubmittedByCheat = false)
                }
            }
        }
    }

    fun selectCbtQuestionIndex(index: Int) {
        _cbtSession.update { state ->
            if (index in state.questions.indices) state.copy(currentQuestionIndex = index) else state
        }
    }

    fun selectCbtMcqOption(questionId: String, optionIndex: Int) {
        _cbtSession.update { state ->
            state.copy(selectedMcqOptions = state.selectedMcqOptions + (questionId to optionIndex))
        }
    }

    fun updateCbtEssayAnswer(text: String) {
        _cbtSession.update { state ->
            state.copy(essayAnswer = text)
        }
    }

    fun toggleCbtDoubtful(questionId: String) {
        _cbtSession.update { state ->
            val nextSet = if (state.doubtfulQuestions.contains(questionId)) {
                state.doubtfulQuestions - questionId
            } else {
                state.doubtfulQuestions + questionId
            }
            state.copy(doubtfulQuestions = nextSet)
        }
    }

    fun triggerAntiCheatViolation(reason: String) {
        val current = _cbtSession.value
        if (!current.isExamRunning) return
        val nextCount = current.cheatWarnings + 1
        if (nextCount >= current.maxCheatWarnings) {
            _cbtSession.update {
                it.copy(
                    cheatWarnings = nextCount,
                    lastWarningMessage = "PELANGGARAN KE-$nextCount ($reason): Batas terlampaui! Sistem melakukan AUTO-SUBMIT."
                )
            }
            finishAndSubmitCbtExam(autoSubmittedByCheat = true)
        } else {
            _cbtSession.update {
                it.copy(
                    cheatWarnings = nextCount,
                    lastWarningMessage = "PERINGATAN ANTI-CHEAT ($nextCount/${current.maxCheatWarnings}): Terdeteksi $reason!"
                )
            }
        }
    }

    fun finishAndSubmitCbtExam(autoSubmittedByCheat: Boolean) {
        val session = _cbtSession.value
        if (!session.isExamRunning) return
        timerJob?.cancel()

        // Auto-grade Multiple Choice questions immediately
        val mcqQuestions = session.questions.filter { !it.isEssay }
        var earnedScore = 0.0
        var totalMaxMcq = 0.0
        for (q in mcqQuestions) {
            totalMaxMcq += q.bobot
            val chosen = session.selectedMcqOptions[q.id]
            if (chosen != null && chosen == q.kunciJawabanIndex) {
                earnedScore += q.bobot
            }
        }
        val normalizedMcqScore = if (totalMaxMcq > 0) (earnedScore / totalMaxMcq) * 100.0 else 0.0

        _cbtSession.update { it.copy(isExamRunning = false) }

        viewModelScope.launch {
            repository.submitCbtExam(
                examCode = session.examCode,
                kodeMk = session.kodeMk,
                namaUjian = session.namaUjian,
                jenisUjian = session.jenisUjian,
                mcqScore = normalizedMcqScore,
                essayAnswer = session.essayAnswer,
                cheatWarnings = session.cheatWarnings,
                autoSubmittedByAntiCheat = autoSubmittedByCheat
            ).onSuccess {
                val modeMsg = if (autoSubmittedByCheat) "AUTO-SUBMIT ANTI-CHEAT" else "SUBMIT MANUAL"
                _statusBanner.value = "Ujian CBT ($modeMsg) selesai! Skor PG Otomatis: ${normalizedMcqScore.toInt()}/100 & tersinkron ke KHS."
            }.onFailure {
                _statusBanner.value = "Gagal menyimpan hasil ujian CBT: ${it.localizedMessage}"
            }
        }
    }

    fun gradeEssayByDosen(attempt: CbtAttempt, essayScore: Double) {
        viewModelScope.launch {
            repository.gradeCbtEssayByDosen(attempt, essayScore)
                .onSuccess { _statusBanner.value = "Nilai Essay untuk ${attempt.namaUjian} berhasil disimpan & rekap KHS diperbarui!" }
                .onFailure { _statusBanner.value = "Gagal menilai essay: ${it.localizedMessage}" }
        }
    }

    // --- PDDIKTI NEO FEEDER SYNC ACTIONS ---
    fun runPddiktiSync(endpointAction: String, entityType: String, direction: String, count: Int, summary: String) {
        viewModelScope.launch {
            repository.triggerPddiktiFeederSync(endpointAction, entityType, direction, count, summary)
                .onSuccess { _statusBanner.value = "Sinkronisasi PDDIKTI Neo Feeder ($endpointAction) sukses diproses!" }
                .onFailure { _statusBanner.value = "Gagal sinkronisasi PDDIKTI: ${it.localizedMessage}" }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private companion object {
        const val TAG = "SiakadViewModel"
    }
}
