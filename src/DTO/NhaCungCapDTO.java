package DTO;

public class NhaCungCapDTO {
    private int maNCC;
    private String tenNCC;
    private String diaChi;
    private String sdt;
    private String email;
    private String trangThai; // 'Hợp tác' hoặc 'Ngừng'

    public NhaCungCapDTO() {
    }

    public NhaCungCapDTO(int maNCC, String tenNCC, String diaChi, String sdt, String email, String trangThai) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.trangThai = trangThai;
    }

    // Getter & Setter
    public int getMaNCC() { return maNCC; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    // [QUAN TRỌNG] Hàm này giúp JComboBox hiển thị Tên thay vì mã hash
    @Override
    public String toString() {
        return this.tenNCC; 
    }
}