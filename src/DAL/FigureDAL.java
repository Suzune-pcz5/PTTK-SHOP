package DAL;

import DTO.FigureDTO;
import Database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FigureDAL {

    // 1. Lấy tất cả sản phẩm
    public List<FigureDTO> layTatCa() {
        List<FigureDTO> list = new ArrayList<>();
        // [FIX]: Sửa 'n.id_ncc' thành 'n.ma_ncc' cho khớp với Database
        String sql = "SELECT f.*, n.ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.ma_ncc"; 

        try (Connection conn = new DBConnection().getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToDTO(rs));
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return list;
    }

    // 2. Tìm theo ID
    public FigureDTO timTheoId(int id) {
        // [FIX]: Sửa 'n.id_ncc' thành 'n.ma_ncc'
        String sql = "SELECT f.*, n.ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.ma_ncc WHERE f.id = ?";
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

    // 3. Tìm kiếm nâng cao
    public List<FigureDTO> timKiemNangCao(String ten, String loai, Double min, Double max, String kt, Integer maNCC) {
        List<FigureDTO> list = new ArrayList<>();
        // [FIX]: Sửa 'n.id_ncc' thành 'n.ma_ncc'
        StringBuilder sql = new StringBuilder("SELECT f.*, n.ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.ma_ncc WHERE 1=1");

        if (ten != null && !ten.isEmpty()) sql.append(" AND f.ten LIKE '%").append(ten).append("%'");
        if (loai != null) sql.append(" AND f.loai = '").append(loai).append("'");
        if (min != null) sql.append(" AND f.gia >= ").append(min);
        if (max != null) sql.append(" AND f.gia <= ").append(max);
        if (kt != null) sql.append(" AND f.kich_thuoc = '").append(kt).append("'");
        
        // Logic lọc Nhà cung cấp
        if (maNCC != null && maNCC > 0) {
            sql.append(" AND f.ma_ncc = ").append(maNCC);
        }
        
        // Sắp xếp
        sql.append(" ORDER BY f.id ASC");

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

    // 4. Hàm map dữ liệu (An toàn hơn)
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
        
        // Lấy tên NCC (Xử lý ngoại lệ nếu cột không tồn tại hoặc null)
        try {
            String tenNCC = rs.getString("ten_ncc");
            f.setTenNCC(tenNCC != null ? tenNCC : "Chưa rõ");
        } catch (SQLException e) {
            f.setTenNCC("Chưa rõ"); 
        }
        return f;
    }
    
    // 5. Cập nhật số lượng (Cho nhập kho/thanh toán)
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
    
    // 6. Xóa sản phẩm (Chỉ xóa được nếu chưa có giao dịch)
    public boolean xoaSanPham(int id) {
        try (Connection conn = new DBConnection().getConnect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM figure WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lỗi do dính khóa ngoại (đã bán/nhập)
            return false;
        }
    }
}