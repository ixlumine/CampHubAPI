package com.camphub.api.config

import com.camphub.api.model.ForumComment
import com.camphub.api.model.ForumThread
import com.camphub.api.model.Role
import com.camphub.api.repository.ForumCommentRepository
import com.camphub.api.repository.ForumThreadRepository
import com.camphub.api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Order(4)
class ForumSeeder(
    private val forumThreadRepository: ForumThreadRepository,
    private val forumCommentRepository: ForumCommentRepository,
    private val userRepository: UserRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(ForumSeeder::class.java)

    override fun run(vararg args: String?) {
        if (forumThreadRepository.count() > 0L) return

        val users = userRepository.findAll().sortedBy { it.id }
        val members = users.filter { it.role == Role.USER }
        val providers = users.filter { it.role == Role.PROVIDER }

        if (members.isEmpty() || providers.isEmpty()) {
            log.warn("ForumSeeder: sample users or providers not found, skipped")
            return
        }

        val rina = members[0]
        val bima = members.getOrNull(1) ?: rina
        val sekar = members.getOrNull(2) ?: rina
        val kodeNusantara = providers[0]

        val thread1 = ForumThread().apply {
            title = "Apakah ada kelas malam untuk karyawan?"
            content = "Saya bekerja sampai pukul 17.00 di hari kerja. Apakah program Full-Stack Web Developer punya jadwal kelas malam atau akhir pekan?"
            author = rina
            createdAt = LocalDateTime.now().minusDays(2)
        }

        val thread2 = ForumThread().apply {
            title = "Laptop RAM 8 GB cukup untuk program Data Analyst?"
            content = "Apakah spesifikasi laptop RAM 8GB i5 generasi 10 sudah cukup untuk mengikuti kelas?"
            author = sekar
            createdAt = LocalDateTime.now().minusDays(1)
        }

        val thread3 = ForumThread().apply {
            title = "Berapa lama proses seleksi setelah mendaftar?"
            content = "Mohon info estimasi waktu pengumuman seleksi setelah kirim berkas."
            author = bima
            createdAt = LocalDateTime.now()
        }

        forumThreadRepository.saveAll(listOf(thread1, thread2, thread3))

        val comment1 = ForumComment().apply {
            thread = thread1
            author = kodeNusantara
            content = "Ada. Kelas malam berjalan Senin–Kamis pukul 19.00–21.30 WIB secara daring."
            createdAt = LocalDateTime.now().minusDays(2).plusHours(2)
        }

        val comment2 = ForumComment().apply {
            thread = thread1
            author = rina
            content = "Saya ikut kelas malam angkatan lalu. Jadwalnya cocok untuk yang bekerja."
            createdAt = LocalDateTime.now().minusDays(1)
        }

        val comment3 = ForumComment().apply {
            thread = thread1
            author = bima
            content = "Apakah kelas akhir pekan juga ada?"
            createdAt = LocalDateTime.now()
        }

        forumCommentRepository.saveAll(listOf(comment1, comment2, comment3))

        log.info("ForumSeeder: sample threads and comments seeded")
    }
}
