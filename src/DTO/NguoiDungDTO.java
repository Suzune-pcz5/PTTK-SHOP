package DTO;

public class NguoiDungDTO {
    private int maNguoiDung;
    private String email;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; 
    // [THÊM]: Thuộc tính trạng thái
    private String trangThai; 

    public NguoiDungDTO() {}

    // Constructor đầy đủ
    public NguoiDungDTO(int maNguoiDung, String email, String tenDangNhap, String matKhau, String vaiTro, String trangThai) {
        this.maNguoiDung = maNguoiDung;
        this.email = email;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }
    
    // Constructor đơn giản (cho đăng ký)
    public NguoiDungDTO(int maNguoiDung, String tenDangNhap, String matKhau, String vaiTro) {
        this.maNguoiDung = maNguoiDung;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = "Mở"; // Mặc định khi tạo mới là Mở
    }

    // Getters & Setters
    public int getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(int maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    // [THÊM]: Getter/Setter cho trạng thái
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}