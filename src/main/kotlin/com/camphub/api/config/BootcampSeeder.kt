package com.camphub.api.config

import com.camphub.api.model.Bootcamp
import com.camphub.api.model.Program
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.BootcampRepository
import com.camphub.api.repository.ProgramRepository
import com.camphub.api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class BootcampSeeder(
    private val userRepository: UserRepository,
    private val bootcampRepository: BootcampRepository,
    private val programRepository: ProgramRepository
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(BootcampSeeder::class.java)

    override fun run(vararg args: String) {
        // Seed only when the table is empty, so restarts do not duplicate data
        if (bootcampRepository.count() != 0L) return

        // [0] Kode Nusantara, [1] Rintis Edukasi (order set in UserSeeder)
        val providers = userRepository.findAll().sortedBy { it.id }.filter { it.role == Role.PROVIDER }
        if (providers.size < 2) {
            log.warn("BootcampSeeder: sample providers not found, skipped")
            return
        }

        val kodeNusantara = newBootcamp(
            "Kode Nusantara Academy",
            "Bootcamp pengembangan web intensif untuk pemula dan pekerja yang ingin beralih karier. " +
                    "Kelas daring dan luring bersama mentor praktisi, dengan proyek nyata dan pendampingan karier.",
            "Jakarta Selatan",
            "https://kodenusantara.example.com",
            providers[0]
        )
        val pijarKode = newBootcamp(
            "Pijar Kode",
            "Bootcamp pengembangan aplikasi Android untuk pemula, dengan proyek aplikasi dari awal sampai rilis.",
            "Bandung",
            null,
            providers[0]
        )
        val rintis = newBootcamp(
            "Rintis Data School",
            "Bootcamp analisis data dan machine learning dengan studi kasus dari industri.",
            "Jakarta Barat",
            "https://rintis.example.com",
            providers[1]
        )
        val lentera = newBootcamp(
            "Lentera UI/UX",
            "Bootcamp desain antarmuka dan pengalaman pengguna, dari riset pengguna sampai prototipe.",
            "Yogyakarta",
            null,
            providers[1]
        )
        val bootcamps = listOf(kodeNusantara, pijarKode, rintis, lentera)
        bootcampRepository.saveAll(bootcamps)

        // At most 3 programs per bootcamp
        val programs = listOf(
            newProgram(
                kodeNusantara, "Full-Stack Web Developer", "Web Development", 15_000_000, 12, true,
                "Minggu 1–2: Dasar HTML, CSS, dan JavaScript modern\n" +
                        "Minggu 3–4: Git, GitHub, dan kolaborasi tim\n" +
                        "Minggu 5–7: React dan manajemen state\n" +
                        "Minggu 8–9: Node.js, Express, dan REST API\n" +
                        "Minggu 10: Basis data PostgreSQL\n" +
                        "Minggu 11–12: Proyek akhir dan persiapan karier"
            ),
            newProgram(
                kodeNusantara, "Back-End Engineer dengan Go", "Web Development", 12_500_000, 10, false,
                "Minggu 1–3: Dasar bahasa Go\nMinggu 4–6: REST API dan basis data\nMinggu 7–10: Proyek akhir"
            ),
            newProgram(
                kodeNusantara, "Data Analyst", "Data", 9_000_000, 8, true,
                "Minggu 1–2: Spreadsheet dan SQL\nMinggu 3–5: Python untuk analisis data\nMinggu 6–8: Visualisasi dan proyek akhir"
            ),
            newProgram(
                pijarKode, "Android Developer", "Mobile Development", 11_000_000, 12, true,
                "Minggu 1–3: Dasar Kotlin\nMinggu 4–8: Jetpack Compose\nMinggu 9–12: Koneksi API dan proyek akhir"
            ),
            newProgram(
                rintis, "Data Science Dasar", "Data", 10_000_000, 10, true,
                "Minggu 1–3: Statistik dan Python\nMinggu 4–7: Pengolahan data\nMinggu 8–10: Proyek akhir"
            ),
            newProgram(
                rintis, "Machine Learning Engineer", "Data", 14_000_000, 14, true,
                "Minggu 1–4: Dasar machine learning\nMinggu 5–10: Model dan evaluasi\nMinggu 11–14: Proyek akhir"
            ),
            newProgram(
                lentera, "UI/UX Design", "Desain", 8_500_000, 8, true,
                "Minggu 1–2: Riset pengguna\nMinggu 3–5: Wireframe dan desain antarmuka\nMinggu 6–8: Prototipe dan uji pengguna"
            )
        )
        programRepository.saveAll(programs)
        log.info("BootcampSeeder: {} bootcamps and {} programs created", bootcamps.size, programs.size)
    }

    private fun newBootcamp(
        name: String,
        description: String,
        location: String,
        website: String?,
        owner: User
    ): Bootcamp {
        val b = Bootcamp()
        b.name = name
        b.description = description
        b.location = location
        b.website = website
        b.owner = owner
        return b
    }

    private fun newProgram(
        bootcamp: Bootcamp, name: String, category: String, price: Long,
        durationWeeks: Int, registrationOpen: Boolean, syllabus: String
    ): Program {
        val p = Program()
        p.bootcamp = bootcamp
        p.name = name
        p.category = category
        p.price = price
        p.durationWeeks = durationWeeks
        p.isRegistrationOpen = registrationOpen
        p.syllabus = syllabus
        return p
    }
}
