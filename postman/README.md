# Postman

File `camphubapi.postman_collection.json` berisi 28 request untuk menguji semua endpoint CampHubAPI.

## Menjalankan

1. Jalankan backend (lihat [README](../README.md)).
2. Buka Postman, klik **Import**, lalu pilih file `camphubapi.postman_collection.json`.
3. Klik kanan collection **CampHubAPI**, pilih **Run collection**, lalu klik **Start run**.

Hasil yang diharapkan: **Passed 28, Failed 0**. Setiap request dicek kode statusnya, misalnya 200 untuk data yang berhasil diambil dan 201 untuk data yang berhasil dibuat.

## Isi collection

| Folder | Isi |
|---|---|
| Autentikasi | Register, login USER, PROVIDER, dan ADMIN |
| Katalog | Bootcamp dan program |
| Ulasan dan Peringkat | Ulasan, ringkasan ulasan, dan peringkat |
| Forum | Pertanyaan dan komentar |

Akun yang dipakai:

| Role | Akun |
|---|---|
| USER | rina@example.com, serta satu akun baru dari request Register |
| PROVIDER | kodenusantara@example.com |
| ADMIN | admin@example.com |

Token dan variabel yang diisi oleh setiap request tertulis di deskripsi request tersebut di Postman.

## Catatan

- Folder Autentikasi harus dijalankan lebih dulu, karena request di folder ini mengambil token login. Token dan ID data yang dibuat disimpan otomatis ke variabel collection, lalu dipakai oleh request berikutnya.
- Bootcamp, program, ulasan, pertanyaan, dan komentar yang dibuat selama pengujian dihapus kembali di akhir folder masing-masing. Akun dari request Register tidak dihapus, jadi setiap kali collection dijalankan, jumlah akun bertambah satu.
- Jika backend tidak berjalan di `http://127.0.0.1:8080`, ubah variabel `baseUrl`: klik collection **CampHubAPI**, lalu buka tab **Variables**.
- Sebelum meng-commit collection yang di-export ulang dari Postman, pastikan variabel `tokenUser`, `tokenProvider`, `tokenAdmin`, dan `tokenNewUser` kosong.
- Hak akses setiap endpoint ada di [README bagian Endpoint](../README.md#endpoint).
