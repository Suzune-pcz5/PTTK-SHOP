// BLL/FigureBLL.java
package BLL;

import DTO.FigureDTO;
import DTO.DonHangDTO;
import DTO.GioHangItemDTO;
import DAL.FigureDAL;
import java.util.ArrayList;
import java.util.List;

public class FigureBLL {
    private FigureDAL dal = new FigureDAL();
    private GioHangBLL gioHangBLL = new GioHangBLL();
    private DonHangBLL donHangBLL = new DonHangBLL();
    private double phanTramGiam = 0;

    public List<FigureDTO> layTatCa() {
        return dal.layTatCa(); // GỌI DAL, KHÔNG CÓ SQL
    }

    public List<FigureDTO> timKiemNangCao(String loai, Double minGia, Double maxGia, String kichThuoc) {
        if (minGia != null && maxGia != null && minGia > maxGia) {
            return new ArrayList<>();
        }
        return dal.timKiemNangCao(loai, minGia, maxGia, kichThuoc);
    }

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
        return donHangBLL.kiemTraMaKhuyenMai(ma); // GỌI DonHangDAL
    }

    public DonHangDTO thanhToan(int maKhachHang, int maNhanVien, double phanTramGiam) {
        List<GioHangItemDTO> gioHang = gioHangBLL.getGioHang();
        if (gioHang.isEmpty()) return null;

        DonHangDTO donHang = donHangBLL.thanhToan(maKhachHang, maNhanVien, phanTramGiam, new ArrayList<>(gioHang));
        if (donHang != null) {
            gioHangBLL.xoaToanBoGio();
            this.phanTramGiam = 0;
        }
        return donHang;
    }

    public FigureDTO timTheoId(int id) {
        return dal.timTheoId(id);
    }
}