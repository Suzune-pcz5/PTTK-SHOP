package DAL;

import DTO.DonHangDTO;
import DTO.FigureDTO;
import DTO.GioHangItemDTO;
import Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAL {
    private DBConnection db = new DBConnection();
    private FigureDAL figureDAL = new FigureDAL();

    public boolean luuDonHang(DonHangDTO donHang) {
        String sqlDonHang = "INSERT INTO donhang (ma_khach_hang, ma_nhan_vien, ngay_dat, tong_tien) VALUES (?, ?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chitiet_donhang (donHangId, figureId, soLuong, thanhTien) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnect()) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong luuDonHang()");
                return false;
            }
            conn.setAutoCommit(false);
            try (PreparedStatement psDonHang = conn.prepareStatement(sqlDonHang, Statement.RETURN_GENERATED_KEYS)) {
                psDonHang.setInt(1, donHang.getMaKhachHang());
                psDonHang.setInt(2, donHang.getMaNhanVien());
                psDonHang.setDate(3, donHang.getNgayDat());
                psDonHang.setDouble(4, donHang.getTongTien());
                int rowsAffected = psDonHang.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
                ResultSet rs = psDonHang.getGeneratedKeys();
                if (rs.next()) {
                    donHang.setMaDonHang(rs.getInt(1));
                }

                try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet)) {
                    for (GioHangItemDTO item : donHang.getGioHang()) {
                        psChiTiet.setInt(1, donHang.getMaDonHang());
                        psChiTiet.setInt(2, item.getFigure().getId());
                        psChiTiet.setInt(3, item.getSoLuong());
                        psChiTiet.setDouble(4, item.getThanhTien());
                        psChiTiet.addBatch();
                    }
                    psChiTiet.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi lưu đơn hàng: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double kiemTraMaKhuyenMai(String ma) {
        String sql = "SELECT phan_tram_giam FROM khuyenmai WHERE ma = ? AND han_dung >= CURDATE()";
        try (Connection conn = db.getConnect()) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong kiemTraMaKhuyenMai()");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ma);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("phan_tram_giam");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mã khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

   public List<DonHangDTO> layTatCa() {
    List<DonHangDTO> danhSach = new ArrayList<>();
    String sql = "SELECT * FROM donhang";
    try (Connection conn = db.getConnect()) {
        if (conn == null) {
            System.err.println("Không thể kết nối cơ sở dữ liệu trong layTatCa()");
            return danhSach;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Tạo đối tượng DonHangDTO và sử dụng setter để gán giá trị
                DonHangDTO dh = new DonHangDTO(0, 0, new ArrayList<>(), 0.0); // Sử dụng constructor 4 tham số với giá trị tạm thời
                dh.setMaDonHang(rs.getInt("ma_don_hang"));
                dh.setMaKhachHang(rs.getInt("ma_khach_hang"));
                dh.setMaNhanVien(rs.getInt("ma_nhan_vien"));
                dh.setNgayDat(rs.getDate("ngay_dat"));
                dh.setTongTien(rs.getDouble("tong_tien"));
                dh.setGioHang(layChiTietDonHang(dh.getMaDonHang()));
                danhSach.add(dh);
            }
        }
    } catch (SQLException e) {
        System.err.println("Lỗi truy vấn danh sách đơn hàng: " + e.getMessage());
        e.printStackTrace();
    }
    return danhSach;
}

    private List<GioHangItemDTO> layChiTietDonHang(int donHangId) {
        List<GioHangItemDTO> chiTiet = new ArrayList<>();
        String sql = "SELECT c.*, g.ten, g.loai, g.gia, g.kich_thuoc, g.so_luong " +
                     "FROM chitiet_donhang c JOIN figure g ON c.figureId = g.id WHERE c.donHangId = ?";
        try (Connection conn = db.getConnect()) {
            if (conn == null) {
                System.err.println("Không thể kết nối cơ sở dữ liệu trong layChiTietDonHang()");
                return chiTiet;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, donHangId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        FigureDTO gau = new FigureDTO();
                        gau.setId(rs.getInt("figureId"));
                        gau.setTen(rs.getString("ten"));
                        gau.setLoai(rs.getString("loai"));
                        gau.setGia(rs.getDouble("gia"));
                        gau.setKichThuoc(rs.getString("kich_thuoc"));
                        gau.setSoLuong(rs.getInt("so_luong"));
                        GioHangItemDTO item = new GioHangItemDTO(gau, rs.getInt("soLuong"), rs.getDouble("thanhTien"));
                        chiTiet.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn chi tiết đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return chiTiet;
    }
}