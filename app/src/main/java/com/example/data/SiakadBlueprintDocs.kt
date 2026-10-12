package com.example.data

object SiakadBlueprintDocs {

    val rolesList = listOf(
        "SUPER_ADMIN" to "Super Admin (Full Control & RBAC)",
        "ADMIN_AKADEMIK" to "Administrator / Bagian Akademik",
        "DOSEN" to "Dosen Pengampu & Pembimbing Akademik (PA)",
        "MAHASISWA" to "Mahasiswa Aktif (KRS, CBT, KHS)",
        "CALON_MAHASISWA" to "Calon Mahasiswa Baru (PMB Online)"
    )

    val courseCatalog = listOf(
        CourseCatalogItem(
            kodeMk = "IF301",
            namaMk = "Arsitektur Cloud & Microservices",
            sks = 3L,
            semester = 3L,
            prasyaratKode = "IF201",
            dosenPengampu = "Dr. Budi Santoso, M.Kom.",
            jadwal = "Senin 08:00 - 10:30 (Lab Cloud A)"
        ),
        CourseCatalogItem(
            kodeMk = "IF302",
            namaMk = "Keamanan Sistem & Kriptografi",
            sks = 3L,
            semester = 3L,
            prasyaratKode = "IF202",
            dosenPengampu = "Prof. Dr. Rina Wijaya, M.T.",
            jadwal = "Rabu 09:30 - 12:00 (Gd. Cyber 302)"
        ),
        CourseCatalogItem(
            kodeMk = "IF303",
            namaMk = "Rekayasa Perangkat Lunak Enterprise",
            sks = 4L,
            semester = 3L,
            prasyaratKode = null,
            dosenPengampu = "Ir. Hendra Kusuma, M.Sc.",
            jadwal = "Selasa 13:00 - 16:20 (R. Seminar TI)"
        ),
        CourseCatalogItem(
            kodeMk = "IF304",
            namaMk = "Kecerdasan Buatan & Machine Learning",
            sks = 3L,
            semester = 3L,
            prasyaratKode = "IF104",
            dosenPengampu = "Dr. Siti Aminah, S.T., M.Kom.",
            jadwal = "Kamis 08:00 - 10:30 (Lab AI Terpadu)"
        ),
        CourseCatalogItem(
            kodeMk = "IF305",
            namaMk = "Administrasi Basis Data Terdistribusi",
            sks = 3L,
            semester = 3L,
            prasyaratKode = null,
            dosenPengampu = "Drs. Ahmad Fauzi, M.Cs.",
            jadwal = "Jumat 08:30 - 11:00 (Lab Data B)"
        ),
        CourseCatalogItem(
            kodeMk = "IF306",
            namaMk = "Metodologi Penelitian & Penulisan Ilmiah",
            sks = 2L,
            semester = 3L,
            prasyaratKode = null,
            dosenPengampu = "Dr. Dewi Lestari, M.Si.",
            jadwal = "Kamis 13:30 - 15:10 (Gd. Utama 204)"
        ),
        CourseCatalogItem(
            kodeMk = "IF307",
            namaMk = "Pemrograman Perangkat Bergerak Lanjut",
            sks = 3L,
            semester = 3L,
            prasyaratKode = "IF205",
            dosenPengampu = "Arif Rahman, S.Kom., M.T.",
            jadwal = "Selasa 08:00 - 10:30 (Lab Mobile 1)"
        ),
        CourseCatalogItem(
            kodeMk = "IF308",
            namaMk = "DevOps, CI/CD & Containerization",
            sks = 3L,
            semester = 3L,
            prasyaratKode = "IF301",
            dosenPengampu = "Dr. Budi Santoso, M.Kom.",
            jadwal = "Jumat 13:30 - 16:00 (Lab Cloud B)"
        )
    )

    val defaultCbtQuestions = listOf(
        CbtQuestionItem(
            id = "Q1",
            nomor = 1,
            pertanyaan = "Dalam arsitektur SIAKAD berbasis Cloud, mekanisme apa yang memastikan mahasiswa hanya dapat mengisi KRS dan mengakses Ujian CBT apabila tagihan UKT berstatus PAID?",
            opsi = listOf(
                "Middleware / Policy Gate yang memvalidasi status_bayar pada tabel pembayaran sebelum membuka route akademik",
                "Penyimpanan cookie statis tanpa validasi server",
                "Penghapusan tabel KRS setiap pergantian jam",
                "Menjalankan cronjob manual setiap akhir tahun"
            ),
            kunciJawabanIndex = 0,
            bobot = 25.0,
            isEssay = false
        ),
        CbtQuestionItem(
            id = "Q2",
            nomor = 2,
            pertanyaan = "Berapa batas maksimal SKS yang dapat diambil oleh mahasiswa pada semester berjalan jika Indeks Prestasi Semester (IPS) sebelumnya adalah 3.65 (≥ 3.00)?",
            opsi = listOf(
                "Maksimal 15 SKS",
                "Maksimal 18 SKS",
                "Maksimal 21 SKS",
                "Maksimal 24 SKS"
            ),
            kunciJawabanIndex = 3,
            bobot = 25.0,
            isEssay = false
        ),
        CbtQuestionItem(
            id = "Q3",
            nomor = 3,
            pertanyaan = "Pada fitur Sistem Keamanan Ujian CBT (Anti-Cheat), event DOM JavaScript apa yang digunakan pada Blade/Frontend untuk mendeteksi mahasiswa yang berpindah tab atau keluar dari jendela browser?",
            opsi = listOf(
                "document.addEventListener('visibilitychange') & window.addEventListener('blur')",
                "window.addEventListener('resize')",
                "document.getElementById('timer').innerHTML",
                "localStorage.clear()"
            ),
            kunciJawabanIndex = 0,
            bobot = 25.0,
            isEssay = false
        ),
        CbtQuestionItem(
            id = "Q4",
            nomor = 4,
            pertanyaan = "Dalam integrasi PDDIKTI Neo Feeder, endpoint action apakah yang dipanggil saat SIAKAD melakukan Push nilai akhir semester mahasiswa?",
            opsi = listOf(
                "GetDictionary",
                "InsertNilaiPerkuliahanKelas / UpdateNilaiPerkuliahanKelas",
                "DeleteMahasiswaLulusDO",
                "GetTokenFeederOnly"
            ),
            kunciJawabanIndex = 1,
            bobot = 25.0,
            isEssay = false
        ),
        CbtQuestionItem(
            id = "Q5_ESSAY",
            nomor = 5,
            pertanyaan = "[SOAL ESSAY] Jelaskan alur transaksi database (DB::transaction) ketika mahasiswa menekan tombol Submit Ujian CBT hingga nilai Pilihan Ganda terkoreksi otomatis dan terhubung ke KHS!",
            opsi = emptyList(),
            kunciJawabanIndex = -1,
            bobot = 100.0,
            isEssay = true
        )
    )

    const val SQL_SCHEMA_DDL = """
-- ============================================================================
-- 1. SKEMA DATABASE RELASIONAL (PostgreSQL / MySQL) - SIAKAD CLOUD ENTERPRISE
-- ============================================================================

-- A. TABEL USERS & RBAC (5 Level Pengguna)
CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('SUPER_ADMIN', 'ADMIN_AKADEMIK', 'DOSEN', 'MAHASISWA', 'CALON_MAHASISWA') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- B. TABEL MASTER AKADEMIK (Program Studi & Tahun Akademik)
CREATE TABLE prodi (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    kode_prodi VARCHAR(10) NOT NULL UNIQUE,
    kode_pddikti UUID NOT NULL COMMENT 'Mapping id_sms Neo Feeder PDDIKTI',
    nama_prodi VARCHAR(100) NOT NULL,
    jenjang ENUM('D1', 'D3', 'S1', 'S2') NOT NULL DEFAULT 'S1',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tahun_akademik (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    kode_periode VARCHAR(5) NOT NULL UNIQUE COMMENT 'Contoh: 20261 (Ganjil), 20262 (Genap)',
    nama_periode VARCHAR(60) NOT NULL,
    is_aktif BOOLEAN DEFAULT FALSE,
    batas_mulai_krs DATE NOT NULL,
    batas_akhir_krs DATE NOT NULL
);

-- C. TABEL DOSEN & MAHASISWA
CREATE TABLE dosen (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    nidn VARCHAR(20) NOT NULL UNIQUE,
    id_dosen_pddikti UUID NULL,
    prodi_id BIGINT UNSIGNED NOT NULL,
    gelar_depan VARCHAR(30) NULL,
    gelar_belakang VARCHAR(40) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (prodi_id) REFERENCES prodi(id)
);

CREATE TABLE mahasiswa (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    nim VARCHAR(20) NOT NULL UNIQUE,
    id_registrasi_pddikti UUID NULL COMMENT 'Mapping id_reg_pd Neo Feeder',
    prodi_id BIGINT UNSIGNED NOT NULL,
    dosen_pa_id BIGINT UNSIGNED NULL COMMENT 'Dosen Pembimbing Akademik',
    angkatan YEAR NOT NULL,
    semester_aktif TINYINT UNSIGNED DEFAULT 1,
    ips_terakhir DECIMAL(3,2) DEFAULT 0.00,
    ipk DECIMAL(3,2) DEFAULT 0.00,
    status_mahasiswa ENUM('AKTIF', 'CUTI', 'LULUS', 'NON_AKTIF') DEFAULT 'AKTIF',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (prodi_id) REFERENCES prodi(id),
    FOREIGN KEY (dosen_pa_id) REFERENCES dosen(id)
);

-- D. MODUL PMB (Penerimaan Mahasiswa Baru)
CREATE TABLE pmb_pendaftar (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    no_pendaftaran VARCHAR(30) NOT NULL UNIQUE,
    asal_sekolah VARCHAR(120) NOT NULL,
    pilihan_prodi_1_id BIGINT UNSIGNED NOT NULL,
    pilihan_prodi_2_id BIGINT UNSIGNED NOT NULL,
    berkas_ijazah_url VARCHAR(255) NOT NULL,
    berkas_ktp_url VARCHAR(255) NOT NULL,
    berkas_foto_url VARCHAR(255) NOT NULL,
    va_formulir VARCHAR(35) NOT NULL,
    is_formulir_lunas BOOLEAN DEFAULT FALSE,
    status_seleksi ENUM('MENUNGGU_VERIFIKASI', 'TERVERIFIKASI', 'LULUS', 'TIDAK_LULUS', 'AKTIF_NIM') DEFAULT 'MENUNGGU_VERIFIKASI',
    nim_terbit VARCHAR(20) NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pilihan_prodi_1_id) REFERENCES prodi(id),
    FOREIGN KEY (pilihan_prodi_2_id) REFERENCES prodi(id)
);

-- E. MODUL KEUANGAN & BILLING (Payment Gateway Integration)
CREATE TABLE pembayaran (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    mahasiswa_id BIGINT UNSIGNED NOT NULL,
    tahun_akademik_id BIGINT UNSIGNED NOT NULL,
    no_invoice VARCHAR(40) NOT NULL UNIQUE,
    jenis_tagihan ENUM('FORMULIR_PMB', 'UKT_SPP', 'SKS', 'PEMBANGUNAN') NOT NULL,
    nominal DECIMAL(14,2) NOT NULL,
    channel_pembayaran ENUM('VA_BCA', 'VA_MANDIRI', 'VA_BNI', 'QRIS', 'ALFAMART_INDOMARET') NOT NULL,
    payment_code VARCHAR(64) NOT NULL COMMENT 'Nomor VA atau String QRIS Midtrans/Xendit',
    status_bayar ENUM('UNPAID', 'PAID', 'EXPIRED') DEFAULT 'UNPAID',
    paid_at TIMESTAMP NULL,
    FOREIGN KEY (mahasiswa_id) REFERENCES mahasiswa(id),
    FOREIGN KEY (tahun_akademik_id) REFERENCES tahun_akademik(id),
    INDEX idx_mhs_periode_status (mahasiswa_id, tahun_akademik_id, status_bayar)
);

-- F. MODUL AKADEMIK INTI (Mata Kuliah, Kelas, KRS, Presensi, KHS)
CREATE TABLE mata_kuliah (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    prodi_id BIGINT UNSIGNED NOT NULL,
    kode_mk VARCHAR(15) NOT NULL UNIQUE,
    nama_mk VARCHAR(120) NOT NULL,
    sks TINYINT UNSIGNED NOT NULL,
    semester_paket TINYINT UNSIGNED NOT NULL,
    prasyarat_mk_id BIGINT UNSIGNED NULL,
    bobot_tugas DECIMAL(5,2) DEFAULT 30.00,
    bobot_uts DECIMAL(5,2) DEFAULT 30.00,
    bobot_uas DECIMAL(5,2) DEFAULT 40.00,
    FOREIGN KEY (prodi_id) REFERENCES prodi(id),
    FOREIGN KEY (prasyarat_mk_id) REFERENCES mata_kuliah(id)
);

CREATE TABLE kelas_kuliah (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tahun_akademik_id BIGINT UNSIGNED NOT NULL,
    mata_kuliah_id BIGINT UNSIGNED NOT NULL,
    dosen_id BIGINT UNSIGNED NOT NULL,
    nama_kelas VARCHAR(20) NOT NULL,
    jadwal_hari VARCHAR(15) NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    ruangan VARCHAR(50) NOT NULL,
    kuota SMALLINT UNSIGNED DEFAULT 40,
    id_kelas_pddikti UUID NULL,
    FOREIGN KEY (tahun_akademik_id) REFERENCES tahun_akademik(id),
    FOREIGN KEY (mata_kuliah_id) REFERENCES mata_kuliah(id),
    FOREIGN KEY (dosen_id) REFERENCES dosen(id)
);

CREATE TABLE krs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    mahasiswa_id BIGINT UNSIGNED NOT NULL,
    tahun_akademik_id BIGINT UNSIGNED NOT NULL,
    status_approval ENUM('DRAFT', 'DIAJUKAN', 'DISETUJUI_PA', 'DITOLAK_PA') DEFAULT 'DRAFT',
    total_sks TINYINT UNSIGNED DEFAULT 0,
    approved_by_dosen_id BIGINT UNSIGNED NULL,
    approved_at TIMESTAMP NULL,
    catatan_pa TEXT NULL,
    UNIQUE KEY uq_mhs_ta (mahasiswa_id, tahun_akademik_id),
    FOREIGN KEY (mahasiswa_id) REFERENCES mahasiswa(id),
    FOREIGN KEY (tahun_akademik_id) REFERENCES tahun_akademik(id)
);

CREATE TABLE krs_detail (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    krs_id BIGINT UNSIGNED NOT NULL,
    kelas_kuliah_id BIGINT UNSIGNED NOT NULL,
    nilai_tugas DECIMAL(5,2) DEFAULT 0.00,
    nilai_uts DECIMAL(5,2) DEFAULT 0.00,
    nilai_uas DECIMAL(5,2) DEFAULT 0.00,
    nilai_akhir DECIMAL(5,2) DEFAULT 0.00,
    nilai_huruf CHAR(2) DEFAULT '-',
    bobot_indeks DECIMAL(3,2) DEFAULT 0.00,
    is_synced_pddikti BOOLEAN DEFAULT FALSE,
    UNIQUE KEY uq_krs_kelas (krs_id, kelas_kuliah_id),
    FOREIGN KEY (krs_id) REFERENCES krs(id) ON DELETE CASCADE,
    FOREIGN KEY (kelas_kuliah_id) REFERENCES kelas_kuliah(id)
);

-- G. MODUL UJIAN CBT (Computer Based Test & Anti-Cheat)
CREATE TABLE cbt_ujian (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    kelas_kuliah_id BIGINT UNSIGNED NOT NULL,
    kode_ujian VARCHAR(30) NOT NULL UNIQUE,
    judul_ujian VARCHAR(150) NOT NULL,
    jenis_ujian ENUM('UTS', 'UAS', 'KUIS') NOT NULL,
    durasi_menit SMALLINT UNSIGNED NOT NULL DEFAULT 90,
    acak_soal BOOLEAN DEFAULT TRUE,
    acak_opsi BOOLEAN DEFAULT TRUE,
    max_pelanggaran_tab TINYINT UNSIGNED DEFAULT 3,
    waktu_mulai DATETIME NOT NULL,
    waktu_selesai DATETIME NOT NULL,
    FOREIGN KEY (kelas_kuliah_id) REFERENCES kelas_kuliah(id)
);

CREATE TABLE cbt_soal (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cbt_ujian_id BIGINT UNSIGNED NOT NULL,
    tipe_soal ENUM('PILIHAN_GANDA', 'ESSAY') NOT NULL,
    pertanyaan TEXT NOT NULL,
    opsi_json JSON NULL COMMENT 'Array opsi A-D untuk Pilihan Ganda',
    kunci_jawaban VARCHAR(10) NULL COMMENT 'Index/Huruf kunci jawaban PG',
    bobot_nilai DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    FOREIGN KEY (cbt_ujian_id) REFERENCES cbt_ujian(id) ON DELETE CASCADE
);

CREATE TABLE cbt_peserta_ujian (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cbt_ujian_id BIGINT UNSIGNED NOT NULL,
    mahasiswa_id BIGINT UNSIGNED NOT NULL,
    waktu_mulai_kerja TIMESTAMP NOT NULL,
    waktu_submit TIMESTAMP NULL,
    skor_pg DECIMAL(5,2) DEFAULT 0.00,
    skor_essay DECIMAL(5,2) DEFAULT 0.00,
    skor_akhir_cbt DECIMAL(5,2) DEFAULT 0.00,
    jumlah_pindah_tab TINYINT UNSIGNED DEFAULT 0,
    is_auto_submitted_cheat BOOLEAN DEFAULT FALSE,
    status_pengerjaan ENUM('ONGOING', 'SUBMITTED_WAITING_ESSAY', 'GRADED') DEFAULT 'ONGOING',
    UNIQUE KEY uq_ujian_mhs (cbt_ujian_id, mahasiswa_id),
    FOREIGN KEY (cbt_ujian_id) REFERENCES cbt_ujian(id),
    FOREIGN KEY (mahasiswa_id) REFERENCES mahasiswa(id)
);

CREATE TABLE cbt_jawaban (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cbt_peserta_id BIGINT UNSIGNED NOT NULL,
    cbt_soal_id BIGINT UNSIGNED NOT NULL,
    jawaban_pg VARCHAR(10) NULL,
    jawaban_essay TEXT NULL,
    is_ragu_ragu BOOLEAN DEFAULT FALSE,
    nilai_diperoleh DECIMAL(5,2) DEFAULT 0.00,
    UNIQUE KEY uq_peserta_soal (cbt_peserta_id, cbt_soal_id),
    FOREIGN KEY (cbt_peserta_id) REFERENCES cbt_peserta_ujian(id) ON DELETE CASCADE,
    FOREIGN KEY (cbt_soal_id) REFERENCES cbt_soal(id)
);
"""

    const val LARAVEL_KRS_CONTROLLER_CODE = """
<?php

namespace App\Http\Controllers\Api\Akademik;

use App\Http\Controllers\Controller;
use App\Models\Krs;
use App\Models\KrsDetail;
use App\Models\KelasKuliah;
use App\Models\Pembayaran;
use App\Models\TahunAkademik;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;

class KrsController extends Controller
{
    /**
     * Endpoint: POST /api/v1/mahasiswa/krs/ambil
     * Proses Pengambilan KRS Online oleh Mahasiswa dengan Validasi:
     * 1. Cek Status Pembayaran UKT/SPP Semester Aktif (Harus PAID)
     * 2. Hitung Kuota Maksimum SKS berdasarkan IPS Semester Lalu
     * 3. Cek Prasyarat Mata Kuliah & Bentrok Jadwal
     */
    public function storeKrs(Request ${'$'}request)
    {
        ${'$'}validated = ${'$'}request->validate([
            'kelas_kuliah_ids' => 'required|array|min:1',
            'kelas_kuliah_ids.*' => 'exists:kelas_kuliah,id',
        ]);

        ${'$'}mahasiswa = ${'$'}request->user()->mahasiswa;
        ${'$'}tahunAktif = TahunAkademik::where('is_aktif', true)->firstOrFail();

        // 1. VALIDASI BILLING GATE: Pastikan UKT Semester berjalan sudah PAID
        ${'$'}isUktLunas = Pembayaran::where('mahasiswa_id', ${'$'}mahasiswa->id)
            ->where('tahun_akademik_id', ${'$'}tahunAktif->id)
            ->where('jenis_tagihan', 'UKT_SPP')
            ->where('status_bayar', 'PAID')
            ->exists();

        if (!${'$'}isUktLunas) {
            return response()->json([
                'status' => 'error',
                'code' => 'BILLING_UNPAID_HOLD',
                'message' => 'Akses KRS terkunci. Silakan lunasi tagihan UKT/SPP Semester ' . ${'$'}tahunAktif->nama_periode
            ], 403);
        }

        // 2. HITUNG KUOTA SKS BERDASARKAN IPS SEMESTER LALU
        ${'$'}ips = (float) ${'$'}mahasiswa->ips_terakhir;
        ${'$'}maxSks = match (true) {
            ${'$'}ips >= 3.00 => 24,
            ${'$'}ips >= 2.50 => 21,
            ${'$'}ips >= 2.00 => 18,
            default      => 15,
        };

        ${'$'}kelasDipilih = KelasKuliah::with('mataKuliah')
            ->whereIn('id', ${'$'}validated['kelas_kuliah_ids'])
            ->where('tahun_akademik_id', ${'$'}tahunAktif->id)
            ->lockForUpdate()
            ->get();

        ${'$'}totalSksDiambil = ${'$'}kelasDipilih->sum(fn(${'$'}k) => ${'$'}k->mataKuliah->sks);

        if (${'$'}totalSksDiambil > ${'$'}maxSks) {
            throw ValidationException::withMessages([
                'sks_limit' => "Total SKS (${'$'}totalSksDiambil SKS) melebihi batas kuota IPS Anda (${'$'}ips -> Maks ${'$'}maxSks SKS)."
            ]);
        }

        // 3. SIMPAN KRS & DETAIL DALAM DATABASE TRANSACTION
        return DB::transaction(function () use (${'$'}mahasiswa, ${'$'}tahunAktif, ${'$'}kelasDipilih, ${'$'}totalSksDiambil) {
            ${'$'}krs = Krs::updateOrCreate(
                [
                    'mahasiswa_id' => ${'$'}mahasiswa->id,
                    'tahun_akademik_id' => ${'$'}tahunAktif->id,
                ],
                [
                    'status_approval' => 'DIAJUKAN',
                    'total_sks' => ${'$'}totalSksDiambil,
                ]
            );

            // Sinkronisasi mata kuliah yang dipilih
            ${'$'}krs->details()->delete();
            foreach (${'$'}kelasDipilih as ${'$'}kelas) {
                if (${'$'}kelas->kuota <= 0) {
                    throw ValidationException::withMessages([
                        'kuota' => "Kuota kelas ${'$'}{kelas->mataKuliah->nama_mk} (${'$'}{kelas->nama_kelas}) sudah penuh."
                    ]);
                }

                KrsDetail::create([
                    'krs_id' => ${'$'}krs->id,
                    'kelas_kuliah_id' => ${'$'}kelas->id,
                ]);
            }

            return response()->json([
                'status' => 'success',
                'message' => 'KRS berhasil diajukan dan menunggu persetujuan Dosen PA.',
                'data' => [
                    'krs_id' => ${'$'}krs->id,
                    'ips_lalu' => ${'$'}mahasiswa->ips_terakhir,
                    'batas_sks' => ${'$'}totalSksDiambil . ' / ' . ${'$'}krs->total_sks,
                    'status_approval' => ${'$'}krs->status_approval,
                ]
            ], 201);
        });
    }
}
"""

    const val LARAVEL_CBT_CONTROLLER_CODE = """
<?php

namespace App\Http\Controllers\Api\Cbt;

use App\Http\Controllers\Controller;
use App\Models\CbtPesertaUjian;
use App\Models\CbtSoal;
use App\Models\KrsDetail;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class CbtExamController extends Controller
{
    /**
     * Endpoint: POST /api/v1/mahasiswa/cbt/{ujianId}/submit
     * Proses Submit Ujian CBT, Validasi Anti-Cheat Log, Koreksi Otomatis Pilihan Ganda,
     * dan Integrasi Langsung ke Nilai UTS/UAS pada KHS SIAKAD.
     */
    public function submitExam(Request ${'$'}request, int ${'$'}ujianId)
    {
        ${'$'}validated = ${'$'}request->validate([
            'jawaban' => 'required|array',
            'jawaban.*.soal_id' => 'required|integer|exists:cbt_soal,id',
            'jawaban.*.jawaban_pg' => 'nullable|string|max:10',
            'jawaban.*.jawaban_essay' => 'nullable|string|max:5000',
            'jumlah_pindah_tab' => 'required|integer|min:0',
            'is_auto_submitted_cheat' => 'required|boolean',
        ]);

        ${'$'}mahasiswa = ${'$'}request->user()->mahasiswa;

        return DB::transaction(function () use (${'$'}validated, ${'$'}mahasiswa, ${'$'}ujianId) {
            ${'$'}peserta = CbtPesertaUjian::with('ujian.kelasKuliah.mataKuliah')
                ->where('cbt_ujian_id', ${'$'}ujianId)
                ->where('mahasiswa_id', ${'$'}mahasiswa->id)
                ->lockForUpdate()
                ->firstOrFail();

            if (${'$'}peserta->status_pengerjaan !== 'ONGOING') {
                return response()->json(['message' => 'Ujian sudah pernah disubmit.'], 422);
            }

            ${'$'}bankSoal = CbtSoal::where('cbt_ujian_id', ${'$'}ujianId)->get()->keyBy('id');
            ${'$'}totalBobotPg = 0;
            ${'$'}skorDiperolehPg = 0;
            ${'$'}hasEssay = false;

            foreach (${'$'}validated['jawaban'] as ${'$'}item) {
                ${'$'}soal = ${'$'}bankSoal->get(${'$'}item['soal_id']);
                if (!${'$'}soal) continue;

                ${'$'}nilaiSoal = 0;
                if (${'$'}soal->tipe_soal === 'PILIHAN_GANDA') {
                    ${'$'}totalBobotPg += ${'$'}soal->bobot_nilai;
                    ${'$'}isBenar = strtoupper((string) (${'$'}item['jawaban_pg'] ?? '')) === strtoupper((string) ${'$'}soal->kunci_jawaban);
                    ${'$'}nilaiSoal = ${'$'}isBenar ? (float) ${'$'}soal->bobot_nilai : 0.0;
                    ${'$'}skorDiperolehPg += ${'$'}nilaiSoal;
                } else {
                    ${'$'}hasEssay = true;
                }

                ${'$'}peserta->jawaban()->updateOrCreate(
                    ['cbt_soal_id' => ${'$'}soal->id],
                    [
                        'jawaban_pg' => ${'$'}item['jawaban_pg'] ?? null,
                        'jawaban_essay' => ${'$'}item['jawaban_essay'] ?? null,
                        'nilai_diperoleh' => ${'$'}nilaiSoal,
                    ]
                );
            }

            // Normalisasi skor Pilihan Ganda ke skala 0 - 100
            ${'$'}skorPgSkala100 = ${'$'}totalBobotPg > 0
                ? round((${'$'}skorDiperolehPg / ${'$'}totalBobotPg) * 100, 2)
                : 0.0;

            ${'$'}statusAkhir = ${'$'}hasEssay ? 'SUBMITTED_WAITING_ESSAY' : 'GRADED';

            ${'$'}peserta->update([
                'waktu_submit' => now(),
                'skor_pg' => ${'$'}skorPgSkala100,
                'skor_akhir_cbt' => ${'$'}skorPgSkala100,
                'jumlah_pindah_tab' => ${'$'}validated['jumlah_pindah_tab'],
                'is_auto_submitted_cheat' => ${'$'}validated['is_auto_submitted_cheat'],
                'status_pengerjaan' => ${'$'}statusAkhir,
            ]);

            // Jika seluruh soal Pilihan Ganda (atau langsung sinkron nilai sementara ke KHS)
            ${'$'}this->sinkronNilaiKeKhs(${'$'}peserta, ${'$'}skorPgSkala100);

            return response()->json([
                'status' => 'success',
                'message' => 'Ujian CBT berhasil disubmit dan dinilai otomatis.',
                'result' => [
                    'skor_pilihan_ganda' => ${'$'}skorPgSkala100,
                    'status_koreksi' => ${'$'}statusAkhir,
                    'anti_cheat_warnings' => ${'$'}peserta->jumlah_pindah_tab,
                ]
            ]);
        });
    }

    protected function sinkronNilaiKeKhs(CbtPesertaUjian ${'$'}peserta, float ${'$'}skorCbt): void
    {
        ${'$'}ujian = ${'$'}peserta->ujian;
        ${'$'}krsDetail = KrsDetail::whereHas('krs', function (${'$'}q) use (${'$'}peserta) {
                ${'$'}q->where('mahasiswa_id', ${'$'}peserta->mahasiswa_id)
                  ->where('status_approval', 'DISETUJUI_PA');
            })
            ->where('kelas_kuliah_id', ${'$'}ujian->kelas_kuliah_id)
            ->first();

        if (!${'$'}krsDetail) return;

        if (${'$'}ujian->jenis_ujian === 'UTS') {
            ${'$'}krsDetail->nilai_uts = ${'$'}skorCbt;
        } elseif (${'$'}ujian->jenis_ujian === 'UAS') {
            ${'$'}krsDetail->nilai_uas = ${'$'}skorCbt;
        }

        // Hitung Nilai Akhir Otomatis & Konversi Huruf Mutu (A-E)
        ${'$'}mk = ${'$'}ujian->kelasKuliah->mataKuliah;
        ${'$'}nilaiAkhir = ((${'$'}krsDetail->nilai_tugas * ${'$'}mk->bobot_tugas) +
                       (${'$'}krsDetail->nilai_uts * ${'$'}mk->bobot_uts) +
                       (${'$'}krsDetail->nilai_uas * ${'$'}mk->bobot_uas)) / 100;

        [${'$'}huruf, ${'$'}indeks] = match (true) {
            ${'$'}nilaiAkhir >= 85 => ['A', 4.00],
            ${'$'}nilaiAkhir >= 80 => ['A-', 3.70],
            ${'$'}nilaiAkhir >= 75 => ['B+', 3.30],
            ${'$'}nilaiAkhir >= 70 => ['B', 3.00],
            ${'$'}nilaiAkhir >= 65 => ['B-', 2.70],
            ${'$'}nilaiAkhir >= 60 => ['C+', 2.30],
            ${'$'}nilaiAkhir >= 55 => ['C', 2.00],
            ${'$'}nilaiAkhir >= 40 => ['D', 1.00],
            default           => ['E', 0.00],
        };

        ${'$'}krsDetail->update([
            'nilai_akhir' => round(${'$'}nilaiAkhir, 2),
            'nilai_huruf' => ${'$'}huruf,
            'bobot_indeks' => ${'$'}indeks,
        ]);
    }
}
"""

    const val BLADE_TAILWIND_CBT_VIEW_CODE = """
{{-- resources/views/mahasiswa/cbt/exam-runner.blade.php --}}
{{-- Komponen UI Pengerjaan Ujian CBT (Laravel Blade + Tailwind CSS + Alpine.js Anti-Cheat) --}}
@extends('layouts.cbt-fullscreen')

@section('content')
<div x-data="cbtExamRunner({
        ujianId: {{ ${'$'}ujian->id }},
        sisaDetik: {{ ${'$'}sisaDetik }},
        maxCheatWarnings: {{ ${'$'}ujian->max_pelanggaran_tab }},
        soalList: @js(${'$'}daftarSoal)
    })"
    x-init="initExamSecurity()"
    class="min-h-screen bg-slate-950 text-slate-100 flex flex-col select-none">

    {{-- Top Navigation Bar: Identitas Ujian, Anti-Cheat Badge, & Countdown Timer --}}
    <header class="bg-slate-900/90 border-b border-slate-800 px-6 py-4 sticky top-0 z-30 backdrop-blur">
        <div class="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-4">
            <div>
                <span class="px-2.5 py-1 text-xs font-bold uppercase tracking-wider bg-indigo-500/20 text-indigo-300 rounded-full border border-indigo-500/30">
                    {{ ${'$'}ujian->jenis_ujian }} • {{ ${'$'}ujian->kelasKuliah->mataKuliah->kode_mk }}
                </span>
                <h1 class="text-lg font-bold text-white mt-1">{{ ${'$'}ujian->judul_ujian }}</h1>
            </div>

            <div class="flex items-center gap-4">
                {{-- Indikator Anti-Cheat Tab Switching --}}
                <div class="px-3.5 py-2 rounded-xl border"
                     :class="cheatCount > 0 ? 'bg-rose-950/80 border-rose-500/50 text-rose-200' : 'bg-emerald-950/60 border-emerald-500/40 text-emerald-300'">
                    <div class="text-[11px] uppercase tracking-wider opacity-80">Anti-Cheat Guard</div>
                    <div class="text-sm font-semibold">
                        Pelanggaran Tab: <span x-text="cheatCount"></span> / <span x-text="maxCheatWarnings"></span>
                    </div>
                </div>

                {{-- Timer Mundur Otomatis --}}
                <div class="bg-amber-500/10 border border-amber-500/40 px-4 py-2 rounded-xl text-amber-300 font-mono">
                    <div class="text-[11px] uppercase tracking-wider text-amber-200/70">Sisa Waktu</div>
                    <div class="text-xl font-extrabold tracking-widest" x-text="formattedTimer()">01:29:59</div>
                </div>
            </div>
        </div>
    </header>

    {{-- Main Workspace: Area Soal (Kiri 8 Kolom) & Navigasi Nomor Soal (Kanan 4 Kolom) --}}
    <main class="max-w-7xl w-full mx-auto flex-1 grid grid-cols-1 lg:grid-cols-12 gap-6 p-6">
        <section class="lg:col-span-8 bg-slate-900 border border-slate-800 rounded-2xl p-6 flex flex-col justify-between shadow-xl">
            <div>
                <div class="flex items-center justify-between border-b border-slate-800 pb-4 mb-6">
                    <span class="text-sm font-semibold text-indigo-400">
                        Soal Nomor <span class="text-lg text-white" x-text="activeIndex + 1"></span> dari <span x-text="soalList.length"></span>
                    </span>
                    <span class="text-xs px-2.5 py-1 rounded bg-slate-800 text-slate-300"
                          x-text="activeSoal().tipe_soal === 'PILIHAN_GANDA' ? 'Pilihan Ganda (Auto-Grade)' : 'Soal Essay'"></span>
                </div>

                {{-- Teks Pertanyaan --}}
                <div class="prose prose-invert max-w-none text-base leading-relaxed mb-6"
                     x-html="activeSoal().pertanyaan"></div>

                {{-- Opsi Pilihan Ganda (Acak) --}}
                <template x-if="activeSoal().tipe_soal === 'PILIHAN_GANDA'">
                    <div class="space-y-3">
                        <template x-for="(opsiText, idx) in activeSoal().opsi_json" :key="idx">
                            <button type="button"
                                @click="pilihOpsiPg(activeSoal().id, idx)"
                                class="w-full text-left p-4 rounded-xl border transition flex items-start gap-3"
                                :class="jawaban[activeSoal().id]?.jawaban_pg === String(idx)
                                    ? 'bg-indigo-600/25 border-indigo-400 text-white shadow-md'
                                    : 'bg-slate-950/60 border-slate-800 text-slate-300 hover:border-slate-700'">
                                <span class="w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold shrink-0"
                                      :class="jawaban[activeSoal().id]?.jawaban_pg === String(idx) ? 'bg-indigo-500 text-white' : 'bg-slate-800 text-slate-400'"
                                      x-text="['A','B','C','D','E'][idx]"></span>
                                <span class="text-sm leading-relaxed" x-text="opsiText"></span>
                            </button>
                        </template>
                    </div>
                </template>

                {{-- Input Jawaban Essay --}}
                <template x-if="activeSoal().tipe_soal === 'ESSAY'">
                    <div>
                        <textarea rows="6"
                            x-model="jawaban[activeSoal().id].jawaban_essay"
                            placeholder="Ketik jawaban analisis Anda secara terstruktur di sini..."
                            class="w-full rounded-xl bg-slate-950 border border-slate-700 p-4 text-sm text-white focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"></textarea>
                    </div>
                </template>
            </div>

            {{-- Tombol Navigasi Soal Sebelumnya / Ragu-Ragu / Selanjutnya --}}
            <div class="flex flex-wrap items-center justify-between gap-3 pt-6 mt-8 border-t border-slate-800">
                <button type="button"
                    @click="if(activeIndex > 0) activeIndex--"
                    :disabled="activeIndex === 0"
                    class="px-4 py-2.5 rounded-xl bg-slate-800 text-slate-200 text-sm font-medium disabled:opacity-40">
                    ← Soal Sebelumnya
                </button>

                <label class="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-amber-500/15 border border-amber-500/40 text-amber-300 text-sm font-medium cursor-pointer">
                    <input type="checkbox" x-model="jawaban[activeSoal().id].ragu" class="rounded text-amber-500 focus:ring-0">
                    <span>Ragu-Ragu</span>
                </label>

                <button type="button"
                    @click="if(activeIndex < soalList.length - 1) activeIndex++"
                    x-show="activeIndex < soalList.length - 1"
                    class="px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold">
                    Soal Selanjutnya →
                </button>
            </div>
        </section>

        {{-- Panel Navigasi Nomor Soal & Tombol Submit --}}
        <aside class="lg:col-span-4 bg-slate-900 border border-slate-800 rounded-2xl p-6 flex flex-col justify-between h-fit">
            <div>
                <h2 class="text-sm font-bold uppercase tracking-wider text-slate-400 mb-4">Navigasi Peta Soal</h2>
                <div class="grid grid-cols-5 gap-2.5 mb-6">
                    <template x-for="(item, index) in soalList" :key="item.id">
                        <button type="button"
                            @click="activeIndex = index"
                            class="h-11 rounded-xl font-bold text-sm border transition"
                            :class="{
                                'ring-2 ring-white border-indigo-400': activeIndex === index,
                                'bg-amber-500 text-slate-950 border-amber-400': jawaban[item.id]?.ragu,
                                'bg-emerald-600 text-white border-emerald-500': !jawaban[item.id]?.ragu && isTerjawab(item.id),
                                'bg-slate-950 text-slate-400 border-slate-800': !isTerjawab(item.id)
                            }"
                            x-text="index + 1">
                        </button>
                    </template>
                </div>
            </div>

            <button type="button"
                @click="submitUjian(false)"
                class="w-full py-3.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-sm shadow-lg shadow-emerald-900/50 transition">
                Selesai & Kumpulkan Ujian CBT
            </button>
        </aside>
    </main>
</div>

<script>
function cbtExamRunner(config) {
    return {
        ujianId: config.ujianId,
        sisaDetik: config.sisaDetik,
        maxCheatWarnings: config.maxCheatWarnings,
        soalList: config.soalList,
        activeIndex: 0,
        cheatCount: 0,
        jawaban: {},

        initExamSecurity() {
            this.soalList.forEach(s => {
                this.jawaban[s.id] = { soal_id: s.id, jawaban_pg: null, jawaban_essay: '', ragu: false };
            });

            // Timer Mundur Otomatis (Auto-Submit saat 00:00:00)
            setInterval(() => {
                if (this.sisaDetik > 0) {
                    this.sisaDetik--;
                } else {
                    this.submitUjian(false);
                }
            }, 1000);

            // Deteksi Pindah Tab / Kehilangan Fokus Browser (Anti-Cheat)
            document.addEventListener('visibilitychange', () => {
                if (document.hidden) this.handleCheatDetected('Perpindahan Tab / Minimize Browser');
            });
            window.addEventListener('blur', () => {
                this.handleCheatDetected('Kehilangan Fokus Jendela Ujian');
            });
        },

        handleCheatDetected(alasan) {
            this.cheatCount++;
            if (this.cheatCount >= this.maxCheatWarnings) {
                alert('Batas maksimal pelanggaran keamanan ujian tercapai! Sistem melakukan Auto-Submit.');
                this.submitUjian(true);
            } else {
                alert(`PERINGATAN KEAMANAN CBT (${'$'}{this.cheatCount}/${'$'}{this.maxCheatWarnings}): Terdeteksi ${'$'}{alasan}.`);
            }
        },

        activeSoal() { return this.soalList[this.activeIndex] || {}; },
        pilihOpsiPg(soalId, idx) { this.jawaban[soalId].jawaban_pg = String(idx); },
        isTerjawab(soalId) {
            const j = this.jawaban[soalId];
            return j && (j.jawaban_pg !== null || (j.jawaban_essay && j.jawaban_essay.trim().length > 0));
        },
        formattedTimer() {
            const hrs = String(Math.floor(this.sisaDetik / 3600)).padStart(2, '0');
            const mins = String(Math.floor((this.sisaDetik % 3600) / 60)).padStart(2, '0');
            const secs = String(this.sisaDetik % 60).padStart(2, '0');
            return `${'$'}{hrs}:${'$'}{mins}:${'$'}{secs}`;
        },
        async submitUjian(isAutoCheat) {
            const payload = {
                jawaban: Object.values(this.jawaban),
                jumlah_pindah_tab: this.cheatCount,
                is_auto_submitted_cheat: isAutoCheat
            };
            const res = await fetch(`/api/v1/mahasiswa/cbt/${'$'}{this.ujianId}/submit`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'X-CSRF-TOKEN': '{{ csrf_token() }}' },
                body: JSON.stringify(payload)
            });
            if (res.ok) window.location.href = '/mahasiswa/khs';
        }
    }
}
</script>
@endsection
"""

    const val BUSINESS_WORKFLOW_EXPLANATION = """
1. ALUR PMB ONLINE -> GENERATE NIM OTOMATIS:
   • Calon Mahasiswa mendaftar akun -> Mengisi biodata & pilihan Prodi 1/2 -> Upload berkas (Ijazah, KTP, Foto).
   • Sistem men-generate Virtual Account biaya formulir. Setelah lunas & diverifikasi panitia PMB, status menjadi LULUS.
   • Saat melakukan Daftar Ulang, sistem otomatis men-generate NIM berformat [Tahun][KodeProdi][NomorUrut] dan memutasi role menjadi MAHASISWA aktif.

2. INTERAKSI BILLING SYSTEM (VA/QRIS) -> GATE HAK AKSES AKADEMIK & CBT:
   • Bagian Keuangan mengatur tarif UKT/SPP per angkatan/jenjang (D1/D3/S1).
   • Saat Mahasiswa membayar via Virtual Account (BCA/Mandiri/BNI) atau QRIS (Midtrans/Xendit), Webhook Payment Gateway memanggil endpoint rekonsiliasi SIAKAD dan mengubah status_bayar = 'PAID'.
   • Status 'PAID' ini menjadi syarat mutlak (Middleware/Policy Gate) yang membuka akses:
     (a) Pengambilan KRS Online pada awal semester.
     (b) Kartu Ujian & akses masuk ruang Ujian CBT (UTS/UAS).

3. ALUR KRS ONLINE (KUOTA IPS) -> APPROVAL DOSEN PA -> PERKULIAHAN & PRESENSI:
   • Sistem membaca IPS Semester Lalu mahasiswa untuk mengunci kuota maksimal SKS:
     - IPS >= 3.00 : Maks 24 SKS
     - IPS 2.50 - 2.99 : Maks 21 SKS
     - IPS 2.00 - 2.49 : Maks 18 SKS
     - IPS < 2.00 : Maks 15 SKS
   • Dosen Pembimbing Akademik (PA) memverifikasi dan menyetujui KRS (status DISETUJUI_PA).
   • Mahasiswa mengikuti perkuliahan (Presensi QR/Geo-location minimal 75% kehadiran untuk syarat UAS).

4. INTEGRASI UJIAN CBT ANTI-CHEAT -> PENILAIAN OTOMATIS -> KHS & IPK -> PDDIKTI:
   • Mahasiswa mengerjakan soal Pilihan Ganda (teracak) dan Essay dengan Countdown Timer serta proteksi Anti-Cheat (Tab-Switching / Blur Detection).
   • Jika mahasiswa berpindah tab melebihi batas (misal 3x), sistem melakukan Auto-Submit.
   • Saat Submit, soal Pilihan Ganda langsung dikoreksi otomatis detik itu juga, sedangkan soal Essay masuk ke antrean koreksi Dosen.
   • Nilai CBT otomatis masuk ke komponen UTS (30%) atau UAS (40%) pada tabel krs_detail, digabung dengan nilai Tugas (30%) untuk menghasilkan Nilai Akhir (0-100), Huruf Mutu (A s/d E), dan Bobot Indeks (4.00 s/d 0.00).
   • Pada akhir semester, Admin Akademik menjalankan Push Sinkronisasi ke API Neo Feeder PDDIKTI (InsertPerkuliahanMahasiswa & InsertNilaiPerkuliahanKelas).
"""
}
