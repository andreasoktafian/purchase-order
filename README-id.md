*Baca dalam bahasa lain: [Inggris](README.md), [Indonesia](README-id.md)*

# Layanan Manajemen Purchase Order (PO)

Sebuah microservice Spring Boot RESTful yang tangguh, dirancang untuk mengelola transaksi Item, User, dan Purchase Order. Proyek ini mendemonstrasikan praktik backend tingkat *enterprise* termasuk migrasi database, *auditing* otomatis, pencegahan N+1 *query*, dan validasi data yang ketat.

## Teknologi yang Digunakan (Tech Stack)

*   **Java 21** (Fitur Virtual Threads diaktifkan)
*   **Spring Boot** (Web, Data JPA, Validation)
*   **MySQL**
*   **Lombok** (Pengurangan kode *boilerplate*)
*   **Maven**

## Asumsi Arsitektur & Keputusan Desain

Sebagai sebuah *downstream microservice*, beberapa keputusan arsitektur telah diambil untuk memastikan skalabilitas, ketergantungan yang longgar (*loose coupling*), dan kesiapan aplikasi untuk lingkungan terdistribusi (*distributed environment*):

1.  **API Gateway Offloading (Otentikasi):**
    Layanan ini berasumsi bahwa ekstraksi dan validasi token JWT ditangani secara terpusat oleh sebuah *API Gateway*. Layanan ini hanya mengharapkan identitas *user* diteruskan melalui HTTP Headers (yang kemudian diintersep dan dipetakan ke dalam `AppRequestContext`). Hal ini membuat aplikasi tetap *stateless* dan murni berfokus pada logika bisnis.
2.  **Loose Coupling (Tanpa Relasi Foreign Key Fisik ke Tabel Users):**
    Tabel `users` bertindak sebagai entitas yang terikat secara longgar. Kolom *audit* (`created_by`, `updated_by`) menyimpan *identifier* berbasis teks (String) daripada menggunakan *Foreign Key* fisik. Hal ini mencegah hilangnya data riwayat transaksi PO jika suatu saat seorang *user* dihapus secara permanen (*hard-delete*) dari layanan IAM (Identity and Access Management) eksternal, sehingga rekam jejak sistem tetap valid.
3.  **Stabilitas JSON Pagination:**
    Menggunakan konfigurasi `PagedModel` dari Spring Data (`@EnableSpringDataWebSupport`) untuk memastikan stabilitas struktur JSON pada respons *pagination*, guna mencegah *breaking changes* (error karena perubahan struktur) di sisi *client/Front End* jika ada pembaruan versi *framework* di masa depan.

## Fitur Utama & Sorotan Teknis

*   **Smart Update (Perilaku PATCH/PUT):** Pembaruan data (*update*) secara cerdas mengabaikan nilai `null` atau kosong. Hal ini memungkinkan sistem melakukan pembaruan parsial tanpa menimpa data lama yang sudah ada di database.
*   **Validasi Lintas Kolom (Cross-Field Validation):** Mengimplementasikan validasi kustom `@AssertTrue` di *layer* DTO untuk menegakkan aturan bisnis (contoh: mencegah input di mana harga jual / `price` disetel lebih rendah dari harga modal / `cost`).
*   **Pencegahan N+1 Query Problem:** Memanfaatkan `LEFT JOIN FETCH` pada *layer* JPA Repository untuk *endpoint* yang mengambil detail Purchase Order. Hal ini secara drastis mengurangi *query* berulang ke database dari O(N) menjadi hanya O(1).
*   **Auditing Otomatis:** Mengintegrasikan `@EnableJpaAuditing` dengan *entity listeners* untuk mengisi kolom tanggal secara otomatis (`created_datetime` dan `updated_datetime`).
*   **Defensive Programming:** Menerapkan anotasi `@NonNull` dan Jakarta Validation (`@NotBlank`, `@Min`) secara ketat di seluruh *layer* Service dan Controller untuk memastikan integritas data sebelum dieksekusi oleh database.

## Cara Menjalankan Aplikasi (Getting Started)

### Prasyarat
*   JDK Java 21 terinstal di komputer.
*   MySQL Server berjalan pada port default `3306`.
*   Maven terinstal (atau gunakan *wrapper* bawaan yang tersedia).

### 1. Setup Database
Buat sebuah database kosong di dalam server MySQL Anda:
```sql
CREATE DATABASE andreas_oktafian;
```

### 2. Insert Data
Insert data dummy:
```sql
INSERT INTO Users (first_name, last_name, email, phone, created_by, created_datetime, updated_datetime)
VALUES
    ('Siti', 'Rahma', 'siti.rahma@example.com', '08111222333', 'system', NOW(), NOW()),
    ('Joko', 'Widodo', 'joko.widodo@example.com', '08122334455', 'system', NOW(), NOW()),
    ('Dewi', 'Lestari', 'dewi.lestari@example.com', '08133445566', 'system', NOW(), NOW()),
    ('Eko', 'Prasetyo', 'eko.prasetyo@example.com', '08144556677', 'system', NOW(), NOW()),
    ('Rina', 'Marlina', 'rina.marlina@example.com', '08155667788', 'system', NOW(), NOW()),
    ('Ahmad', 'Fauzi', 'ahmad.fauzi@example.com', '08166778899', 'system', NOW(), NOW()),
    ('Dian', 'Sastro', 'dian.sastro@example.com', '08177889900', 'system', NOW(), NOW()),
    ('Reza', 'Rahadian', 'reza.rahadian@example.com', '08188990011', 'system', NOW(), NOW()),
    ('Putri', 'Ariani', 'putri.ariani@example.com', '08199001122', 'system', NOW(), NOW()),
    ('Rizky', 'Febian', 'rizky.febian@example.com', '08100112233', 'system', NOW(), NOW()),
    ('Angga', 'Yunanda', 'angga.yunanda@example.com', '08211223344', 'system', NOW(), NOW()),
    ('Tasya', 'Kamila', 'tasya.kamila@example.com', '08222334455', 'system', NOW(), NOW()),
    ('Raffi', 'Ahmad', 'raffi.ahmad@example.com', '08233445566', 'system', NOW(), NOW()),
    ('Nagita', 'Slavina', 'nagita.slavina@example.com', '08244556677', 'system', NOW(), NOW()),
    ('Deddy', 'Corbuzier', 'deddy.corbuzier@example.com', '08255667788', 'system', NOW(), NOW()),
    ('Najwa', 'Shihab', 'najwa.shihab@example.com', '08266778899', 'system', NOW(), NOW()),
    ('Raditya', 'Dika', 'raditya.dika@example.com', '08277889900', 'system', NOW(), NOW()),
    ('Maudy', 'Ayunda', 'maudy.ayunda@example.com', '08288990011', 'system', NOW(), NOW()),
    ('Jerome', 'Polin', 'jerome.polin@example.com', '08299001122', 'system', NOW(), NOW()),
    ('Jessica', 'Jane', 'jessica.jane@example.com', '08200112233', 'system', NOW(), NOW());


INSERT INTO Item (name, description, price, cost, created_by, created_datetime, updated_datetime)
VALUES
    ('Dell UltraSharp 27', 'Monitor 4K USB-C', 7500000, 6000000, 'system', NOW(), NOW()),
    ('Sony WH-1000XM5', 'Wireless Noise Canceling Headphones', 5500000, 4200000, 'system', NOW(), NOW()),
    ('Anker PowerCore 24K', 'Power Bank 140W', 2200000, 1600000, 'system', NOW(), NOW()),
    ('Samsung T7 Shield 1TB', 'Portable SSD External', 1800000, 1300000, 'system', NOW(), NOW()),
    ('Razer DeathAdder V3', 'Gaming Mouse', 950000, 700000, 'system', NOW(), NOW()),
    ('HyperX Cloud III', 'Gaming Headset', 1300000, 950000, 'system', NOW(), NOW()),
    ('Elgato Stream Deck MK.2', 'Control Pad for Content Creators', 2800000, 2100000, 'system', NOW(), NOW()),
    ('Apple iPad Air M2', 'Tablet Apple 11 inch', 10500000, 9000000, 'system', NOW(), NOW()),
    ('iPhone 15 Pro 128GB', 'Smartphone Apple', 18500000, 16000000, 'system', NOW(), NOW()),
    ('Asus ROG Ally', 'Handheld Gaming Console', 11000000, 9500000, 'system', NOW(), NOW()),
    ('Kindle Paperwhite 11th Gen', 'E-Reader E-Ink Display', 2400000, 1800000, 'system', NOW(), NOW()),
    ('DJI Osmo Pocket 3', 'Vlogging Camera 4K', 8500000, 7000000, 'system', NOW(), NOW()),
    ('Smart Desk Lamp RGB', 'Smart Desk Lamp LED', 450000, 300000, 'system', NOW(), NOW()),
    ('Ugreen 100W GaN Charger', 'Multiport Fast Charger', 750000, 500000, 'system', NOW(), NOW()),
    ('SanDisk Extreme 128GB', 'MicroSD Card V30', 350000, 220000, 'system', NOW(), NOW()),
    ('Lian Li O11 Dynamic', 'PC Gaming Case', 2100000, 1500000, 'system', NOW(), NOW()),
    ('Corsair RM850x', 'Power Supply Unit 850W 80+ Gold', 2300000, 1750000, 'system', NOW(), NOW()),
    ('NZXT Kraken 240', 'CPU Liquid Cooler', 2600000, 1900000, 'system', NOW(), NOW()),
    ('Bose SoundLink Flex', 'Portable Bluetooth Speaker', 2500000, 1900000, 'system', NOW(), NOW()),
    ('TP-Link Archer AX55', 'Wi-Fi 6 Router', 1250000, 900000, 'system', NOW(), NOW());
```

### 3. Konfigurasi & Menjalankan Aplikasi
Sebelum menjalankan aplikasi, pastikan Anda meninjau file `src/main/resources/application-local.yaml` dan memperbarui kredensial database (`username` dan `password`) agar sesuai dengan pengaturan MySQL lokal Anda. Setelah sesuai, jalankan aplikasi menggunakan Maven dengan profil `local` yang aktif:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```