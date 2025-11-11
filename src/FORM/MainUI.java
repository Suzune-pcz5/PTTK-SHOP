// src/FORM/MainUI.java
package FORM;

import BLL.FigureBLL;
import DTO.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class MainUI extends JFrame {
    private JTable tblDanhSach, tblGioHang;
    private JTextField txtMinGia, txtMaxGia, txtMaKM, txtTenTimKiem;
    private JComboBox<String> cbLoai, cbKichThuoc;
    private JLabel lblTongTien, lblTenNguoiDung;
    private JTextArea txtKetQua;
    private FigureBLL bll = new FigureBLL();
    private double phanTramGiam = 0;
    private NguoiDungDTO nguoiDungHienTai = null;

    public MainUI(NguoiDungDTO nd) {
        this.nguoiDungHienTai = nd;
        hienThiTenNguoiDung();
        initComponents();
    }

    private void hienThiTenNguoiDung() {
        lblTenNguoiDung = new JLabel();
        lblTenNguoiDung.setFont(new Font("Arial", Font.BOLD, 18));
        lblTenNguoiDung.setForeground(Color.WHITE);
        lblTenNguoiDung.setText(nguoiDungHienTai != null
                ? nguoiDungHienTai.getTenDangNhap()
                : "Khách");
    }

    private void initComponents() {
        setTitle("MAHIRU. - Quản Lý Figure");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));

        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
        UIManager.put("ComboBox.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createSplitContent(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        taiDanhSach();
        capNhatGioHang();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel logo = new JLabel("MAHIRU.");
        logo.setFont(new Font("Arial", Font.BOLD, 32));
        logo.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.setOpaque(false);

        searchPanel.add(createLabelWhite("Tìm tên:"));
        txtTenTimKiem = new JTextField(15);
        searchPanel.add(txtTenTimKiem);

        searchPanel.add(createLabelWhite("Loại:"));
        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Gundam", "Anime", "Game", "Khác"});
        cbLoai.setPreferredSize(new Dimension(130, 38));
        searchPanel.add(cbLoai);

        searchPanel.add(createLabelWhite("Giá từ:"));
        txtMinGia = new JTextField(8);
        searchPanel.add(txtMinGia);
        searchPanel.add(createLabelWhite("đến:"));
        txtMaxGia = new JTextField(8);
        searchPanel.add(txtMaxGia);

        searchPanel.add(createLabelWhite("KT:"));
        cbKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/144", "1/100", "1/60", "Khác"});
        cbKichThuoc.setPreferredSize(new Dimension(130, 38));
        searchPanel.add(cbKichThuoc);

        JButton btnTimKiem = createRedButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemNangCao());
        searchPanel.add(btnTimKiem);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(lblTenNguoiDung);

        panel.add(logo, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private JSplitPane createSplitContent() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(), createRightPanel());
        split.setResizeWeight(0.65);
        split.setDividerSize(10);
        split.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        return split;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 20, 60)));

        JLabel title = new JLabel("DANH SÁCH FIGURE");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(220, 20, 60));
        title.setBorder(new EmptyBorder(15, 20, 10, 20));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 20, 60)));
        panel.add(title, BorderLayout.NORTH);

        tblDanhSach = new JTable();
        tblDanhSach.setRowHeight(45);
        tblDanhSach.setFont(new Font("Arial", Font.PLAIN, 14));
        tblDanhSach.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblDanhSach.getTableHeader().setBackground(new Color(60, 60, 60));
        tblDanhSach.getTableHeader().setForeground(Color.WHITE);
        tblDanhSach.setGridColor(new Color(200, 200, 200));
        tblDanhSach.setSelectionBackground(new Color(240, 240, 240));
        tblDanhSach.setSelectionForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(tblDanhSach);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("GIỎ HÀNG");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(220, 20, 60));
        title.setBorder(new EmptyBorder(15, 20, 10, 20));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 20, 60)));
        panel.add(title, BorderLayout.NORTH);

        tblGioHang = new JTable();
        tblGioHang.setRowHeight(50);
        tblGioHang.setFont(new Font("Arial", Font.PLAIN, 14));
        tblGioHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblGioHang.getTableHeader().setBackground(new Color(60, 60, 60));
        tblGioHang.getTableHeader().setForeground(Color.WHITE);
        tblGioHang.setGridColor(new Color(200, 200, 200));
        tblGioHang.setSelectionBackground(new Color(240, 240, 240));
        tblGioHang.setSelectionForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(tblGioHang);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        panel.add(scroll, BorderLayout.CENTER);

        // Footer: 1 hàng
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));
        footer.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        footer.add(new JLabel("Mã KM:"), gbc);
        gbc.gridx = 1;
        txtMaKM = new JTextField(12);
        footer.add(txtMaKM, gbc);

        gbc.gridx = 2;
        JButton apply = createOrangeButton("Áp dụng");
        apply.addActionListener(e -> apDungMaKM());
        footer.add(apply, gbc);

        gbc.gridx = 3; gbc.weightx = 1;
        footer.add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        lblTongTien = new JLabel("Tổng: 0 VND (Giảm 0%)");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(220, 20, 60));
        footer.add(lblTongTien, gbc);

        gbc.gridx = 5;
        JButton pay = createGreenButton("Thanh toán");
        pay.addActionListener(e -> thanhToan());
        footer.add(pay, gbc);

        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));
        panel.setPreferredSize(new Dimension(0, 180));
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel title = new JLabel("KẾT QUẢ / HÓA ĐƠN");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        txtKetQua = new JTextArea();
        txtKetQua.setEditable(false);
        txtKetQua.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtKetQua.setBackground(new Color(60, 60, 60));
        txtKetQua.setForeground(Color.WHITE);
        txtKetQua.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtKetQua.setLineWrap(true);
        txtKetQua.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtKetQua);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // === HỖ TRỢ ===
    private JLabel createLabelWhite(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        return lbl;
    }

    private JButton createRedButton(String text) {
        return createRoundedButton(text, new Color(220, 20, 60), Color.WHITE);
    }

    private JButton createGreenButton(String text) {
        return createRoundedButton(text, new Color(34, 139, 34), Color.WHITE);
    }

    private JButton createOrangeButton(String text) {
        return createRoundedButton(text, new Color(255, 140, 0), Color.WHITE);
    }

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // === XỬ LÝ DỮ LIỆU ===
   private void timKiemNangCao() {
    String ten = txtTenTimKiem.getText().trim();
    String loai = "Tất cả".equals(cbLoai.getSelectedItem()) ? null : (String) cbLoai.getSelectedItem();
    Double min = parseDouble(txtMinGia.getText());
    Double max = parseDouble(txtMaxGia.getText());
    String kt = "Tất cả".equals(cbKichThuoc.getSelectedItem()) ? null : (String) cbKichThuoc.getSelectedItem();
    
    // GỌI ĐÚNG: 4 tham số (ten, min, max, kt) hoặc 5 nếu có loai
    List<FigureDTO> ketQua = bll.timKiemNangCao(ten, loai, min, max, kt);
    capNhatBangDanhSach(ketQua);
}

    private Double parseDouble(String s) {
        try { return s.isEmpty() ? null : Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }

    private void taiDanhSach() {
        capNhatBangDanhSach(bll.layTatCa());
    }

    private void capNhatBangDanhSach(List<FigureDTO> list) {
        String[] cols = {"ID", "Tên Figure", "Loại", "Giá (VND)", "Kích thước", "Số lượng", "Chi tiết", "Thêm"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c >= 6; }
        };

        for (FigureDTO f : list) {
            model.addRow(new Object[]{
                    f.getId(), f.getTen(), f.getLoai(),
                    String.format("%,.0f", f.getGia()), f.getKichThuoc(), f.getSoLuong(),
                    "Chi tiết", "Thêm"
            });
        }

        tblDanhSach.setModel(model);

        // Cột Chi tiết
        TableColumn colDetail = tblDanhSach.getColumnModel().getColumn(6);
        colDetail.setCellRenderer(new DetailButtonRenderer());
        colDetail.setCellEditor(new DetailButtonEditor(new JCheckBox(), list));

        // Cột Thêm
        TableColumn colAdd = tblDanhSach.getColumnModel().getColumn(7);
        colAdd.setCellRenderer(new AddButtonRenderer());
        colAdd.setCellEditor(new AddButtonEditor(new JCheckBox(), list));

        // TỰ ĐỘNG CO CỘT
        tblDanhSach.getColumnModel().getColumn(0).setPreferredWidth(50);
        for (int i = 1; i < 6; i++) {
            TableColumn col = tblDanhSach.getColumnModel().getColumn(i);
            int width = 100;
            for (int row = 0; row < tblDanhSach.getRowCount(); row++) {
                Component comp = tblDanhSach.prepareRenderer(tblDanhSach.getCellRenderer(row, i), row, i);
                width = Math.max(comp.getPreferredSize().width + 15, width);
            }
            col.setPreferredWidth(Math.min(width, 200));
        }
    }

    private void capNhatGioHang() {
        String[] cols = {"ID", "Tên", "SL", "Thành tiền", "Xóa"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };

        double tong = 0;
        for (GioHangItemDTO i : bll.getGioHang()) {
            double tt = i.getThanhTien();
            tong += tt;
            model.addRow(new Object[]{i.getFigure().getId(), i.getFigure().getTen(), i.getSoLuong(),
                    String.format("%,.0f", tt), "Xóa"});
        }

        tblGioHang.setModel(model);

        TableColumn colDel = tblGioHang.getColumnModel().getColumn(4);
        colDel.setCellRenderer(new DeleteButtonRenderer());
        colDel.setCellEditor(new DeleteButtonEditor(new JCheckBox(), bll.getGioHang()));

        double sauGiam = tong * (1 - phanTramGiam / 100);
        lblTongTien.setText(String.format("Tổng: %, .0f VND (Giảm %.0f%%)", sauGiam, phanTramGiam));
    }

    private void apDungMaKM() {
        String ma = txtMaKM.getText().trim();
        if (ma.isEmpty()) return;
        double giam = bll.kiemTraMaKhuyenMai(ma);
        if (giam > 0) {
            phanTramGiam = giam;
            JOptionPane.showMessageDialog(this, "Áp dụng mã giảm " + giam + "%!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            capNhatGioHang();
        } else {
            JOptionPane.showMessageDialog(this, "Mã không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themVaoGio(int id, int soLuong) {
        if (bll.themVaoGio(id, soLuong)) {
            capNhatGioHang(); taiDanhSach();
        } else {
            JOptionPane.showMessageDialog(this, "Không đủ hàng!");
        }
    }

    private void xoaKhoiGio(int id) {
        bll.xoaKhoiGio(id);
        capNhatGioHang(); taiDanhSach();
    }

    private void thanhToan() {
        if (nguoiDungHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập!");
            return;
        }
        JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
    }

    // === BUTTON RENDERER & EDITOR ===
    private class DetailButtonRenderer extends JButton implements TableCellRenderer {
        public DetailButtonRenderer() {
            setText("Chi tiết");
            setBackground(new Color(70, 130, 180));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            return this;
        }
    }

    private class DetailButtonEditor extends DefaultCellEditor {
        private JButton button;
        private FigureDTO figure;
        private List<FigureDTO> list;

        public DetailButtonEditor(JCheckBox cb, List<FigureDTO> list) {
            super(cb);
            this.list = list;
            button = new JButton("Chi tiết");
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> { moChiTiet(figure); fireEditingStopped(); });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            figure = list.get(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() { return "Chi tiết"; }
    }

    private void moChiTiet(FigureDTO f) {
        JDialog dialog = new JDialog(MainUI.this, "Chi tiết: " + f.getTen(), true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Ảnh
        JLabel imgLabel = new JLabel();
        try {
            BufferedImage img = ImageIO.read(new File("resources/images/" + f.getId() + ".jpg"));
            Image scaled = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("Không có ảnh");
        }
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        // Thông tin
        JTextArea info = new JTextArea(String.format(
                "ID: %d\nTên: %s\nLoại: %s\nGiá: %, .0f VND\n" +
                "Kích thước: %s\nSố lượng: %d\nMô tả: %s",
                f.getId(), f.getTen(), f.getLoai(), f.getGia(),
                f.getKichThuoc(), f.getSoLuong(), f.getMoTa()
        ));
        info.setEditable(false);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setBackground(Color.WHITE);

        panel.add(imgLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(info), BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private class AddButtonRenderer extends JButton implements TableCellRenderer {
        public AddButtonRenderer() {
            setText("Thêm");
            setBackground(new Color(50, 205, 50));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            return this;
        }
    }

    private class AddButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int figureId;
        private List<FigureDTO> list;

        public AddButtonEditor(JCheckBox cb, List<FigureDTO> list) {
            super(cb);
            this.list = list;
            button = new JButton("Thêm");
            button.setBackground(new Color(50, 205, 50));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                String sl = JOptionPane.showInputDialog("Số lượng:");
                if (sl != null) {
                    try {
                        int soLuong = Integer.parseInt(sl);
                        themVaoGio(figureId, soLuong);
                    } catch (Exception ignored) {}
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            figureId = list.get(row).getId();
            return button;
        }

        @Override
        public Object getCellEditorValue() { return "Thêm"; }
    }

    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Xóa");
            setBackground(new Color(220, 20, 60));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            return this;
        }
    }

    private class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int figureId;
        private List<GioHangItemDTO> list;

        public DeleteButtonEditor(JCheckBox cb, List<GioHangItemDTO> list) {
            super(cb);
            this.list = list;
            button = new JButton("Xóa");
            button.setBackground(new Color(220, 20, 60));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> { xoaKhoiGio(figureId); fireEditingStopped(); });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            figureId = list.get(row).getFigure().getId();
            return button;
        }

        @Override
        public Object getCellEditorValue() { return "Xóa"; }
    }
}