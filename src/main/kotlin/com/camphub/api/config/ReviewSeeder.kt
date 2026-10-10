package com.camphub.api.config

import com.camphub.api.model.CareerStatus
import com.camphub.api.model.Review
import com.camphub.api.model.Role
import com.camphub.api.repository.BootcampRepository
import com.camphub.api.repository.ReviewRepository
import com.camphub.api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Order(3)
class ReviewSeeder(
    private val reviewRepository: ReviewRepository,
    private val bootcampRepository: BootcampRepository,
    private val userRepository: UserRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(ReviewSeeder::class.java)

    override fun run(vararg args: String?) {
        if (reviewRepository.count() > 0L) return

        val users = userRepository.findAll().sortedBy { it.id }
        val members = users.filter { it.role == Role.USER }
        val bootcamps = bootcampRepository.findAll().sortedBy { it.id }

        if (members.size < 3 || bootcamps.size < 2) {
            log.warn("ReviewSeeder: sample users (min 3) or bootcamps (min 2) not found, skipped")
            return
        }

        val rina = members[0]
        val bima = members[1]
        val sekar = members[2]

        val kodeNusantara = bootcamps[0]
        val pijarKode = bootcamps[1]

        val r1 = Review().apply {
            bootcamp = kodeNusantara
            author = rina
            rating = 5
            content = "Kurikulumnya sangat komprehensif dan mentor selalu siap membantu saat kesulitan belajar."
            careerStatus = CareerStatus.EMPLOYED
            createdAt = LocalDateTime.now().minusDays(5)
        }

        val r2 = Review().apply {
            bootcamp = kodeNusantara
            author = bima
            rating = 4
            content = "Proyek akhirnya sangat mendekati kebutuhan industri. Bimbingan kariernya juga sangat membantu."
            careerStatus = CareerStatus.EMPLOYED
            createdAt = LocalDateTime.now().minusDays(4)
        }

        val r3 = Review().apply {
            bootcamp = kodeNusantara
            author = sekar
            rating = 5
            content = "Materi tersusun rapi dari dasar hingga mahir. Sangat cocok untuk pemula."
            careerStatus = CareerStatus.SEEKING_JOB
            createdAt = LocalDateTime.now().minusDays(3)
        }

        val r4 = Review().apply {
            bootcamp = pijarKode
            author = rina
            rating = 4
            content = "Fokus ke Jetpack Compose sangat bagus dan modern, tugas-tugasnya cukup menantang."
            careerStatus = CareerStatus.EMPLOYED
            createdAt = LocalDateTime.now().minusDays(3)
        }

        val r5 = Review().apply {
            bootcamp = pijarKode
            author = bima
            rating = 4
            content = "Mentor berpengalaman dan komunikatif. Waktu belajar cukup padat tapi sepadan."
            careerStatus = CareerStatus.SEEKING_JOB
            createdAt = LocalDateTime.now().minusDays(2)
        }

        val r6 = Review().apply {
            bootcamp = pijarKode
            author = sekar
            rating = 4
            content = "Banyak studi kasus pembuatan aplikasi nyata yang bisa dijadikan portofolio."
            careerStatus = CareerStatus.SEEKING_JOB
            createdAt = LocalDateTime.now().minusDays(1)
        }

        val reviews = listOf(r1, r2, r3, r4, r5, r6)
        reviewRepository.saveAll(reviews)

        log.info("ReviewSeeder: {} sample reviews created", reviews.size)
    }
}
