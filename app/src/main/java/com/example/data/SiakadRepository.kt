package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

class SiakadRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing SIAKAD Cloud Firestore.")
    }

    // --- 1. ACADEMIC PROFILE & RBAC ---
    fun observeProfile(userId: String): Flow<AcademicProfile?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(AcademicProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                throw e
            }
    }

    suspend fun ensureOrCreateProfile(fullName: String, email: String): Result<AcademicProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                val existing = snap.toObject(AcademicProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                if (existing != null) return@runCatching existing
            }
            val payload = mapOf(
                "userId" to uid,
                "fullName" to fullName.ifBlank { "Mahasiswa Cloud" }.take(100),
                "email" to email.ifBlank { "mahasiswa@univ-cloud.ac.id" }.take(120),
                "role" to "MAHASISWA",
                "nimOrNidn" to "2026101042",
                "prodi" to "S1 Teknik Informatika",
                "semester" to 3L,
                "ipSemesterLalu" to 3.72,
                "ipk" to 3.68,
                "isUktPaid" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            seedInitialDataIfNeeded(uid, fullName.ifBlank { "Mahasiswa Cloud" })
            val createdSnap = docRef.get().await()
            createdSnap.toObject(AcademicProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: AcademicProfile(id = uid, userId = uid, fullName = fullName, email = email)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun switchRole(newRole: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update(
                mapOf(
                    "role" to newRole,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateUktPaidStatus(isPaid: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update(
                mapOf(
                    "isUktPaid" to isPaid,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 2. PMB ONLINE MODULE ---
    fun observePmbApplicants(userId: String): Flow<List<PmbApplicant>> {
        val path = "users/$userId/pmb_applicants"
        return db.collection("users").document(userId).collection("pmb_applicants")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(PmbApplicant::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun createPmbApplicant(
        fullName: String,
        asalSekolah: String,
        pilihan1: String,
        pilihan2: String,
        ijazahUploaded: Boolean,
        ktpUploaded: Boolean,
        fotoUploaded: Boolean
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val colRef = db.collection("users").document(uid).collection("pmb_applicants")
        val docRef = colRef.document()
        val regNo = "PMB-2026-${(1000..9999).random()}"
        val vaNo = "880102026${(10000..99999).random()}"
        try {
            val payload = mapOf(
                "userId" to uid,
                "registrationNo" to regNo,
                "fullName" to fullName.trim().take(100),
                "asalSekolah" to asalSekolah.trim().take(100),
                "pilihanProdi1" to pilihan1.trim().take(80),
                "pilihanProdi2" to pilihan2.trim().take(80),
                "berkasIjazah" to ijazahUploaded,
                "berkasKtp" to ktpUploaded,
                "berkasFoto" to fotoUploaded,
                "vaNumber" to vaNo,
                "formFeePaid" to false,
                "statusSeleksi" to "MENUNGGU_VERIFIKASI",
                "generatedNim" to "-",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun advancePmbStatus(applicant: PmbApplicant, nextStep: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("pmb_applicants").document(applicant.id)
        try {
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            when (nextStep) {
                "BAYAR_FORMULIR" -> {
                    updates["formFeePaid"] = true
                }
                "VERIFIKASI_BERKAS" -> {
                    updates["statusSeleksi"] = "TERVERIFIKASI"
                }
                "LULUS_SELEKSI" -> {
                    updates["statusSeleksi"] = "LULUS"
                }
                "DAFTAR_ULANG_GENERATE_NIM" -> {
                    val prodiCode = if (applicant.pilihanProdi1.contains("Sistem", ignoreCase = true)) "102" else "101"
                    val newNim = "2026${prodiCode}${(100..999).random()}"
                    updates["statusSeleksi"] = "AKTIF_NIM"
                    updates["generatedNim"] = newNim
                    // Also update user profile NIM if desired
                    db.collection("users").document(uid).update(
                        mapOf(
                            "nimOrNidn" to newNim,
                            "prodi" to applicant.pilihanProdi1,
                            "role" to "MAHASISWA",
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 3. BILLING & PAYMENT GATEWAY MODULE ---
    fun observeBillingInvoices(userId: String): Flow<List<BillingInvoice>> {
        val path = "users/$userId/billing_invoices"
        return db.collection("users").document(userId).collection("billing_invoices")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(BillingInvoice::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun createBillingInvoice(
        title: String,
        category: String,
        jenjang: String,
        amount: Long,
        paymentMethod: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("billing_invoices").document()
        val invNo = "INV/2026/${(10000..99999).random()}"
        val codePrefix = when (paymentMethod) {
            "VA_BCA" -> "70012"
            "VA_MANDIRI" -> "89022"
            "VA_BNI" -> "98811"
            "QRIS" -> "QRIS-MIDTRANS-"
            else -> "ALFA-XENDIT-"
        }
        val vaCode = "$codePrefix${(10000000..99999999).random()}"
        try {
            val payload = mapOf(
                "userId" to uid,
                "invoiceNumber" to invNo,
                "title" to title.trim().take(120),
                "category" to category,
                "jenjang" to jenjang,
                "amount" to amount,
                "paymentMethod" to paymentMethod,
                "vaOrPaymentCode" to vaCode,
                "status" to "UNPAID",
                "semester" to "2026/2027 Ganjil",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            if (category == "UKT_SPP") {
                updateUktPaidStatus(false)
            }
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun reconcileInvoiceWebhook(invoice: BillingInvoice, newStatus: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("billing_invoices").document(invoice.id)
        try {
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            if (invoice.category == "UKT_SPP") {
                updateUktPaidStatus(newStatus == "PAID")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 4. KRS, PRESENSI & KHS GRADING MODULE ---
    fun observeKrsEnrollments(userId: String): Flow<List<KrsEnrollment>> {
        val path = "users/$userId/krs_enrollments"
        return db.collection("users").document(userId).collection("krs_enrollments")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(KrsEnrollment::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun enrollCourseKrs(course: CourseCatalogItem): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("krs_enrollments").document(course.kodeMk)
        try {
            val payload = mapOf(
                "userId" to uid,
                "kodeMk" to course.kodeMk,
                "namaMk" to course.namaMk,
                "sks" to course.sks,
                "semester" to course.semester,
                "dosenPengampu" to course.dosenPengampu,
                "jadwal" to course.jadwal,
                "statusApproval" to "DIAJUKAN",
                "presensiCount" to 1L,
                "nilaiTugas" to 85.0,
                "nilaiUts" to 80.0,
                "nilaiUas" to 0.0,
                "nilaiAkhir" to 0.0,
                "gradeHuruf" to "-",
                "bobotIndeks" to 0.0,
                "syncedPddikti" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun removeCourseKrs(enrollmentId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("krs_enrollments").document(enrollmentId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun updateKrsApproval(enrollmentId: String, statusApproval: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("krs_enrollments").document(enrollmentId)
        try {
            docRef.update(
                mapOf(
                    "statusApproval" to statusApproval,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun recordAttendance(enrollment: KrsEnrollment): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("krs_enrollments").document(enrollment.id)
        val nextCount = (enrollment.presensiCount + 1L).coerceAtMost(16L)
        try {
            docRef.update(
                mapOf(
                    "presensiCount" to nextCount,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun submitCourseGrades(
        enrollmentId: String,
        tugas: Double,
        uts: Double,
        uas: Double
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("krs_enrollments").document(enrollmentId)
        // Formula Bobot SIAKAD: 30% Tugas + 30% UTS + 40% UAS
        val rawAkhir = (tugas * 0.30) + (uts * 0.30) + (uas * 0.40)
        val nilaiAkhir = ((rawAkhir * 10.0).roundToInt() / 10.0).coerceIn(0.0, 100.0)
        val (huruf, bobot) = calculateGradeConversion(nilaiAkhir)
        try {
            docRef.update(
                mapOf(
                    "nilaiTugas" to tugas.coerceIn(0.0, 100.0),
                    "nilaiUts" to uts.coerceIn(0.0, 100.0),
                    "nilaiUas" to uas.coerceIn(0.0, 100.0),
                    "nilaiAkhir" to nilaiAkhir,
                    "gradeHuruf" to huruf,
                    "bobotIndeks" to bobot,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    fun calculateGradeConversion(score: Double): Pair<String, Double> {
        return when {
            score >= 85.0 -> "A" to 4.00
            score >= 80.0 -> "A-" to 3.70
            score >= 75.0 -> "B+" to 3.30
            score >= 70.0 -> "B" to 3.00
            score >= 65.0 -> "B-" to 2.70
            score >= 60.0 -> "C+" to 2.30
            score >= 55.0 -> "C" to 2.00
            score >= 40.0 -> "D" to 1.00
            else -> "E" to 0.00
        }
    }

    // --- 5. CBT EXAM ENGINE & ANTI-CHEAT MODULE ---
    fun observeCbtAttempts(userId: String): Flow<List<CbtAttempt>> {
        val path = "users/$userId/cbt_attempts"
        return db.collection("users").document(userId).collection("cbt_attempts")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(CbtAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun submitCbtExam(
        examCode: String,
        kodeMk: String,
        namaUjian: String,
        jenisUjian: String,
        mcqScore: Double,
        essayAnswer: String,
        cheatWarnings: Int,
        autoSubmittedByAntiCheat: Boolean
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("cbt_attempts").document(examCode)
        // Initial essay score auto-estimated if lecturer hasn't graded yet, or 85 if non-empty
        val initialEssayScore = if (essayAnswer.trim().length >= 20) 85.0 else 60.0
        val finalScore = (((mcqScore * 0.70) + (initialEssayScore * 0.30)) * 10.0).roundToInt() / 10.0
        try {
            val payload = mapOf(
                "userId" to uid,
                "examCode" to examCode,
                "kodeMk" to kodeMk,
                "namaUjian" to namaUjian,
                "jenisUjian" to jenisUjian,
                "mcqScore" to ((mcqScore * 10.0).roundToInt() / 10.0).coerceIn(0.0, 100.0),
                "essayAnswer" to essayAnswer.trim().ifBlank { "Tidak menjawab soal essay." }.take(3500),
                "essayScore" to initialEssayScore,
                "finalCbtScore" to finalScore.coerceIn(0.0, 100.0),
                "cheatWarnings" to cheatWarnings.toLong().coerceIn(0L, 100L),
                "autoSubmittedByAntiCheat" to autoSubmittedByAntiCheat,
                "status" to "SUBMITTED_WAITING_ESSAY",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            // Sync score directly to KHS / KRS enrollment if enrolled
            syncCbtScoreToKrs(uid, kodeMk, jenisUjian, finalScore)
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun gradeCbtEssayByDosen(attempt: CbtAttempt, newEssayScore: Double): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("cbt_attempts").document(attempt.id)
        val clampedEssay = newEssayScore.coerceIn(0.0, 100.0)
        val combined = (((attempt.mcqScore * 0.70) + (clampedEssay * 0.30)) * 10.0).roundToInt() / 10.0
        try {
            docRef.update(
                mapOf(
                    "essayScore" to clampedEssay,
                    "finalCbtScore" to combined.coerceIn(0.0, 100.0),
                    "status" to "GRADED",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            syncCbtScoreToKrs(uid, attempt.kodeMk, attempt.jenisUjian, combined)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    private suspend fun syncCbtScoreToKrs(uid: String, kodeMk: String, jenisUjian: String, score: Double) {
        val krsRef = db.collection("users").document(uid).collection("krs_enrollments").document(kodeMk)
        val snap = krsRef.get().await()
        if (snap.exists()) {
            val krs = snap.toObject(KrsEnrollment::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) ?: return
            val newUts = if (jenisUjian == "UTS") score else krs.nilaiUts
            val newUas = if (jenisUjian == "UAS") score else if (krs.nilaiUas == 0.0) 82.0 else krs.nilaiUas
            submitCourseGrades(kodeMk, krs.nilaiTugas, newUts, newUas)
        }
    }

    // --- 6. PDDIKTI NEO FEEDER SYNC MODULE ---
    fun observePddiktiLogs(userId: String): Flow<List<PddiktiSyncLog>> {
        val path = "users/$userId/pddikti_logs"
        return db.collection("users").document(userId).collection("pddikti_logs")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(PddiktiSyncLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun triggerPddiktiFeederSync(
        endpointAction: String,
        entityType: String,
        direction: String,
        recordsCount: Int,
        summary: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("pddikti_logs").document()
        try {
            val payload = mapOf(
                "userId" to uid,
                "endpointAction" to endpointAction.take(80),
                "entityType" to entityType,
                "direction" to direction,
                "recordsProcessed" to recordsCount.toLong().coerceAtLeast(1L),
                "status" to "SUCCESS",
                "responseSummary" to summary.take(450),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- SEED INITIAL REPRESENTATIVE DATA ---
    private suspend fun seedInitialDataIfNeeded(uid: String, fullName: String) {
        // 1. Seed 1 PMB Applicant
        val pmbRef = db.collection("users").document(uid).collection("pmb_applicants").document("pmb_sample_1")
        pmbRef.set(
            mapOf(
                "userId" to uid,
                "registrationNo" to "PMB-2026-0891",
                "fullName" to fullName.take(100),
                "asalSekolah" to "SMAN 3 Unggulan Jakarta",
                "pilihanProdi1" to "S1 Teknik Informatika",
                "pilihanProdi2" to "S1 Sistem Informasi",
                "berkasIjazah" to true,
                "berkasKtp" to true,
                "berkasFoto" to true,
                "vaNumber" to "88010202600891",
                "formFeePaid" to true,
                "statusSeleksi" to "LULUS",
                "generatedNim" to "2026101042",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        // 2. Seed 2 Billing Invoices (1 PAID UKT, 1 UNPAID Pembangunan)
        val inv1Ref = db.collection("users").document(uid).collection("billing_invoices").document("inv_ukt_20261")
        inv1Ref.set(
            mapOf(
                "userId" to uid,
                "invoiceNumber" to "INV/2026/UKT-0042",
                "title" to "UKT / SPP Semester Ganjil 2026/2027",
                "category" to "UKT_SPP",
                "jenjang" to "S1",
                "amount" to 5500000L,
                "paymentMethod" to "VA_BCA",
                "vaOrPaymentCode" to "700122026101042",
                "status" to "PAID",
                "semester" to "2026/2027 Ganjil",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        val inv2Ref = db.collection("users").document(uid).collection("billing_invoices").document("inv_sks_20261")
        inv2Ref.set(
            mapOf(
                "userId" to uid,
                "invoiceNumber" to "INV/2026/PRAK-0118",
                "title" to "Biaya Praktikum Cloud & Sertifikasi",
                "category" to "SKS",
                "jenjang" to "S1",
                "amount" to 750000L,
                "paymentMethod" to "QRIS",
                "vaOrPaymentCode" to "QRIS-MIDTRANS-99281721",
                "status" to "UNPAID",
                "semester" to "2026/2027 Ganjil",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        // 3. Seed 3 Initial KRS Enrollments
        val initialCourses = listOf(
            Triple("IF301", "Arsitektur Cloud & Microservices", "Dr. Budi Santoso, M.Kom."),
            Triple("IF302", "Keamanan Sistem & Kriptografi", "Prof. Dr. Rina Wijaya, M.T."),
            Triple("IF303", "Rekayasa Perangkat Lunak Enterprise", "Ir. Hendra Kusuma, M.Sc.")
        )
        initialCourses.forEachIndexed { idx, (kode, nama, dosen) ->
            val krsRef = db.collection("users").document(uid).collection("krs_enrollments").document(kode)
            val tugas = 88.0 - (idx * 3)
            val uts = 85.0 - (idx * 2)
            val uas = 86.0 - (idx * 2)
            val akhir = ((tugas * 0.3 + uts * 0.3 + uas * 0.4) * 10.0).roundToInt() / 10.0
            val (huruf, bobot) = calculateGradeConversion(akhir)
            krsRef.set(
                mapOf(
                    "userId" to uid,
                    "kodeMk" to kode,
                    "namaMk" to nama,
                    "sks" to 3L,
                    "semester" to 3L,
                    "dosenPengampu" to dosen,
                    "jadwal" to "Senin/Rabu 08:00-10:30 (Gd. Cyber Lt.${idx + 2})",
                    "statusApproval" to "DISETUJUI_PA",
                    "presensiCount" to (14L - idx),
                    "nilaiTugas" to tugas,
                    "nilaiUts" to uts,
                    "nilaiUas" to uas,
                    "nilaiAkhir" to akhir,
                    "gradeHuruf" to huruf,
                    "bobotIndeks" to bobot,
                    "syncedPddikti" to (idx == 0),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

        // 4. Seed 1 PDDIKTI Log
        val logRef = db.collection("users").document(uid).collection("pddikti_logs").document("sync_initial_1")
        logRef.set(
            mapOf(
                "userId" to uid,
                "endpointAction" to "InsertPerkuliahanMahasiswa (Neo Feeder WS 2.1)",
                "entityType" to "KRS",
                "direction" to "PUSH",
                "recordsProcessed" to 142L,
                "status" to "SUCCESS",
                "responseSummary" to "142 aktivitas kuliah mahasiswa semester 20261 berhasil dipush ke Neo Feeder PDDIKTI.",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
