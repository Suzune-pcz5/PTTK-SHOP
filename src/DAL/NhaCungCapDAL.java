package DAL;

import DTO.NhaCungCapDTO;
import Database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAL {
    private DBConnection db = new DBConnection();

    // 1. Lấy danh sách
    public List<NhaCungCapDTO> layDanhSachNCC() {
        List<NhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap"; // Lấy hết để quản lý
        try (Connection conn = db.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new NhaCungCapDTO(
                    rs.getInt("ma_ncc"),
                    rs.getString("ten_ncc"),
                    rs.getString("dia_chi"),
                    rs.getString("so_dien_thoai"),
                    rs.getString("email"),
                    rs.getString("trang_thai")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Thêm
    public boolean themNhaCungCap(NhaCungCapDTO ncc) {
        String sql = "INSERT INTO nhacungcap (ten_ncc, dia_chi, so_dien_thoai, email, trang_thai) VALUES (?, ?, ?, ?, 'Hợp tác')";
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getEmail());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 3. Sửa
    public boolean suaNhaCungCap(NhaCungCapDTO ncc) {
        String sql = "UPDATE nhacungcap SET ten_ncc=?, dia_chi=?, so_dien_thoai=?, email=?, trang_thai=? WHERE ma_ncc=?";
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getEmail());
            ps.setString(5, ncc.getTrangThai());
            ps.setInt(6, ncc.getMaNCC());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 4. Đổi trạng thái
    public boolean doiTrangThai(int maNCC, String status) {
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("UPDATE nhacungcap SET trang_thai=? WHERE ma_ncc=?")) {
            ps.setString(1, status);
            ps.setInt(2, maNCC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
    
    // 5. Xóa nhà cung cấp
    public boolean xoaNhaCungCap(int maNCC) {
        try (Connection conn = new DBConnection().getConnect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nhacungcap WHERE ma_ncc = ?")) {
            ps.setInt(1, maNCC);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}