package DAL;

import DTO.KhuyenMaiDTO;
import Database.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAL {
    private DBConnection db = new DBConnection();

    public List<KhuyenMaiDTO> layTatCa() {
        List<KhuyenMaiDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM khuyenmai";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong layTatCa()");
                return danhSach;
            }
            while (rs.next()) {
                KhuyenMaiDTO km = new KhuyenMaiDTO();
                km.setMa(rs.getString("ma"));
                km.setPhanTramGiam(rs.getDouble("phan_tram_giam"));
                km.setHanDung(rs.getDate("han_dung"));
                danhSach.add(km);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn danh sách khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }

    public boolean themKhuyenMai(KhuyenMaiDTO km) {
        String sql = "INSERT INTO khuyenmai (ma, phan_tram_giam, han_dung) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong themKhuyenMai()");
                return false;
            }
            ps.setString(1, km.getMa());
            ps.setDouble(2, km.getPhanTramGiam());
            ps.setDate(3, (Date) km.getHanDung());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm mã khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaKhuyenMai(String ma) {
        String sql = "DELETE FROM khuyenmai WHERE ma = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong xoaKhuyenMai()");
                return false;
            }
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa mã khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}