package DAL;

import DTO.FigureDTO;
import Database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FigureDAL {

    public List<FigureDTO> layTatCa() {
        List<FigureDTO> list = new ArrayList<>();
        // Cần JOIN để lấy ten_ncc, dùng LEFT JOIN để tránh mất SP nếu chưa có NCC
        String sql = "SELECT f.*, n.ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.ma_ncc"; 

        try (Connection conn = new DBConnection().getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToDTO(rs));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Xem lỗi ở Output nếu có
        }
        return list;
    }

    // --- SỬA HÀM timTheoId() ---
    public FigureDTO timTheoId(int id) {
        // Sửa query: Thêm JOIN với nhacungcap để lấy ten_ncc
        String sql = "SELECT f.*, n.ten AS ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.id_ncc WHERE f.id = ?";
        try (Connection conn = new DBConnection().getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToDTO(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- HÀM TÌM KIẾM NÂNG CAO (ĐÃ SỬA QUERY) ---
    public List<FigureDTO> timKiemNangCao(String ten, String loai, Double min, Double max, String kt, Integer maNCC) {
        List<FigureDTO> list = new ArrayList<>();
        // Sửa query: Thêm JOIN với nhacungcap để lấy ten_ncc
        StringBuilder sql = new StringBuilder("SELECT f.*, n.ten AS ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.id_ncc WHERE 1=1");

        if (ten != null && !ten.isEmpty()) sql.append(" AND f.ten LIKE '%").append(ten).append("%'");
        if (loai != null) sql.append(" AND f.loai = '").append(loai).append("'");
        if (min != null) sql.append(" AND f.gia >= ").append(min);
        if (max != null) sql.append(" AND f.gia <= ").append(max);
        if (kt != null) sql.append(" AND f.kich_thuoc = '").append(kt).append("'");
        
        // Logic lọc Nhà cung cấp
        if (maNCC != null && maNCC > 0) {
            sql.append(" AND f.ma_ncc = ").append(maNCC);
        }

        try (Connection conn = new DBConnection().getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            
            while (rs.next()) {
                list.add(mapRowToDTO(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- HÀM PHỤ MAP DỮ LIỆU (ĐÃ THÊM tenNCC) ---
    private FigureDTO mapRowToDTO(ResultSet rs) throws SQLException {
        FigureDTO f = new FigureDTO();
        f.setId(rs.getInt("id"));
        f.setTen(rs.getString("ten"));
        f.setLoai(rs.getString("loai"));
        f.setGia(rs.getLong("gia"));
        f.setKichThuoc(rs.getString("kich_thuoc"));
        f.setSoLuong(rs.getInt("so_luong"));
        f.setMoTa(rs.getString("mo_ta"));
        f.setHinhAnh(rs.getString("hinh_anh"));
        f.setMaNCC(rs.getInt("ma_ncc")); 
        f.setTenNCC(rs.getString("ten_ncc")); // <--- LẤY TEN_NCC TỪ KẾT QUẢ JOIN
        try {
            f.setTenNCC(rs.getString("ten_ncc")); // Lấy tên NCC
        } catch (SQLException e) {
            f.setTenNCC("Chưa rõ"); // Fallback nếu không join
        }
        return f;
    }
    
    // Phương thức cập nhật số lượng tồn kho (Cộng thêm hoặc Trừ đi)
    // quantityChange: Số lượng thay đổi (dương để cộng, âm để trừ)
    public boolean capNhatSoLuong(int figureId, int quantityChange) {
        String sql = "UPDATE figure SET so_luong = so_luong + ? WHERE id = ?";
        try (Connection conn = new DBConnection().getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quantityChange);
            ps.setInt(2, figureId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}