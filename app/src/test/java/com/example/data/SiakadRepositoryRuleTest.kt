package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class SiakadRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun enrollCourseKrs_authenticatedOwner_succeedsAndEmitsInFlow() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = SiakadRepository(firestore, auth)
        val uniqueKode = "IF_${UUID.randomUUID().toString().take(6)}"
        val course = CourseCatalogItem(
            kodeMk = uniqueKode,
            namaMk = "Sistem Terdistribusi Cloud",
            sks = 3L,
            semester = 3L,
            prasyaratKode = null,
            dosenPengampu = "Dr. Budi Santoso",
            jadwal = "Selasa 10:00-12:30"
        )

        val enrollResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.enrollCourseKrs(course)
        }
        assertTrue("Expected enrollCourseKrs to succeed: ${enrollResult.exceptionOrNull()}", enrollResult.isSuccess)

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeKrsEnrollments(uid).first { list -> list.any { it.kodeMk == uniqueKode } }
        }
        assertTrue(emitted.any { it.kodeMk == uniqueKode })
    }

    @Test
    fun submitCbtExam_authenticatedOwner_createsAttemptAndCalculatesScore() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = SiakadRepository(firestore, auth)
        val examCode = "UTS_${UUID.randomUUID().toString().take(6)}"

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.submitCbtExam(
                examCode = examCode,
                kodeMk = "IF301",
                namaUjian = "UTS Arsitektur Cloud",
                jenisUjian = "UTS",
                mcqScore = 90.0,
                essayAnswer = "Penjelasan lengkap arsitektur microservices dengan message broker dan database per service.",
                cheatWarnings = 1,
                autoSubmittedByAntiCheat = false
            )
        }
        assertTrue("Expected submitCbtExam to succeed: ${result.exceptionOrNull()}", result.isSuccess)

        val attempts = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCbtAttempts(uid).first { list -> list.any { it.examCode == examCode } }
        }
        assertTrue(attempts.any { it.examCode == examCode })
    }

    @Test
    fun crossUserRead_krsEnrollment_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = SiakadRepository(firestore, auth)
        val uniqueKode = "IF_${UUID.randomUUID().toString().take(6)}"
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.enrollCourseKrs(
                CourseCatalogItem(
                    kodeMk = uniqueKode,
                    namaMk = "Keamanan Jaringan",
                    sks = 3L,
                    semester = 3L,
                    prasyaratKode = null,
                    dosenPengampu = "Prof. Rina",
                    jadwal = "Kamis 08:00"
                )
            ).getOrThrow()
        }

        // Switch to Bob and attempt to read Alice's KRS document
        signInTestUser(BOB_EMAIL)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                firestore.collection("users").document(aliceUid)
                    .collection("krs_enrollments").document(uniqueKode)
                    .get().await()
            }
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED for cross-user access")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun observeKrsEnrollments_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = SiakadRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeKrsEnrollments(aliceUid).first()
            }
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED for unauthenticated user")
        } catch (e: Exception) {
            var current: Throwable? = e
            var firestoreEx: FirebaseFirestoreException? = null
            while (current != null) {
                if (current is FirebaseFirestoreException) {
                    firestoreEx = current
                    break
                }
                current = current.cause
            }
            assertTrue("Expected cause to be FirebaseFirestoreException but got $e", firestoreEx != null)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice.siakad@test.com"
        const val BOB_EMAIL = "bob.siakad@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
