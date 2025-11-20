package FORM;

import BLL.FigureBLL;
import BLL.NhaCungCapBLL; 
import DTO.NhaCungCapDTO;
import DTO.*;
import Database.DBConnection; 

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainUI extends JFrame {

    // --- KHAI BÁO BIẾN GIAO DIỆN ---
    private JTable tblDanhSach, tblGioHang;
    private JTextField txtMinGia, txtMaxGia, txtTenTimKiem;
    private JComboBox<String> cbLoai, cbKichThuoc;
    private JSplitPane splitPane;
    private JComboBox<NhaCungCapDTO> cbLocNCC; // <--- THÊM MỚI
    private NhaCungCapBLL nccBLL = new NhaCungCapBLL(); // <--- THÊM MỚI
    
    // Header components
    private JLabel lblTenNguoiDung;
    private JPopupMenu userMenuPopup;

    // --- BIẾN UI THANH TOÁN (MỚI) ---
    private JComboBox<String> cbPhuongThucTT;
    private JComboBox<String> cbKhuyenMai; // Chọn mã KM
    private JTextField txtTienKhachDua;    // Nhập tiền khách
    private JLabel lblTienThua;            // Hiển thị tiền thừa
    private JLabel lblTongTien;            // Tổng tiền phải trả
    
    // --- BIẾN LOGIC ---
    private FigureBLL bll = new FigureBLL();
    private NguoiDungDTO nguoiDungHienTai = null;
    private List<FigureDTO> danhSachHienTai; 
    
    private double phanTramGiam = 0;
    private long tongTienHienTai = 0;      

    // =========================================================================
    // 1. CONSTRUCTOR & INIT
    // =========================================================================
    public MainUI(NguoiDungDTO nd) {
        this.nguoiDungHienTai = nd;
        initComponents();
    }

    private void initComponents() {
        setTitle("MAHIRU. - Quản Lý Bán Hàng");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));

        // Cấu hình Font chữ toàn cục cho đẹp
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Thêm Header và Nội dung chính
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createSplitContent(), BorderLayout.CENTER);

        add(mainPanel);
        
        // Load dữ liệu ban đầu
        taiDanhSach();
        capNhatGioHang();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Chia tỉ lệ màn hình (65% Trái - 35% Phải) sau khi hiện lên
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.65));
    }

    // =========================================================================
    // 2. HEADER PANEL (LOGO, TÌM KIẾM, USER)
    // =========================================================================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30)); // Màu nền tối
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Logo
        JLabel logo = new JLabel("MAHIRU.");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(Color.WHITE);

        // Panel Tìm kiếm (Ở giữa)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setOpaque(false);
        int h = 35; // Chiều cao chung cho ô nhập

        searchPanel.add(createLabelWhite("Tên:"));
        txtTenTimKiem = new JTextField(12); txtTenTimKiem.setPreferredSize(new Dimension(120, h));
        searchPanel.add(txtTenTimKiem);

        searchPanel.add(createLabelWhite("Loại:"));
        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Gundam", "Anime", "Game", "Khác"});
        cbLoai.setPreferredSize(new Dimension(100, h));
        searchPanel.add(cbLoai);

        searchPanel.add(createLabelWhite("Giá:"));
        txtMinGia = new JTextField(6); txtMinGia.setPreferredSize(new Dimension(60, h));
        searchPanel.add(txtMinGia);

        searchPanel.add(createLabelWhite("-"));
        txtMaxGia = new JTextField(6); txtMaxGia.setPreferredSize(new Dimension(60, h));
        searchPanel.add(txtMaxGia);

        searchPanel.add(createLabelWhite("KT:"));
        cbKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/6", "1/8", "1/10", "1/12", "Khác"});
        cbKichThuoc.setPreferredSize(new Dimension(80, h));
        searchPanel.add(cbKichThuoc);

        // Thêm ComboBox NCC vào searchPanel
        searchPanel.add(createLabelWhite("NCC:"));
        cbLocNCC = new JComboBox<>();
        cbLocNCC.setPreferredSize(new Dimension(120, 35));
        // Thêm mục mặc định
        cbLocNCC.addItem(new NhaCungCapDTO(0, "Tất cả", "", "", "", "")); 
        // Load danh sách từ DB
        List<NhaCungCapDTO> listNCC = nccBLL.getListNhaCungCap();
        for(NhaCungCapDTO ncc : listNCC) cbLocNCC.addItem(ncc);
        
        searchPanel.add(cbLocNCC);
        
        JButton btnTimKiem = createRedButton("Tìm");
        btnTimKiem.setPreferredSize(new Dimension(80, h));
        btnTimKiem.addActionListener(e -> timKiemNangCao());
        searchPanel.add(btnTimKiem);

        // Panel User (Bên phải)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        
        // Hiển thị tên User
        if (lblTenNguoiDung == null) {
             lblTenNguoiDung = new JLabel();
             lblTenNguoiDung.setFont(new Font("Segoe UI", Font.BOLD, 16));
             lblTenNguoiDung.setForeground(Color.WHITE);
        }
        lblTenNguoiDung.setText(nguoiDungHienTai != null
          ? "<html>Xin chào, <b>" + nguoiDungHienTai.getTenDangNhap() + "</b></html>"
          : "Xin chào, Khách");

        // Popup Menu User
        if (nguoiDungHienTai != null) {
            userMenuPopup = new JPopupMenu();
            userMenuPopup.setBackground(new Color(50, 50, 50));
            userMenuPopup.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));

            JMenuItem itemChangePass = createStyledMenuItem("Đổi mật khẩu");
            itemChangePass.addActionListener(e -> {
                 ChangePasswordUI changeUI = new ChangePasswordUI(MainUI.this, nguoiDungHienTai);
                 changeUI.setVisible(true);
            });
            userMenuPopup.add(itemChangePass);

            JMenuItem itemLogout = createStyledMenuItem("Đăng xuất");
            itemLogout.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginUI().setVisible(true);
                }
            });
            userMenuPopup.add(itemLogout);

            lblTenNguoiDung.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblTenNguoiDung.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    userMenuPopup.show(evt.getComponent(), 0, evt.getComponent().getHeight());
                }
            });
        }
        right.add(lblTenNguoiDung);

        panel.add(logo, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // =========================================================================
    // 3. SPLIT PANE (CHIA ĐÔI MÀN HÌNH)
    // =========================================================================
    private JSplitPane createSplitContent() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        return splitPane;
    }

    // --- LEFT PANEL: DANH SÁCH SẢN PHẨM ---
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel title = new JLabel(" DANH SÁCH SẢN PHẨM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(220, 20, 60));
        title.setBorder(new EmptyBorder(15, 10, 10, 10));
        panel.add(title, BorderLayout.NORTH);

        tblDanhSach = new JTable();
        tblDanhSach.setRowHeight(70); // Cao để chứa ảnh
        
        JScrollPane scroll = new JScrollPane(tblDanhSach);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // --- RIGHT PANEL: GIỎ HÀNG & THANH TOÁN (ĐÃ THIẾT KẾ LẠI) ---
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 1. Header Giỏ hàng
        JLabel title = new JLabel(" GIỎ HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 123, 255));
        title.setBorder(new EmptyBorder(15, 10, 10, 10));
        panel.add(title, BorderLayout.NORTH);

        // 2. Bảng Giỏ hàng
        tblGioHang = new JTable();
        tblGioHang.setRowHeight(60);
        JScrollPane scroll = new JScrollPane(tblGioHang);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        // 3. Panel Thanh toán (GridBagLayout)
        JPanel payPanel = new JPanel(new GridBagLayout());
        payPanel.setBackground(new Color(248, 249, 250)); // Xám rất nhạt
        payPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 15);
        Font fontBig = new Font("Segoe UI", Font.BOLD, 22);

        // --- DÒNG 1: Khuyến mãi (ComboBox) ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel l1 = new JLabel("Khuyến mãi:"); l1.setFont(fontLabel);
        payPanel.add(l1, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.addItem("Không áp dụng");
        loadKhuyenMaiData(); // Load DB
        cbKhuyenMai.addActionListener(e -> capNhatTongTien()); 
        payPanel.add(cbKhuyenMai, gbc);

        // --- DÒNG 2: Phương thức thanh toán ---
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Thanh toán:"); l2.setFont(fontLabel);
        payPanel.add(l2, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        cbPhuongThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử"});
        payPanel.add(cbPhuongThucTT, gbc);

        // --- DÒNG 3: Tổng tiền ---
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("TỔNG CỘNG:"); l3.setFont(fontBold);
        payPanel.add(l3, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        lblTongTien = new JLabel("0 đ");
        lblTongTien.setFont(fontBig);
        lblTongTien.setForeground(new Color(220, 53, 69)); // Đỏ
        lblTongTien.setHorizontalAlignment(JLabel.RIGHT);
        payPanel.add(lblTongTien, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        payPanel.add(new JSeparator(), gbc);

        // --- DÒNG 4: Tiền khách đưa ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel l4 = new JLabel("Khách đưa:"); l4.setFont(fontLabel);
        payPanel.add(l4, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtTienKhachDua = new JTextField();
        txtTienKhachDua.setFont(fontBold);
        txtTienKhachDua.setHorizontalAlignment(JTextField.RIGHT);
        // Sự kiện tính tiền thừa
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhTienThua(); }
            public void removeUpdate(DocumentEvent e) { tinhTienThua(); }
            public void changedUpdate(DocumentEvent e) { tinhTienThua(); }
        });
        payPanel.add(txtTienKhachDua, gbc);

        // --- DÒNG 5: Tiền thừa ---
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel l5 = new JLabel("Tiền thừa:"); l5.setFont(fontLabel);
        payPanel.add(l5, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        lblTienThua = new JLabel("0 đ");
        lblTienThua.setFont(fontBold);
        lblTienThua.setForeground(new Color(40, 167, 69)); // Xanh
        lblTienThua.setHorizontalAlignment(JLabel.RIGHT);
        payPanel.add(lblTienThua, gbc);

        // --- DÒNG 6: Nút Thanh toán ---
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.insets = new Insets(15, 10, 15, 10);
        JButton btnPay = createGreenButton("THANH TOÁN & IN HÓA ĐƠN");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setPreferredSize(new Dimension(200, 50));
        btnPay.addActionListener(e -> thanhToan());
        payPanel.add(btnPay, gbc);

        panel.add(payPanel, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // 4. LOGIC & XỬ LÝ DỮ LIỆU
    // =========================================================================

    // Hàm căn giữa và format bảng
    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setOpaque(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);

        // Renderer căn giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Renderer căn trái (cho Tên)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 5, 0, 0));

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 2) { // Cột tên sản phẩm luôn là cột 2
                table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private void loadKhuyenMaiData() {
        try (Connection conn = new Database.DBConnection().getConnect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ma, phan_tram_giam FROM khuyenmai WHERE han_dung >= CURDATE()")) {
            while (rs.next()) {
                cbKhuyenMai.addItem(rs.getString("ma") + " - " + rs.getInt("phan_tram_giam") + "%");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void taiDanhSach() {
        this.danhSachHienTai = bll.layTatCa();
        capNhatBangDanhSach(this.danhSachHienTai);
    }

    private void capNhatBangDanhSach(List<FigureDTO> list) {
        // THÊM CỘT "Nhà cung cấp" vào đây
        String[] cols = {"ID", "Hình", "Tên Figure", "Loại", "Giá", "Kích thước", "Số lượng", "Nhà cung cấp", "Chi tiết", "Thêm"};
        
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c >= 8; }
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
        };
        
        this.danhSachHienTai = list; 
        for (FigureDTO f : list) {
            ImageIcon icon = loadResizedIcon(f.getHinhAnh(), 60, 60); // Tăng size ảnh lên chút cho rõ
            model.addRow(new Object[]{
                    f.getId(), icon, f.getTen(), f.getLoai(), 
                    String.format("%,.0f", f.getGia()), f.getKichThuoc(), f.getSoLuong(),
                    f.getTenNCC(), // <--- THÊM TEN NCC VÀO ĐÂY
                    "Chi tiết", "Thêm"
            });
        }
        tblDanhSach.setModel(model);

        // --- Cập nhật lại chiều rộng cột ---
        tblDanhSach.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tblDanhSach.getColumnModel().getColumn(1).setPreferredWidth(80);  // Ảnh
        tblDanhSach.getColumnModel().getColumn(2).setPreferredWidth(200); // Tên
        tblDanhSach.getColumnModel().getColumn(3).setPreferredWidth(100); // Loại
        tblDanhSach.getColumnModel().getColumn(4).setPreferredWidth(120); // Giá
        tblDanhSach.getColumnModel().getColumn(5).setPreferredWidth(100); // Kích thước
        tblDanhSach.getColumnModel().getColumn(6).setPreferredWidth(80);  // Số lượng
        tblDanhSach.getColumnModel().getColumn(7).setPreferredWidth(150); // NCC (Mới)
        tblDanhSach.getColumnModel().getColumn(8).setPreferredWidth(80);  // Chi tiết (Button)
        tblDanhSach.getColumnModel().getColumn(9).setPreferredWidth(80);  // Thêm (Button)
        // ------------------------------------
        
        styleTable(tblDanhSach); // Hàm căn giữa có sẵn

        // --- [QUAN TRỌNG] THÊM ĐOẠN NÀY ĐỂ HIỂN THỊ ẢNH ---
        tblDanhSach.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(JLabel.CENTER);
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                    label.setText("");
                } else {
                    label.setText("No IMG");
                }
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setOpaque(true);
                }
                return label;
            }
        });
        // --------------------------------------------------

        // --- Cập nhật lại chỉ số cột cho Button Renderer/Editor ---
        tblDanhSach.getColumnModel().getColumn(8).setCellRenderer(new DetailButtonRenderer()); // Cột Chi tiết
        tblDanhSach.getColumnModel().getColumn(8).setCellEditor(new DetailButtonEditor(new JCheckBox())); 
        tblDanhSach.getColumnModel().getColumn(9).setCellRenderer(new AddButtonRenderer());    // Cột Thêm
        tblDanhSach.getColumnModel().getColumn(9).setCellEditor(new AddButtonEditor(new JCheckBox())); 
    }

    private void capNhatGioHang() {
        String[] cols = {"ID", "Hình", "Tên SP", "SL", "Thành tiền", "Xóa"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
        };

        for (GioHangItemDTO i : bll.getGioHang()) {
            ImageIcon icon = loadResizedIcon(i.getFigure().getHinhAnh(), 50, 50);
            model.addRow(new Object[]{
                i.getFigure().getId(), icon, i.getFigure().getTen(),
                i.getSoLuong(), String.format("%,.0f", i.getThanhTien()), "Xóa"
            });
        }
        tblGioHang.setModel(model);
        
        // Set Width
        tblGioHang.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblGioHang.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblGioHang.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        styleTable(tblGioHang); // Căn giữa text

        // --- [QUAN TRỌNG] THÊM ĐOẠN NÀY ĐỂ HIỂN THỊ ẢNH TRONG GIỎ HÀNG ---
        tblGioHang.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(JLabel.CENTER);
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                    label.setText("");
                } else {
                    label.setText("No IMG");
                }
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setOpaque(true);
                }
                return label;
            }
        });
        // ------------------------------------------------------------------

        // Button Xóa
        tblGioHang.getColumnModel().getColumn(5).setCellRenderer(new DeleteButtonRenderer());
        tblGioHang.getColumnModel().getColumn(5).setCellEditor(new DeleteButtonEditor(new JCheckBox())); 

        capNhatTongTien();
    }

    private void capNhatTongTien() {
        long tongGoc = 0;
        for (GioHangItemDTO i : bll.getGioHang()) {
            tongGoc += i.getThanhTien();
        }

        int phanTram = 0;
        String kmSelect = (String) cbKhuyenMai.getSelectedItem();
        if (kmSelect != null && !kmSelect.equals("Không áp dụng")) {
            try {
                String[] parts = kmSelect.split(" - ");
                if (parts.length > 1) phanTram = Integer.parseInt(parts[1].replace("%", ""));
            } catch(Exception e) {}
        }
        this.phanTramGiam = phanTram;

        long giamGia = tongGoc * phanTram / 100;
        tongTienHienTai = tongGoc - giamGia;

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblTongTien.setText(nf.format(tongTienHienTai));
        
        tinhTienThua();
    }

    private void tinhTienThua() {
        try {
            String text = txtTienKhachDua.getText().replace(".", "").replace(",", "").trim();
            if (text.isEmpty()) {
                lblTienThua.setText("0 đ"); return;
            }
            long tienKhach = Long.parseLong(text);
            long tienThua = tienKhach - tongTienHienTai;
            
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            lblTienThua.setText(nf.format(tienThua));
            
            if (tienThua < 0) lblTienThua.setForeground(Color.RED);
            else lblTienThua.setForeground(new Color(40, 167, 69));
            
        } catch (NumberFormatException e) {
            lblTienThua.setText("0 đ");
        }
    }

    private void themVaoGio(int id, int soLuong) {
        if (bll.themVaoGio(id, soLuong)) {
            capNhatGioHang();
            for (FigureDTO fig : this.danhSachHienTai) {
                if (fig.getId() == id) {
                    fig.setSoLuong(fig.getSoLuong() - soLuong); break;
                }
            }
            capNhatBangDanhSach(this.danhSachHienTai);
        } else {
            JOptionPane.showMessageDialog(this, "Không đủ hàng hoặc lỗi kho!");
        }
    }

    private void xoaKhoiGio(int id) {
        int slTra = 0;
        for(GioHangItemDTO i : bll.getGioHang()) if(i.getFigureId()==id) slTra=i.getSoLuong();

        if (bll.xoaKhoiGio(id)) {
            capNhatGioHang();
            for (FigureDTO fig : this.danhSachHienTai) {
                if (fig.getId() == id) {
                    fig.setSoLuong(fig.getSoLuong() + slTra); break;
                }
            }
            capNhatBangDanhSach(this.danhSachHienTai);
        }
    }

    private void thanhToan() {
        // 1. Kiểm tra đăng nhập
        if (nguoiDungHienTai == null) { 
            JOptionPane.showMessageDialog(this, "Chưa đăng nhập!"); return; 
        }
        
        // --- [LỚP BẢO VỆ MỚI: CHECK SESSION CHẾT] ---
        if (nguoiDungHienTai.getMaNguoiDung() <= 0) {
            JOptionPane.showMessageDialog(this, 
                "Phiên làm việc không hợp lệ (Lỗi ID Nhân viên)!\nVui lòng đăng nhập lại.", 
                "Lỗi hệ thống", 
                JOptionPane.ERROR_MESSAGE);
            
            // Tự động đăng xuất để fix lỗi cho người dùng
            this.dispose();
            new LoginUI().setVisible(true);
            return;
        }
        // ---------------------------------------------
        
        // 2. Kiểm tra giỏ hàng
        if (bll.getGioHang().isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng!"); return; 
        }
        
        // 3. Kiểm tra tiền khách đưa (nếu là Tiền mặt)
        String pttt = (String) cbPhuongThucTT.getSelectedItem();
        if ("Tiền mặt".equals(pttt)) {
            try {
                String textTien = txtTienKhachDua.getText().replace(".", "").replace(",", "").trim();
                if (textTien.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khách đưa!"); return;
                }
                long khachDua = Long.parseLong(textTien);
                if (khachDua < tongTienHienTai) {
                     JOptionPane.showMessageDialog(this, "Khách đưa chưa đủ tiền!"); return;
                }
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(this, "Tiền nhập không hợp lệ!"); return;
            }
        }

        // 4. Xử lý Mã KM
        String maKM = null;
        Object itemSelect = cbKhuyenMai.getSelectedItem();
        if (itemSelect != null && !itemSelect.toString().equals("Không áp dụng")) {
            try {
                // Cắt chuỗi an toàn
                String raw = itemSelect.toString();
                maKM = raw.contains("-") ? raw.split("-")[0].trim() : raw.trim();
            } catch (Exception e) {}
        }
        if (maKM != null && maKM.isEmpty()) maKM = null;

        // 5. Map PTTT
        String ptttDB = switch(pttt) {
            case "Tiền mặt" -> "TienMat";
            case "Chuyển khoản" -> "ChuyenKhoan";
            case "Thẻ" -> "The";
            default -> "ViDienTu";
        };

        // 6. Gọi BLL xử lý
        DonHangDTO donHang = bll.thanhToan(nguoiDungHienTai.getMaNguoiDung(), ptttDB, maKM);
        
        if (donHang != null) {
            // --- [THAY ĐỔI THỨ TỰ CODE TẠI ĐÂY] ---

            // B1: Cập nhật giao diện chính trước (cho sạch sẽ)
            capNhatGioHang(); 
            taiDanhSach();    
            
            // B2: Reset ô nhập liệu
            txtTienKhachDua.setText("");
            lblTienThua.setText("0 đ");
            cbKhuyenMai.setSelectedIndex(0);

            // B3: Hiện thông báo "Thanh toán thành công" TRƯỚC
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");

            // B4: Sau khi bấm OK ở thông báo trên, mới hiện Hóa đơn ra
            hienThiPopupHoaDon(donHang);
            
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi thanh toán! (Vui lòng kiểm tra kho hoặc kết nối)");
        }
    }

    private void hienThiPopupHoaDon(DonHangDTO donHang) {
        JDialog d = new JDialog(this, "HÓA ĐƠN THANH TOÁN", true);
        d.setSize(400, 500);
        d.setLocationRelativeTo(this);
        JTextArea txt = new JTextArea();
        txt.setFont(new Font("Consolas", Font.PLAIN, 13));
        txt.setEditable(false);
        
        StringBuilder sb = new StringBuilder();
        sb.append("        MAHIRU SHOP\n");
        sb.append("---------------------------\n");
        sb.append("Mã ĐH: ").append(donHang.getMaDonHang()).append("\n");
        sb.append("Ngày: ").append(donHang.getNgayDat()).append("\n");
        sb.append("---------------------------\n");
        for(GioHangItemDTO i : donHang.getGioHang()) {
            sb.append(String.format("%-20s x%d\n", 
                i.getFigure().getTen().length()>20?i.getFigure().getTen().substring(0,18)+"..":i.getFigure().getTen(), 
                i.getSoLuong()));
            sb.append(String.format("%25s\n", String.format("%,.0f", i.getThanhTien())));
        }
        sb.append("---------------------------\n");
        sb.append("TỔNG TIỀN: ").append(String.format("%,.0f VND", donHang.getTongTien())).append("\n");
        sb.append("\nCảm ơn quý khách!");
        
        txt.setText(sb.toString());
        d.add(new JScrollPane(txt));
        d.setVisible(true);
    }

    private void moChiTiet(FigureDTO f) {
        JDialog d = new JDialog(MainUI.this, "Chi tiết sản phẩm", true);
        d.setSize(650, 500); // Tăng chiều cao lên một chút cho thoải mái
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(Color.WHITE);
        d.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20); // Padding
        
        // --- 1. ẢNH (BÊN TRÁI) ---
        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(250, 250));
        lblImg.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = loadResizedIcon(f.getHinhAnh(), 240, 240); 
        if(icon != null) lblImg.setIcon(icon);
        else lblImg.setText("No Image");

        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.gridheight = 8; // Tăng số dòng chiếm dụng lên 8 để cân đối với bên phải
        gbc.anchor = GridBagConstraints.NORTH; // Neo ảnh lên trên cùng
        p.add(lblImg, gbc);

        // --- 2. THÔNG TIN (BÊN PHẢI) ---
        gbc.gridx = 1; 
        gbc.gridheight = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Cho phép giãn ngang
        
        // Tên sản phẩm
        JLabel lName = new JLabel("<html><div style='width:280px'><b>" + f.getTen() + "</b></div></html>");
        lName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        p.add(lName, gbc);

        // ID
        gbc.gridy++; 
        JLabel lblID = new JLabel("ID Sản phẩm: " + f.getId());
        lblID.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblID.setForeground(Color.GRAY);
        p.add(lblID, gbc);

        // Các thông tin khác
        gbc.gridy++; p.add(new JLabel("Loại: " + f.getLoai()), gbc);
        gbc.gridy++; p.add(new JLabel("Size: " + f.getKichThuoc()), gbc);
        gbc.gridy++; p.add(new JLabel("Kho: " + f.getSoLuong()), gbc);
        
        // THÊM THÔNG TIN NHÀ CUNG CẤP VÀO ĐÂY
        gbc.gridy++; 
        p.add(new JLabel("Nhà cung cấp: " + (f.getTenNCC() != null ? f.getTenNCC() : "Không xác định")), gbc);
        
        // Giá
        gbc.gridy++; 
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        JLabel lPrice = new JLabel(nf.format(f.getGia()));
        lPrice.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lPrice.setForeground(new Color(220, 53, 69));
        p.add(lPrice, gbc);

        // Tiêu đề Mô tả
        gbc.gridy++;
        p.add(new JLabel("Mô tả:"), gbc); 

        // --- [SỬA LỖI TẠI ĐÂY] ---
        gbc.gridy++;
        JTextArea txtMoTa = new JTextArea(f.getMoTa());
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setEditable(false);
        // txtMoTa.setBackground(new Color(245, 245, 245)); // Nếu thích nền xám thì bỏ comment
        
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Cấu hình quan trọng để ô mô tả bung ra
        scrollMoTa.setPreferredSize(new Dimension(280, 100)); // Kích thước mặc định
        gbc.fill = GridBagConstraints.BOTH; // Giãn cả ngang lẫn dọc
        gbc.weighty = 1.0; // [QUAN TRỌNG] Chiếm hết khoảng trống dọc còn lại
        
        p.add(scrollMoTa, gbc);

        d.add(p, BorderLayout.CENTER);
        d.setVisible(true);
    }

    private ImageIcon loadResizedIcon(String filename, int w, int h) {
         if (filename == null || filename.isEmpty()) return null;
         try {
             URL url = getClass().getResource("/Resources/figure_images/" + filename);
             if (url != null) {
                 BufferedImage img = ImageIO.read(url);
                 return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
             }
         } catch(Exception e) {}
         return null;
    }
    
    private void timKiemNangCao() {
        String ten = txtTenTimKiem.getText().trim();
        String loai = "Tất cả".equals(cbLoai.getSelectedItem()) ? null : (String) cbLoai.getSelectedItem();
        Double min = parseDouble(txtMinGia.getText());
        Double max = parseDouble(txtMaxGia.getText());
        String kt = "Tất cả".equals(cbKichThuoc.getSelectedItem()) ? null : (String) cbKichThuoc.getSelectedItem();
        
        // --- LẤY MÃ NCC (Nếu bạn chưa làm ComboBox NCC bên MainUI thì để là 0 hoặc null) ---
        Integer maNCC = 0; 
        // Nếu bạn đã thêm cbLocNCC thì dùng dòng dưới:
        // if (cbLocNCC != null && cbLocNCC.getSelectedIndex() > 0) maNCC = ((NhaCungCapDTO)cbLocNCC.getSelectedItem()).getMaNCC();
        
        // Truyền thêm maNCC vào hàm
        this.danhSachHienTai = bll.timKiemNangCao(ten, loai, min, max, kt, maNCC);
        capNhatBangDanhSach(this.danhSachHienTai);
    }
    
    private Double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch(Exception e) { return null; }
    }
    
    // --- HỖ TRỢ ---
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("Arial", Font.BOLD, 14));
        menuItem.setBackground(new Color(50, 50, 50));
        menuItem.setForeground(Color.WHITE);
        menuItem.setOpaque(true);
        menuItem.setBorder(new EmptyBorder(10, 15, 10, 15));
        menuItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { menuItem.setBackground(new Color(80, 80, 80)); }
            public void mouseExited(MouseEvent e) { menuItem.setBackground(new Color(50, 50, 50)); }
        });
        return menuItem;
    }

    private JLabel createLabelWhite(String t) { JLabel l = new JLabel(t); l.setForeground(Color.WHITE); l.setFont(new Font("Segoe UI", Font.BOLD, 14)); return l; }
    private JButton createRedButton(String t) { JButton b = new JButton(t); b.setBackground(new Color(220, 53, 69)); b.setForeground(Color.WHITE); return b; }
    private JButton createGreenButton(String t) { JButton b = new JButton(t); b.setBackground(new Color(40, 167, 69)); b.setForeground(Color.WHITE); return b; }

    // --- INNER CLASSES ---
    private class DetailButtonRenderer extends JButton implements TableCellRenderer {
        public DetailButtonRenderer() {
            setText("Chi tiết"); setBackground(new Color(23, 162, 184)); setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return this; }
    }
    private class DetailButtonEditor extends DefaultCellEditor {
        JButton b; int r;
        public DetailButtonEditor(JCheckBox cb) {
            super(cb); b = new JButton("Chi tiết");
            b.addActionListener(e -> {
                int id = (int)tblDanhSach.getModel().getValueAt(r, 0);
                for(FigureDTO f: danhSachHienTai) if(f.getId()==id) { moChiTiet(f); break; }
                fireEditingStopped();
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int c) { r=row; return b; }
        public Object getCellEditorValue() { return "Chi tiết"; }
    }

    private class AddButtonRenderer extends JButton implements TableCellRenderer {
        public AddButtonRenderer() {
            setText("Thêm"); setBackground(new Color(40, 167, 69)); setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return this; }
    }
    private class AddButtonEditor extends DefaultCellEditor {
        JButton b; int r;
        public AddButtonEditor(JCheckBox cb) {
            super(cb); b = new JButton("Thêm");
            b.addActionListener(e -> {
                int id = (int)tblDanhSach.getModel().getValueAt(r, 0);
                String sl = JOptionPane.showInputDialog("Nhập số lượng:");
                try { if(sl!=null) themVaoGio(id, Integer.parseInt(sl)); } catch(Exception ex){}
                fireEditingStopped();
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int c) { r=row; return b; }
        public Object getCellEditorValue() { return "Thêm"; }
    }

    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Xóa"); setBackground(new Color(220, 53, 69)); setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return this; }
    }
    private class DeleteButtonEditor extends DefaultCellEditor {
        JButton b; int r;
        public DeleteButtonEditor(JCheckBox cb) {
            super(cb); b = new JButton("Xóa");
            b.addActionListener(e -> {
                int id = (int)tblGioHang.getModel().getValueAt(r, 0);
                xoaKhoiGio(id);
                fireEditingStopped();
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int c) { r=row; return b; }
        public Object getCellEditorValue() { return "Xóa"; }
    }
}