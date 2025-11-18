// COPY TẤT CẢ TỪ ĐÂY TRỞ XUỐNG VÀ DÁN VÀO FILE MainUI.java CỦA BẠN

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
import java.net.URL;
import java.awt.event.MouseAdapter; // <--- THÊM DÒNG NÀY
import java.awt.event.MouseEvent;  // <--- THÊM DÒNG NÀY

public class MainUI extends JFrame {
    private JTable tblDanhSach, tblGioHang;
    private JTextField txtMinGia, txtMaxGia, txtMaKM, txtTenTimKiem;
    private JComboBox<String> cbLoai, cbKichThuoc;
    private JSplitPane splitPane;
    private JComboBox<String> cbPhuongThucTT;
    private JLabel lblTongTien, lblTenNguoiDung;
    private JTextArea txtKetQua;
    private FigureBLL bll = new FigureBLL();
    private double phanTramGiam = 0;
    private NguoiDungDTO nguoiDungHienTai = null;

    // === SỬA LỖI 1: Thêm biến tạm để quản lý danh sách
    private List<FigureDTO> danhSachHienTai; 
    
    private JPopupMenu userMenuPopup; // <--- THÊM BIẾN NÀY

    public MainUI(NguoiDungDTO nd) {
        this.nguoiDungHienTai = nd;
        // hienThiTenNguoiDung() phải được gọi SAU KHI lblTenNguoiDung được khởi tạo
        // Tốt nhất là gọi nó bên trong createHeaderPanel
        initComponents(); 
    }

    private void hienThiTenNguoiDung() {
        if (lblTenNguoiDung == null) { // Khởi tạo nếu chưa có
             lblTenNguoiDung = new JLabel();
             lblTenNguoiDung.setFont(new Font("Arial", Font.BOLD, 18));
             lblTenNguoiDung.setForeground(Color.WHITE);
        }
        lblTenNguoiDung.setText(nguoiDungHienTai != null
         ? "<html>Xin chào, <b>" + nguoiDungHienTai.getTenDangNhap() + "</b></html>"
         : "Xin chào, Khách");
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

        add(mainPanel);
        taiDanhSach();
        capNhatGioHang();
        setLocationRelativeTo(null);
        setVisible(true);
        // === DÁN ĐOẠN CODE NÀY VÀO SAU DÒNG setVisible(true) ===
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Ép thanh chia về 75% sau khi cửa sổ đã hiển thị
                splitPane.setDividerLocation(0.6);
            }
        });
        // =======================================================
    }

    // DÁN THAY THẾ HÀM createHeaderPanel CŨ (khoảng dòng 98)

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel logo = new JLabel("MAHIRU.");
        logo.setFont(new Font("Arial", Font.BOLD, 32));
        logo.setForeground(Color.WHITE);

        // === CĂN CHỈNH THANH TÌM KIẾM ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); 
        searchPanel.setOpaque(false);
        int componentHeight = 38; 

        searchPanel.add(createLabelWhite("Tìm tên:"));
        txtTenTimKiem = new JTextField(15);
        txtTenTimKiem.setPreferredSize(new Dimension(txtTenTimKiem.getPreferredSize().width, componentHeight));
        searchPanel.add(txtTenTimKiem);

        searchPanel.add(createLabelWhite("Loại:"));
        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Gundam", "Anime", "Game", "Khác"});
        cbLoai.setPreferredSize(new Dimension(130, componentHeight)); 
        searchPanel.add(cbLoai);

        searchPanel.add(createLabelWhite("Giá từ:"));
        txtMinGia = new JTextField(8);
        txtMinGia.setPreferredSize(new Dimension(txtMinGia.getPreferredSize().width, componentHeight));
        searchPanel.add(txtMinGia);

        searchPanel.add(createLabelWhite("đến:"));
        txtMaxGia = new JTextField(8);
        txtMaxGia.setPreferredSize(new Dimension(txtMaxGia.getPreferredSize().width, componentHeight));
        searchPanel.add(txtMaxGia);

        searchPanel.add(createLabelWhite("KT:"));
        cbKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/6", "1/8", "1/10", "1/12", "Khác"});
        cbKichThuoc.setPreferredSize(new Dimension(130, componentHeight)); 
        searchPanel.add(cbKichThuoc);

        JButton btnTimKiem = createRedButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(100, componentHeight)); 
        btnTimKiem.addActionListener(e -> timKiemNangCao());
        searchPanel.add(btnTimKiem);
        // === KẾT THÚC CĂN CHỈNH ===

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);

        hienThiTenNguoiDung(); 

        // === SỬA MENU ĐĂNG XUẤT ===

        if (nguoiDungHienTai != null) {
            // Khởi tạo Popup Menu
            userMenuPopup = new JPopupMenu();
            userMenuPopup.setBackground(new Color(50, 50, 50));
            userMenuPopup.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));

            // 1. TẠO NÚT "ĐỔI MẬT KHẨU" (MỚI)
            JMenuItem itemChangePass = createStyledMenuItem("Đổi mật khẩu");
            itemChangePass.addActionListener(e -> {
                // Mở cửa sổ ChangePasswordUI và truyền MainUI, User vào
                ChangePasswordUI changeUI = new ChangePasswordUI(MainUI.this, nguoiDungHienTai);
                changeUI.setVisible(true);
            });
            userMenuPopup.add(itemChangePass);

            // 2. TẠO NÚT "ĐĂNG XUẤT"
            JMenuItem itemLogout = createStyledMenuItem("Đăng xuất");
            itemLogout.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(this, 
                        "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    dispose(); 
                    new LoginUI().setVisible(true);
                }
            });
            userMenuPopup.add(itemLogout);

            // Thêm MouseListener vào tên người dùng
            lblTenNguoiDung.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
            lblTenNguoiDung.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    userMenuPopup.show(evt.getComponent(), 0, evt.getComponent().getHeight());
                }
            });
        }
        // === KẾT THÚC SỬA LỖI 2 ===

        right.add(lblTenNguoiDung);

        panel.add(logo, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

// THÊM HÀM HELPER NÀY (để style JMenuItem)
private JMenuItem createStyledMenuItem(String text) {
    JMenuItem menuItem = new JMenuItem(text);
    menuItem.setFont(new Font("Arial", Font.BOLD, 14));
    menuItem.setBackground(new Color(50, 50, 50));
    menuItem.setForeground(Color.WHITE);
    menuItem.setOpaque(true);
    menuItem.setBorder(new EmptyBorder(10, 15, 10, 15));

    // Thêm hiệu ứng Hover
    menuItem.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            menuItem.setBackground(new Color(80, 80, 80)); 
        }
        @Override
        public void mouseExited(MouseEvent e) {
            menuItem.setBackground(new Color(50, 50, 50));
        }
    });
    return menuItem;
}

    private JSplitPane createSplitContent() {
    // SỬA TỪ "JSplitPane split" THÀNH "splitPane"
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            createLeftPanel(), createRightPanel());
    
        splitPane.setResizeWeight(0.6); // SỬA TỪ 0.75 THÀNH 0.6 (cho tỉ lệ 3/5)
        splitPane.setDividerSize(10);
        splitPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
    
        return splitPane; // Trả về splitPane
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
        
        // === SỬA LỖI 2: TĂNG CHIỀU CAO HÀNG ĐỂ CHỨA ẢNH ===
        tblDanhSach.setRowHeight(60); // Tăng từ 45 lên 60
        // === KẾT THÚC SỬA ===
        
        tblDanhSach.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // (Phần code style header bảng của m giữ nguyên...)
        JTableHeader headerLeft = tblDanhSach.getTableHeader();
        headerLeft.setFont(new Font("Arial", Font.BOLD, 14));
        headerLeft.setBackground(new Color(60, 60, 60));
        headerLeft.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(60, 60, 60)); 
                c.setForeground(Color.WHITE); 
                c.setFont(new Font("Arial", Font.BOLD, 14));
                setBorder(UIManager.getBorder("TableHeader.cellBorder")); 
                setHorizontalAlignment(JLabel.LEFT);
                return c;
            }
        });

        tblDanhSach.setGridColor(new Color(200, 200, 200));
        tblDanhSach.setSelectionBackground(new Color(240, 240, 240));
        tblDanhSach.setSelectionForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(tblDanhSach);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // DÁN THAY THẾ HÀM createRightPanel CŨ (khoảng dòng 227)

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
    tblGioHang.setRowHeight(60);
    tblGioHang.setFont(new Font("Arial", Font.PLAIN, 14));
    tblGioHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    tblGioHang.getTableHeader().setBackground(new Color(60, 60, 60));
    tblGioHang.getTableHeader().setForeground(Color.WHITE);
    // === THÊM ĐOẠN NÀY ĐỂ ÉP CHỮ MÀU TRẮNG ===
    ((DefaultTableCellRenderer) tblGioHang.getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(JLabel.LEFT); // Căn lề trái (hoặc CENTER)
            
    tblGioHang.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(new Color(60, 60, 60)); // Giữ nền đen
            c.setForeground(Color.WHITE); // Ép chữ trắng
            c.setFont(new Font("Arial", Font.BOLD, 14));
            setBorder(UIManager.getBorder("TableHeader.cellBorder")); // Giữ viền
            return c;
        }
    });
    tblGioHang.setGridColor(new Color(200, 200, 200));
    tblGioHang.setSelectionBackground(new Color(240, 240, 240));
    tblGioHang.setSelectionForeground(Color.BLACK);

    JScrollPane scroll = new JScrollPane(tblGioHang);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
    panel.add(scroll, BorderLayout.CENTER);

    // === BẮT ĐẦU THAY THẾ TOÀN BỘ FOOTER ===

    JPanel footer = new JPanel(new GridBagLayout());
    // Giảm lề trên/dưới để bù cho việc thêm 1 hàng
    footer.setBorder(new EmptyBorder(15, 20, 15, 20)); 
    footer.setBackground(Color.WHITE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // Lề giữa các component

    // --- HÀNG 0 (Mã KM, PTTT, Thanh toán) ---
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Cột 0: Nhãn "Mã KM:"
    gbc.gridx = 0;
    gbc.weightx = 0.0; // Không co dãn
    gbc.fill = GridBagConstraints.NONE;
    footer.add(new JLabel("Mã KM:"), gbc);

    // Cột 1: Ô txtMaKM
    gbc.gridx = 1;
    gbc.weightx = 0.2; // Cho phép co dãn (20%)
    gbc.fill = GridBagConstraints.HORIZONTAL;
    txtMaKM = new JTextField(8);
    txtMaKM.setMinimumSize(new Dimension(60, 30)); // Cho phép co lại
    footer.add(txtMaKM, gbc);

    // Cột 2: Nút "Áp dụng"
    gbc.gridx = 2;
    gbc.weightx = 0.0; // Không co dãn
    gbc.fill = GridBagConstraints.NONE;
    JButton apply = createOrangeButton("Áp dụng");
    apply.addActionListener(e -> apDungMaKM());
    footer.add(apply, gbc);

    // Cột 3: Khoảng trống (co dãn chính)
    gbc.gridx = 3;
    gbc.weightx = 1.0; // Hấp thụ phần lớn co dãn (100%)
    gbc.fill = GridBagConstraints.HORIZONTAL;
    footer.add(Box.createHorizontalGlue(), gbc);

    // Cột 4: Nhãn "PTTT:"
    gbc.gridx = 4;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    footer.add(new JLabel("PTTT:"), gbc);

    // Cột 5: ComboBox PTTT
    gbc.gridx = 5;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    String[] phuongThucOptions = {"TienMat", "ChuyenKhoan", "The", "ViDienTu"};
    cbPhuongThucTT = new JComboBox<>(phuongThucOptions);
    cbPhuongThucTT.setFont(new Font("Arial", Font.PLAIN, 14));
    footer.add(cbPhuongThucTT, gbc);

    // Cột 6: Nút Thanh toán
    gbc.gridx = 6;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    JButton pay = createGreenButton("Thanh toán");
    pay.addActionListener(e -> thanhToan());
    footer.add(pay, gbc);


    // --- HÀNG 1 (Tổng tiền) ---
    gbc.gridy = 1; // Đặt ở hàng mới
    gbc.gridx = 3; // Bắt đầu từ cột 3
    gbc.gridwidth = 4; // Trải dài 4 cột (từ cột 3 đến cột 6)
    gbc.anchor = GridBagConstraints.LINE_END; // Căn lề phải
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0; // Không co dãn
    // Thêm lề trên để tách khỏi hàng 0
    gbc.insets = new Insets(10, 5, 5, 5); 

    lblTongTien = new JLabel("Tổng: 0 VND (Giảm 0%)");
    lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
    lblTongTien.setForeground(new Color(220, 20, 60));
    footer.add(lblTongTien, gbc);


    panel.add(footer, BorderLayout.SOUTH);

    // === KẾT THÚC THAY THẾ FOOTER ===
    
    // Giữ dòng này để JSplitPane hoạt động đúng
    panel.setMinimumSize(new Dimension(0, 0));
    
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
    
    // === ĐÃ SỬA: LƯU VÀO BIẾN TẠM ===
    private void timKiemNangCao() {
        String ten = txtTenTimKiem.getText().trim();
        String loai = "Tất cả".equals(cbLoai.getSelectedItem()) ? null : (String) cbLoai.getSelectedItem();
        Double min = parseDouble(txtMinGia.getText());
        Double max = parseDouble(txtMaxGia.getText());
        String kt = "Tất cả".equals(cbKichThuoc.getSelectedItem()) ? null : (String) cbKichThuoc.getSelectedItem();
        
        this.danhSachHienTai = bll.timKiemNangCao(ten, loai, min, max, kt);
        capNhatBangDanhSach(this.danhSachHienTai);
    }

    private Double parseDouble(String s) {
        try { return s.isEmpty() ? null : Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }

    // === ĐÃ SỬA: LƯU VÀO BIẾN TẠM ===
    private void taiDanhSach() {
        this.danhSachHienTai = bll.layTatCa();
        capNhatBangDanhSach(this.danhSachHienTai);
    }

    // === ĐÃ SỬA: BỎ TRUYỀN LIST VÀO EDITOR ===
    private void capNhatBangDanhSach(List<FigureDTO> list) {
        // === SỬA LỖI 2: THÊM CỘT "ẢNH" VÀO VỊ TRÍ 1 ===
        String[] cols = {"ID", "Ảnh", "Tên Figure", "Loại", "Giá (VND)", "Kích thước", "Số lượng", "Chi tiết", "Thêm"};
        
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { 
                return c >= 7; // Chỉ cho sửa 2 cột cuối (Chi tiết = 7, Thêm = 8)
            }
            
            // === THÊM HÀM NÀY ĐỂ JTABLE HIỂU CỘT NÀO LÀ ẢNH ===
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return ImageIcon.class; // Cột 1 là Ảnh
                }
                return Object.class; // Các cột khác là chữ/số
            }
            // === KẾT THÚC THÊM ===
        };

        this.danhSachHienTai = list; 

        for (FigureDTO f : list) {
            // GỌI HÀM: Truyền getHinhAnh() (ví dụ: "aqua.jpg") thay vì getId()
            ImageIcon icon = loadResizedIcon(f.getHinhAnh(), 50, 50);

            model.addRow(new Object[]{
                    f.getId(), icon, f.getTen(), f.getLoai(), 
                    String.format("%,.0f", f.getGia()), f.getKichThuoc(), f.getSoLuong(),
                    "Chi tiết", "Thêm"
            });
        }

        tblDanhSach.setModel(model);

        // === SỬA LỖI 2: CẬP NHẬT CHỈ SỐ CỘT (VÌ ĐÃ THÊM CỘT ẢNH) ===
        TableColumn colDetail = tblDanhSach.getColumnModel().getColumn(7); // Sửa: 6 -> 7
        colDetail.setCellRenderer(new DetailButtonRenderer());
        colDetail.setCellEditor(new DetailButtonEditor(new JCheckBox())); 

        TableColumn colAdd = tblDanhSach.getColumnModel().getColumn(8); // Sửa: 7 -> 8
        colAdd.setCellRenderer(new AddButtonRenderer());
        colAdd.setCellEditor(new AddButtonEditor(new JCheckBox())); 
        // === KẾT THÚC SỬA ===

        // === SỬA LỖI 2: CẬP NHẬT KÍCH THƯỚC CỘT ===
        tblDanhSach.getColumnModel().getColumn(0).setPreferredWidth(40); // Cột ID
        tblDanhSach.getColumnModel().getColumn(1).setPreferredWidth(60); // Cột Ảnh MỚI
        
        for (int i = 2; i < 7; i++) { // Bắt đầu từ 2 (Tên) đến 6 (Số lượng)
            TableColumn col = tblDanhSach.getColumnModel().getColumn(i);
            int width = 100;
            if(tblDanhSach.getRowCount() > 0) { 
                for (int row = 0; row < tblDanhSach.getRowCount(); row++) {
                    Component comp = tblDanhSach.prepareRenderer(tblDanhSach.getCellRenderer(row, i), row, i);
                    width = Math.max(comp.getPreferredSize().width + 15, width);
                }
            }
            col.setPreferredWidth(Math.min(width, 200));
        }
        // === KẾT THÚC SỬA ===
        
        tblDanhSach.revalidate();
        tblDanhSach.repaint();
    }

    // === ĐÃ SỬA: BỎ TRUYỀN LIST VÀO EDITOR ===
    private void capNhatGioHang() {
        String[] cols = {"ID", "Ảnh", "Tên", "SL", "Thành tiền", "Xóa"};
        // 2. SỬA MODEL ĐỂ CHẤP NHẬN ẢNH
    DefaultTableModel model = new DefaultTableModel(cols, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return c == 5; // Sửa: Cột "Xóa" bây giờ là 5
        }

        // THÊM HÀM NÀY:
        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 1) { // Cột "Ảnh" là 1
                return ImageIcon.class;
            }
            return Object.class;
        }
    };

    double tong = 0;
    for (GioHangItemDTO i : bll.getGioHang()) {
        double tt = i.getThanhTien();
        tong += tt;

        // 3. TẢI ẢNH (size nhỏ 50x50)
        ImageIcon icon = loadResizedIcon(i.getFigure().getHinhAnh(), 50, 50);

        // 4. THÊM ẢNH VÀO HÀNG
        model.addRow(new Object[]{
            i.getFigure().getId(),
            icon, 
            i.getFigure().getTen(),
            i.getSoLuong(),
            String.format("%,.0f", tt),
            "Xóa"
        });
    }

    tblGioHang.setModel(model);
    
    // 5. CẬP NHẬT KÍCH THƯỚC CÁC CỘT MỚI
    tblGioHang.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
    tblGioHang.getColumnModel().getColumn(1).setPreferredWidth(60);  // Ảnh
    tblGioHang.getColumnModel().getColumn(2).setPreferredWidth(150); // Tên (cho rộng hơn)
    tblGioHang.getColumnModel().getColumn(3).setPreferredWidth(40);  // SL
    tblGioHang.getColumnModel().getColumn(4).setPreferredWidth(100); // Thành tiền
    tblGioHang.getColumnModel().getColumn(5).setPreferredWidth(50);  // Xóa

    // Cột Xóa (Sửa chỉ số cột)
    TableColumn colDel = tblGioHang.getColumnModel().getColumn(5); // Sửa: từ 4 thành 5
    colDel.setCellRenderer(new DeleteButtonRenderer());
    colDel.setCellEditor(new DeleteButtonEditor(new JCheckBox())); 

    // (Code cập nhật lblTongTien giữ nguyên)
    double sauGiam = tong * (1 - phanTramGiam / 100);
    lblTongTien.setText(String.format("Tổng: %,.0f VND (Giảm %.0f%%)", sauGiam, phanTramGiam));
    
    tblGioHang.revalidate();
    tblGioHang.repaint();
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

    // === ĐÃ SỬA: LOGIC KHO TẠM ===
    private void themVaoGio(int id, int soLuong) {
        if (bll.themVaoGio(id, soLuong)) {
            capNhatGioHang(); // Cập nhật giỏ hàng (ĐÚNG)
            
            // Tìm sản phẩm trong danh sách tạm và trừ số lượng
            for (FigureDTO fig : this.danhSachHienTai) {
                if (fig.getId() == id) {
                    fig.setSoLuong(fig.getSoLuong() - soLuong);
                    break;
                }
            }
            // Vẽ lại bảng danh sách TỪ danh sách tạm đã sửa
            capNhatBangDanhSach(this.danhSachHienTai);

        } else {
            JOptionPane.showMessageDialog(this, "Không đủ hàng!");
        }
    }

    // === ĐÃ SỬA: LOGIC KHO TẠM ===
    private void xoaKhoiGio(int id) {
        // Lấy số lượng cần trả lại TRƯỚC KHI XÓA
        int soLuongTraLai = 0;
        for (GioHangItemDTO item : bll.getGioHang()) {
            if (item.getFigureId() == id) {
                soLuongTraLai = item.getSoLuong();
                break;
            }
        }

        if (bll.xoaKhoiGio(id)) { 
            capNhatGioHang(); // Cập nhật giỏ hàng (ĐÚNG)
            
            // Tìm sản phẩm trong danh sách tạm và CỘNG TRẢ LẠI
            for (FigureDTO fig : this.danhSachHienTai) {
                if (fig.getId() == id) {
                    fig.setSoLuong(fig.getSoLuong() + soLuongTraLai);
                    break;
                }
            }
            // Vẽ lại bảng danh sách TỪ danh sách tạm đã sửa
            capNhatBangDanhSach(this.danhSachHienTai);
        }
    }

    // === ĐÃ SỬA: CẬP NHẬT GIAO DIỆN SAU KHI THANH TOÁN ===
    private void thanhToan() {
        // 1. Kiểm tra đăng nhập
        if (nguoiDungHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập!");
            return;
        }
        
        // 2. Kiểm tra giỏ hàng
        List<GioHangItemDTO> gioHangHienTai = bll.getGioHang();
        if (gioHangHienTai == null || gioHangHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng!");
            return;
        }

        // 3. Lấy thông tin thanh toán
        int maNhanVien = nguoiDungHienTai.getMaNguoiDung();
        String phuongThucTT = (String) cbPhuongThucTT.getSelectedItem(); 
        String maKhuyenMai = txtMaKM.getText().trim();
        if (phanTramGiam == 0 || maKhuyenMai.isEmpty()) {
            maKhuyenMai = null;
        }

        // 4. Gọi BLL xử lý (Lưu xuống DB -> Trigger SQL sẽ chạy để trừ kho)
        DonHangDTO donHang = bll.thanhToan(maNhanVien, phuongThucTT, maKhuyenMai);

        if (donHang != null) {
            // --- THANH TOÁN THÀNH CÔNG ---
            
            // A. Hiển thị hóa đơn
            hienThiPopupHoaDon(donHang);
            
            // B. Cập nhật giao diện Giỏ hàng (Lúc này BLL đã xóa giỏ, cần vẽ lại bảng rỗng)
            capNhatGioHang(); 
            
            // C. Cập nhật giao diện Danh sách sản phẩm (QUAN TRỌNG: Tải lại từ DB để thấy tồn kho giảm)
            taiDanhSach();    
            
            // D. Reset các trường nhập liệu
            phanTramGiam = 0;
            txtMaKM.setText("");
            lblTongTien.setText("Tổng: 0 VND (Giảm 0%)"); 
            
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại! (Lỗi lưu CSDL)");
        }
    }
    
    private void hienThiPopupHoaDon(DonHangDTO donHang) {
        // 1. Tạo cửa sổ popup
        JDialog hoaDonDialog = new JDialog(this, "Chi Tiết Hóa Đơn", true); // 'true' = modal (khóa MainUI)
        hoaDonDialog.setSize(450, 400);
        hoaDonDialog.setLocationRelativeTo(this);
        hoaDonDialog.setLayout(new BorderLayout());

        // 2. Tạo JTextArea để chứa nội dung
        JTextArea txtHoaDon = new JTextArea();
        txtHoaDon.setEditable(false);
        txtHoaDon.setFont(new Font("Consolas", Font.PLAIN, 15)); // Font giống ô txtKetQua
        txtHoaDon.setBorder(new EmptyBorder(15, 15, 15, 15)); // Thêm lề

        // 3. Tạo nội dung hóa đơn (Giống code cũ của bạn)
        StringBuilder sb = new StringBuilder();
        sb.append("--- HÓA ĐƠN ĐÃ LƯU ---\n\n");
        sb.append("Mã đơn hàng:\t").append(donHang.getMaDonHang()).append("\n");
        sb.append("Ngày đặt:\t").append(donHang.getNgayDat().toString()).append("\n");
        sb.append("Nhân viên:\t").append(nguoiDungHienTai.getTenDangNhap()).append("\n");
        sb.append("PT Thanh toán:\t").append(donHang.getPhuongThucTT()).append("\n");
        sb.append("\n----------------------------------\n");
        sb.append("SẢN PHẨM:\n");

        // Lấy chi tiết từ giỏ hàng đã thanh toán (đã lưu trong donHang DTO)
        for (GioHangItemDTO item : donHang.getGioHang()) {
            sb.append(String.format("- %s (SL: %d)\n", item.getFigure().getTen(), item.getSoLuong()));
        }
        
        sb.append("\n----------------------------------\n");
        if (donHang.getMaKhuyenMai() != null) {
            sb.append("Khuyến mãi:\t").append(donHang.getMaKhuyenMai()).append("\n");
        }
        sb.append("TỔNG TIỀN:\t").append(String.format("%,.0f VND", donHang.getTongTien())).append("\n");
        sb.append("Trạng thái:\tĐã thanh toán.\n");

        txtHoaDon.setText(sb.toString());
        
        // 4. Thêm JTextArea (trong JScrollPane) vào Dialog
        hoaDonDialog.add(new JScrollPane(txtHoaDon), BorderLayout.CENTER);
        
        // 5. Hiển thị popup
        hoaDonDialog.setVisible(true);
    }

    // THAY THẾ HÀM CŨ BẰNG HÀM NÀY ĐỂ DEBUG
    private ImageIcon loadResizedIcon(String filename, int width, int height) {
        if (filename == null || filename.trim().isEmpty()) return null;
        
        try {
            // CÁCH 1: Dùng ClassLoader (Chuẩn nhất khi chạy trong NetBeans src)
            // Lưu ý: Đường dẫn bắt đầu bằng dấu /
            URL imgUrl = getClass().getResource("/Resources/figure_images/" + filename);
            
            if (imgUrl != null) {
                BufferedImage img = ImageIO.read(imgUrl);
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
            
            // CÁCH 2: Nếu cách 1 tạch, thử tìm bằng đường dẫn File cứng
            // In ra thư mục làm việc hiện tại để kiểm tra
            // System.out.println("Working Dir: " + System.getProperty("user.dir"));
            
            File f = new File("src/Resources/figure_images/" + filename);
            if (f.exists()) {
                BufferedImage img = ImageIO.read(f);
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } else {
                // DÒNG NÀY QUAN TRỌNG: Nhìn vào Output để xem nó báo lỗi gì
                System.err.println("❌ KHÔNG TÌM THẤY ẢNH: " + filename);
                System.err.println("   -> Đã tìm tại: " + f.getAbsolutePath());
                System.err.println("   -> Và tìm tại classpath: /Resources/figure_images/" + filename);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // === BUTTON RENDERER & EDITOR ===
    
    // (Class DetailButtonRenderer không đổi)
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

    // === ĐÃ SỬA: CONSTRUCTOR VÀ LOGIC LẤY ID ===
    private class DetailButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow; 

        public DetailButtonEditor(JCheckBox cb) { // <--- ĐÃ SỬA
            super(cb);
            button = new JButton("Chi tiết");
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                try {
                    int id = (int) tblDanhSach.getModel().getValueAt(currentRow, 0); 
                    FigureDTO f = null;
                    for(FigureDTO fig : danhSachHienTai) {
                        if(fig.getId() == id) {
                            f = fig;
                            break;
                        }
                    }
                    if(f != null) moChiTiet(f);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.currentRow = row; 
            return button;
        }
        @Override
        public Object getCellEditorValue() { return "Chi tiết"; }
    }

    // (Hàm moChiTiet không đổi)
    private void moChiTiet(FigureDTO f) {
        // 1. Tạo cửa sổ popup
        JDialog dialog = new JDialog(MainUI.this, "Chi tiết: " + f.getTen(), true);
        
        // === SỬA KÍCH THƯỚC DIALOG TẠI ĐÂY ===
        dialog.setSize(750, 550); // Tăng kích thước (rộng x cao)
        // === KẾT THÚC SỬA ===
        
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15)); 
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // 2. PHẦN ẢNH (BÊN TRÁI - WEST)
        JLabel imgLabel = new JLabel();
        
        // === SỬA KÍCH THƯỚC ẢNH TẠI ĐÂY ===
        imgLabel.setPreferredSize(new Dimension(300, 300)); // Tăng kích thước ảnh
        // === KẾT THÚC SỬA ===
        
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        try {
        String imgName = f.getHinhAnh(); 
        if (imgName == null || imgName.isEmpty()) imgName = "default.jpg";

        URL imgUrl = getClass().getResource("/Resources/figure_images/" + imgName); 
            if (imgUrl == null) {
                 throw new java.io.FileNotFoundException("Không tìm thấy file: /Resources/figure_images/" + f.getId() + ".jpg");
            }
            BufferedImage img = ImageIO.read(imgUrl);
            
            // === SỬA KÍCH THƯỚC SCALE ẢNH TẠI ĐÂY ===
            Image scaled = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH); // Tăng scale ảnh
            // === KẾT THÚC SỬA ===
            
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("Không có ảnh");
            System.err.println("Lỗi load ảnh chi tiết: " + e.getMessage()); 
        }
        mainPanel.add(imgLabel, BorderLayout.WEST); 

        // 3. PHẦN THÔNG TIN (BÊN PHẢI - CENTER)
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; 

        addDetailRow(infoPanel, gbc, 0, "ID:", String.valueOf(f.getId()));
        addDetailRow(infoPanel, gbc, 1, "Tên:", f.getTen());
        addDetailRow(infoPanel, gbc, 2, "Loại:", f.getLoai());
        addDetailRow(infoPanel, gbc, 3, "Kích thước:", f.getKichThuoc());
        addDetailRow(infoPanel, gbc, 4, "Tồn kho:", f.getSoLuong() + " (sản phẩm)");
        
        gbc.gridy = 5;
        gbc.gridx = 0;
        JLabel lblGiaTitle = new JLabel("Giá:");
        lblGiaTitle.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(lblGiaTitle, gbc);
        
        gbc.gridx = 1;
        JLabel lblGiaValue = new JLabel(String.format("%,.0f VND", f.getGia()));
        lblGiaValue.setFont(new Font("Arial", Font.BOLD, 18)); 
        lblGiaValue.setForeground(new Color(220, 20, 60)); 
        infoPanel.add(lblGiaValue, gbc);
        
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        infoPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        JTextArea txtMoTa = new JTextArea(f.getMoTa());
        txtMoTa.setEditable(false);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMoTa.setBackground(new Color(245, 245, 245)); 
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setPreferredSize(new Dimension(100, 100)); 
        infoPanel.add(scrollMoTa, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER); 

        // 4. PHẦN NÚT BẤM (BÊN DƯỚI - SOUTH)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.BOLD, 14));
        btnDong.addActionListener(e -> dialog.dispose()); 
        
        buttonPanel.add(btnDong);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Hàm helper để thêm 1 dòng (Nhãn + Giá trị) vào panel chi tiết
     */
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int yPos, String title, String value) {
        gbc.gridy = yPos;
        gbc.gridx = 0;
        gbc.weightx = 0; // Nhãn không co giãn
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTitle, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Giá trị co giãn
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(lblValue, gbc);
    }

    // (Class AddButtonRenderer không đổi)
    private class AddButtonRenderer extends JButton implements TableCellRenderer {
        public AddButtonRenderer() {
            setText("Thêm");
            setBackground(new Color(34, 139, 34));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            return this;
        }
    }

    // === ĐÃ SỬA: CONSTRUCTOR VÀ LOGIC LẤY ID ===
    private class AddButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow; 

        public AddButtonEditor(JCheckBox cb) { // <--- ĐÃ SỬA
            super(cb);
            button = new JButton("Thêm");
            button.setBackground(new Color(34, 139, 34));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                try {
                    int figureId = (int) tblDanhSach.getModel().getValueAt(currentRow, 0);

                    String sl = JOptionPane.showInputDialog("Số lượng:");
                    if (sl != null) {
                        try {
                            int soLuong = Integer.parseInt(sl);
                            if(soLuong > 0) themVaoGio(figureId, soLuong);
                        } catch (Exception ignored) {}
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.currentRow = row; 
            return button;
        }
        @Override
        public Object getCellEditorValue() { return "Thêm"; }
    }

    // (Class DeleteButtonRenderer không đổi)
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

    // === ĐÃ SỬA: CONSTRUCTOR VÀ LOGIC LẤY ID ===
    private class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow; 

        public DeleteButtonEditor(JCheckBox cb) { // <--- ĐÃ SỬA
            super(cb);
            button = new JButton("Xóa");
            button.setBackground(new Color(220, 20, 60));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                try {
                    int figureId = (int) tblGioHang.getModel().getValueAt(currentRow, 0);
                    xoaKhoiGio(figureId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.currentRow = row; 
            return button;
        }
        @Override
        public Object getCellEditorValue() { return "Xóa"; }
    }
}