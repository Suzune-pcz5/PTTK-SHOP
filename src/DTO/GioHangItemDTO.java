// DTO/GioHangItemDTO.java
package DTO;

public class GioHangItemDTO {
    private FigureDTO figure;
    private int soLuong;
    private double giaBan;   // giá tại thời điểm mua

    public GioHangItemDTO(FigureDTO figure, int soLuong, double giaBan) {
        this.figure = figure;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
    }

    public FigureDTO getFigure() { return figure; }
    public void setFigure(FigureDTO figure) { this.figure = figure; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public double getThanhTien() { return giaBan * soLuong; }

    public int getFigureId() { return figure.getId(); }
}