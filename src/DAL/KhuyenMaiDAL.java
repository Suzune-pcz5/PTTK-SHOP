package DAL;

import Database.DBConnection;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class KhuyenMaiDAL {
    private DBConnection db = new DBConnection();

    // 1. Hàm loadData (Dùng để đổ dữ liệu vào bảng)
    public void loadData(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM khuyenmai";
        try (Connection conn = db.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("ma"),
                    rs.getInt("phan_tram_giam"),
                    rs.getString("han_dung"),
                    rs.getString("mo_ta")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Hàm them (Thêm khuyến mãi mới)
    public boolean them(String ma, int pt, String han, String mota) {
        String sql = "INSERT INTO khuyenmai (ma, phan_tram_giam, han_dung, mo_ta) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ps.setInt(2, pt);
            ps.setString(3, han);
            ps.setString(4, mota);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Hàm sua (Cập nhật khuyến mãi)
    public boolean sua(String ma, int pt, String han, String mota) {
        String sql = "UPDATE khuyenmai SET phan_tram_giam=?, han_dung=?, mo_ta=? WHERE ma=?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pt);
            ps.setString(2, han);
            ps.setString(3, mota);
            ps.setString(4, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Hàm xoa (Xóa khuyến mãi)
    public boolean xoa(String ma) {
        String sql = "DELETE FROM khuyenmai WHERE ma=?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}