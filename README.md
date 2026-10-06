# CampHubAPI

Backend REST API untuk aplikasi CampHub (AFL3 Visual Programming, Universitas Ciputra).
Spring Boot 3.5 + Kotlin, MySQL, autentikasi JWT.

## Prasyarat

- JDK 17
- Salah satu database: MySQL (disarankan 9.7), XAMPP, Laragon, atau Docker Desktop
- Gradle tidak perlu di-install (sudah ada Gradle wrapper)

## Konfigurasi

Semua konfigurasi punya nilai default di `application.properties`, jadi project bisa langsung jalan.
Untuk mengganti nilai, salin `.env.example` menjadi `.env`, lalu ubah yang perlu.
File `.env` tidak boleh di-commit.

| Variabel | Default | Keterangan |
|---|---|---|
| `DB_PORT` | `3306` | Port MySQL |
| `DB_USERNAME` | `camphub_user` | User database |
| `DB_PASSWORD` | `camphub_dev_password` | Password database |
| `JWT_SECRET` | (string contoh) | Kunci tanda tangan token, minimal 32 karakter |
| `ADMIN_PASSWORD` | `admin_dev_password` | Password akun admin contoh |

Backend membaca `.env` dari folder project secara otomatis, tanpa `export`.
Format `.env`: satu pengaturan per baris, tanpa tanda kutip dan tanpa `export`; komentar di baris sendiri.
Isi `JWT_SECRET` dengan nilai acak sendiri, misalnya hasil `openssl rand -hex 32`.

## Cara menjalankan

### Cara A: MySQL native, XAMPP, atau Laragon

1. Salin `.env.example` menjadi `.env`, lalu isi port dan login MySQL. Contoh XAMPP/Laragon dengan root tanpa password:
```
   DB_PORT=3306
   DB_USERNAME=root
   DB_PASSWORD=
```
2. Jalankan backend. Database `camphub_db` dibuat otomatis.
```bash
   ./gradlew bootRun        # Windows: gradlew.bat bootRun
```

### Cara B: Docker (database + backend)

```bash
docker compose up --build
```

### Cara C: Docker hanya database, backend dari terminal

```bash
docker compose up -d db
docker compose ps          # tunggu status db menjadi healthy
./gradlew bootRun
```

Jika port 3306 sudah terpakai (misalnya XAMPP menyala), isi `DB_PORT=3307` di `.env`.

Tabel dibuat otomatis saat aplikasi start, dan data contoh diisi oleh seeder hanya jika tabel masih kosong.

## Akun contoh

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@example.com | nilai `ADMIN_PASSWORD` (default `admin_dev_password`) |
| PROVIDER | kodenusantara@example.com | password123 |
| PROVIDER | rintis@example.com | password123 |
| USER | rina@example.com | password123 |
| USER | bima@example.com | password123 |
| USER | sekar@example.com | password123 |

Akun ini hanya untuk development.

## Mengakses backend

| Dari | Alamat |
|---|---|
| Postman | `http://127.0.0.1:8080` |
| Emulator Android | `http://10.0.2.2:8080` |
| HP via USB | Jalankan `adb reverse tcp:8080 tcp:8080`, lalu `http://127.0.0.1:8080` |

Contoh login:

```bash
curl -X POST http://127.0.0.1:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"rina@example.com","password":"password123"}'
```

Endpoint selain `/api/auth/**` membutuhkan header `Authorization: Bearer <token>`.
