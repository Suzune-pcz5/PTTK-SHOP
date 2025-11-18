package FORM;

import Database.DBConnection;
import DTO.NguoiDungDTO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;

public class AdminUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel tongQuanPanel, nhanVienPanel, donHangPanel, sanPhamPanel, khoPanel, baoCaoPanel;
    private NguoiDungDTO currentUser;
    private DBConnection db;

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

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        statsPanel.setMaximumSize(new Dimension(1600, 120));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        try (Connection conn = db.getConnect()) {
            int tongHangBan = getInt(conn,
                "SELECT COALESCE(SUM(c.so_luong), 0) FROM chitiet_donhang c " +
                "JOIN donhang d ON c.donhangId = d.ma_don_hang WHERE d.trang_thai = 'Đã thanh toán'", 0);

            int tongDonHang = getInt(conn,
                "SELECT COUNT(*) FROM donhang WHERE trang_thai = 'Đã thanh toán'", 0);

            int tonKho = getInt(conn,
                "SELECT COALESCE(SUM(so_luong), 0) FROM figure WHERE trang_thai = 'Enable'", 0);

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

    private JPanel taoTopProductCard(String ten, ImageIcon icon, String gia, String sold) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(260, 360));

        JLabel imgLbl = new JLabel();
        imgLbl.setPreferredSize(new Dimension(160, 160));
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
            imgLbl.setIcon(new ImageIcon(scaled));
        } else {
            imgLbl.setText("No Image");
            imgLbl.setForeground(Color.GRAY);
            imgLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        }
        imgLbl.setHorizontalAlignment(JLabel.CENTER);
        card.add(imgLbl, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        JLabel name = new JLabel("<html><div style='text-align:center;width:220px'><b>" + ten + "</b></div></html>");
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel price = new JLabel(gia);
        price.setFont(new Font("Segoe UI", Font.BOLD, 18));
        price.setForeground(new Color(220, 53, 69));
        price.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sl = new JLabel(sold);
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sl.setForeground(Color.GRAY);
        sl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("Sửa");
        btn.setBackground(new Color(40, 167, 69));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(100, 38));
        btn.setMaximumSize(new Dimension(100, 38));

        info.add(Box.createVerticalStrut(12));
        info.add(name);
        info.add(Box.createVerticalStrut(10));
        info.add(price);
        info.add(Box.createVerticalStrut(10));
        info.add(sl);
        info.add(Box.createVerticalStrut(20));
        info.add(btn);

        card.add(info, BorderLayout.CENTER);
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
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        
        JLabel title = new JLabel("Quản lý người dùng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JButton btnAdd = new JButton("+ Thêm nhân viên");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(160, 40));
        btnAdd.addActionListener(e -> hienThiFormThemNhanVien());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- BẢNG ---
        String[] cols = {"#", "Tên đăng nhập", "Email", "Vai trò", "Trạng thái", "Khóa", "Hành động"};
        nhanVienModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5 || c == 6; }
        };
        
        nhanVienTable = new JTable(nhanVienModel);
        nhanVienTable.setRowHeight(50);
        nhanVienTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleTableHeader(nhanVienTable);
        centerAllTableCells(nhanVienTable);

        // --- CẤU HÌNH CỘT KHÓA/MỞ (TOGGLE BUTTON) ---
        nhanVienTable.getColumn("Khóa").setCellRenderer((t, v, s, h, r, c) -> {
            boolean isActive = (Boolean) v; // True = Active, False = Locked
            JToggleButton btn = new JToggleButton(isActive ? "Mở" : "Khóa");
            btn.setSelected(isActive); 
            // Logic màu: Active -> Xanh, Locked -> Đỏ
            btn.setBackground(isActive ? new Color(200, 200, 200) : new Color(220, 53, 69)); 
            btn.setForeground(isActive ? Color.BLACK : Color.WHITE);
            // Nếu bạn muốn nút Mở màu xanh thì dùng dòng dưới:
            // btn.setBackground(isActive ? new Color(40, 167, 69) : new Color(220, 53, 69));
            // btn.setForeground(Color.WHITE);
            
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nhanVienTable.getColumn("Khóa").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JToggleButton btn;
            private boolean currentState;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currentState = (Boolean) value;
                btn = new JToggleButton(currentState ? "Mở" : "Khóa", currentState);
                // Cập nhật màu ngay khi bấm vào để edit
                updateButtonColor(btn, currentState);
                
                // Xử lý sự kiện click
                btn.addActionListener(e -> {
                    // Đảo ngược trạng thái
                    currentState = !currentState; 
                    updateButtonColor(btn, currentState);
                    btn.setText(currentState ? "Mở" : "Khóa");
                    
                    // Cập nhật Database
                    String username = (String) table.getModel().getValueAt(row, 1);
                    String newStatus = currentState ? "Mở" : "Tắt";
                    updateUserStatus(username, newStatus);
                    
                    // Cập nhật Text cột "Trạng thái" (Cột 4)
                    table.getModel().setValueAt(currentState ? "Hoạt động" : "Khóa", row, 4);
                    
                    // Dừng edit để lưu giá trị Boolean mới vào cột 5
                    fireEditingStopped();
                });
                return btn;
            }

            @Override
            public Object getCellEditorValue() {
                return currentState;
            }
            
            private void updateButtonColor(JToggleButton b, boolean active) {
                // Logic màu giống Renderer
                b.setBackground(active ? new Color(200, 200, 200) : new Color(220, 53, 69));
                b.setForeground(active ? Color.BLACK : Color.WHITE);
            }
        });

        // --- CẤU HÌNH CỘT SỬA (MÀU XANH LÁ) ---
        nhanVienTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Sửa");
            btn.setBackground(new Color(40, 167, 69)); // <--- ĐÃ SỬA THÀNH MÀU XANH LÁ CÂY
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nhanVienTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton btn = new JButton("Sửa");
                btn.setBackground(new Color(40, 167, 69)); // <--- ĐÃ SỬA THÀNH MÀU XANH LÁ CÂY
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.addActionListener(e -> {
                    String username = (String) table.getModel().getValueAt(row, 1);
                    hienThiFormSuaNhanVien(username);
                    fireEditingStopped();
                });
                return btn;
            }
        });

        JScrollPane scroll = new JScrollPane(nhanVienTable);
        panel.add(scroll, BorderLayout.CENTER);

        loadNhanVienData();
        return panel;
    }

        private void hienThiFormThemNhanVien() {
        JDialog dialog = new JDialog(this, "Thêm người dùng mới", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel lblTitle = new JLabel("Thông tin người dùng mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(lblTitle, gbc);

        // Fields
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"NhanVien", "Admin"});

        gbc.gridwidth = 1;
        gbc.gridy++; addFormRow(dialog, gbc, "Tên đăng nhập:", txtUser);
        gbc.gridy++; addFormRow(dialog, gbc, "Mật khẩu:", txtPass);
        gbc.gridy++; addFormRow(dialog, gbc, "Email:", txtEmail);
        gbc.gridy++; addFormRow(dialog, gbc, "Vai trò:", cbRole);

        // Button Add
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(100, 40));
        
        btnAdd.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String email = txtEmail.getText().trim();
            String role = cbRole.getSelectedItem().toString();

            if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            try (Connection conn = db.getConnect();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO nguoidung (ten_dang_nhap, mat_khau, email, vai_tro, trang_thai) VALUES (?, ?, ?, ?, 'Mở')")) {
                ps.setString(1, user);
                ps.setString(2, pass); // Nên mã hóa MD5/BCrypt thực tế
                ps.setString(3, email);
                ps.setString(4, role);
                
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "Thêm thành công!");
                    loadNhanVienData(); // Refresh bảng
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: Tên đăng nhập hoặc Email đã tồn tại!");
                ex.printStackTrace();
            }
        });

        gbc.gridy++;
        gbc.insets = new Insets(30, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(btnAdd, gbc);

        dialog.setVisible(true);
    }
    
    private void hienThiFormSuaNhanVien(String username) {
        JDialog dialog = new JDialog(this, "Chỉnh sửa thông tin", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Sửa thông tin: " + username);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(lblTitle, gbc);

        // Fields
        JTextField txtEmail = new JTextField();
        JPasswordField txtPassNew = new JPasswordField(); // Để trống nếu không đổi
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"NhanVien", "Admin"});
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Hoạt động", "Khóa"});

        // Load dữ liệu cũ
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement("SELECT email, vai_tro, trang_thai FROM nguoidung WHERE ten_dang_nhap = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtEmail.setText(rs.getString("email"));
                cbRole.setSelectedItem(rs.getString("vai_tro"));
                String dbStatus = rs.getString("trang_thai"); // Lấy về "Mở" hoặc "Tắt"
                if (dbStatus != null && (dbStatus.equals("Tắt") || dbStatus.equals("Khóa"))) {
                    cbStatus.setSelectedItem("Khóa"); // Chọn dòng "Khóa" trong ComboBox
                } else {
                // Nếu là "Mở" hoặc "Active" -> Chọn dòng "Hoạt động" (hoặc "Mở" tùy vào bạn đặt tên item)
                cbStatus.setSelectedItem("Hoạt động"); 
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        gbc.gridwidth = 1;
        gbc.gridy++; addFormRow(dialog, gbc, "Email:", txtEmail);
        gbc.gridy++; addFormRow(dialog, gbc, "Mật khẩu mới (để trống nếu k đổi):", txtPassNew);
        gbc.gridy++; addFormRow(dialog, gbc, "Vai trò:", cbRole);
        gbc.gridy++; addFormRow(dialog, gbc, "Trạng thái:", cbStatus);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton("Cập nhật");
        btnSave.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnSave.setForeground(Color.WHITE);
        
        JButton btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(220, 53, 69)); // Đỏ
        btnDelete.setForeground(Color.WHITE);

        btnSave.setPreferredSize(new Dimension(100, 35));
        btnDelete.setPreferredSize(new Dimension(80, 35));

        // Sự kiện Cập nhật
        btnSave.addActionListener(e -> {
            String newPass = new String(txtPassNew.getPassword()).trim();
            String uiStatus = cbStatus.getSelectedItem().toString(); 
            String dbValue;
            if (uiStatus.equals("Khóa")) {
                dbValue = "Tắt"; // Database cần chữ "Tắt"
            } else {
                dbValue = "Mở";  // Database cần chữ "Mở"
            }
            StringBuilder sql = new StringBuilder("UPDATE nguoidung SET email=?, vai_tro=?, trang_thai=?");
            if (!newPass.isEmpty()) sql.append(", mat_khau=?");
            sql.append(" WHERE ten_dang_nhap=?");

            try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                ps.setString(1, txtEmail.getText());
                ps.setString(2, cbRole.getSelectedItem().toString());
                ps.setString(3, dbValue);
                
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

        // Sự kiện Xóa
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Bạn chắc chắn muốn xóa user này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("DELETE FROM nguoidung WHERE ten_dang_nhap=?")) {
                    ps.setString(1, username);
                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(dialog, "Đã xóa!");
                        loadNhanVienData();
                        dialog.dispose();
                    }
                } catch (SQLException ex) { 
                    JOptionPane.showMessageDialog(dialog, "Không thể xóa (User này có thể đang dính đơn hàng)!");
                }
            }
        });

        btnPanel.add(btnSave);
        btnPanel.add(btnDelete);

        gbc.gridy++;
        gbc.insets = new Insets(30, 20, 20, 20);
        dialog.add(btnPanel, gbc);

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

    // ================== QUẢN LÝ ĐƠN HÀNG - ĐÃ KHÔI PHỤC HOÀN TOÀN ==================
    // ================== QUẢN LÝ ĐƠN HÀNG (ĐÃ SỬA LỖI NÚT CHI TIẾT) ==================
    private JPanel taoQuanLyDonHangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Quản lý đơn hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtFrom = new JTextField(10);
        JTextField txtTo = new JTextField(10);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Đã hủy", "Chờ xử lý"});
        JComboBox<String> cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan", "The", "ViDienTu"});
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(40, 167, 69));
        btnSearch.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; searchPanel.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 1; searchPanel.add(txtFrom, gbc);
        gbc.gridx = 2; searchPanel.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 3; searchPanel.add(txtTo, gbc);
        gbc.gridx = 4; searchPanel.add(btnSearch, gbc);

        gbc.gridx = 0; gbc.gridy = 1; searchPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; searchPanel.add(cbStatus, gbc);
        gbc.gridx = 2; searchPanel.add(new JLabel("Phương thức:"), gbc);
        gbc.gridx = 3; searchPanel.add(cbPhuongThuc, gbc);

        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- TABLE ---
        String[] cols = {"Mã đơn", "Nhân viên", "Ngày", "Tổng tiền", "Trạng thái", "Phương thức", "Hành động"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { 
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return c == 6; // [QUAN TRỌNG] Cho phép sửa cột 6 (cột nút bấm)
            } 
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(50);
        styleTableHeader(table);
        centerAllTableCells(table);

        // 1. RENDERER (Để hiển thị nút)
        table.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Chi tiết");
            btn.setBackground(new Color(40, 167, 69)); // Màu xanh
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        // 2. EDITOR (Để xử lý sự kiện Click) - [PHẦN MỚI THÊM]
        table.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton btn;
            private String maDonHienTai;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                btn = new JButton("Chi tiết");
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                // Lấy Mã đơn hàng từ cột 0 (VD: "#3")
                maDonHienTai = table.getModel().getValueAt(row, 0).toString();

                btn.addActionListener(e -> {
                    // Xử lý logic mở popup
                    try {
                        // Cắt bỏ dấu '#' để lấy số ID (VD: "#3" -> 3)
                        int id = Integer.parseInt(maDonHienTai.replace("#", ""));
                        hienThiChiTietDonHangPopup(id);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    // Dừng việc edit để trả lại trạng thái bình thường cho bảng
                    fireEditingStopped();
                });
                return btn;
            }

            @Override
            public Object getCellEditorValue() {
                return "Chi tiết";
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDonHangData(model, "", "", "Tất cả", "Tất cả");

        // Sự kiện tìm kiếm
        btnSearch.addActionListener(e -> {
            String from = txtFrom.getText().trim();
            String to = txtTo.getText().trim();
            String status = cbStatus.getSelectedItem().toString();
            String phuongthuc = cbPhuongThuc.getSelectedItem().toString();
            loadDonHangData(model, from, to, status, phuongthuc);
        });

        return panel;
    }

    private void loadDonHangData(DefaultTableModel model, String from, String to, String status, String phuongthuc) {
        model.setRowCount(0);
        StringBuilder sql = new StringBuilder(
            "SELECT d.ma_don_hang, n.ten_dang_nhap, DATE_FORMAT(d.ngay_dat, '%d/%m/%Y'), d.tong_tien, d.trang_thai, d.phuong_thuc_tt " +
            "FROM donhang d JOIN nguoidung n ON d.ma_nhan_vien = n.ma_nguoi_dung WHERE 1=1"
        );

        if (!from.isEmpty()) sql.append(" AND d.ngay_dat >= STR_TO_DATE('").append(from).append("', '%d/%m/%Y')");
        if (!to.isEmpty()) sql.append(" AND d.ngay_dat <= STR_TO_DATE('").append(to).append("', '%d/%m/%Y')");
        if (!"Tất cả".equals(status)) sql.append(" AND d.trang_thai = '").append(status).append("'");
        if (!"Tất cả".equals(phuongthuc)) sql.append(" AND d.phuong_thuc_tt = '").append(phuongthuc).append("'");

        sql.append(" ORDER BY d.ngay_dat ASC");

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {

            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while (rs.next()) {
                String trangThai = switch (rs.getString(5)) {
                    case "Đã thanh toán" -> "Hoàn thành";
                    case "Đã hủy" -> "Đã hủy";
                    default -> rs.getString(5);
                };
                model.addRow(new Object[]{
                    "#" + rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    nf.format(rs.getLong(4)),
                    trangThai,
                    rs.getString(6),
                    "Chi tiết"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 1. Hàm chính để hiển thị Popup
    private void hienThiChiTietDonHangPopup(int maDonHang) {
        JDialog dialog = new JDialog(this, "Chi tiết đơn hàng - #" + maDonHang, true);
        dialog.setSize(700, 850);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Màu nền xám nhạt cho vùng bao quanh
        Color bgColor = new Color(245, 245, 245); 

        // 1. Content Panel (Tờ hóa đơn)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(bgColor);
        // Giới hạn chiều rộng tối đa của hóa đơn để nó không bị bè ra quá mức
        contentPanel.setPreferredSize(new Dimension(600, 600)); 
        
        try (Connection conn = db.getConnect()) {
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

                // A. Header
                contentPanel.add(createInvoiceHeader(maDonHang, ngayDat, trangThai));
                contentPanel.add(Box.createVerticalStrut(15));

                // B. Danh sách sản phẩm
                JPanel productsList = new JPanel();
                productsList.setLayout(new BoxLayout(productsList, BoxLayout.Y_AXIS));
                productsList.setBackground(Color.WHITE);
                productsList.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        new EmptyBorder(10, 0, 10, 0)
                ));

                // Tính tạm tính
                long subTotal = 0; 
                String sqlChiTiet = "SELECT f.ten, f.hinh_anh, f.loai, c.so_luong, c.gia_ban, c.thanh_tien " +
                                    "FROM chitiet_donhang c JOIN figure f ON c.figureId = f.id " +
                                    "WHERE c.donhangId = ?";
                PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet);
                psChiTiet.setInt(1, maDonHang);
                ResultSet rsChiTiet = psChiTiet.executeQuery();

                while (rsChiTiet.next()) {
                    long thanhTienItem = rsChiTiet.getLong("thanh_tien");
                    subTotal += thanhTienItem;

                    productsList.add(createProductItemPanel(
                            rsChiTiet.getString("hinh_anh"),
                            rsChiTiet.getString("ten"),
                            rsChiTiet.getString("loai"),
                            rsChiTiet.getLong("gia_ban"),
                            rsChiTiet.getInt("so_luong")
                    ));
                    JSeparator sep = new JSeparator();
                    sep.setForeground(new Color(240, 240, 240));
                    sep.setMaximumSize(new Dimension(2000, 1));
                    productsList.add(sep);
                }
                contentPanel.add(productsList);
                contentPanel.add(Box.createVerticalStrut(15));

                // C. Tổng kết
                contentPanel.add(createSummaryPanel(subTotal, tongTienFinal, pttt, maKM));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Wrapper Panel (Dùng GridBagLayout để CĂN GIỮA contentPanel)
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(bgColor);
        wrapperPanel.setBorder(new EmptyBorder(20, 0, 20, 0)); // Khoảng cách trên dưới
        wrapperPanel.add(contentPanel); // GridBagLayout mặc định sẽ đặt cái này vào chính giữa (CENTER)

        // 3. ScrollPane chứa Wrapper
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
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
        panel.setBorder(new EmptyBorder(10, 20, 10, 20)); 
        panel.setMaximumSize(new Dimension(2000, 80));

        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(60, 60));
        lblImg.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        
        ImageIcon icon = loadProductImage(imgName); 
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(scaled));
        } else {
            lblImg.setText("K.Ảnh"); // No Img -> K.Ảnh
            lblImg.setFont(new Font("Arial", Font.PLAIN, 10));
        }
        
        JPanel center = new JPanel(new GridLayout(2, 1)); 
        center.setBackground(Color.WHITE);
        
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Hiển thị: Loại | Giá x Số lượng
        JLabel lblPriceQty = new JLabel(type + " | " + nf.format(price) + " x " + qty);
        lblPriceQty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPriceQty.setForeground(Color.GRAY);

        center.add(lblName);
        center.add(lblPriceQty);

        panel.add(lblImg, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
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

    // ================== SẢN PHẨM ==================
    private JPanel taoSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Quản lý sản phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"#", "Tên sản phẩm", "Hình ảnh", "Loại", "Giá", "Tồn kho", "Trạng thái", "Hành động"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        table.setRowHeight(70);
        styleTableHeader(table);
        centerAllTableCells(table);

        try (Connection conn = db.getConnect();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT id, ten, hinh_anh, loai, gia, so_luong, trang_thai FROM figure ORDER BY id")) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            int i = 1;
            while (rs.next()) {
                ImageIcon icon = loadProductImage(rs.getString("hinh_anh"));
                model.addRow(new Object[]{
                    i++, rs.getString("ten"), icon, rs.getString("loai"),
                    nf.format(rs.getLong("gia")), rs.getInt("so_luong"),
                    "Enable".equals(rs.getString("trang_thai")) ? "Bật" : "Tắt", "Sửa"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }

        table.getColumn("Hình ảnh").setCellRenderer((t, v, s, h, r, c) -> {
            JLabel lbl = new JLabel();
            if (v instanceof ImageIcon icon) {
                Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(scaled));
            }
            lbl.setHorizontalAlignment(JLabel.CENTER);
            return lbl;
        });

        table.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Sửa");
            btn.setBackground(new Color(40, 167, 69));
            btn.setForeground(Color.WHITE);
            return btn;
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
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

    // ================== BÁO CÁO ==================
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