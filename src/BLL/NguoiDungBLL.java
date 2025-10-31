package BLL;

import DAL.NguoiDungDAL;
import DTO.NguoiDungDTO;

import java.util.List;

public class NguoiDungBLL {
    private NguoiDungDAL dal = new NguoiDungDAL();

    public boolean dangKy(NguoiDungDTO nguoiDung) {
        if (dal.kiemTraTenDangNhap(nguoiDung.getTenDangNhap())) {
            return false; // Tên đăng nhập đã tồn tại
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
}