// DTO/NhapKhoDTO.java
package DTO;

import java.sql.Date;

public class NhapKhoDTO {
    private int maNhap;
    private int figureId;
    private int soLuongNhap;
    private Date ngayNhap;
    private int maNhanVien;

    // Constructor khi tạo mới
    public NhapKhoDTO(int figureId, int soLuongNhap, Date ngayNhap, int maNhanVien) {
        this.figureId = figureId;
        this.soLuongNhap = soLuongNhap;
        this.ngayNhap = ngayNhap;
        this.maNhanVien = maNhanVien;
    }

    // Constructor đầy đủ (khi lấy từ DB)
    public NhapKhoDTO(int maNhap, int figureId, int soLuongNhap, Date ngayNhap, int maNhanVien) {
        this.maNhap = maNhap;
        this.figureId = figureId;
        this.soLuongNhap = soLuongNhap;
        this.ngayNhap = ngayNhap;
        this.maNhanVien = maNhanVien;
    }

    // Getters & Setters
    public int getMaNhap() { return maNhap; }
    public void setMaNhap(int maNhap) { this.maNhap = maNhap; }

    public int getFigureId() { return figureId; }
    public void setFigureId(int figureId) { this.figureId = figureId; }

    public int getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }

    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }

    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }
}