package library.model;

import java.util.Date;

public class IssuedBook {
    private int issueId;
    private int bookId;
    private int memberId;
    private Date dateIssued;
    private Date dueDate;
    private Date dateReturned;
    private double fine;
    private String status;

    // For display (joined columns)
    private String bookTitle;
    private String memberName;

    public IssuedBook() {}

    // Getters & Setters
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public Date getDateIssued() { return dateIssued; }
    public void setDateIssued(Date dateIssued) { this.dateIssued = dateIssued; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getDateReturned() { return dateReturned; }
    public void setDateReturned(Date dateReturned) { this.dateReturned = dateReturned; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
}
