package DTO;

public class FigureDTO {
    private int id;
    private String ten;
    private String loai;
    private double gia;
    private String kichThuoc;
    private int soLuong;
    private String moTa;
    private String hinhAnh;
    private int maNCC;
    private String tenNCC;

    // Constructor
    public FigureDTO() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }
    public String getKichThuoc() { return kichThuoc; }
    public void setKichThuoc(String kichThuoc) { this.kichThuoc = kichThuoc; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getHinhAnh() { 
        return hinhAnh; 
    }
    public void setHinhAnh(String hinhAnh) { 
        this.hinhAnh = hinhAnh; 
    }
    // Getter & Setter cho 2 thuộc tính mới
    public int getMaNCC() { return maNCC; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
}