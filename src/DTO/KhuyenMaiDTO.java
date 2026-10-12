package DTO;

import java.sql.Date;

public class KhuyenMaiDTO {
    private String ma;
    private double phanTramGiam;
    private Date hanDung;
    private String moTa; // Thêm trường mô tả

    public KhuyenMaiDTO() {
    }

    public KhuyenMaiDTO(String ma, double phanTramGiam, Date hanDung, String moTa) {
        this.ma = ma;
        this.phanTramGiam = phanTramGiam;
        this.hanDung = hanDung;
        this.moTa = moTa;
    }

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public Date getHanDung() { return hanDung; }
    public void setHanDung(Date hanDung) { this.hanDung = hanDung; }
    
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    
    @Override
    public String toString() {
        return ma + " - " + (int)phanTramGiam + "%";
    }
}