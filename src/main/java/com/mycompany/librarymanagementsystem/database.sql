-- ============================================
-- Library Management System Database
-- Compatible with MySQL (Laragon / DBeaver)
-- ============================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Table: Category
CREATE TABLE IF NOT EXISTS category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Table: Books
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    publisher VARCHAR(150),
    year_published INT,
    total_stock INT DEFAULT 1,
    available_stock INT DEFAULT 1,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE SET NULL
);

-- Table: Member
CREATE TABLE IF NOT EXISTS member (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    register_date DATE NOT NULL,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

-- Table: Issued Books
CREATE TABLE IF NOT EXISTS issued_books (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    date_issued DATE NOT NULL,
    due_date DATE NOT NULL,
    date_returned DATE,
    fine DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('Issued', 'Returned', 'Overdue') DEFAULT 'Issued',
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ============ SAMPLE DATA ============

INSERT INTO category (category_name, description) VALUES
('Technology', 'Books related to computers, programming, and IT'),
('Science', 'Natural and applied sciences'),
('Literature', 'Fiction and non-fiction literature'),
('History', 'Historical events and biographies'),
('Mathematics', 'Pure and applied mathematics');

INSERT INTO books (title, author, isbn, publisher, year_published, total_stock, available_stock, category_id) VALUES
('Clean Code', 'Robert C. Martin', '9780132350884', 'Prentice Hall', 2008, 3, 3, 1),
('The Pragmatic Programmer', 'David Thomas', '9780135957059', 'Addison-Wesley', 2019, 2, 2, 1),
('Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848', 'MIT Press', 2009, 4, 4, 5),
('A Brief History of Time', 'Stephen Hawking', '9780553380163', 'Bantam Books', 1988, 2, 2, 2),
('Sapiens', 'Yuval Noah Harari', '9780062316097', 'Harper', 2011, 3, 3, 4),
('Design Patterns', 'Gang of Four', '9780201633610', 'Addison-Wesley', 1994, 2, 2, 1),
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner', 1925, 3, 3, 3),
('Calculus', 'James Stewart', '9781285740621', 'Cengage Learning', 2015, 2, 2, 5);

INSERT INTO member (full_name, email, phone, address, register_date, status) VALUES
('Budi Santoso', 'budi@email.com', '08123456789', 'Jl. Merdeka No. 1, Jakarta', '2024-01-10', 'Active'),
('Siti Rahayu', 'siti@email.com', '08234567890', 'Jl. Sudirman No. 5, Bandung', '2024-02-15', 'Active'),
('Andi Wijaya', 'andi@email.com', '08345678901', 'Jl. Diponegoro No. 3, Surabaya', '2024-03-20', 'Active'),
('Dewi Kusuma', 'dewi@email.com', '08456789012', 'Jl. Pahlawan No. 7, Yogyakarta', '2024-04-05', 'Active');
