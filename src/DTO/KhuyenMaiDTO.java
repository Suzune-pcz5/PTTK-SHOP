package DTO;

import java.util.Date;

public class KhuyenMaiDTO {
    private String ma;
    private double phanTramGiam;
    private Date hanDung;

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public Date getHanDung() { return hanDung; }
    public void setHanDung(Date hanDung) { this.hanDung = hanDung; }
}