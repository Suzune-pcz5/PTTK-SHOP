package DAL;

import Database.DBConnection;
import java.sql.*;
import java.util.*;

public class ThongKeDAL {
    private DBConnection db = new DBConnection();

    // 1. Thống kê Doanh thu (Tên hàm chuẩn: getDoanhThuTheoNgay)
    public Map<String, Long> getDoanhThuTheoNgay(String dateFrom, String dateTo) {
        Map<String, Long> map = new LinkedHashMap<>();
        String sql = "SELECT DATE(ngay_dat) as ngay, SUM(tong_tien) as doanh_thu FROM donhang " +
                     "WHERE trang_thai = 'Đã thanh toán' AND DATE(ngay_dat) BETWEEN ? AND ? GROUP BY DATE(ngay_dat)";
        
        try (Connection conn = db.getConnect(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dateFrom); 
            ps.setString(2, dateTo);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("ngay"), rs.getLong("doanh_thu"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return map;
    }

    // 2. Top 10 Sản phẩm bán chạy
    public List<Object[]> getTopBanChay() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT f.ten, SUM(c.so_luong) as sl_ban FROM chitiet_donhang c " +
                     "JOIN figure f ON c.figureId = f.id " +
                     "JOIN donhang d ON c.donhangId = d.ma_don_hang " +
                     "WHERE d.trang_thai = 'Đã thanh toán' " +
                     "GROUP BY f.id ORDER BY sl_ban DESC LIMIT 10";
        
        try (Connection conn = db.getConnect(); 
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("ten"), rs.getInt("sl_ban")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Cảnh báo tồn kho (Dưới 10)
    public List<Object[]> getCanhBaoTonKho() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, ten, so_luong FROM figure WHERE so_luong < 10 AND trang_thai = 'Mở'";
        
        try (Connection conn = db.getConnect(); 
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id"), rs.getString("ten"), rs.getInt("so_luong")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}