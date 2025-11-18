// DAL/NguoiDungDAL.java
package DAL;

import DTO.NguoiDungDTO;
import Database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NguoiDungDAL {
    private DBConnection db = new DBConnection();

    public boolean luuNguoiDung(NguoiDungDTO nd) {
        // [SỬA]: Thêm cột trang_thai
        String sql = "INSERT INTO nguoidung (email, ten_dang_nhap, mat_khau, vai_tro, trang_thai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nd.getEmail());
            ps.setString(2, nd.getTenDangNhap());
            ps.setString(3, nd.getMatKhau());
            ps.setString(4, nd.getVaiTro());
            ps.setString(5, "Mở"); // Mặc định là Mở
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public NguoiDungDTO timNguoiDung(String tenDangNhap, String matKhau) {
        // [SỬA]: Lấy thêm trang_thai
        String sql = "SELECT * FROM nguoidung WHERE ten_dang_nhap = ? AND mat_khau = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // [SỬA]: Map thêm cột trang_thai
                    return new NguoiDungDTO(
                        rs.getInt("ma_nguoi_dung"),
                        rs.getString("email"),
                        rs.getString("ten_dang_nhap"),
                        rs.getString("mat_khau"),
                        rs.getString("vai_tro"),
                        rs.getString("trang_thai") // Lấy trạng thái Mở/Tắt
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean kiemTraTenDangNhap(String tenDangNhap) {
        String sql = "SELECT COUNT(*) FROM nguoidung WHERE ten_dang_nhap = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public List<NguoiDungDTO> layTatCa() {
        List<NguoiDungDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nguoidung";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NguoiDungDTO(
                    rs.getInt("ma_nguoi_dung"),
                    rs.getString("email"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    rs.getString("vai_tro"),
                    rs.getString("trang_thai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public boolean xoaNguoiDung(int maNguoiDung) {
        String sql = "DELETE FROM nguoidung WHERE ma_nguoi_dung = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // MỚI: KIỂM TRA TÊN + EMAIL

    public boolean kiemTraDanhTinh(String tenDangNhap, String email) {
        String sql = "SELECT COUNT(*) FROM nguoidung WHERE ten_dang_nhap = ? AND email = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // MỚI: CẬP NHẬT MẬT KHẨU

    public boolean capNhatMatKhau(NguoiDungDTO nd) {
        String sql = "UPDATE nguoidung SET mat_khau = ? WHERE ten_dang_nhap = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nd.getMatKhau());
            ps.setString(2, nd.getTenDangNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}