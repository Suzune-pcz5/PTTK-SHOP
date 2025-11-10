// FORM/MainUI.java
package FORM;

import BLL.FigureBLL;
import BLL.NguoiDungBLL;
import DTO.FigureDTO;
import DTO.GioHangItemDTO;
import DTO.NguoiDungDTO;
import DTO.DonHangDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

public class MainUI extends JFrame {
    private JTable tblDanhSach, tblGioHang;
    private JTextField txtSoLuong, txtMaKM, txtTenDangNhap, txtMatKhau, txtTenDangKy, txtMatKhauDangKy;
    private JTextField txtMinGia, txtMaxGia;
    private JComboBox<String> cbLoai, cbKichThuoc;
    private JLabel lblTongTien;
    private JTextArea txtKetQua;
    private FigureBLL bll = new FigureBLL();
    private NguoiDungBLL nguoiDungBLL = new NguoiDungBLL();
    private double phanTramGiam = 0;
    private NguoiDungDTO nguoiDungHienTai = null;
    private JDialog loginDialog;
    // THÊM FIELD MỚI
    private JLabel lblTenNguoiDung; // LABEL HIỂN THỊ TÊN

    public MainUI(NguoiDungDTO nd) {
        this.nguoiDungHienTai = nd;
        initComponents();
        hienThiTenNguoiDung(); // HIỂN THỊ TÊN
    }

    private void initComponents() {
        setTitle("Cửa Hàng Figure");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(createPanelTimKiem(), BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(createPanelDanhSach(), BorderLayout.WEST);
        add(createPanelChucNang(), BorderLayout.CENTER);
        add(createPanelKetQua(), BorderLayout.SOUTH);

        taiDanhSach();
        capNhatGioHang();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // THÊM HÀM HIỂN THỊ TÊN
    private void hienThiTenNguoiDung() {
        lblTenNguoiDung = new JLabel();
        lblTenNguoiDung.setFont(new Font("Arial", Font.BOLD, 14));
        lblTenNguoiDung.setForeground(Color.BLUE);

        if (nguoiDungHienTai != null) {
            lblTenNguoiDung.setText("Xin chào, " + nguoiDungHienTai.getTenDangNhap());
        } else {
            lblTenNguoiDung.setText("Khách");
        }

        JPanel panelTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTopRight.add(lblTenNguoiDung);

        // THÊM VÀO HEADER (PHÍA TRÊN PANEL TÌM KIẾM)
        ((JPanel) getContentPane().getComponent(0)).add(panelTopRight, BorderLayout.NORTH);
    }

    private JPanel createPanelTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Tìm Kiếm Figure"));
        panel.setBackground(new Color(245, 245, 220));

        panel.add(new JLabel("Loại:"));
        String[] loaiOptions = {"Tất cả", "Teddy", "Unicorn", "Panda", "Khác"};
        cbLoai = new JComboBox<>(loaiOptions);
        cbLoai.setPreferredSize(new Dimension(100, 25));
        panel.add(cbLoai);

        panel.add(new JLabel("Giá từ:"));
        txtMinGia = new JTextField(5);
        panel.add(txtMinGia);

        panel.add(new JLabel("đến:"));
        txtMaxGia = new JTextField(5);
        panel.add(txtMaxGia);

        panel.add(new JLabel("Kích thước:"));
        String[] kichThuocOptions = {"Tất cả", "Nhỏ", "Vừa", "Lớn"};
        cbKichThuoc = new JComboBox<>(kichThuocOptions);
        cbKichThuoc.setPreferredSize(new Dimension(100, 25));
        panel.add(cbKichThuoc);

        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(new Color(0, 191, 255));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.addActionListener(e -> timKiemNangCao());
        panel.add(btnTimKiem);

        return panel;
    }

    private void timKiemNangCao() {
        String loai = (String) cbLoai.getSelectedItem();
        if ("Tất cả".equals(loai)) loai = null;
        Double minGia = null;
        try {
            if (!txtMinGia.getText().isEmpty()) minGia = Double.parseDouble(txtMinGia.getText());
        } catch (NumberFormatException ignored) {}
        Double maxGia = null;
        try {
            if (!txtMaxGia.getText().isEmpty()) maxGia = Double.parseDouble(txtMaxGia.getText());
        } catch (NumberFormatException ignored) {}
        String kichThuoc = (String) cbKichThuoc.getSelectedItem();
        if ("Tất cả".equals(kichThuoc)) kichThuoc = null;
        List<FigureDTO> ketQua = bll.timKiemNangCao(loai, minGia, maxGia, kichThuoc);
        capNhatBangDanhSach(ketQua);
    }

    private JPanel createPanelDanhSach() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📋 Danh Sách Figure"));
        panel.setPreferredSize(new Dimension(400, 0));
        panel.setBackground(new Color(240, 255, 240));

        tblDanhSach = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblDanhSach);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPanelChucNang() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🛒 Giỏ Hàng"));
        panel.setBackground(new Color(255, 228, 225));

        tblGioHang = new JTable();
        JScrollPane scrollGioHang = new JScrollPane(tblGioHang);
        panel.add(scrollGioHang, BorderLayout.CENTER);

        JPanel panelTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongTien = new JLabel("Tổng tiền: 0 VND");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        panelTongTien.add(lblTongTien);
        panel.add(panelTongTien, BorderLayout.SOUTH);

        JPanel panelChucNang = new JPanel(new GridLayout(4, 1, 5, 5));
        panelChucNang.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnThemGio = new JButton("Thêm vào giỏ");
        btnThemGio.setBackground(new Color(50, 205, 50));
        btnThemGio.setForeground(Color.WHITE);
        btnThemGio.addActionListener(e -> themVaoGio());
        panelChucNang.add(btnThemGio);

        JButton btnXoaGio = new JButton("Xóa khỏi giỏ");
        btnXoaGio.setBackground(new Color(255, 69, 0));
        btnXoaGio.setForeground(Color.WHITE);
        btnXoaGio.addActionListener(e -> xoaKhoiGio());
        panelChucNang.add(btnXoaGio);

        JButton btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setBackground(new Color(0, 128, 0));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.addActionListener(e -> thanhToan());
        panelChucNang.add(btnThanhToan);

        panel.add(panelChucNang, BorderLayout.EAST);

        JPanel panelMaKM = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMaKM.add(new JLabel("Mã KM:"));
        txtMaKM = new JTextField(10);
        panelMaKM.add(txtMaKM);
        JButton btnApDung = new JButton("Áp dụng");
        btnApDung.addActionListener(e -> apDungMaKM());
        panelMaKM.add(btnApDung);
        panel.add(panelMaKM, BorderLayout.NORTH);

        return panel;
    }

    private void apDungMaKM() {
        String ma = txtMaKM.getText().trim();
        if (!ma.isEmpty()) {
            double giam = bll.kiemTraMaKhuyenMai(ma);
            if (giam > 0) {
                phanTramGiam = giam;
                JOptionPane.showMessageDialog(this, "Áp dụng thành công! Giảm " + giam + "%");
                capNhatTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Mã khuyến mãi không hợp lệ hoặc hết hạn.");
            }
        }
    }

    private JPanel createPanelKetQua() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📝 Kết Quả"));
        panel.setPreferredSize(new Dimension(0, 150));
        panel.setBackground(new Color(230, 230, 250));

        txtKetQua = new JTextArea();
        txtKetQua.setEditable(false);
        JScrollPane scrollKetQua = new JScrollPane(txtKetQua);
        panel.add(scrollKetQua, BorderLayout.CENTER);
        return panel;
    }

    private void taiDanhSach() {
        List<FigureDTO> danhSach = bll.layTatCa();
        capNhatBangDanhSach(danhSach);
    }

    private void capNhatBangDanhSach(List<FigureDTO> danhSach) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên", "Loại", "Giá", "Kích thước", "Số lượng", "Xem"}, 0);
        for (FigureDTO gb : danhSach) {
            model.addRow(new Object[]{gb.getId(), gb.getTen(), gb.getLoai(), gb.getGia(), gb.getKichThuoc(), gb.getSoLuong(), "Xem"});
        }
        tblDanhSach.setModel(model);
        TableColumn buttonColumn = tblDanhSach.getColumnModel().getColumn(6);
        buttonColumn.setCellRenderer(new ButtonRenderer());
        buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox(), danhSach));
    }

    private void hienThiChiTiet(FigureDTO gb) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(gb.getId()).append("\n");
        sb.append("Tên: ").append(gb.getTen()).append("\n");
        sb.append("Loại: ").append(gb.getLoai()).append("\n");
        sb.append("Giá: ").append(gb.getGia()).append(" VND\n");
        sb.append("Kích thước: ").append(gb.getKichThuoc()).append("\n");
        sb.append("Số lượng: ").append(gb.getSoLuong()).append("\n");
        sb.append("Mô tả: ").append(gb.getMoTa()).append("\n");
        txtKetQua.setText(sb.toString());
    }

    private void capNhatGioHang() {
        DefaultTableModel modelGio = new DefaultTableModel(new String[]{"ID", "Tên", "Số lượng", "Thành tiền"}, 0);
        for (GioHangItemDTO item : bll.getGioHang()) {
            modelGio.addRow(new Object[]{item.getFigure().getId(), item.getFigure().getTen(), item.getSoLuong(), item.getThanhTien()});
        }
        tblGioHang.setModel(modelGio);
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        double tong = bll.tinhTongTien();
        double tongSauGiam = tong * (1 - phanTramGiam / 100);
        lblTongTien.setText(String.format("Tổng tiền: %.2f VND (Giảm %.0f%%)", tongSauGiam, phanTramGiam));
    }

    private void themVaoGio() {
        int row = tblDanhSach.getSelectedRow();
        if (row >= 0) {
            int id = (int) tblDanhSach.getValueAt(row, 0);
            String input = JOptionPane.showInputDialog(this, "Nhập số lượng:");
            try {
                int soLuong = Integer.parseInt(input);
                if (bll.themVaoGio(id, soLuong)) {
                    capNhatGioHang();
                    taiDanhSach();
                } else {
                    JOptionPane.showMessageDialog(this, "Số lượng không đủ hoặc lỗi.");
                }
            } catch (NumberFormatException ignored) {}
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm.");
        }
    }

    private void xoaKhoiGio() {
        int row = tblGioHang.getSelectedRow();
        if (row >= 0) {
            int id = (int) tblGioHang.getValueAt(row, 0);
            if (bll.xoaKhoiGio(id)) {
                capNhatGioHang();
                taiDanhSach();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa khỏi giỏ.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ.");
        }
    }

    private void thanhToan() {
        if (nguoiDungHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để thanh toán.");
            return;
        }

        int maNhanVien = nguoiDungHienTai.getMaNguoiDung();

        String[] options = {"TienMat", "ChuyenKhoan", "The", "ViDienTu"};
        String phuongThuc = (String) JOptionPane.showInputDialog(
            this, "Chọn phương thức thanh toán:", "Thanh toán",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );

        if (phuongThuc == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn phương thức thanh toán.");
            return;
        }

        String maKM = txtMaKM.getText().trim();
        if (maKM.isEmpty()) maKM = null;

        DonHangDTO donHang = bll.thanhToan(maNhanVien, phuongThuc, maKM);

        if (donHang != null) {
            StringBuilder sb = new StringBuilder("ĐƠN HÀNG ĐÃ THANH TOÁN:\n");
            sb.append("Mã đơn: ").append(donHang.getMaDonHang()).append("\n");
            sb.append("Phương thức: ").append(donHang.getPhuongThucTT()).append("\n");
            sb.append("Tổng tiền: ").append(String.format("%.0f", donHang.getTongTien())).append(" VND\n");
            if (donHang.getMaKhuyenMai() != null) {
                sb.append("Mã KM: ").append(donHang.getMaKhuyenMai()).append("\n");
            }
            txtKetQua.setText(sb.toString());

            capNhatGioHang();
            taiDanhSach();
            phanTramGiam = 0;
            txtMaKM.setText("");
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại. Vui lòng kiểm tra lại.");
        }
    }

    private class ButtonRenderer implements TableCellRenderer {
        private final JButton button = new JButton("Xem");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private FigureDTO currentFigure;
        private boolean isPushed;
        private List<FigureDTO> dataList;

        public ButtonEditor(JCheckBox checkBox, List<FigureDTO> dataList) {
            super(checkBox);
            this.dataList = dataList;
            button = new JButton("Xem");
            button.setBackground(new Color(135, 206, 250));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isPushed = true;
                    if (currentFigure != null) {
                        hienThiChiTiet(currentFigure);
                    } else {
                        System.out.println("currentFigure is null");
                    }
                    fireEditingStopped();
                }
            });
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (row >= 0 && row < dataList.size()) {
                currentFigure = dataList.get(row);
            } else {
                currentFigure = null;
                System.out.println("Row index out of bounds: " + row);
            }
            isPushed = false;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return currentFigure;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            isPushed = false;
            super.cancelCellEditing();
        }
    }
}