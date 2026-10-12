package DAL;

import Database.DBConnection;
import java.sql.*;

public class PhieuDieuChinhDAL {
    private DBConnection db = new DBConnection();

    // Lưu phiếu điều chỉnh và tự động cập nhật kho (nhờ Trigger)
    public boolean luuPhieuDieuChinh(int figureId, int slTruoc, int slThucTe, int chenhLech, int maNV, String lyDo) {
        String sql = "INSERT INTO phieu_dieuchinh (figureId, so_luong_truoc, so_luong_thuc_te, chenh_lech, ma_nhan_vien, ly_do, ngay_dieu_chinh) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, figureId);
            ps.setInt(2, slTruoc);
            ps.setInt(3, slThucTe);
            ps.setInt(4, chenhLech);
            ps.setInt(5, maNV);
            ps.setString(6, lyDo);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}