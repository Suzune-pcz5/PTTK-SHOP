package DTO;

public class NguoiDungDTO {
    private int maNguoiDung;
    private String email;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; // Chỉ "Admin" hoặc "NhanVien"

    public NguoiDungDTO() {}

    public NguoiDungDTO(int maNguoiDung, String email, String tenDangNhap, String matKhau, String vaiTro) {
        this.maNguoiDung = maNguoiDung;
        this.email = email;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        setVaiTro(vaiTro); // Validate
    }
        // Thêm vào class NguoiDungDTO
    public NguoiDungDTO(int maNguoiDung, String tenDangNhap, String matKhau, String vaiTro) {
        this.maNguoiDung = maNguoiDung;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
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
    public void setVaiTro(String vaiTro) {
        if (!"Admin".equals(vaiTro) && !"NhanVien".equals(vaiTro)) {
            throw new IllegalArgumentException("Vai trò chỉ có thể là 'Admin' hoặc 'NhanVien'");
        }
        this.vaiTro = vaiTro;
    }
}