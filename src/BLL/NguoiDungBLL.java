// BLL/NguoiDungBLL.java
package BLL;

import DAL.NguoiDungDAL;
import DTO.NguoiDungDTO;
import java.util.List;

public class NguoiDungBLL {
    private NguoiDungDAL dal = new NguoiDungDAL();

    public boolean dangKy(NguoiDungDTO nguoiDung) {
        if (dal.kiemTraTenDangNhap(nguoiDung.getTenDangNhap())) {
            return false;
        }
        return dal.luuNguoiDung(nguoiDung);
    }

    public NguoiDungDTO dangNhap(String tenDangNhap, String matKhau) {
        return dal.timNguoiDung(tenDangNhap, matKhau);
    }

    public List<NguoiDungDTO> layTatCa() {
        return dal.layTatCa();
    }

    public boolean xoaNguoiDung(int maNguoiDung) {
        return dal.xoaNguoiDung(maNguoiDung);
    }

    // KIỂM TRA DANH TÍNH
    public boolean kiemTraDanhTinh(String tenDangNhap, String email) {
        if (tenDangNhap == null || email == null || 
            tenDangNhap.trim().isEmpty() || email.trim().isEmpty()) {
            return false;
        }
        return dal.kiemTraDanhTinh(tenDangNhap, email);
    }

    // ĐỔI MẬT KHẨU (chỉ cần tên + mk mới)
    public boolean doiMatKhau(String tenDangNhap, String matKhauMoi) {
        NguoiDungDTO nd = new NguoiDungDTO();
        nd.setTenDangNhap(tenDangNhap);
        nd.setMatKhau(matKhauMoi);
        return dal.capNhatMatKhau(nd);
    }
}