package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class AcademicProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "MAHASISWA", // SUPER_ADMIN, ADMIN_AKADEMIK, DOSEN, MAHASISWA, CALON_MAHASISWA
    val nimOrNidn: String = "2026101001",
    val prodi: String = "S1 Teknik Informatika",
    val semester: Long = 3L,
    val ipSemesterLalu: Double = 3.65,
    val ipk: Double = 3.58,
    val isUktPaid: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PmbApplicant(
    @DocumentId val id: String = "",
    val userId: String = "",
    val registrationNo: String = "",
    val fullName: String = "",
    val asalSekolah: String = "",
    val pilihanProdi1: String = "",
    val pilihanProdi2: String = "",
    val berkasIjazah: Boolean = true,
    val berkasKtp: Boolean = true,
    val berkasFoto: Boolean = true,
    val vaNumber: String = "",
    val formFeePaid: Boolean = false,
    val statusSeleksi: String = "MENUNGGU_VERIFIKASI", // MENUNGGU_VERIFIKASI, TERVERIFIKASI, LULUS, TIDAK_LULUS, AKTIF_NIM
    val generatedNim: String = "-",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BillingInvoice(
    @DocumentId val id: String = "",
    val userId: String = "",
    val invoiceNumber: String = "",
    val title: String = "",
    val category: String = "UKT_SPP", // FORMULIR_PMB, UKT_SPP, SKS, PEMBANGUNAN
    val jenjang: String = "S1", // D1, D3, S1
    val amount: Long = 0L,
    val paymentMethod: String = "VA_BCA", // VA_BCA, VA_MANDIRI, VA_BNI, QRIS, ALFAMART_INDOMARET
    val vaOrPaymentCode: String = "",
    val status: String = "UNPAID", // UNPAID, PAID
    val semester: String = "2026/2027 Ganjil",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class KrsEnrollment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val kodeMk: String = "",
    val namaMk: String = "",
    val sks: Long = 3L,
    val semester: Long = 3L,
    val dosenPengampu: String = "",
    val jadwal: String = "",
    val statusApproval: String = "DIAJUKAN", // DRAFT, DIAJUKAN, DISETUJUI_PA, DITOLAK_PA
    val presensiCount: Long = 0L,
    val nilaiTugas: Double = 0.0,
    val nilaiUts: Double = 0.0,
    val nilaiUas: Double = 0.0,
    val nilaiAkhir: Double = 0.0,
    val gradeHuruf: String = "-", // -, A, A-, B+, B, B-, C+, C, D, E
    val bobotIndeks: Double = 0.0,
    val syncedPddikti: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CbtAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val examCode: String = "",
    val kodeMk: String = "",
    val namaUjian: String = "",
    val jenisUjian: String = "UTS", // UTS, UAS, KUIS
    val mcqScore: Double = 0.0,
    val essayAnswer: String = "",
    val essayScore: Double = 0.0,
    val finalCbtScore: Double = 0.0,
    val cheatWarnings: Long = 0L,
    val autoSubmittedByAntiCheat: Boolean = false,
    val status: String = "ONGOING", // ONGOING, SUBMITTED_WAITING_ESSAY, GRADED
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PddiktiSyncLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val endpointAction: String = "",
    val entityType: String = "MAHASISWA", // MAHASISWA, DOSEN, KELAS_KULIAH, KRS, NILAI_AKHIR, KELULUSAN_IJAZAH
    val direction: String = "PUSH", // PUSH, PULL
    val recordsProcessed: Long = 0L,
    val status: String = "SUCCESS", // SUCCESS, PARTIAL_WARNING
    val responseSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CourseCatalogItem(
    val kodeMk: String,
    val namaMk: String,
    val sks: Long,
    val semester: Long,
    val prasyaratKode: String?,
    val dosenPengampu: String,
    val jadwal: String
)

data class CbtQuestionItem(
    val id: String,
    val nomor: Int,
    val pertanyaan: String,
    val opsi: List<String>, // Empty if Essay
    val kunciJawabanIndex: Int, // -1 if Essay
    val bobot: Double,
    val isEssay: Boolean = false
)
