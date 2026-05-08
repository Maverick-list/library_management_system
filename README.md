# 📚 Library Management System
## Object-Oriented & Visual Programming | Semester 20252

---

## ✅ FITUR YANG ADA

| Fitur | Keterangan |
|-------|-----------|
| Dashboard | Statistik jumlah buku, anggota, dan pinjaman aktif |
| Book Management | Add, Read, Update, Delete, Search buku |
| Member Management | Registrasi anggota, Update, Delete, Filter Active |
| Issue & Return | Pinjam buku ke anggota, Kembalikan buku, Hitung denda |
| History | Lihat riwayat seluruh peminjaman, Filter by status |

---

## 🧩 KOMPONEN GUI YANG DIGUNAKAN (lebih dari 7)

1. `JTextField` – Input teks (Judul, Author, ISBN, Email, dll.)
2. `JTextArea` – Input alamat (multi-line)
3. `JComboBox` – Dropdown pilihan kategori, status, member, buku
4. `JSpinner` – Input tahun, stok, dan tanggal
5. `JCheckBox` – Filter "Active Only" pada daftar member
6. `JTable` – Tampilan data dalam bentuk tabel
7. `JLabel` – Label dan status bar
8. `JButton` – Tombol aksi
9. `JScrollPane` – Scroll pada tabel dan textarea
10. `JTabbedPane` – Tab navigasi antar halaman
11. `JMenuBar` / `JMenu` – Menu bar
12. `JSeparator` – Pemisah form

---

## 🗄️ STRUKTUR DATABASE

```
library_db
├── category        (category_id, category_name, description)
├── books           (book_id, title, author, isbn, publisher, year_published, total_stock, available_stock, category_id)
├── member          (member_id, full_name, email, phone, address, register_date, status)
└── issued_books    (issue_id, book_id, member_id, date_issued, due_date, date_returned, fine, status)
```

**Relasi:**
- `books.category_id` → `category.category_id`
- `issued_books.book_id` → `books.book_id`
- `issued_books.member_id` → `member.member_id`

---

## ⚙️ CARA SETUP DI NETBEANS

### Step 1: Setup Database
1. Buka **Laragon** → klik **Start All**
2. Buka **DBeaver** atau **phpMyAdmin** (port 8080 di Laragon)
3. Buat database baru dengan nama: `library_db`
4. Jalankan file `database.sql` (copy paste ke query editor, lalu Run)

### Step 2: Buat Project di Netbeans
1. **File → New Project → Java → Java Application**
2. Nama project: `LibraryManagementSystem`
3. Hapus checklist "Create Main Class" (kita sudah punya)

### Step 3: Tambahkan Source Files
1. Klik kanan **Source Packages → New → Java Package**
2. Buat packages:
   - `library.db`
   - `library.gui`
   - `library.model`
3. Copy semua file `.java` ke package yang sesuai

### Step 4: Tambahkan MySQL Connector
1. Download **mysql-connector-java-8.x.x.jar** dari:
   https://dev.mysql.com/downloads/connector/j/
   (pilih "Platform Independent" → ZIP)
2. Di Netbeans: klik kanan project → **Properties**
3. **Libraries → Add JAR/Folder**
4. Browse ke file `.jar` yang sudah didownload
5. Klik OK

### Step 5: Set Main Class
1. Klik kanan project → **Properties → Run**
2. Main Class: `library.gui.MainDashboard`
3. Klik OK

### Step 6: Konfigurasi Koneksi
Buka `src/library/db/DatabaseConnection.java`, sesuaikan:
```java
private static final String USERNAME = "root";
private static final String PASSWORD = ""; // kosong jika default Laragon
```

### Step 7: Run Project
- Klik tombol ▶️ (Run) atau tekan `F6`

---

## 🐛 TROUBLESHOOTING

| Error | Solusi |
|-------|--------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | MySQL connector JAR belum ditambahkan ke project |
| `Connection refused` | Pastikan Laragon/MySQL sudah running |
| `Unknown database 'library_db'` | Jalankan `database.sql` terlebih dahulu |
| `Access denied for user 'root'` | Cek username/password di `DatabaseConnection.java` |
| Tabel merah di DBeaver | Refresh connection (F5) |

---

## 📁 STRUKTUR FILE PROJECT

```
LibraryManagementSystem/
├── database.sql                    ← Script database
├── README.md                       ← Panduan ini
└── src/
    └── library/
        ├── db/
        │   ├── DatabaseConnection.java
        │   ├── BookDAO.java
        │   ├── MemberDAO.java
        │   └── IssuedBookDAO.java
        ├── model/
        │   ├── Book.java
        │   ├── Member.java
        │   ├── IssuedBook.java
        │   └── Category.java
        └── gui/
            ├── MainDashboard.java  ← MAIN CLASS (run ini)
            ├── BookPanel.java
            ├── MemberPanel.java
            ├── IssueReturnPanel.java
            └── HistoryPanel.java
```

---

**Good luck with your project! 🚀**
