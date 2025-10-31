package DTO;

public class GioHangItemDTO {
    private FigureDTO figure;
    private int soLuong;

    public GioHangItemDTO(FigureDTO figure, int soLuong, double aDouble) {
        this.figure = figure;
        this.soLuong = soLuong;
    }

    public FigureDTO getFigure() {
        return figure;
    }

    public void setFigure(FigureDTO figure) {
        this.figure = figure;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return figure.getGia() * soLuong;
    }

    public int getFigureId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}