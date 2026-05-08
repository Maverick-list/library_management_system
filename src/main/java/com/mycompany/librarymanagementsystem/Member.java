package library.model;

import java.util.Date;

public class Member {
    private int memberId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Date registerDate;
    private String status;

    public Member() {}

    public Member(int memberId, String fullName, String email, String phone,
                  String address, Date registerDate, String status) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registerDate = registerDate;
        this.status = status;
    }

    // Getters & Setters
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getRegisterDate() { return registerDate; }
    public void setRegisterDate(Date registerDate) { this.registerDate = registerDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return memberId + " - " + fullName; }
}
