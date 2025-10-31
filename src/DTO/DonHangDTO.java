package DTO;

import java.sql.Date;
import java.util.List;

public class DonHangDTO {
    private int maDonHang;
    private int maKhachHang;
    private int maNhanVien;
    private Date ngayDat;
    private double tongTien;
    private List<GioHangItemDTO> gioHang;

    public DonHangDTO(int maDonHang, int maKhachHang, int maNhanVien, List<GioHangItemDTO> gioHang, double tongTien) {
        this.maDonHang = maDonHang;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.gioHang = gioHang;
        this.tongTien = tongTien;
        this.ngayDat = new Date(System.currentTimeMillis());
    }

    public DonHangDTO(int maKhachHang, int maNhanVien, List<GioHangItemDTO> gioHang, double tongTien) {
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.gioHang = gioHang;
        this.tongTien = tongTien;
        this.ngayDat = new Date(System.currentTimeMillis());
    }

    public int getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(int maDonHang) {
        this.maDonHang = maDonHang;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public Date getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Date ngayDat) {
        this.ngayDat = ngayDat;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public List<GioHangItemDTO> getGioHang() {
        return gioHang;
    }

    public void setGioHang(List<GioHangItemDTO> gioHang) {
        this.gioHang = gioHang;
    }

    public double tinhTongTien() {
        double tong = 0;
        for (GioHangItemDTO item : gioHang) {
            tong += item.getThanhTien();
        }
        return tong;
    }
}