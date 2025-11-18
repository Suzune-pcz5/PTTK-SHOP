package DTO;

import java.sql.Date;
import java.util.List;

public class DonHangDTO {
    private int maDonHang;
    private int maNhanVien;
    private Date ngayDat;
    private double tongTien;
    private String phuongThucTT;
    private String maKhuyenMai;
    private String trangThai;
    private List<GioHangItemDTO> gioHang;

    // Constructor khi TẠO MỚI đơn hàng
    public DonHangDTO(int maNhanVien, List<GioHangItemDTO> gioHang,
                      double tongTien, String phuongThucTT, String maKhuyenMai) {
        this.maNhanVien = maNhanVien;
        this.gioHang = gioHang;
        this.tongTien = tongTien;
        this.ngayDat = new Date(System.currentTimeMillis());
        this.phuongThucTT = phuongThucTT;
        this.maKhuyenMai = maKhuyenMai;
        // [SỬA]: Mặc định là Tiếng Việt khớp với Database
        this.trangThai = "Đã thanh toán"; 
    }

    // Constructor khi LẤY từ Database
    public DonHangDTO(int maDonHang, int maNhanVien,
                      List<GioHangItemDTO> gioHang, double tongTien) {
        this.maDonHang = maDonHang;
        this.maNhanVien = maNhanVien;
        this.gioHang = gioHang;
        this.tongTien = tongTien;
    }

    // Getters & Setters
    public int getMaDonHang() { return maDonHang; }
    public void setMaDonHang(int maDonHang) { this.maDonHang = maDonHang; }

    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }

    public Date getNgayDat() { return ngayDat; }
    public void setNgayDat(Date ngayDat) { this.ngayDat = ngayDat; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getPhuongThucTT() { return phuongThucTT; }
    public void setPhuongThucTT(String phuongThucTT) { this.phuongThucTT = phuongThucTT; }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public List<GioHangItemDTO> getGioHang() { return gioHang; }
    public void setGioHang(List<GioHangItemDTO> gioHang) { this.gioHang = gioHang; }
}