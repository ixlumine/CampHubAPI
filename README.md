# CampHubAPI

Backend REST API untuk aplikasi CampHub (AFL3 Visual Programming, Universitas Ciputra).
Spring Boot 3.5 + Kotlin, MySQL, autentikasi JWT.

## Prasyarat

- JDK 17
- Salah satu database: MySQL (disarankan 9.7), XAMPP (MariaDB), atau Docker Desktop
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

`./gradlew bootRun` tidak membaca `.env` secara otomatis. Di WSL/Linux/Mac, muat dulu isinya:

```bash
set -a && source .env && set +a && ./gradlew bootRun
```

Di Windows PowerShell, isi variabel satu per satu, contoh:
`$env:DB_PASSWORD="passwordku"; .\gradlew.bat bootRun`

## Cara menjalankan

### Cara A: MySQL native atau XAMPP

1. Jalankan SQL berikut sekali (MySQL Workbench, phpMyAdmin, atau terminal):
```sql
   CREATE DATABASE camphub_db;
   CREATE USER 'camphub_user'@'localhost' IDENTIFIED BY 'camphub_dev_password';
   GRANT ALL PRIVILEGES ON camphub_db.* TO 'camphub_user'@'localhost';
```
2. Jalankan backend:
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

Jika port 3306 sudah terpakai (misalnya XAMPP menyala), isi `DB_PORT=3307` di `.env`, lalu jalankan cara C dengan `.env` yang sudah dimuat.

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
