// DAL/DonHangDAL.java
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

    /* ---------- LƯU ĐƠN HÀNG ---------- */
    public boolean luuDonHang(DonHangDTO donHang) {
        String sqlDonHang = "INSERT INTO donhang (ma_nhan_vien, tong_tien, phuong_thuc_tt, ma_khuyen_mai) VALUES (?, ?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chitiet_donhang (donhangId, figureId, so_luong, gia_ban, thanh_tien) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnect()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try (PreparedStatement psDonHang = conn.prepareStatement(sqlDonHang, Statement.RETURN_GENERATED_KEYS)) {
                psDonHang.setInt(1, donHang.getMaNhanVien());
                psDonHang.setDouble(2, donHang.getTongTien());               // DB: INT → Java double
                psDonHang.setString(3, donHang.getPhuongThucTT());
                psDonHang.setString(4, donHang.getMaKhuyenMai());

                if (psDonHang.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = psDonHang.getGeneratedKeys()) {
                    if (rs.next()) donHang.setMaDonHang(rs.getInt(1));
                }

                try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet)) {
                    for (GioHangItemDTO item : donHang.getGioHang()) {
                        psChiTiet.setInt(1, donHang.getMaDonHang());
                        psChiTiet.setInt(2, item.getFigureId());
                        psChiTiet.setInt(3, item.getSoLuong());
                        psChiTiet.setDouble(4, item.getGiaBan());          // DB: INT
                        psChiTiet.setDouble(5, item.getThanhTien());       // DB: INT
                        psChiTiet.addBatch();
                    }
                    psChiTiet.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ---------- KIỂM TRA MÃ KHUYẾN MÃI ---------- */
    public double kiemTraMaKhuyenMai(String ma) {
        String sql = "SELECT phan_tram_giam FROM khuyenmai WHERE ma = ? AND han_dung >= CURDATE()";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("phan_tram_giam");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* ---------- LẤY TẤT CẢ ĐƠN HÀNG ---------- */
    public List<DonHangDTO> layTatCa() {
        List<DonHangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM donhang";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DonHangDTO dh = new DonHangDTO(
                        rs.getInt("ma_don_hang"),
                        rs.getInt("ma_nhan_vien"),
                        new ArrayList<>(),
                        rs.getDouble("tong_tien")
                );
                dh.setNgayDat(rs.getDate("ngay_dat"));
                dh.setPhuongThucTT(rs.getString("phuong_thuc_tt"));
                dh.setMaKhuyenMai(rs.getString("ma_khuyen_mai"));
                dh.setTrangThai(rs.getString("trang_thai"));
                dh.setGioHang(layChiTietDonHang(dh.getMaDonHang()));
                danhSach.add(dh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    private List<GioHangItemDTO> layChiTietDonHang(int donHangId) {
        List<GioHangItemDTO> chiTiet = new ArrayList<>();
        String sql = """
                SELECT c.*, g.ten, g.loai, g.gia, g.kich_thuoc, g.so_luong, g.mo_ta
                FROM chitiet_donhang c
                JOIN figure g ON c.figureId = g.id
                WHERE c.donhangId = ?
                """;
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, donHangId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FigureDTO f = new FigureDTO();
                    f.setId(rs.getInt("figureId"));
                    f.setTen(rs.getString("ten"));
                    f.setLoai(rs.getString("loai"));
                    f.setGia(rs.getDouble("gia"));
                    f.setKichThuoc(rs.getString("kich_thuoc"));
                    f.setSoLuong(rs.getInt("so_luong"));
                    f.setMoTa(rs.getString("mo_ta"));

                    GioHangItemDTO item = new GioHangItemDTO(
                            f,
                            rs.getInt("so_luong"),
                            rs.getDouble("gia_ban")
                    );
                    chiTiet.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chiTiet;
    }

    /* ---------- HỦY ĐƠN HÀNG (trigger sẽ tự cộng lại kho) ---------- */
    public boolean huyDonHang(int maDonHang) {
        String sql = "UPDATE donhang SET trang_thai = 'DaHuy' WHERE ma_don_hang = ? AND trang_thai = 'DaThanhToan'";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDonHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}