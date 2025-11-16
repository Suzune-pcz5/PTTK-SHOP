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
            for(GioHangItemDTO item : gioHang) {
                    if(item.getFigureId() == figureId) {
                        // Nếu đã có, chỉ cập nhật số lượng
                        item.setSoLuong(item.getSoLuong() + soLuong);
                        return true;
                    }
                }
            gioHang.add(new GioHangItemDTO(gb, soLuong, gb.getGia()));
            return true;
            }
        return false;
    }

    public boolean xoaKhoiGio(int figureId) {
        for (GioHangItemDTO item : new ArrayList<>(gioHang)) {
            if (item.getFigure().getId() == figureId) {
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
        gioHang.clear();
    }
}