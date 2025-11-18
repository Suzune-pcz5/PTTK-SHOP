// BLL/NguoiDungBLL.java
package BLL;

import DAL.NguoiDungDAL;
import DTO.NguoiDungDTO;
import java.util.List;
import javax.swing.JOptionPane; // Thêm để thông báo lỗi (nếu cần thiết)

public class NguoiDungBLL {
    private NguoiDungDAL dal = new NguoiDungDAL();

    public boolean dangKy(NguoiDungDTO nguoiDung) {
        if (dal.kiemTraTenDangNhap(nguoiDung.getTenDangNhap())) {
            return false;
        }
        return dal.luuNguoiDung(nguoiDung);
    }

    public NguoiDungDTO dangNhap(String tenDangNhap, String matKhau) {
        NguoiDungDTO nd = dal.timNguoiDung(tenDangNhap, matKhau);
        
        // [SỬA]: Kiểm tra trạng thái
        if (nd != null) {
            if ("Tắt".equals(nd.getTrangThai()) || "Locked".equalsIgnoreCase(nd.getTrangThai())) {
                // Nếu trạng thái là Tắt/Locked -> Trả về null coi như đăng nhập thất bại
                // (Hoặc bạn có thể throw exception để UI hứng và báo lỗi cụ thể hơn)
                return null; 
            }
        }
        return nd;
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