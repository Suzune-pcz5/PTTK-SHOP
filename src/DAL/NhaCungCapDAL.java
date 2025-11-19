package DAL;

import DTO.NhaCungCapDTO;
import Database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAL {
    private DBConnection db = new DBConnection();

    public List<NhaCungCapDTO> layDanhSachNCC() {
        List<NhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap WHERE trang_thai = 'Hợp tác'"; // Chỉ lấy đối tác đang hoạt động

        try (Connection conn = db.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhaCungCapDTO ncc = new NhaCungCapDTO(
                    rs.getInt("ma_ncc"),
                    rs.getString("ten_ncc"),
                    rs.getString("dia_chi"),
                    rs.getString("so_dien_thoai"),
                    rs.getString("email")
                );
                list.add(ncc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}