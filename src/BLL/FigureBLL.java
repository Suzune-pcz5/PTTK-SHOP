// src/BLL/FigureBLL.java
package BLL;

import DTO.FigureDTO;
import DTO.DonHangDTO;
import DTO.GioHangItemDTO;
import DAL.FigureDAL;
import Database.DBConnection;  // ĐÃ THÊM

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FigureBLL {
    private FigureDAL dal = new FigureDAL();
    private GioHangBLL gioHangBLL = new GioHangBLL();
    private DonHangBLL donHangBLL = new DonHangBLL();

    public List<FigureDTO> layTatCa() {
        return dal.layTatCa();
    }

    public List<FigureDTO> timKiemNangCao(String ten, String loai, Double minGia, Double maxGia, String kichThuoc) {
        List<FigureDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM figure WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (ten != null && !ten.isEmpty()) {
            sql += " AND LOWER(ten) LIKE ?";
            params.add("%" + ten.toLowerCase() + "%");
        }
        if (loai != null && !loai.isEmpty()) {
            sql += " AND loai = ?";
            params.add(loai);
        }
        if (minGia != null) {
            sql += " AND gia >= ?";
            params.add(minGia);
        }
        if (maxGia != null) {
            sql += " AND gia <= ?";
            params.add(maxGia);
        }
        if (kichThuoc != null && !kichThuoc.isEmpty()) {
            sql += " AND kichThuoc = ?";
            params.add(kichThuoc);
        }

        DBConnection db = new DBConnection();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = db.getConnect();  // DÙNG DBConnection CỦA BẠN
            if (conn == null) {
                System.err.println("Không thể kết nối database!");
                return list;
            }

            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                FigureDTO f = new FigureDTO();
                f.setId(rs.getInt("id"));
                f.setTen(rs.getString("ten"));
                f.setLoai(rs.getString("loai"));
                f.setGia(rs.getDouble("gia"));
                f.setKichThuoc(rs.getString("kich_thuoc"));
                f.setSoLuong(rs.getInt("so_luong"));
                f.setMoTa(rs.getString("mo_ta"));
                list.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // ĐÓNG TÀI NGUYÊN
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            db.closeConnect();  // DÙNG PHƯƠNG THỨC CỦA BẠN
        }
        return list;
    }

    // ... các method khác giữ nguyên
    public boolean themVaoGio(int figureId, int soLuong) {
        return gioHangBLL.themVaoGio(figureId, soLuong);
    }

    public boolean xoaKhoiGio(int figureId) {
        return gioHangBLL.xoaKhoiGio(figureId);
    }

    public double tinhTongTien() {
        return gioHangBLL.tinhTongTien();
    }

    public List<GioHangItemDTO> getGioHang() {
        return gioHangBLL.getGioHang();
    }

    public void xoaToanBoGio() {
        gioHangBLL.xoaToanBoGio();
    }

    public double kiemTraMaKhuyenMai(String ma) {
        return donHangBLL.kiemTraMaKhuyenMai(ma);
    }

    public DonHangDTO thanhToan(int maNhanVien, String phuongThucTT, String maKhuyenMai) {
        List<GioHangItemDTO> gioHang = gioHangBLL.getGioHang();
        if (gioHang.isEmpty()) return null;
        DonHangDTO donHang = donHangBLL.thanhToan(maNhanVien, phuongThucTT, maKhuyenMai, new ArrayList<>(gioHang));
        if (donHang != null) {
            gioHangBLL.xoaToanBoGio();
        }
        return donHang;
    }

    public FigureDTO timTheoId(int id) {
        return dal.timTheoId(id);
    }
}