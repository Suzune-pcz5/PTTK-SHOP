package DAL;

import DTO.NguoiDungDTO;
import Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NguoiDungDAL {
    private DBConnection db = new DBConnection();

    public boolean luuNguoiDung(NguoiDungDTO nguoiDung) {
        String sql = "INSERT INTO nguoidung (ten_dang_nhap, mat_khau, vai_tro) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong luuNguoiDung()");
                return false;
            }
            ps.setString(1, nguoiDung.getTenDangNhap());
            ps.setString(2, nguoiDung.getMatKhau());
            ps.setString(3, nguoiDung.getVaiTro());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    nguoiDung.setMaNguoiDung(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lưu người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean kiemTraTenDangNhap(String tenDangNhap) {
        String sql = "SELECT COUNT(*) FROM nguoidung WHERE ten_dang_nhap = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong kiemTraTenDangNhap()");
                return false;
            }
            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra tên đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public NguoiDungDTO timNguoiDung(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM nguoidung WHERE ten_dang_nhap = ? AND mat_khau = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong timNguoiDung()");
                return null;
            }
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new NguoiDungDTO(
                    rs.getInt("ma_nguoi_dung"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    rs.getString("vai_tro")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<NguoiDungDTO> layTatCa() {
        List<NguoiDungDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nguoidung";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong layTatCa()");
                return danhSach;
            }
            while (rs.next()) {
                NguoiDungDTO nd = new NguoiDungDTO(
                    rs.getInt("ma_nguoi_dung"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    rs.getString("vai_tro")
                );
                danhSach.add(nd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn danh sách người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }

    public boolean xoaNguoiDung(int maNguoiDung) {
        String sql = "DELETE FROM nguoidung WHERE ma_nguoi_dung = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong xoaNguoiDung()");
                return false;
            }
            ps.setInt(1, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}