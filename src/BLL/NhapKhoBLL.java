// BLL/NhapKhoBLL.java
package BLL;

import DAL.NhapKhoDAL;
import DTO.NhapKhoDTO;
import java.sql.Date;
import java.util.List;

public class NhapKhoBLL {
    private NhapKhoDAL dal = new NhapKhoDAL();

    // === 1. NHẬP HÀNG ===
    public boolean nhapHang(int figureId, int soLuong, int maNhanVien) {
        if (soLuong <= 0) return false;

        NhapKhoDTO nhapKho = new NhapKhoDTO(
            figureId,
            soLuong,
            new Date(System.currentTimeMillis()),
            maNhanVien
        );
        return dal.luuPhieuNhap(nhapKho);
    }

    // === 2. LẤY TẤT CẢ PHIẾU NHẬP ===
    public List<NhapKhoDTO> layTatCa() {
        return dal.layTatCa();
    }

    // === 3. LẤY THEO NHÂN VIÊN ===
    public List<NhapKhoDTO> layTheoNhanVien(int maNhanVien) {
        return dal.layTheoNhanVien(maNhanVien);
    }
}