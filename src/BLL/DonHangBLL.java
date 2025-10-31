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

    public DonHangDTO thanhToan(int maKhachHang, int maNhanVien, double phanTramGiam, List<GioHangItemDTO> gioHang) {
        if (gioHang == null || gioHang.isEmpty()) return null;

        double tongTien = gioHang.stream().mapToDouble(GioHangItemDTO::getThanhTien).sum();
        double tongSauGiam = tongTien * (1 - phanTramGiam / 100);

        List<GioHangItemDTO> items = new ArrayList<>();
        for (GioHangItemDTO item : gioHang) {
            items.add(new GioHangItemDTO(item.getFigure(), item.getSoLuong(), item.getThanhTien()));
        }

        DonHangDTO donHang = new DonHangDTO(0, maKhachHang, maNhanVien, items, tongSauGiam);
        donHang.setNgayDat(new java.sql.Date(System.currentTimeMillis()));

        if (!donHangDAL.luuDonHang(donHang)) return null;

        // CẬP NHẬT KHO
        for (GioHangItemDTO item : gioHang) {
            figureDAL.capNhatSoLuong(item.getFigure().getId(), item.getSoLuong());
        }

        return donHang;
    }

    public double kiemTraMaKhuyenMai(String ma) {
        return donHangDAL.kiemTraMaKhuyenMai(ma);
    }

    public List<DonHangDTO> layTatCaDonHang() {
        return donHangDAL.layTatCa();
    }
}