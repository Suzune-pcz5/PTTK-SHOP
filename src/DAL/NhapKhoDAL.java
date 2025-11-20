// DAL/NhapKhoDAL.java
package DAL;

import DTO.NhapKhoDTO;
import Database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NhapKhoDAL {
    private DBConnection db = new DBConnection();
    private FigureDAL figureDAL = new FigureDAL(); // <--- Đảm bảo dòng này tồn tại
        
    // === 1. LƯU PHIẾU NHẬP ===
    public boolean luuPhieuNhap(NhapKhoDTO nhapKho) {
        String sql = "INSERT INTO nhapkho (figureId, so_luong_nhap, ngay_nhap, ma_nhan_vien) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (conn == null) return false;

            ps.setInt(1, nhapKho.getFigureId());
            ps.setInt(2, nhapKho.getSoLuongNhap());
            ps.setDate(3, nhapKho.getNgayNhap());
            ps.setInt(4, nhapKho.getMaNhanVien());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        nhapKho.setMaNhap(rs.getInt(1));
                    }
                }
                // Cập nhật tồn kho
                FigureDAL figureDAL = new FigureDAL();
                return figureDAL.capNhatSoLuong(nhapKho.getFigureId(), nhapKho.getSoLuongNhap());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lưu phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // === 2. LẤY TẤT CẢ PHIẾU NHẬP ===
    public List<NhapKhoDTO> layTatCa() {
        List<NhapKhoDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nhapkho ORDER BY ngay_nhap DESC";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhapKhoDTO nk = new NhapKhoDTO(
                    rs.getInt("ma_nhap"),
                    rs.getInt("figureId"),
                    rs.getInt("so_luong_nhap"),
                    rs.getDate("ngay_nhap"),
                    rs.getInt("ma_nhan_vien")
                );
                list.add(nk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách nhập kho: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // === 3. LẤY THEO MÃ NHÂN VIÊN ===
    public List<NhapKhoDTO> layTheoNhanVien(int maNhanVien) {
        List<NhapKhoDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nhapkho WHERE ma_nhan_vien = ? ORDER BY ngay_nhap DESC";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhapKhoDTO nk = new NhapKhoDTO(
                        rs.getInt("ma_nhap"),
                        rs.getInt("figureId"),
                        rs.getInt("so_luong_nhap"),
                        rs.getDate("ngay_nhap"),
                        rs.getInt("ma_nhan_vien")
                    );
                    list.add(nk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}