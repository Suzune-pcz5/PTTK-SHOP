package BLL;

import DAL.NhaCungCapDAL;
import DTO.NhaCungCapDTO;
import java.util.List;

public class NhaCungCapBLL {
    // Khai báo DAL
    private NhaCungCapDAL dal = new NhaCungCapDAL();

    // 1. Lấy danh sách NCC đang hợp tác (Dùng cho ComboBox nhập kho)
    public List<NhaCungCapDTO> getListNhaCungCap() {
        return dal.layDanhSachNCC(); 
    }

    // 2. Lấy tất cả NCC (Dùng cho bảng Quản lý NCC)
    public List<NhaCungCapDTO> layDanhSachTatCa() {
        // Nếu DAL chưa có hàm lấy tất cả, tạm thời dùng hàm lấy Hợp tác
        // Hoặc bạn cần viết thêm hàm layTatCa() bên DAL
        return dal.layDanhSachNCC(); 
    }

    // 3. Thêm NCC mới
    public boolean themNhaCungCap(NhaCungCapDTO ncc) {
        // Bạn nên chuyển logic SQL này sang DAL nếu muốn chuẩn 100%
        // Nhưng tạm thời để đây cũng được nếu DAL chưa có hàm insert
        return dal.themNhaCungCap(ncc); 
    }

    // 4. Sửa thông tin NCC
    public boolean suaNhaCungCap(NhaCungCapDTO ncc) {
        return dal.suaNhaCungCap(ncc);
    }
    
    // 5. Đổi trạng thái (Khóa/Mở)
    public boolean doiTrangThai(int maNCC, String trangThaiMoi) {
        return dal.doiTrangThai(maNCC, trangThaiMoi);
    }
}