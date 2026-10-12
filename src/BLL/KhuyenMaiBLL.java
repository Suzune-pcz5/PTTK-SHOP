package BLL;

import DAL.KhuyenMaiDAL;
import DTO.KhuyenMaiDTO;
import javax.swing.table.DefaultTableModel;

public class KhuyenMaiBLL {
    private KhuyenMaiDAL dal = new KhuyenMaiDAL();

    // Hàm load dữ liệu (Gọi DAL load vào model)
    public void loadData(DefaultTableModel model) {
        dal.loadData(model);
    }

    // Hàm Thêm (Chuyển đổi DTO -> tham số DAL)
    public boolean themKhuyenMai(KhuyenMaiDTO km) {
        // Chuyển Date sang String yyyy-MM-dd
        String hanDungStr = (km.getHanDung() != null) ? km.getHanDung().toString() : "";
        return dal.them(km.getMa(), (int)km.getPhanTramGiam(), hanDungStr, km.getMoTa());
    }

    // Hàm Sửa
    public boolean suaKhuyenMai(KhuyenMaiDTO km) {
        String hanDungStr = (km.getHanDung() != null) ? km.getHanDung().toString() : "";
        return dal.sua(km.getMa(), (int)km.getPhanTramGiam(), hanDungStr, km.getMoTa());
    }

    // Hàm Xóa
    public boolean xoaKhuyenMai(String ma) {
        return dal.xoa(ma);
    }
}