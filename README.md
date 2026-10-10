# CampHubAPI

Backend REST API aplikasi CampHub.

- **Teknologi:** Spring Boot 3.5.16, Kotlin 1.9.25, Java 17, MySQL, JWT
- **Aplikasi Android:** repository [CampHub](https://github.com/ixlumine/CampHub)

## Struktur

```
src/main/kotlin/com/camphub/api/
├── controller/   Menerima request dan mengembalikan response
├── service/      Logika bisnis
├── repository/   Akses data ke database (Spring Data JPA)
├── model/        Entity (pemetaan tabel database) dan enum
├── dto/          Data Transfer Object untuk request dan response
├── security/     JWT dan aturan akses endpoint (Spring Security)
├── exception/    Penanganan error terpusat
└── config/       Seed data
```

## Fitur

- **Autentikasi**: register dan login dengan token JWT.
- **Katalog**: bootcamp dan program.
  - Daftar bootcamp diurutkan berdasarkan nama (A–Z).
  - Bootcamp yang masih memiliki program atau ulasan tidak bisa dihapus.
- **Ulasan**: rating 1–5, isi ulasan, dan status karier.
  - Satu ulasan per user untuk setiap bootcamp.
- **Peringkat**: bootcamp dengan minimal 3 ulasan.
  - Urutan: rata-rata rating tertinggi (dibulatkan 1 desimal), lalu jumlah ulasan terbanyak.
- **Forum**: pertanyaan dan komentar.
  - Menghapus pertanyaan ikut menghapus komentarnya.

Hak akses setiap endpoint ada di bagian Endpoint.

## Prasyarat

Pilih salah satu:
- **Tanpa Docker:** JDK 17 dan MySQL (disarankan 9.7), XAMPP, atau Laragon.
- **Dengan Docker:** Docker Desktop. JDK 17 hanya diperlukan jika backend dijalankan dari terminal.

## Konfigurasi

Pengaturan dibaca dari `.env` di folder utama project. Untuk membuatnya, salin `.env.example` menjadi `.env`. File ini dibaca otomatis dan tidak di-commit.

| Menjalankan dengan | Isi `.env` | Keterangan |
|---|---|---|
| Docker | Tidak perlu dibuat. Jika port 3306 sudah terpakai, isi `DB_PORT=3307`. | Nilai default sesuai Docker |
| XAMPP, Laragon, atau MySQL native | `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD` sesuai MySQL yang dipakai | Database `camphub_db` dibuat otomatis |

Contoh `.env` untuk XAMPP atau Laragon (user `root` tanpa password):
```
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=
```

Daftar variabel:

| Variabel | Default | Keterangan |
|---|---|---|
| `DB_PORT` | `3306` | Port MySQL |
| `DB_USERNAME` | `camphub_user` | User database |
| `DB_PASSWORD` | `camphub_dev_password` | Password database |
| `MYSQL_ROOT_PASSWORD` | `root_dev_password` | Password root MySQL di Docker |
| `JWT_SECRET` | (string contoh) | Minimal 32 karakter, contoh hasil `openssl rand -hex 32` |
| `ADMIN_PASSWORD` | `admin_dev_password` | Password akun ADMIN |

Satu pengaturan per baris, tanpa tanda kutip dan tanpa `export`.

## Menjalankan

**XAMPP, Laragon, atau MySQL native**
1. Pastikan `.env` sudah diisi (lihat Konfigurasi).
2. Jalankan `./gradlew bootRun` (Windows: `gradlew.bat bootRun`).

**Docker (database dan backend)**
1. Jalankan `docker compose up --build`.

**Docker hanya database, backend dari terminal**
1. Jalankan `docker compose up -d db`, lalu tunggu status `healthy` di `docker compose ps`.
2. Jalankan `./gradlew bootRun`.

Backend siap setelah log menampilkan `Started CampHubApiApplicationKt`. Tabel dibuat otomatis, dan seed data diisi jika tabel masih kosong.

## Akun contoh

Dibuat oleh seed data saat database masih kosong. Hanya untuk development.

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@example.com | nilai `ADMIN_PASSWORD` (default `admin_dev_password`) |
| PROVIDER | kodenusantara@example.com,<br>rintis@example.com | password123 |
| USER | rina@example.com,<br>bima@example.com,<br>sekar@example.com | password123 |

## Endpoint

| Diakses dari | Base URL |
|---|---|
| Postman | `http://127.0.0.1:8080` |
| Emulator | `http://10.0.2.2:8080` |
| HP via USB | `http://127.0.0.1:8080` setelah `adb reverse tcp:8080 tcp:8080` |

Semua endpoint kecuali `/api/auth/**` membutuhkan header `Authorization: Bearer <token>`. Token berlaku 24 jam.

| Method | Path | Akses |
|---|---|---|
| POST | `/api/auth/register` | Publik |
| POST | `/api/auth/login` | Publik |
| GET | `/api/bootcamps` | Login |
| GET | `/api/bootcamps/{id}` | Login |
| POST | `/api/bootcamps` | PROVIDER |
| PUT | `/api/bootcamps/{id}` | Pemilik |
| DELETE | `/api/bootcamps/{id}` | Pemilik, ADMIN |
| GET | `/api/bootcamps/{id}/programs` | Login |
| GET | `/api/programs/{id}` | Login |
| POST | `/api/bootcamps/{id}/programs` | Pemilik bootcamp |
| PUT | `/api/programs/{id}` | Pemilik bootcamp |
| DELETE | `/api/programs/{id}` | Pemilik bootcamp, ADMIN |
| GET | `/api/bootcamps/{id}/reviews` | Login |
| POST | `/api/bootcamps/{id}/reviews` | USER |
| PUT | `/api/reviews/{id}` | Pemilik |
| DELETE | `/api/reviews/{id}` | Pemilik, ADMIN |
| GET | `/api/bootcamps/{id}/review-summary` | Login |
| GET | `/api/rankings` | Login |
| GET | `/api/threads` | Login |
| GET | `/api/threads/{id}` | Login |
| POST | `/api/threads` | USER, PROVIDER |
| PUT | `/api/threads/{id}` | Pemilik |
| DELETE | `/api/threads/{id}` | Pemilik, ADMIN |
| GET | `/api/threads/{id}/comments` | Login |
| POST | `/api/threads/{id}/comments` | USER, PROVIDER |
| DELETE | `/api/comments/{id}` | Pemilik, ADMIN |

Contoh login:
```bash
curl -X POST http://127.0.0.1:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"rina@example.com","password":"password123"}'
```
