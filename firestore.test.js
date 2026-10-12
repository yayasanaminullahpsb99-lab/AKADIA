const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write academic profile or KRS", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("krs_enrollments").get()
  );
});

test("Authenticated user: can create and read own AcademicProfile and KrsEnrollment", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Rizky Pratama",
      email: "rizky@univ-cloud.ac.id",
      role: "MAHASISWA",
      nimOrNidn: "2026101001",
      prodi: "S1 Teknik Informatika",
      semester: 3,
      ipSemesterLalu: 3.65,
      ipk: 3.58,
      isUktPaid: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("krs_enrollments").doc("krs_1").set({
      userId: ALICE_UID,
      kodeMk: "IF301",
      namaMk: "Arsitektur Cloud & Microservices",
      sks: 3,
      semester: 3,
      dosenPengampu: "Dr. Budi Santoso, M.Kom.",
      jadwal: "Senin 08:00 - 10:30 (Lab Cloud A)",
      statusApproval: "DIAJUKAN",
      presensiCount: 1,
      nilaiTugas: 0,
      nilaiUts: 0,
      nilaiUas: 0,
      nilaiAkhir: 0,
      gradeHuruf: "-",
      bobotIndeks: 0,
      syncedPddikti: false,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("krs_enrollments")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});

test("Authenticated user: cannot read or write another user's KRS or CBT attempt", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("cbt_attempts").doc("cbt_1").set({
      userId: ALICE_UID,
      examCode: "UTS-IF301",
      kodeMk: "IF301",
      namaUjian: "UTS Arsitektur Cloud",
      jenisUjian: "UTS",
      mcqScore: 85.0,
      essayAnswer: "Microservices memisahkan service berdasarkan bounded context.",
      essayScore: 90.0,
      finalCbtScore: 86.5,
      cheatWarnings: 0,
      autoSubmittedByAntiCheat: false,
      status: "GRADED",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("cbt_attempts").doc("cbt_1").get()
  );
});

test("Shadow update test: rejects ghost fields in update", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Rizky Pratama",
      email: "rizky@univ-cloud.ac.id",
      role: "MAHASISWA",
      nimOrNidn: "2026101001",
      prodi: "S1 Teknik Informatika",
      semester: 3,
      ipSemesterLalu: 3.65,
      ipk: 3.58,
      isUktPaid: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Rizky Updated",
      ghostAdminFlag: true,
      updatedAt: now,
    })
  );
});
