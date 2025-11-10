// BLL/GioHangBLL.java
package BLL;

import DTO.FigureDTO;
import DTO.GioHangItemDTO;
import DAL.FigureDAL;
import java.util.ArrayList;
import java.util.List;

public class GioHangBLL {
    private FigureDAL figureDAL = new FigureDAL();
    private List<GioHangItemDTO> gioHang = new ArrayList<>();

    public boolean themVaoGio(int figureId, int soLuong) {
        FigureDTO gb = figureDAL.timTheoId(figureId);
        if (gb != null && gb.getSoLuong() >= soLuong) {
            gioHang.add(new GioHangItemDTO(gb, soLuong, gb.getGia()));
            figureDAL.capNhatSoLuong(figureId, soLuong); // Trừ kho tạm
            return true;
        }
        return false;
    }

    public boolean xoaKhoiGio(int figureId) {
        for (GioHangItemDTO item : new ArrayList<>(gioHang)) {
            if (item.getFigure().getId() == figureId) {
                figureDAL.capNhatSoLuong(figureId, -item.getSoLuong()); // Trả lại kho
                gioHang.remove(item);
                return true;
            }
        }
        return false;
    }

    public double tinhTongTien() {
        return gioHang.stream().mapToDouble(GioHangItemDTO::getThanhTien).sum();
    }

    public List<GioHangItemDTO> getGioHang() {
        return new ArrayList<>(gioHang);
    }

    public void xoaToanBoGio() {
        for (GioHangItemDTO item : new ArrayList<>(gioHang)) {
            figureDAL.capNhatSoLuong(item.getFigure().getId(), -item.getSoLuong());
        }
        gioHang.clear();
    }
}