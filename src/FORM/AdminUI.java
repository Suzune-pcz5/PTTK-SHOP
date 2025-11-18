package FORM;

import Database.DBConnection;
import DTO.NguoiDungDTO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;       // Sửa lỗi ArrayList
import java.util.List;            // Sửa lỗi List
import javax.imageio.ImageIO;
import java.io.File;              // Sửa lỗi File
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;  // Sửa lỗi chọn file ảnh (nếu chưa có)
import java.awt.Image;            // Sửa lỗi xử lý ảnh
import javax.swing.ImageIcon;     // Sửa lỗi hiển thị icon

public class AdminUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel tongQuanPanel, nhanVienPanel, donHangPanel, sanPhamPanel, khoPanel, baoCaoPanel;
    private NguoiDungDTO currentUser;
    private DBConnection db;
    private DefaultTableModel sanPhamModel;
    private JTable sanPhamTable;
    private JTextField txtTimTen, txtGiaTu, txtGiaDen;
    private JComboBox<String> cbTimLoai, cbTimKichThuoc;
    private File fileAnhMoi = null; // Biến tạm để lưu file ảnh khi chọn

    public AdminUI() {
        this(null);
    }

    public AdminUI(NguoiDungDTO nd) {
        this.currentUser = nd;
        this.db = new DBConnection();
        initComponents();
    }

    private void initComponents() {
        setTitle("MAHIRU.ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1400, 800));
        setLayout(new BorderLayout());

        // ================== HEADER ==================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("MAHIRU.ADMIN", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        // Dùng GridBagLayout để dễ dàng căn giữa nút theo chiều dọc
        JPanel rightHeader = new JPanel(new GridBagLayout());
        rightHeader.setOpaque(false);
        rightHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // Cách lề phải 20px

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 15); // Khoảng cách giữa tên và nút

        // 1. Hiển thị tên user
        if (currentUser != null) {
            JLabel userLbl = new JLabel("Xin chào, " + currentUser.getTenDangNhap());
            userLbl.setForeground(Color.WHITE);
            userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            rightHeader.add(userLbl, gbc);
        }

        // 2. Tạo nút Đăng xuất BO GÓC
        gbc.gridx++; // Chuyển sang cột tiếp theo
        JButton logoutBtn = new JButton("Đăng xuất") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Màu nền (Đỏ)
                g2.setColor(new Color(220, 53, 69));
                // Vẽ hình chữ nhật bo tròn (radius 20)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g); // Vẽ chữ lên trên
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Không vẽ viền mặc định để tránh hình chữ nhật đè lên
            }
        };

        // Cấu hình giao diện nút
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false); // Quan trọng: Tắt nền mặc định để thấy bo tròn
        logoutBtn.setBorderPainted(false); // Tắt viền mặc định
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });

        rightHeader.add(logoutBtn, gbc);

        header.add(title, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ================== TABBED PANE ==================
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tongQuanPanel = taoTongQuanPanel();
        nhanVienPanel = taoNhanVienPanel();
        donHangPanel = taoQuanLyDonHangPanel();
        sanPhamPanel = taoSanPhamPanel();
        khoPanel = taoKhoPanel();
        baoCaoPanel = taoBaoCaoPanel();

        tabbedPane.addTab("Tổng quan", tongQuanPanel);
        tabbedPane.addTab("Quản lý nhân viên", nhanVienPanel);
        tabbedPane.addTab("Quản lý đơn hàng", donHangPanel);
        tabbedPane.addTab("Quản lý sản phẩm", sanPhamPanel);
        tabbedPane.addTab("Quản lý kho", khoPanel);
        tabbedPane.addTab("Báo cáo thống kê", baoCaoPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Mahiru shop", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.setBackground(new Color(30, 30, 30));
        footer.setForeground(Color.WHITE);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ================== TỔNG QUAN ==================
    private JPanel taoTongQuanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Tổng quan hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 20, 0));
        statsPanel.setMaximumSize(new Dimension(1800, 120));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        try (Connection conn = db.getConnect()) {
            int tongHangBan = getInt(conn,
                "SELECT COALESCE(SUM(c.so_luong), 0) FROM chitiet_donhang c " +
                "JOIN donhang d ON c.donhangId = d.ma_don_hang WHERE d.trang_thai = 'Đã thanh toán'", 0);

            int tongDonHang = getInt(conn,
                "SELECT COUNT(*) FROM donhang WHERE trang_thai = 'Đã thanh toán'", 0);

            int tonKho = getInt(conn,
                "SELECT COALESCE(SUM(so_luong), 0) FROM figure WHERE trang_thai = 'Mở'", 0);
 
            int tongNV = 0;
            try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM nguoidung WHERE vai_tro = 'NhanVien' AND trang_thai = 'Mở'")) {
                if (rs.next()) tongNV = rs.getInt(1);
            }

            long tongDoanhThu = getLong(conn,
                "SELECT COALESCE(SUM(tong_tien), 0) FROM donhang WHERE trang_thai = 'Đã thanh toán'", 0L);

            statsPanel.add(taoCardThongKe("Tổng hàng đã bán", String.valueOf(tongHangBan), new Color(255, 99, 132)));
            statsPanel.add(taoCardThongKe("Tổng đơn hàng", String.valueOf(tongDonHang), new Color(54, 162, 235)));
            statsPanel.add(taoCardThongKe("Tồn kho", String.valueOf(tonKho), new Color(255, 206, 86)));
            statsPanel.add(taoCardThongKe("Tổng doanh thu", currency.format(tongDoanhThu), new Color(75, 192, 192)));
        } catch (Exception e) { e.printStackTrace(); }
        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(40));

        // === SẢN PHẨM BÁN CHẠY ===
        JLabel topTitle = new JLabel("Sản phẩm bán chạy");
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(topTitle);
        panel.add(Box.createVerticalStrut(15));

        JPanel topProductsPanel = new JPanel(new GridLayout(1, 5, 25, 0));
        topProductsPanel.setMaximumSize(new Dimension(1600, 360));
        topProductsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT f.ten, f.hinh_anh, f.gia, COALESCE(SUM(c.so_luong), 0) AS sl " +
                 "FROM figure f LEFT JOIN chitiet_donhang c ON f.id = c.figureId " +
                 "LEFT JOIN donhang d ON c.donhangId = d.ma_don_hang AND d.trang_thai = 'Đã thanh toán' " +
                 "GROUP BY f.id ORDER BY sl DESC LIMIT 5")) {

            ResultSet rs = ps.executeQuery();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while (rs.next()) {
                ImageIcon icon = loadProductImage(rs.getString("hinh_anh"));
                topProductsPanel.add(taoTopProductCard(
                    rs.getString("ten"), icon, nf.format(rs.getLong("gia")),
                    "Đã bán: " + rs.getInt("sl") + " cái"
                ));
            }
            while (topProductsPanel.getComponentCount() < 5) {
                topProductsPanel.add(taoTopProductCard("Chưa có dữ liệu", null, "", "Đã bán: 0 cái"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        JScrollPane sp = new JScrollPane(topProductsPanel);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(0, 380));
        panel.add(sp);

        JScrollPane mainScroll = new JScrollPane(panel);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(mainScroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel taoCardThongKe(String tieuDe, String giaTri, Color mau) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setPreferredSize(new Dimension(300, 110));

        JLabel lblTitle = new JLabel(tieuDe, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel lblValue = new JLabel(giaTri, JLabel.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(mau);
        lblValue.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel taoTopProductCard(String ten, ImageIcon icon, String gia, String daBan) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding trong thẻ
        
        // 1. Ảnh sản phẩm
        JLabel lblImg = new JLabel();
        lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (icon != null) {
            // Scale ảnh đẹp (150x150)
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(img));
        } else {
            lblImg.setText("No Image");
            lblImg.setPreferredSize(new Dimension(150, 150));
            lblImg.setHorizontalAlignment(JLabel.CENTER);
        }
        card.add(lblImg);
        card.add(Box.createVerticalStrut(15)); // Khoảng cách

        // 2. Tên sản phẩm (Dùng HTML để xuống dòng + set size cố định)
        JLabel lblTen = new JLabel("<html><center>" + ten + "</center></html>");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTen.setHorizontalAlignment(JLabel.CENTER);
        // Cố định chiều cao cho tên (đủ cho 2 dòng) để không bị lệch giá bên dưới
        lblTen.setPreferredSize(new Dimension(180, 45)); 
        lblTen.setMaximumSize(new Dimension(180, 45)); 
        card.add(lblTen);
        card.add(Box.createVerticalStrut(5));

        // 3. Giá tiền
        JLabel lblGia = new JLabel(gia);
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGia.setForeground(new Color(220, 53, 69)); // Màu đỏ
        lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblGia);
        card.add(Box.createVerticalStrut(5));

        // 4. Số lượng bán
        JLabel lblBan = new JLabel(daBan);
        lblBan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBan.setForeground(Color.GRAY);
        lblBan.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblBan);
        card.add(Box.createVerticalStrut(15));

        // 5. Nút Sửa (Xanh lá)
        JButton btnSua = new JButton("Sửa");
        btnSua.setBackground(new Color(40, 167, 69));
        btnSua.setForeground(Color.WHITE);
        btnSua.setFocusPainted(false);
        btnSua.setAlignmentX(Component.CENTER_ALIGNMENT);
        // (Thêm actionListener nếu cần)
        
        card.add(btnSua);

        return card;
    }

    private ImageIcon loadProductImage(String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) return null;
            
            // Đường dẫn tuyệt đối tới thư mục dự án
            java.io.File imgFile = new java.io.File("src/Resources/figure_images/" + filename);
            
            if (!imgFile.exists()) {
                // System.out.println("Không thấy ảnh: " + imgFile.getAbsolutePath());
                return null;
            }
            
            BufferedImage img = ImageIO.read(imgFile);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private int getInt(Connection conn, String sql, int def) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : def;
        }
    }

    private long getLong(Connection conn, String sql, long def) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : def;
        }
    }

    private void centerAllTableCells(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(60, 60, 60));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    // ==================QUẢN LÝ NHÂN VIÊN ==================
    private DefaultTableModel nhanVienModel;
    private JTable nhanVienTable;

    private JPanel taoNhanVienPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // --- 1. HEADER (TIÊU ĐỀ & NÚT THÊM) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Cách bảng 1 chút

        JLabel title = new JLabel("Quản lý nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnAdd = new JButton("+ Thêm nhân viên");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0, 123, 255)); // Màu xanh dương
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(160, 35));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> hienThiFormThemNhanVien());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- 2. BẢNG NHÂN VIÊN ---
        String[] cols = {"#", "Tên đăng nhập", "Email", "Vai trò", "Trạng thái", "Khóa", "Hành động"};
        nhanVienModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5 || c == 6; } // Chỉ sửa cột Khóa và Hành động
        };

        nhanVienTable = new JTable(nhanVienModel);
        nhanVienTable.setRowHeight(50); // Chiều cao hàng thoáng
        nhanVienTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Căn chỉnh kích thước cột
        nhanVienTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // #
        nhanVienTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên đăng nhập
        nhanVienTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email

        styleTableHeader(nhanVienTable);    // Dùng lại hàm style chung
        centerAllTableCells(nhanVienTable); // Dùng lại hàm căn giữa chung

        // --- SETUP CỘT "KHÓA" (TOGGLE BUTTON) ---
        nhanVienTable.getColumn("Khóa").setCellRenderer((t, v, s, h, r, c) -> {
            boolean isActive = (Boolean) v; // True = Mở, False = Tắt
            JToggleButton btn = new JToggleButton(isActive ? "Mở" : "Khóa");
            // Màu xanh lá nếu Mở, Đỏ nếu Khóa
            btn.setBackground(isActive ? new Color(23, 162, 184) : new Color(220, 53, 69));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nhanVienTable.getColumn("Khóa").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JToggleButton btn; boolean currState;
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                currState = (Boolean) v;
                btn = new JToggleButton(currState ? "Mở" : "Khóa", currState);
                btn.addActionListener(e -> {
                    currState = !currState;
                    String username = (String) t.getValueAt(r, 1); // Lấy tên đăng nhập ở cột 1
                    
                    // Cập nhật Database (Mở/Tắt)
                    updateUserStatus(username, currState ? "Mở" : "Tắt");
                    
                    // Cập nhật hiển thị bảng
                    t.setValueAt(currState ? "Hoạt động" : "Đã khóa", r, 4); // Cập nhật cột Trạng thái (Text)
                    fireEditingStopped(); // Dừng edit để lưu giá trị boolean mới
                });
                return btn;
            }
            @Override public Object getCellEditorValue() { return currState; }
        });

        // --- SETUP CỘT "HÀNH ĐỘNG" (NÚT SỬA XANH LÁ) ---
        nhanVienTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Sửa");
            btn.setBackground(new Color(40, 167, 69)); // Xanh lá cây
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nhanVienTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                JButton btn = new JButton("Sửa");
                btn.setBackground(new Color(40, 167, 69));
                btn.addActionListener(e -> {
                    String username = (String) t.getValueAt(r, 1);
                    hienThiFormSuaNhanVien(username);
                    fireEditingStopped();
                });
                return btn;
            }
        });

        panel.add(new JScrollPane(nhanVienTable), BorderLayout.CENTER);
        loadNhanVienData(); // Load dữ liệu
        return panel;
    }

        private void hienThiFormThemNhanVien() {
        JDialog dialog = new JDialog(this, "Thêm nhân viên mới", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0); // Khoảng cách dưới

        // Fields
        JTextField txtUser = styleTextField();
        JPasswordField txtPass = new JPasswordField(); // Cần style riêng cho password nếu muốn
        txtPass.setPreferredSize(new Dimension(100, 35)); // Kích thước giống styleTextField
        
        JTextField txtEmail = styleTextField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"NhanVien", "Admin"});
        cbRole.setBackground(Color.WHITE);
        cbRole.setPreferredSize(new Dimension(100, 35));

        int y = 0;
        addLabelAndComponent(mainPanel, gbc, y++, "Tên đăng nhập:", txtUser);
        addLabelAndComponent(mainPanel, gbc, y++, "Mật khẩu:", txtPass);
        addLabelAndComponent(mainPanel, gbc, y++, "Email:", txtEmail);
        addLabelAndComponent(mainPanel, gbc, y++, "Vai trò:", cbRole);

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 245, 245));
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(110, 35));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(80, 35));
        btnCancel.addActionListener(e -> dialog.dispose());

        // Logic Thêm
        btnAdd.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();
            String em = txtEmail.getText().trim();
            String r = cbRole.getSelectedItem().toString();

            if (u.isEmpty() || p.isEmpty() || em.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đủ thông tin!");
                return;
            }

            try (Connection conn = db.getConnect();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO nguoidung (ten_dang_nhap, mat_khau, email, vai_tro, trang_thai) VALUES (?, ?, ?, ?, 'Mở')")) {
                ps.setString(1, u);
                ps.setString(2, p); 
                ps.setString(3, em);
                ps.setString(4, r);
                
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "Thêm thành công!");
                    loadNhanVienData();
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: Tên đăng nhập hoặc Email đã tồn tại!");
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnAdd);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void hienThiFormSuaNhanVien(String username) {
        JDialog dialog = new JDialog(this, "Chỉnh sửa: " + username, true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);

        // Fields
        JTextField txtEmail = styleTextField();
        JPasswordField txtPassNew = new JPasswordField();
        txtPassNew.setPreferredSize(new Dimension(100, 35));
        
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"NhanVien", "Admin"});
        cbRole.setBackground(Color.WHITE);
        
        // ComboBox Trạng thái: Map hiển thị -> DB
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Hoạt động", "Khóa"});
        cbStatus.setBackground(Color.WHITE);
        cbStatus.setPreferredSize(new Dimension(100, 35));

        // Load Data
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement("SELECT email, vai_tro, trang_thai FROM nguoidung WHERE ten_dang_nhap = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtEmail.setText(rs.getString("email"));
                cbRole.setSelectedItem(rs.getString("vai_tro"));
                
                // Logic map trạng thái DB -> UI
                String dbStatus = rs.getString("trang_thai"); // "Mở" hoặc "Tắt"
                if ("Tắt".equalsIgnoreCase(dbStatus)) {
                    cbStatus.setSelectedItem("Khóa");
                } else {
                    cbStatus.setSelectedItem("Hoạt động");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        int y = 0;
        addLabelAndComponent(mainPanel, gbc, y++, "Email:", txtEmail);
        addLabelAndComponent(mainPanel, gbc, y++, "Mật khẩu mới (để trống nếu ko đổi):", txtPassNew);
        addLabelAndComponent(mainPanel, gbc, y++, "Vai trò:", cbRole);
        addLabelAndComponent(mainPanel, gbc, y++, "Trạng thái:", cbStatus);

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 245, 245));
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnSave = new JButton("Cập nhật");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(80, 35));

        // Logic Cập nhật
        btnSave.addActionListener(e -> {
            String newPass = new String(txtPassNew.getPassword()).trim();
            String uiStatus = cbStatus.getSelectedItem().toString();
            String dbStatus = "Khóa".equals(uiStatus) ? "Tắt" : "Mở"; // Map ngược UI -> DB

            StringBuilder sql = new StringBuilder("UPDATE nguoidung SET email=?, vai_tro=?, trang_thai=?");
            if (!newPass.isEmpty()) sql.append(", mat_khau=?");
            sql.append(" WHERE ten_dang_nhap=?");

            try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                ps.setString(1, txtEmail.getText());
                ps.setString(2, cbRole.getSelectedItem().toString());
                ps.setString(3, dbStatus);
                
                if (!newPass.isEmpty()) {
                    ps.setString(4, newPass);
                    ps.setString(5, username);
                } else {
                    ps.setString(4, username);
                }

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                    loadNhanVienData();
                    dialog.dispose();
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        
        // Logic Xóa
        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(dialog, "Xóa nhân viên này?") == JOptionPane.YES_OPTION) {
                try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("DELETE FROM nguoidung WHERE ten_dang_nhap=?")) {
                    ps.setString(1, username);
                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(dialog, "Đã xóa!");
                        loadNhanVienData();
                        dialog.dispose();
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Không thể xóa (Đang có dữ liệu liên quan)!"); }
            }
        });

        btnPanel.add(btnDelete);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Helper để tạo dòng trong form (Label + Component)
    private void addFormRow(JDialog d, GridBagConstraints gbc, String labelText, Component comp) {
        gbc.gridx = 0; 
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        d.add(lbl, gbc);
        
        gbc.gridx = 1;
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (comp instanceof JTextField) ((JTextField)comp).setPreferredSize(new Dimension(200, 30));
        d.add(comp, gbc);
    }
        
    // Hàm tải dữ liệu tách riêng để tái sử dụng khi thêm/sửa xong
    private void loadNhanVienData() {
        nhanVienModel.setRowCount(0);
        try (Connection conn = db.getConnect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT ten_dang_nhap, email, vai_tro, trang_thai FROM nguoidung")) {
            int i = 1;
            while (rs.next()) {
                boolean active = "Mở".equals(rs.getString("trang_thai"));
                nhanVienModel.addRow(new Object[]{ 
                    i++, 
                    rs.getString(1), 
                    rs.getString(2), 
                    rs.getString(3), 
                    active ? "Hoạt động" : "Khóa", 
                    active, 
                    "Sửa" 
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ================== QUẢN LÝ ĐƠN HÀNG  ========================
    private DefaultTableModel donHangModel;
    private JTable donHangTable;
    private JTextField txtDateFrom, txtDateTo;
    private JComboBox<String> cbStatusOrder, cbPhuongThuc;
    
    private JPanel taoQuanLyDonHangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // --- 1. HEADER (TIÊU ĐỀ & BỘ LỌC) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Quản lý đơn hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);

        // Panel Bộ lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        txtDateFrom = new JTextField(8); txtDateFrom.setToolTipText("dd/mm/yyyy");
        txtDateTo = new JTextField(8);   txtDateTo.setToolTipText("dd/mm/yyyy");
        
        cbStatusOrder = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Đã hủy", "Chờ xử lý"});
        cbStatusOrder.setBackground(Color.WHITE);
        
        cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan", "The", "ViDienTu"});
        cbPhuongThuc.setBackground(Color.WHITE);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(0, 123, 255)); // Màu xanh dương
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> loadDonHangData());

        filterPanel.add(new JLabel("Từ ngày:"));    filterPanel.add(txtDateFrom);
        filterPanel.add(new JLabel("Đến ngày:"));   filterPanel.add(txtDateTo);
        filterPanel.add(new JLabel("Trạng thái:")); filterPanel.add(cbStatusOrder);
        filterPanel.add(new JLabel("PTTT:"));       filterPanel.add(cbPhuongThuc);
        filterPanel.add(btnSearch);

        topPanel.add(filterPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- 2. BẢNG DỮ LIỆU ---
        String[] cols = {"Mã đơn", "Nhân viên", "Ngày tạo", "Tổng tiền", "Trạng thái", "PTTT", "Hành động"};
        donHangModel = new DefaultTableModel(cols, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return c == 6; } // Chỉ sửa cột nút bấm
        };
        
        donHangTable = new JTable(donHangModel);
        donHangTable.setRowHeight(50);
        styleTableHeader(donHangTable);
        centerAllTableCells(donHangTable);
        
        // Căn chỉnh độ rộng cột
        donHangTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        donHangTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Renderer: Màu chữ Trạng thái
        donHangTable.getColumn("Trạng thái").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st = (String) value;
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if ("Hoàn thành".equals(st) || "Đã thanh toán".equals(st)) lbl.setForeground(new Color(40, 167, 69));
                else if ("Đã hủy".equals(st)) lbl.setForeground(new Color(220, 53, 69));
                else lbl.setForeground(Color.BLACK);
                return lbl;
            }
        });

        // Renderer: Nút Chi tiết (Màu Xanh lá)
        donHangTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Chi tiết");
            btn.setBackground(new Color(40, 167, 69)); // Xanh lá
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            return btn;
        });

        // Editor: Xử lý sự kiện Click (Logic của bạn)
        donHangTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton btn;
            private String maDonHienTai;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                btn = new JButton("Chi tiết");
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                maDonHienTai = table.getModel().getValueAt(row, 0).toString(); // Lấy #123
                
                btn.addActionListener(e -> {
                    try {
                        int id = Integer.parseInt(maDonHienTai.replace("#", ""));
                        hienThiChiTietDonHangPopup(id);
                    } catch (Exception ex) { ex.printStackTrace(); }
                    fireEditingStopped();
                });
                return btn;
            }
            @Override public Object getCellEditorValue() { return "Chi tiết"; }
        });

        panel.add(new JScrollPane(donHangTable), BorderLayout.CENTER);
        loadDonHangData(); // Load lần đầu
        return panel;
    }

    private void loadDonHangData() {
        donHangModel.setRowCount(0);
        String from = txtDateFrom.getText().trim();
        String to = txtDateTo.getText().trim();
        String status = cbStatusOrder.getSelectedItem().toString();
        String pttt = cbPhuongThuc.getSelectedItem().toString();

        StringBuilder sql = new StringBuilder(
            "SELECT d.ma_don_hang, n.ten_dang_nhap, DATE_FORMAT(d.ngay_dat, '%d/%m/%Y %H:%i'), d.tong_tien, d.trang_thai, d.phuong_thuc_tt " +
            "FROM donhang d JOIN nguoidung n ON d.ma_nhan_vien = n.ma_nguoi_dung WHERE 1=1"
        );

        if (!from.isEmpty()) sql.append(" AND d.ngay_dat >= STR_TO_DATE('").append(from).append("', '%d/%m/%Y')");
        if (!to.isEmpty()) sql.append(" AND d.ngay_dat <= STR_TO_DATE('").append(to).append(" 23:59:59', '%d/%m/%Y %H:%i:%s')");
        if (!"Tất cả".equals(status)) sql.append(" AND d.trang_thai = '").append(status).append("'");
        if (!"Tất cả".equals(pttt)) sql.append(" AND d.phuong_thuc_tt = '").append(pttt).append("'");

        sql.append(" ORDER BY d.ma_don_hang ASC"); // Mới nhất lên đầu

        try (Connection conn = db.getConnect();
             ResultSet rs = conn.createStatement().executeQuery(sql.toString())) {

            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while (rs.next()) {
                String stDB = rs.getString("trang_thai");
                // Map trạng thái hiển thị
                String trangThaiHienThi = stDB; 
                if("Đã thanh toán".equals(stDB)) trangThaiHienThi = "Hoàn thành";
                
                donHangModel.addRow(new Object[]{
                    "#" + rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3), // Ngày giờ
                    nf.format(rs.getLong(4)),
                    trangThaiHienThi,
                    rs.getString(6),
                    "Chi tiết"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 1. Hàm chính để hiển thị Popup
    private void hienThiChiTietDonHangPopup(int maDonHang) {
        JDialog dialog = new JDialog(this, "Chi tiết đơn hàng #" + maDonHang, true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        Color bgColor = new Color(245, 245, 245); // Nền xám nhẹ

        // Panel chứa nội dung hóa đơn
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // Căn lề vào trong cho giống tờ giấy

        try (Connection conn = db.getConnect()) {
            // 1. Lấy thông tin chung
            String sqlDon = "SELECT ngay_dat, trang_thai, tong_tien, phuong_thuc_tt, ma_khuyen_mai FROM donhang WHERE ma_don_hang = ?";
            PreparedStatement psDon = conn.prepareStatement(sqlDon);
            psDon.setInt(1, maDonHang);
            ResultSet rsDon = psDon.executeQuery();

            if (rsDon.next()) {
                String ngayDat = rsDon.getString("ngay_dat");
                String trangThai = rsDon.getString("trang_thai");
                long tongTienFinal = rsDon.getLong("tong_tien");
                String pttt = rsDon.getString("phuong_thuc_tt");
                String maKM = rsDon.getString("ma_khuyen_mai");

                // A. HEADER
                contentPanel.add(createInvoiceHeader(maDonHang, ngayDat, trangThai));
                contentPanel.add(Box.createVerticalStrut(15));

                // B. DANH SÁCH SẢN PHẨM
                JPanel listPanel = new JPanel();
                listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
                listPanel.setBackground(Color.WHITE);
                listPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

                long subTotal = 0;
                String sqlChiTiet = "SELECT f.ten, f.hinh_anh, f.loai, c.so_luong, c.gia_ban, c.thanh_tien " +
                                    "FROM chitiet_donhang c JOIN figure f ON c.figureId = f.id WHERE c.donhangId = ?";
                PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet);
                psChiTiet.setInt(1, maDonHang);
                ResultSet rsChiTiet = psChiTiet.executeQuery();

                while (rsChiTiet.next()) {
                    subTotal += rsChiTiet.getLong("thanh_tien");
                    listPanel.add(createProductItemPanel(
                        rsChiTiet.getString("hinh_anh"),
                        rsChiTiet.getString("ten"),
                        rsChiTiet.getString("loai"),
                        rsChiTiet.getLong("gia_ban"),
                        rsChiTiet.getInt("so_luong")
                    ));
                    // Đường kẻ mờ giữa các SP
                    JSeparator sep = new JSeparator();
                    sep.setForeground(new Color(245, 245, 245));
                    listPanel.add(sep);
                }
                contentPanel.add(listPanel);
                contentPanel.add(Box.createVerticalStrut(15));

                // C. TỔNG KẾT
                contentPanel.add(createSummaryPanel(subTotal, tongTienFinal, pttt, maKM));
                
                // --- [QUAN TRỌNG]: THÊM GLUE ĐỂ ĐẨY NỘI DUNG LÊN TRÊN CÙNG ---
                contentPanel.add(Box.createVerticalGlue());
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        // Wrapper Panel để căn giữa
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(bgColor);
        wrapperPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Chỉ giãn ngang, không giãn dọc
        gbc.anchor = GridBagConstraints.NORTH;    // Neo lên trên cùng
        
        wrapperPanel.add(contentPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // --- [SỬA LỖI QUAN TRỌNG TẠI ĐÂY] ---
        
        // 1. Đặt kích thước ưu tiên cho ScrollPane để pack() không bị lỗi bé xíu
        // Chiều rộng 650, Chiều cao 600 (Đây là kích thước tối đa ban đầu)
        scrollPane.setPreferredSize(new Dimension(650, 600));
        
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 2. Gọi pack() để tính toán layout
        dialog.pack(); 

        // 3. Tính toán lại chiều cao thực tế của nội dung
        // Lấy chiều cao của contentPanel + khoảng dư (padding trên dưới)
        int contentHeight = contentPanel.getPreferredSize().height + 80; 
        int finalHeight;
        
        // Nếu nội dung ngắn hơn 600px, ta thu nhỏ cửa sổ lại cho vừa khít
        if (contentHeight < 650) {
            finalHeight = Math.max(contentHeight, 350); // Nếu ngắn quá thì tối thiểu 350
        } else {
            finalHeight = 700; // Nếu dài quá thì cố định 700
        }
        // Nếu nội dung dài hơn 700px, nó sẽ giữ nguyên 700px và hiện thanh cuộn
        
        // Đặt kích thước (BẮT BUỘC phải làm trước bước đặt vị trí)
        dialog.setSize(700, finalHeight);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // 2. Panel Header: Chứa ID, Ngày và Nút Edit Status
    private JPanel createInvoiceHeader(int id, String date, String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20)); 
        panel.setMaximumSize(new Dimension(2000, 80));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(Color.WHITE);
        
        JLabel lblId = new JLabel("HÓA ĐƠN #" + id);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblId.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel subInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subInfo.setBackground(Color.WHITE);
        subInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDate = new JLabel(date + "  |  ");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(Color.GRAY);
        
        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // === SỬA LOGIC MÀU SẮC THEO TIẾNG VIỆT ===
        if(status.equals("Đã thanh toán") || status.equalsIgnoreCase("Completed")) 
            lblStatus.setForeground(new Color(40, 167, 69)); // Xanh lá
        else if(status.equals("Đã hủy") || status.equalsIgnoreCase("Cancelled")) 
            lblStatus.setForeground(Color.RED); // Đỏ
        else 
            lblStatus.setForeground(Color.ORANGE); // Cam (cho Chờ xử lý...)
            
        subInfo.add(lblDate);
        subInfo.add(lblStatus);

        left.add(lblId);
        left.add(Box.createVerticalStrut(5)); 
        left.add(subInfo);

        JButton btnEdit = new JButton("Sửa");
        btnEdit.setBackground(new Color(40, 167, 69));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEdit.setMargin(new Insets(5, 15, 5, 15)); 
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> showEditStatusDialog(id, status));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Color.WHITE);
        right.add(btnEdit);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // 3. Panel cho từng Sản phẩm (Giống hình mẫu)
    private JPanel createProductItemPanel(String imgName, String name, String type, long price, int qty) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 15, 5, 15)); 
        panel.setMaximumSize(new Dimension(2000, 60));

        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(40, 40));
        lblImg.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        
        ImageIcon icon = loadProductImage(imgName); 
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(scaled));
        } else {
            lblImg.setText("K.Ảnh"); // No Img -> K.Ảnh
            lblImg.setFont(new Font("Arial", Font.PLAIN, 9));
        }
        
        JPanel center = new JPanel(new GridLayout(2, 1)); 
        center.setBackground(Color.WHITE);
        
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // --- [SỬA Ở ĐÂY] --- 
        // Chỉ hiện Loại và Số lượng. Bỏ phần giá tiền đơn vị.
        // Ví dụ cũ: "Anime | 1.500.000 x 1"
        // Ví dụ mới: "Anime  |  Số lượng: 1"
        JLabel lblTypeQty = new JLabel(type + "  |  Số lượng: " + qty);
        lblTypeQty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTypeQty.setForeground(Color.GRAY);

        center.add(lblName);
        center.add(lblTypeQty);

        panel.add(lblImg, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
        
        // 3. Tổng tiền item (Bên phải)
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        JLabel lblTotalItem = new JLabel(nf.format(price * qty));
        lblTotalItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        panel.add(lblImg, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
        panel.add(lblTotalItem, BorderLayout.EAST); // Thêm giá tổng bên phải
        
        return panel;
    }

    // 4. Panel Tổng kết tiền (Summary)
    private JPanel createSummaryPanel(long subTotal, long finalTotal, String pttt, String maKM) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 hàng (tự động), 2 cột
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setMaximumSize(new Dimension(2000, 180)); // Tăng chiều cao tối đa

        Font fontNormal = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 16);
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // 1. Phương thức thanh toán
        panel.add(createLabel("Phương thức TT:", fontNormal));
        panel.add(createLabelRight(pttt, fontNormal));

        // 2. Tạm tính (Tổng giá trị các món hàng)
        panel.add(createLabel("Tạm tính:", fontNormal));
        panel.add(createLabelRight(nf.format(subTotal), fontNormal));

        // 3. Mã Khuyến mãi (Luôn hiển thị)
        panel.add(createLabel("Mã khuyến mãi:", fontNormal));

        // Xử lý nội dung hiển thị
        String hienThiKM;
        Color mauChu;
        
        if (maKM != null && !maKM.isEmpty()) {
            hienThiKM = maKM;
            mauChu = new Color(40, 167, 69); // Có mã -> Màu xanh
        } else {
            hienThiKM = "Không áp dụng";
            mauChu = Color.GRAY;             // Không có mã -> Màu xám
        }

        JLabel lblKM = createLabelRight(hienThiKM, fontNormal);
        lblKM.setForeground(mauChu);
        panel.add(lblKM);

        // --- Phần hiển thị số tiền giảm (Chỉ hiện khi có giảm giá thực sự) ---
        long discount = subTotal - finalTotal;
        if (discount > 0) {
            panel.add(createLabel("Đã giảm:", fontNormal));
            JLabel lblDiscount = createLabelRight("-" + nf.format(discount), fontNormal);
            lblDiscount.setForeground(new Color(40, 167, 69));
            panel.add(lblDiscount);
        }

        // Kẻ đường gạch ngang
        JPanel line = new JPanel();
        line.setBackground(Color.LIGHT_GRAY);
        line.setPreferredSize(new Dimension(0, 1));
        // Mẹo: Thêm line vào panel gridlayout hơi khó đẹp, nên ta bỏ qua line ở đây 
        // hoặc thêm panel trống nếu cần thiết, nhưng giao diện phẳng thì không cần thiết lắm.

        // 4. Tổng cộng (Final)
        panel.add(createLabel("Tổng cộng:", fontBold));
        JLabel lblTotal = createLabelRight(nf.format(finalTotal), fontBold);
        lblTotal.setForeground(new Color(220, 53, 69)); // Màu đỏ
        panel.add(lblTotal);

        return panel;
    }

    // Helper tạo Label
    private JLabel createLabel(String text, Font f) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        return l;
    }
    private JLabel createLabelRight(String text, Font f) {
        JLabel l = new JLabel(text, JLabel.RIGHT);
        l.setFont(f);
        return l;
    }

    // 5. Chức năng Edit Status (Cập nhật trạng thái)
    private void showEditStatusDialog(int maDonHang, String currentStatus) {
        // === SỬA LẠI DANH SÁCH TRẠNG THÁI KHỚP VỚI DATABASE ===
        // Bạn có thể thêm "Chờ xử lý" hoặc "Đang giao" nếu DB có hỗ trợ
        String[] statuses = {"Đã thanh toán", "Đã hủy", "Chờ xử lý"}; 
        
        String input = (String) JOptionPane.showInputDialog(this, 
                "Chọn trạng thái mới cho đơn hàng #" + maDonHang,
                "Cập nhật trạng thái",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                currentStatus);

        if (input != null && !input.equals(currentStatus)) {
            try (Connection conn = db.getConnect();
                 PreparedStatement ps = conn.prepareStatement("UPDATE donhang SET trang_thai = ? WHERE ma_don_hang = ?")) {
                ps.setString(1, input);
                ps.setInt(2, maDonHang);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    // Đóng dialog cũ để người dùng mở lại sẽ thấy cập nhật mới
                    Window w = SwingUtilities.getWindowAncestor((Component)this);
                    if (w != null) w.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
            }
        }
    }

    // ================== QUẢN LÝ SẢN PHẨM ==================
    private JPanel taoSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // --- A. HEADER (TIÊU ĐỀ & BỘ LỌC) ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Khoảng cách dưới header

        // Tiêu đề
        JLabel title = new JLabel("Quản lý sản phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);

        // Panel Bộ lọc bên phải
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setBackground(Color.WHITE);

        // Khởi tạo các Components lọc
        txtTimTen = new JTextField(10);
        cbTimLoai = new JComboBox<>(new String[]{"Tất cả", "Anime", "Game", "Gundam", "Khác"});
        cbTimLoai.setBackground(Color.WHITE);
        
        cbTimKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/6", "1/8", "1/10", "1/12", "1/144", "Khác"});
        cbTimKichThuoc.setBackground(Color.WHITE);
        
        txtGiaTu = new JTextField(6); txtGiaTu.setToolTipText("Giá từ");
        txtGiaDen = new JTextField(6); txtGiaDen.setToolTipText("Đến");

        JButton btnTim = new JButton("Tìm");
        btnTim.setBackground(new Color(0, 123, 255)); // Xanh dương
        btnTim.setForeground(Color.WHITE);
        btnTim.setFocusPainted(false);
        btnTim.addActionListener(e -> loadSanPhamData());

        JButton btnThem = new JButton("+ Thêm SP");
        btnThem.setBackground(new Color(0, 123, 255)); // Xanh dương
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnThem.setFocusPainted(false);
        btnThem.addActionListener(e -> hienThiFormSanPham(null));

        // Add components vào panel lọc
        filterPanel.add(new JLabel("Tên:"));      filterPanel.add(txtTimTen);
        filterPanel.add(new JLabel("Loại:"));     filterPanel.add(cbTimLoai);
        filterPanel.add(new JLabel("Size:"));     filterPanel.add(cbTimKichThuoc);
        filterPanel.add(new JLabel("Giá:"));      filterPanel.add(txtGiaTu);
        filterPanel.add(new JLabel("-"));         filterPanel.add(txtGiaDen);
        filterPanel.add(btnTim);
        filterPanel.add(Box.createHorizontalStrut(10)); // Khoảng cách
        filterPanel.add(btnThem);

        topPanel.add(filterPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- B. BẢNG DỮ LIỆU ---
        // Cột 7 là Text trạng thái (ẩn), Cột 8 là Nút gạt, Cột 9 là Nút sửa
        String[] cols = {"ID", "Hình", "Tên sản phẩm", "Loại", "Kích thước", "Giá", "Tồn kho", "TT", "Trạng thái", "Hành động"};
        
        sanPhamModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 8 || c == 9; } // Sửa cột 8 & 9
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
        };

        sanPhamTable = new JTable(sanPhamModel);
        sanPhamTable.setRowHeight(60);
        
        // Căn chỉnh cột
        sanPhamTable.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        sanPhamTable.getColumnModel().getColumn(1).setPreferredWidth(70); // Hình
        sanPhamTable.getColumnModel().getColumn(2).setPreferredWidth(200);// Tên
        sanPhamTable.getColumnModel().getColumn(8).setPreferredWidth(100);// Trạng thái (Nút gạt)
        
        // Ẩn cột text trạng thái (Cột 7)
        sanPhamTable.getColumnModel().getColumn(7).setMinWidth(0);
        sanPhamTable.getColumnModel().getColumn(7).setMaxWidth(0);
        
        styleTableHeader(sanPhamTable);
        centerAllTableCells(sanPhamTable);
        
        // Renderer Ảnh (Giữ nguyên)
        sanPhamTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
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

        // --- 1. SETUP NÚT GẠT TRẠNG THÁI (Màu Cyan / Đỏ) ---
        setupProductToggle(sanPhamTable, 8); 

        // --- 2. SETUP NÚT SỬA (Màu Xanh Lá - Đồng bộ) ---
        sanPhamTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Sửa");
            btn.setBackground(new Color(40, 167, 69)); // Xanh lá cây (#28a745)
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });
        
        sanPhamTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton btn = new JButton("Sửa");
                btn.setBackground(new Color(40, 167, 69)); // Xanh lá cây
                btn.addActionListener(e -> {
                    int id = Integer.parseInt(table.getValueAt(row, 0).toString());
                    hienThiFormSanPham(id); // Gọi form sửa
                    fireEditingStopped();
                });
                return btn;
            }
        });

        panel.add(new JScrollPane(sanPhamTable), BorderLayout.CENTER);
        loadSanPhamData(); // Load lần đầu
        return panel;
    }
    
    private void loadSanPhamData() {
        // Bao try-catch để an toàn
        try {
            if (sanPhamModel == null) return;
            sanPhamModel.setRowCount(0);
            
            String ten = (txtTimTen != null) ? txtTimTen.getText().trim() : "";
            String loai = (cbTimLoai != null) ? cbTimLoai.getSelectedItem().toString() : "Tất cả";
            String size = (cbTimKichThuoc != null) ? cbTimKichThuoc.getSelectedItem().toString() : "Tất cả";
            String giaTu = (txtGiaTu != null) ? txtGiaTu.getText().trim() : "";
            String giaDen = (txtGiaDen != null) ? txtGiaDen.getText().trim() : "";

            StringBuilder sql = new StringBuilder("SELECT * FROM figure WHERE 1=1");
            
            if (!ten.isEmpty()) sql.append(" AND ten LIKE '%").append(ten).append("%'");
            if (!"Tất cả".equals(loai)) sql.append(" AND loai = '").append(loai).append("'");
            if (!"Tất cả".equals(size)) sql.append(" AND kich_thuoc = '").append(size).append("'");
            if (!giaTu.isEmpty()) sql.append(" AND gia >= ").append(giaTu);
            if (!giaDen.isEmpty()) sql.append(" AND gia <= ").append(giaDen);

            sql.append(" ORDER BY id ASC"); // Mới nhất lên đầu

            try (Connection conn = db.getConnect();
                 ResultSet rs = conn.createStatement().executeQuery(sql.toString())) {
                
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                
                while (rs.next()) {
                    String imgName = rs.getString("hinh_anh");
                    ImageIcon icon = null;
                    try {
                         icon = loadProductImage(imgName);
                         if (icon != null) {
                             Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                             icon = new ImageIcon(img);
                         }
                    } catch (Exception e) { }

                    String stDB = rs.getString("trang_thai");
                    boolean isActive = (stDB != null && stDB.equalsIgnoreCase("Mở"));

                    sanPhamModel.addRow(new Object[]{
                        rs.getInt("id"),
                        icon,
                        rs.getString("ten"),
                        rs.getString("loai"),
                        rs.getString("kich_thuoc"),
                        nf.format(rs.getLong("gia")),
                        rs.getInt("so_luong"),
                        isActive ? "Mở" : "Tắt", // Cột 7 (Ẩn)
                        isActive,                // Cột 8: Boolean cho nút gạt
                        "Sửa"                    // Cột 9: Nút sửa
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi load sản phẩm: " + e.getMessage());
        }
    }

    // ================== KHO ==================
    private JPanel taoKhoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Quản lý kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);
        JLabel placeholder = new JLabel("Chức năng đang phát triển...", JLabel.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }
    
    private void hienThiFormSanPham(Integer idSanPham) {
        boolean isEdit = (idSanPham != null);
        JDialog dialog = new JDialog(this, isEdit ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới", true);
        dialog.setSize(850, 550); // Tăng chiều rộng để chứa 2 cột
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // --- PHẦN CHÍNH: CHIA 2 CỘT (CENTER) ---
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 hàng, 2 cột, khoảng cách giữa 2 cột là 20
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề 4 phía

        // === CỘT TRÁI: NHẬP LIỆU ===
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0); // Khoảng cách dưới mỗi dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0; 

        // Khai báo components
        JTextField txtTen = styleTextField();
        JTextArea txtMoTa = new JTextArea(4, 20); 
        txtMoTa.setLineWrap(true); 
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 8, 5, 8)
        ));
        
        JTextField txtGia = styleTextField();
        JTextField txtSoLuong = styleTextField();
        
        // ComboBox style
        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"Anime", "Game", "Gundam", "Khác"});
        cbLoai.setBackground(Color.WHITE);
        JComboBox<String> cbKichThuoc = new JComboBox<>(new String[]{"Khác", "1/6", "1/8", "1/10", "1/12", "1/144"});
        cbKichThuoc.setBackground(Color.WHITE);

        // Add vào Cột Trái
        int y = 0;
        addLabelAndComponent(leftPanel, gbc, y++, "Tên sản phẩm:", txtTen);
        addLabelAndComponent(leftPanel, gbc, y++, "Mô tả:", new JScrollPane(txtMoTa));
        
        // Gom Giá và Tồn kho vào 1 dòng cho gọn
        JPanel priceStockPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        priceStockPanel.setBackground(Color.WHITE);
        JPanel p1 = createFieldGroup("Giá (VNĐ):", txtGia);
        JPanel p2 = createFieldGroup("Tồn kho:", txtSoLuong);
        priceStockPanel.add(p1);
        priceStockPanel.add(p2);
        
        gbc.gridy = y++; 
        leftPanel.add(priceStockPanel, gbc);

        // Gom Loại và Size vào 1 dòng
        JPanel typeSizePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        typeSizePanel.setBackground(Color.WHITE);
        JPanel p3 = createFieldGroup("Loại:", cbLoai);
        JPanel p4 = createFieldGroup("Kích thước:", cbKichThuoc);
        typeSizePanel.add(p3);
        typeSizePanel.add(p4);
        
        gbc.gridy = y++;
        leftPanel.add(typeSizePanel, gbc);

        // === CỘT PHẢI: ẢNH (Sửa theo yêu cầu) ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Hình ảnh sản phẩm"));
        
        JLabel lblHinhAnh = new JLabel("Chưa có ảnh", JLabel.CENTER); // Mặc định chữ ở giữa
        lblHinhAnh.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblHinhAnh.setForeground(Color.GRAY);
        // Tạo viền nét đứt hoặc viền nhẹ cho ảnh
        lblHinhAnh.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
        
        JButton btnChonAnh = new JButton("Tải ảnh lên");
        btnChonAnh.setFocusPainted(false);
        btnChonAnh.setBackground(new Color(240, 240, 240));
        btnChonAnh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel btnImgWrapper = new JPanel(); // Bọc nút để nó không bị giãn
        btnImgWrapper.setBackground(Color.WHITE);
        btnImgWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnImgWrapper.add(btnChonAnh);

        rightPanel.add(lblHinhAnh, BorderLayout.CENTER);
        rightPanel.add(btnImgWrapper, BorderLayout.SOUTH);

        // Thêm 2 cột vào Main
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        dialog.add(mainPanel, BorderLayout.CENTER);

        // --- LOGIC LOAD DỮ LIỆU (Giữ nguyên logic cũ) ---
        fileAnhMoi = null; 
        String currentImgName = "default.jpg"; 

        if (isEdit) {
            try (Connection conn = db.getConnect(); 
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM figure WHERE id=?")) {
                ps.setInt(1, idSanPham);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtTen.setText(rs.getString("ten"));
                    txtMoTa.setText(rs.getString("mo_ta"));
                    txtGia.setText(String.valueOf(rs.getInt("gia")));
                    txtSoLuong.setText(String.valueOf(rs.getInt("so_luong")));
                    cbLoai.setSelectedItem(rs.getString("loai"));
                    cbKichThuoc.setSelectedItem(rs.getString("kich_thuoc"));
                    currentImgName = rs.getString("hinh_anh");
                    
                    ImageIcon icon = loadProductImage(currentImgName);
                    if (icon != null) {
                        // Scale ảnh to hơn cho đẹp (250x250)
                        Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                        lblHinhAnh.setIcon(new ImageIcon(img));
                        lblHinhAnh.setText(""); // Xóa chữ khi có ảnh
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // --- SỰ KIỆN CHỌN ẢNH ---
        btnChonAnh.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                fileAnhMoi = fileChooser.getSelectedFile();
                ImageIcon newIcon = new ImageIcon(fileAnhMoi.getAbsolutePath());
                Image img = newIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                lblHinhAnh.setIcon(new ImageIcon(img));
                lblHinhAnh.setText(""); // Xóa chữ
            }
        });

        // --- BOTTOM: CÁC NÚT CHỨC NĂNG ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        bottomPanel.setBackground(new Color(245, 245, 245)); // Nền xám nhẹ cho footer
        bottomPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220))); // Viền trên

        JButton btnLuu = new JButton(isEdit ? "Cập nhật" : "Thêm mới");
        btnLuu.setPreferredSize(new Dimension(120, 35));
        btnLuu.setBackground(new Color(40, 167, 69)); 
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnXoa = new JButton("Xóa");
        btnXoa.setPreferredSize(new Dimension(80, 35));
        btnXoa.setBackground(new Color(220, 53, 69)); 
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setVisible(isEdit);
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(80, 35));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnXoa);
        bottomPanel.add(btnHuy);
        bottomPanel.add(btnLuu);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        // --- LOGIC LƯU & XÓA (Code cũ của bạn, copy lại vào đây) ---
        final String finalImgName = currentImgName;
        
        btnLuu.addActionListener(e -> {
            try {
                String ten = txtTen.getText();
                int gia = Integer.parseInt(txtGia.getText());
                int sl = Integer.parseInt(txtSoLuong.getText());
                String imgSave = (fileAnhMoi != null) ? fileAnhMoi.getName() : finalImgName;
                
                // Code copy ảnh vào folder src (nếu cần)
                
                String sql = isEdit 
                    ? "UPDATE figure SET ten=?, mo_ta=?, gia=?, so_luong=?, loai=?, kich_thuoc=?, hinh_anh=? WHERE id=?"
                    : "INSERT INTO figure (ten, mo_ta, gia, so_luong, loai, kich_thuoc, hinh_anh, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, 'Mở')";
                
                try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, ten);
                    ps.setString(2, txtMoTa.getText());
                    ps.setInt(3, gia);
                    ps.setInt(4, sl);
                    ps.setString(5, cbLoai.getSelectedItem().toString());
                    ps.setString(6, cbKichThuoc.getSelectedItem().toString());
                    ps.setString(7, imgSave);
                    if (isEdit) ps.setInt(8, idSanPham);
                    
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thành công!");
                    loadSanPhamData();
                    dialog.dispose();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Lỗi: Giá/SL phải là số!"); }
        });

        btnXoa.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(dialog, "Xóa sản phẩm này?") == JOptionPane.YES_OPTION) {
                try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("DELETE FROM figure WHERE id=?")) {
                    ps.setInt(1, idSanPham);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Đã xóa!");
                    loadSanPhamData();
                    dialog.dispose();
                } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Không thể xóa!"); }
            }
        });

        dialog.setVisible(true);
    }

    // --- CÁC HÀM HỖ TRỢ LÀM ĐẸP UI ---
    
    // Hàm tạo JTextField đẹp (Padding + Border)
    private JTextField styleTextField() {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(100, 30));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 8, 5, 8) // Padding trong: Trên, Trái, Dưới, Phải
        ));
        return txt;
    }

    // Hàm thêm Label + Component vào GridBagLayout
    private void addLabelAndComponent(JPanel p, GridBagConstraints gbc, int y, String text, Component comp) {
        gbc.gridy = y;
        
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Label in đậm
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0)); // Khoảng cách giữa Label và Ô nhập
        
        // Tạo panel phụ để chứa Label (dòng trên) và Input (dòng dưới) cho đẹp
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(comp, BorderLayout.CENTER);
        
        p.add(wrapper, gbc);
    }
    
    // Hàm tạo nhóm Field (Label + Input) dùng cho dòng chia đôi
    private JPanel createFieldGroup(String text, Component comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // Helper thêm dòng form
    private void addFormRow(JDialog d, GridBagConstraints gbc, int y, String lbl, Component comp) {
        gbc.gridx = 0; gbc.gridy = y; d.add(new JLabel(lbl), gbc);
        gbc.gridx = 1; gbc.gridy = y; d.add(comp, gbc);
    }
    
    private void setupProductToggle(JTable table, int colIndex) {
        table.getColumnModel().getColumn(colIndex).setCellRenderer((t, v, s, h, r, c) -> {
            boolean active = (Boolean) v;
            JToggleButton btn = new JToggleButton(active ? "Mở" : "Khóa");
            // Mở = Xanh Cyan (#17a2b8), Khóa = Đỏ
            btn.setBackground(active ? new Color(23, 162, 184) : new Color(220, 53, 69)); 
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        table.getColumnModel().getColumn(colIndex).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JToggleButton btn;
            private boolean currState;
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currState = (Boolean) value;
                btn = new JToggleButton(currState ? "Mở" : "Khóa", currState);
                btn.addActionListener(e -> {
                    currState = !currState;
                    int id = Integer.parseInt(table.getValueAt(row, 0).toString());
                    // Update DB
                    try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("UPDATE figure SET trang_thai=? WHERE id=?")) {
                        ps.setString(1, currState ? "Mở" : "Tắt");
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    } catch (Exception ex) { ex.printStackTrace(); }
                    
                    table.setValueAt(currState, row, column);
                    fireEditingStopped();
                });
                return btn;
            }
            @Override public Object getCellEditorValue() { return currState; }
        });
    }

    // ================== BÁO CÁO - THỐNG KÊ ==================
    private JPanel taoBaoCaoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Hiệu suất kinh doanh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"#", "Sản phẩm", "Hình ảnh", "Giá", "Số lượng bán", "Doanh thu", "Xem chi tiết"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        table.setRowHeight(60);
        styleTableHeader(table);
        centerAllTableCells(table);

        try (Connection conn = db.getConnect();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT f.ten, f.hinh_anh, f.gia, COALESCE(SUM(c.so_luong),0), COALESCE(SUM(c.thanh_tien),0) " +
                 "FROM figure f LEFT JOIN chitiet_donhang c ON f.id = c.figureId " +
                 "LEFT JOIN donhang d ON c.donhangId = d.ma_don_hang AND d.trang_thai = 'Đã thanh toán' " +
                 "GROUP BY f.id ORDER BY SUM(c.so_luong) DESC LIMIT 10")) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            int i = 1;
            while (rs.next()) {
                ImageIcon icon = loadProductImage(rs.getString(2));
                model.addRow(new Object[]{
                    i++, rs.getString(1), icon, nf.format(rs.getLong(3)),
                    rs.getInt(4), nf.format(rs.getLong(5)), "Xem"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }

        table.getColumn("Hình ảnh").setCellRenderer((t, v, s, h, r, c) -> {
            JLabel lbl = new JLabel();
            if (v instanceof ImageIcon icon) {
                Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(scaled));
            }
            lbl.setHorizontalAlignment(JLabel.CENTER);
            return lbl;
        });

        table.getColumn("Xem chi tiết").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Xem");
            btn.setBackground(new Color(40, 167, 69));
            btn.setForeground(Color.WHITE);
            return btn;
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel summary = new JPanel(new GridLayout(1, 3, 20, 0));
        summary.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        try (Connection conn = db.getConnect()) {
            long total = getLong(conn, "SELECT COALESCE(SUM(tong_tien), 0) FROM donhang WHERE trang_thai = 'Đã thanh toán'", 0L);
            ResultSet top = conn.createStatement().executeQuery(
                "SELECT f.ten, COALESCE(SUM(c.so_luong),0) FROM figure f " +
                "LEFT JOIN chitiet_donhang c ON f.id = c.figureId " +
                "LEFT JOIN donhang d ON c.donhangId = d.ma_don_hang AND d.trang_thai = 'Đã thanh toán' " +
                "GROUP BY f.id ORDER BY SUM(c.so_luong) DESC LIMIT 1");
            String topSP = top.next() ? top.getString(1) + " (" + top.getInt(2) + " cái)" : "N/A";

            ResultSet min = conn.createStatement().executeQuery(
                "SELECT f.ten, COALESCE(SUM(c.so_luong),0) FROM figure f " +
                "LEFT JOIN chitiet_donhang c ON f.id = c.figureId " +
                "LEFT JOIN donhang d ON c.donhangId = d.ma_don_hang AND d.trang_thai = 'Đã thanh toán' " +
                "GROUP BY f.id HAVING SUM(c.so_luong) > 0 ORDER BY SUM(c.so_luong) ASC LIMIT 1");
            String minSP = min.next() ? min.getString(1) + " (" + min.getInt(2) + " cái)" : "N/A";

            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            summary.add(taoTheTomTat("Tổng doanh thu", nf.format(total), Color.BLUE));
            summary.add(taoTheTomTat("Bán chạy nhất", topSP, Color.GREEN));
            summary.add(taoTheTomTat("Bán ít nhất", minSP, Color.RED));
        } catch (Exception e) { e.printStackTrace(); }
        panel.add(summary, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoTheTomTat(String tieuDe, String giaTri, Color mau) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JLabel t = new JLabel(tieuDe, JLabel.CENTER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel v = new JLabel(giaTri, JLabel.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 16));
        v.setForeground(mau);
        v.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
            new AdminUI().setVisible(true);
        });
    }
    
    private void updateUserStatus(String username, String status) {
        try (Connection conn = db.getConnect(); 
             PreparedStatement ps = conn.prepareStatement("UPDATE nguoidung SET trang_thai = ? WHERE ten_dang_nhap = ?")) {
            ps.setString(1, status);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái: " + ex.getMessage());
        }
    }
}