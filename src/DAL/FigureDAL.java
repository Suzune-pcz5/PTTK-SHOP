// DAL/FigureDAL.java
package DAL;

import DTO.FigureDTO;
import Database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FigureDAL {
    private DBConnection db = new DBConnection();

    public List<FigureDTO> layTatCa() {
        List<FigureDTO> danhSach = new ArrayList<>();
        String sql = "SELECT id, ten, loai, gia, kich_thuoc, so_luong, mo_ta FROM figure";

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FigureDTO f = new FigureDTO();
                f.setId(rs.getInt("id"));
                f.setTen(rs.getString("ten"));
                f.setLoai(rs.getString("loai"));
                f.setGia(rs.getInt("gia"));
                f.setKichThuoc(rs.getString("kich_thuoc"));
                f.setSoLuong(rs.getInt("so_luong"));
                f.setMoTa(rs.getString("mo_ta"));
                danhSach.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi layTatCa(): " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }

    public FigureDTO timTheoId(int id) {
        String sql = "SELECT id, ten, loai, gia, kich_thuoc, so_luong, mo_ta FROM figure WHERE id = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FigureDTO f = new FigureDTO();
                    f.setId(rs.getInt("id"));
                    f.setTen(rs.getString("ten"));
                    f.setLoai(rs.getString("loai"));
                    f.setGia(rs.getInt("gia"));
                    f.setKichThuoc(rs.getString("kich_thuoc"));
                    f.setSoLuong(rs.getInt("so_luong"));
                    f.setMoTa(rs.getString("mo_ta"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi timTheoId(): " + e.getMessage());
        }
        return null;
    }

    public List<FigureDTO> timKiemNangCao(String loai, Double minGia, Double maxGia, String kichThuoc) {
        List<FigureDTO> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, ten, loai, gia, kich_thuoc, so_luong, mo_ta FROM figure WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (loai != null && !loai.trim().isEmpty()) {
            sql.append(" AND loai = ?");
            params.add(loai);
        }
        if (minGia != null) {
            sql.append(" AND gia >= ?");
            params.add(minGia.intValue());
        }
        if (maxGia != null) {
            sql.append(" AND gia <= ?");
            params.add(maxGia.intValue());
        }
        if (kichThuoc != null && !kichThuoc.trim().isEmpty()) {
            sql.append(" AND kich_thuoc = ?");
            params.add(kichThuoc);
        }

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FigureDTO f = new FigureDTO();
                    f.setId(rs.getInt("id"));
                    f.setTen(rs.getString("ten"));
                    f.setLoai(rs.getString("loai"));
                    f.setGia(rs.getInt("gia"));
                    f.setKichThuoc(rs.getString("kich_thuoc"));
                    f.setSoLuong(rs.getInt("so_luong"));
                    f.setMoTa(rs.getString("mo_ta"));
                    danhSach.add(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi timKiemNangCao(): " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }

    public boolean capNhatSoLuong(int id, int delta) {
        String sql = delta >= 0
                ? "UPDATE figure SET so_luong = so_luong - ? WHERE id = ? AND so_luong >= ?"
                : "UPDATE figure SET so_luong = so_luong + ? WHERE id = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.abs(delta));
            ps.setInt(2, id);
            if (delta >= 0) ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi capNhatSoLuong(): " + e.getMessage());
            return false;
        }
    }

    public boolean kiemTraSoLuong(int id, int soLuongCan) {
        String sql = "SELECT so_luong FROM figure WHERE id = ?";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("so_luong") >= soLuongCan;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiemTraSoLuong(): " + e.getMessage());
        }
        return false;
    }
}