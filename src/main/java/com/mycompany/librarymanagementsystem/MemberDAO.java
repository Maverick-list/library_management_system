package library.db;

import library.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM member ORDER BY member_id";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM member WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY member_id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addMember(Member member) {
        String sql = "INSERT INTO member (full_name, email, phone, address, register_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setDate(5, new java.sql.Date(member.getRegisterDate().getTime()));
            ps.setString(6, member.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMember(Member member) {
        String sql = "UPDATE member SET full_name=?, email=?, phone=?, address=?, status=? WHERE member_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setString(5, member.getStatus());
            ps.setInt(6, member.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM member WHERE member_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Member> getActiveMembers() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM member WHERE status='Active' ORDER BY full_name";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Member mapResultSet(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setMemberId(rs.getInt("member_id"));
        m.setFullName(rs.getString("full_name"));
        m.setEmail(rs.getString("email"));
        m.setPhone(rs.getString("phone"));
        m.setAddress(rs.getString("address"));
        m.setRegisterDate(rs.getDate("register_date"));
        m.setStatus(rs.getString("status"));
        return m;
    }
}
