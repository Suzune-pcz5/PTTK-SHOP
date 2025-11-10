// BLL/DonHangBLL.java
package BLL;

import DAL.DonHangDAL;
import DAL.FigureDAL;
import DTO.DonHangDTO;
import DTO.GioHangItemDTO;
import java.util.ArrayList;
import java.util.List;

public class DonHangBLL {
    private DonHangDAL donHangDAL = new DonHangDAL();
    private FigureDAL figureDAL = new FigureDAL();

    public DonHangDTO thanhToan(int maNhanVien, String phuongThucTT, String maKhuyenMai, List<GioHangItemDTO> gioHang) {
    if (gioHang == null || gioHang.isEmpty()) return null;

    double tongTien = gioHang.stream().mapToDouble(GioHangItemDTO::getThanhTien).sum();
    double phanTramGiam = (maKhuyenMai != null) ? donHangDAL.kiemTraMaKhuyenMai(maKhuyenMai) : 0;
    double tongSauGiam = tongTien * (1 - phanTramGiam / 100);

    List<GioHangItemDTO> items = new ArrayList<>();
    for (GioHangItemDTO item : gioHang) {
        items.add(new GioHangItemDTO(item.getFigure(), item.getSoLuong(), item.getGiaBan()));
    }

    DonHangDTO donHang = new DonHangDTO(maNhanVien, items, tongSauGiam, phuongThucTT, maKhuyenMai);
    donHang.setNgayDat(new java.sql.Date(System.currentTimeMillis()));

    if (!donHangDAL.luuDonHang(donHang)) return null;

    return donHang;
}

    public double kiemTraMaKhuyenMai(String ma) {
        return donHangDAL.kiemTraMaKhuyenMai(ma);
    }

    public List<DonHangDTO> layTatCaDonHang() {
        return (List<DonHangDTO>) donHangDAL.layTatCa();
    }

    public boolean huyDonHang(int maDonHang) {
        return donHangDAL.huyDonHang(maDonHang);
    }
}