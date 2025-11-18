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

    // === HÀM PHỤ TRỢ: MAP DỮ LIỆU (Tránh lặp code) ===
    private FigureDTO mapRowToDTO(ResultSet rs) throws SQLException {
        FigureDTO f = new FigureDTO();
        f.setId(rs.getInt("id"));
        f.setTen(rs.getString("ten"));
        f.setLoai(rs.getString("loai"));
        f.setGia(rs.getInt("gia"));
        f.setKichThuoc(rs.getString("kich_thuoc"));
        f.setSoLuong(rs.getInt("so_luong"));
        f.setMoTa(rs.getString("mo_ta"));
        
        // Lấy hình ảnh (Quan trọng)
        String img = rs.getString("hinh_anh");
        f.setHinhAnh((img == null || img.trim().isEmpty()) ? "default.jpg" : img);
        return f;
    }

    // 1. LẤY TẤT CẢ
    public List<FigureDTO> layTatCa() {
        List<FigureDTO> list = new ArrayList<>();
        String sql = "SELECT id, ten, loai, gia, kich_thuoc, so_luong, mo_ta, hinh_anh FROM figure";
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRowToDTO(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. TÌM KIẾM NÂNG CAO (ĐÃ SỬA LOGIC)
    public List<FigureDTO> timKiemNangCao(String ten, String loai, Double minGia, Double maxGia, String kichThuoc) {
        List<FigureDTO> danhSach = new ArrayList<>();
        
        // SQL CƠ BẢN: Lấy đủ cột (bao gồm hinh_anh)
        StringBuilder sql = new StringBuilder("SELECT id, ten, loai, gia, kich_thuoc, so_luong, mo_ta, hinh_anh FROM figure WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // 1. Lọc theo Tên
        if (ten != null && !ten.trim().isEmpty()) {
            sql.append(" AND LOWER(ten) LIKE ?");
            params.add("%" + ten.toLowerCase().trim() + "%");
        }
        
        // 2. Lọc theo Loại (Chỉ lọc nếu khác "Tất cả")
        if (loai != null && !loai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(loai)) {
            sql.append(" AND loai = ?");
            params.add(loai);
        }
        
        // 3. Lọc theo Giá
        if (minGia != null) {
            sql.append(" AND gia >= ?");
            params.add(minGia);
        }
        if (maxGia != null) {
            sql.append(" AND gia <= ?");
            params.add(maxGia);
        }
        
        // 4. Lọc theo Kích thước (QUAN TRỌNG: Dùng LIKE để tìm chuỗi con)
        if (kichThuoc != null && !kichThuoc.trim().isEmpty() && !"Tất cả".equals(kichThuoc)) {
            // SỬA DÒNG NÀY: Phải là "kich_thuoc" (giống tên cột trong Database)
            // Không được viết là "kichThuoc"
            sql.append(" AND kich_thuoc LIKE ?"); 
            params.add("%" + kichThuoc.trim() + "%");
        }

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Gán tham số theo thứ tự
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapRowToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi timKiemNangCao: " + e.getMessage());
            e.printStackTrace(); // In lỗi ra để debug
        }
        return danhSach;
    }

    // 3. CÁC HÀM KHÁC (Giữ nguyên logic)
    public FigureDTO timTheoId(int id) {
        String sql = "SELECT id, ten, loai, gia, kich_thuoc, so_luong, mo_ta, hinh_anh FROM figure WHERE id = ?";
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToDTO(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
    public boolean capNhatSoLuong(int id, int delta) {
        // Logic cập nhật số lượng giữ nguyên...
        // (Bạn có thể copy lại đoạn code cập nhật số lượng từ file cũ nếu cần, hoặc tôi viết ngắn gọn ở đây)
        String sql = delta >= 0 
            ? "UPDATE figure SET so_luong = so_luong - ? WHERE id = ? AND so_luong >= ?" 
            : "UPDATE figure SET so_luong = so_luong + ? WHERE id = ?";
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.abs(delta));
            ps.setInt(2, id);
            if(delta >= 0) ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean kiemTraSoLuong(int id, int sl) {
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("SELECT so_luong FROM figure WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) >= sl;
        } catch (Exception e) { return false; }
    }
}