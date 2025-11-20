package BLL;

import DTO.FigureDTO;
import DTO.DonHangDTO;
import DTO.GioHangItemDTO;
import DAL.FigureDAL;
// Lưu ý: Không import java.sql.* ở đây nữa vì BLL không được đụng vào SQL

import java.util.ArrayList;
import java.util.List;

public class FigureBLL {
    private FigureDAL dal = new FigureDAL();
    private GioHangBLL gioHangBLL = new GioHangBLL();
    private DonHangBLL donHangBLL = new DonHangBLL();

    public List<FigureDTO> layTatCa() {
        return dal.layTatCa();
    }

    // === ĐÂY LÀ HÀM GÂY LỖI CŨ, ĐÃ ĐƯỢC SỬA ĐỂ GỌI SANG DAL ===
    // Thêm tham số "Integer maNCC"
    public List<FigureDTO> timKiemNangCao(String ten, String loai, Double minGia, Double maxGia, String kichThuoc, Integer maNCC) {
        // Truyền maNCC xuống DAL
        return dal.timKiemNangCao(ten, loai, minGia, maxGia, kichThuoc, maNCC);
    }
    // =========================================================

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