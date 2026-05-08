package library.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private int yearPublished;
    private int totalStock;
    private int availableStock;
    private int categoryId;
    private String categoryName; // untuk join query

    public Book() {}

    public Book(int bookId, String title, String author, String isbn,
                String publisher, int yearPublished, int totalStock,
                int availableStock, int categoryId) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.categoryId = categoryId;
    }

    // Getters & Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getYearPublished() { return yearPublished; }
    public void setYearPublished(int yearPublished) { this.yearPublished = yearPublished; }

    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() { return title + " - " + author; }
}
