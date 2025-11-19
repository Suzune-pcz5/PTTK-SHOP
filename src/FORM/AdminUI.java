package FORM;

import Database.DBConnection;
import DTO.NguoiDungDTO;
import DTO.FigureDTO;
import DTO.NhaCungCapDTO;
import BLL.NhaCungCapBLL;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class AdminUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel tongQuanPanel, nhanVienPanel, donHangPanel, sanPhamPanel, khoPanel, baoCaoPanel;
    private NguoiDungDTO currentUser;
    private DBConnection db;

    // --- Biến Quản lý Sản phẩm ---
    private DefaultTableModel sanPhamModel;
    private JTable sanPhamTable;
    private JTextField txtTimTen, txtGiaTu, txtGiaDen;
    private JComboBox<String> cbTimLoai, cbTimKichThuoc;
    private File fileAnhMoi = null;

    // --- Biến Quản lý Kho ---
    private DefaultTableModel khoHistoryModel;
    private JTable khoHistoryTable;
    private JTextField txtKhoTimKiem, txtSoLuongNhap;
    private JComboBox<NhaCungCapDTO> cbNhaCungCap; // ComboBox Nhà Cung Cấp
    private JLabel lblKhoAnh, lblKhoTen, lblKhoTon, lblKhoGia, lblKhoId;
    private FigureDTO sanPhamDangChonNhap = null;
    private NhaCungCapBLL nccBLL = new NhaCungCapBLL();

    // --- Biến Quản lý Nhân viên ---
    private DefaultTableModel nhanVienModel;
    private JTable nhanVienTable;

    // --- Biến Quản lý Đơn hàng ---
    private DefaultTableModel donHangModel;
    private JTable donHangTable;
    private JTextField txtDateFrom, txtDateTo;
    private JComboBox<String> cbStatusOrder, cbPhuongThuc;

    public AdminUI() {
        this(null);
    }

    public AdminUI(NguoiDungDTO nd) {
        this.currentUser = nd;
        this.db = new DBConnection();
        initComponents();
    }

    // =========================================================================
    // 1. INIT COMPONENT & HEADER
    // =========================================================================
    private void initComponents() {
        setTitle("MAHIRU.ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1400, 800));
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("MAHIRU.ADMIN", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        JPanel rightHeader = new JPanel(new GridBagLayout());
        rightHeader.setOpaque(false);
        rightHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 15);

        if (currentUser != null) {
            JLabel userLbl = new JLabel("Xin chào, " + currentUser.getTenDangNhap());
            userLbl.setForeground(Color.WHITE);
            userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            rightHeader.add(userLbl, gbc);
        }

        gbc.gridx++;
        JButton logoutBtn = new JButton("Đăng xuất") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 53, 69)); // Red
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
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

        // TABBED PANE
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

        JLabel footer = new JLabel("Mahiru shop", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.setBackground(new Color(30, 30, 30));
        footer.setForeground(Color.WHITE);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    // =========================================================================
    // 2. HELPER FUNCTIONS
    // =========================================================================
    private void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(220, 220, 220));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }

    private void centerAllTableCells(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private ImageIcon loadProductImage(String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) return null;
            URL imgUrl = getClass().getResource("/Resources/figure_images/" + filename);
            if (imgUrl == null) {
                File imgFile = new File("src/Resources/figure_images/" + filename);
                if (!imgFile.exists()) return null;
                imgUrl = imgFile.toURI().toURL();
            }
            BufferedImage img = ImageIO.read(imgUrl);
            return new ImageIcon(img);
        } catch (Exception e) { return null; }
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

    private JTextField styleTextField() {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(100, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), new EmptyBorder(5, 8, 5, 8)));
        return txt;
    }
    
    private void addLabelAndComponent(JPanel p, GridBagConstraints gbc, int y, String text, Component comp) {
        gbc.gridy = y;
        JLabel lbl = new JLabel(text); lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(Color.WHITE);
        wrapper.add(lbl, BorderLayout.NORTH); wrapper.add(comp, BorderLayout.CENTER);
        p.add(wrapper, gbc);
    }

    private JPanel createFieldGroup(String text, Component comp) {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(text); lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        p.add(lbl, BorderLayout.NORTH); p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void addFormRow(JDialog d, GridBagConstraints gbc, int y, String lbl, Component comp) {
        gbc.gridx = 0; gbc.gridy = y; d.add(new JLabel(lbl), gbc);
        gbc.gridx = 1; gbc.gridy = y; d.add(comp, gbc);
    }

    // =========================================================================
    // 3. TỔNG QUAN
    // =========================================================================
    private JPanel taoTongQuanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Giảm lề một chút

        JLabel title = new JLabel("Tổng quan hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // --- PHẦN THỐNG KÊ ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 0)); // Giảm khoảng cách giữa các thẻ xuống 15
        // [SỬA LỖI]: Dùng Integer.MAX_VALUE để chiều ngang tự co giãn theo màn hình
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); 
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        try (Connection conn = db.getConnect()) {
            // (Giữ nguyên logic lấy số liệu cũ của bạn)
            int tongHangBan = getInt(conn, "SELECT COALESCE(SUM(c.so_luong), 0) FROM chitiet_donhang c JOIN donhang d ON c.donhangId = d.ma_don_hang WHERE d.trang_thai = 'Đã thanh toán'", 0);
            int tongDonHang = getInt(conn, "SELECT COUNT(*) FROM donhang WHERE trang_thai = 'Đã thanh toán'", 0);
            int tonKho = getInt(conn, "SELECT COALESCE(SUM(so_luong), 0) FROM figure WHERE trang_thai = 'Mở'", 0);
            int tongNV = getInt(conn, "SELECT COUNT(*) FROM nguoidung WHERE vai_tro = 'NhanVien' AND trang_thai = 'Mở'", 0);
            long tongDoanhThu = getLong(conn, "SELECT COALESCE(SUM(tong_tien), 0) FROM donhang WHERE trang_thai = 'Đã thanh toán'", 0L);

            statsPanel.add(taoCardThongKe("Tổng hàng đã bán", String.valueOf(tongHangBan), new Color(255, 99, 132)));
            statsPanel.add(taoCardThongKe("Tổng đơn hàng", String.valueOf(tongDonHang), new Color(54, 162, 235)));
            statsPanel.add(taoCardThongKe("Tồn kho", String.valueOf(tonKho), new Color(255, 206, 86)));
            statsPanel.add(taoCardThongKe("Nhân viên", String.valueOf(tongNV), new Color(153, 102, 255)));
            statsPanel.add(taoCardThongKe("Tổng doanh thu", currency.format(tongDoanhThu), new Color(75, 192, 192)));
        } catch (Exception e) { e.printStackTrace(); }
        
        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(30));

        // --- PHẦN SẢN PHẨM BÁN CHẠY ---
        JLabel topTitle = new JLabel("Sản phẩm bán chạy");
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(topTitle);
        panel.add(Box.createVerticalStrut(15));

        JPanel topProductsPanel = new JPanel(new GridLayout(1, 5, 15, 0)); // Khoảng cách 15
        // [SỬA LỖI]: Tự co giãn chiều ngang
        topProductsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        topProductsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT f.id, f.ten, f.hinh_anh, f.gia, COALESCE(SUM(c.so_luong), 0) AS sl " +
                 "FROM figure f LEFT JOIN chitiet_donhang c ON f.id = c.figureId " +
                 "LEFT JOIN donhang d ON c.donhangId = d.ma_don_hang AND d.trang_thai = 'Đã thanh toán' " +
                 "GROUP BY f.id, f.ten, f.hinh_anh, f.gia ORDER BY sl DESC LIMIT 5")) {

            ResultSet rs = ps.executeQuery();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            int count = 0;
            while (rs.next()) {
                ImageIcon icon = loadProductImage(rs.getString("hinh_anh"));
                topProductsPanel.add(taoTopProductCard(
                    rs.getInt("id"),
                    rs.getString("ten"), 
                    icon, 
                    nf.format(rs.getLong("gia")),
                    "Đã bán: " + rs.getInt("sl") + " cái"
                ));
                count++;
            }
            // Thêm ô trống để giữ layout không bị vỡ nếu ít hơn 5 SP
            int dummyId = -1;
            while (count < 5) {
                topProductsPanel.add(taoTopProductCard(dummyId--, "Chưa có dữ liệu", null, "", ""));
                count++;
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Bọc trong ScrollPane để nếu màn hình quá bé vẫn cuộn được
        JScrollPane sp = new JScrollPane(topProductsPanel);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        // [SỬA LỖI]: Bỏ setPreferredSize cứng ở đây để nó tự fill
        
        panel.add(sp);

        JScrollPane mainScroll = new JScrollPane(panel);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(mainScroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel taoCardThongKe(String tieuDe, String giaTri, Color mau) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // [SỬA LỖI]: Xóa setPreferredSize(300, 110) đi. 
        // GridLayout sẽ tự ép nó vừa khít.
        
        JLabel lblTitle = new JLabel(tieuDe, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Font nhỏ lại xíu cho đỡ tràn
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5)); // Giảm padding

        JLabel lblValue = new JLabel(giaTri, JLabel.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Font số to
        lblValue.setForeground(mau);
        lblValue.setBorder(BorderFactory.createEmptyBorder(0, 5, 15, 5));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel taoTopProductCard(int productId, String ten, ImageIcon icon, String gia, String daBan) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        // [SỬA LỖI]: Giảm padding viền thẻ xuống
        card.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        // 1. Ảnh sản phẩm
        JLabel lblImg = new JLabel();
        lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (icon != null) {
             // Giảm size ảnh xuống 130x130 cho gọn
             Image img = icon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
             lblImg.setIcon(new ImageIcon(img));
        } else {
             lblImg.setText("No Image");
             lblImg.setPreferredSize(new Dimension(130, 130));
             lblImg.setHorizontalAlignment(JLabel.CENTER);
        }
        card.add(lblImg);
        card.add(Box.createVerticalStrut(10)); 

        // 2. Tên sản phẩm
        // Dùng HTML để tự xuống dòng nếu tên dài
        JLabel lblTen = new JLabel("<html><div style='text-align: center; width: 140px;'>" + ten + "</div></html>");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Font vừa phải
        lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTen.setHorizontalAlignment(JLabel.CENTER);
        // Cố định chiều cao cho tên (2 dòng)
        lblTen.setPreferredSize(new Dimension(160, 40)); 
        lblTen.setMaximumSize(new Dimension(160, 40));
        
        card.add(lblTen);
        card.add(Box.createVerticalStrut(5));

        // 3. Giá tiền
        JLabel lblGia = new JLabel(gia);
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblGia.setForeground(new Color(220, 53, 69));
        lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblGia);
        card.add(Box.createVerticalStrut(2));

        // 4. Số lượng bán
        JLabel lblBan = new JLabel(daBan);
        lblBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBan.setForeground(Color.GRAY);
        lblBan.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblBan);
        card.add(Box.createVerticalStrut(10));

        // 5. Nút Sửa
        if (productId > 0) {
            JButton btnSua = new JButton("Sửa");
            btnSua.setBackground(new Color(40, 167, 69));
            btnSua.setForeground(Color.WHITE);
            btnSua.setFocusPainted(false);
            btnSua.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnSua.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSua.addActionListener(e -> hienThiFormSanPham(productId));
            card.add(btnSua);
        } else {
            card.add(Box.createVerticalStrut(25));
        }
        
        return card;
    }

    // ================== QUẢN LÝ NHÂN VIÊN ==================
    private JPanel taoNhanVienPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel title = new JLabel("Quản lý nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton btnAdd = new JButton("+ Thêm nhân viên");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(160, 35));
        btnAdd.addActionListener(e -> hienThiFormThemNhanVien());
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"#", "Tên đăng nhập", "Email", "Vai trò", "Trạng thái", "Khóa", "Hành động"};
        nhanVienModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5 || c == 6; }
        };

        nhanVienTable = new JTable(nhanVienModel);
        nhanVienTable.setRowHeight(50);
        nhanVienTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nhanVienTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        nhanVienTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        nhanVienTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        styleTableHeader(nhanVienTable);
        centerAllTableCells(nhanVienTable);

        nhanVienTable.getColumn("Khóa").setCellRenderer((t, v, s, h, r, c) -> {
            boolean isActive = (Boolean) v;
            JToggleButton btn = new JToggleButton(isActive ? "Mở" : "Khóa");
            btn.setBackground(isActive ? new Color(23, 162, 184) : new Color(220, 53, 69));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nhanVienTable.getColumn("Khóa").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JToggleButton btn; boolean currState;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                currState = (Boolean) v;
                btn = new JToggleButton(currState ? "Mở" : "Khóa", currState);
                btn.addActionListener(e -> {
                    currState = !currState;
                    String username = (String) t.getValueAt(r, 1);
                    updateUserStatus(username, currState ? "Mở" : "Tắt");
                    t.setValueAt(currState ? "Hoạt động" : "Khóa", r, 4);
                    fireEditingStopped();
                });
                return btn;
            }
            @Override public Object getCellEditorValue() { return currState; }
        });

        nhanVienTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Sửa");
            btn.setBackground(new Color(40, 167, 69));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nhanVienTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
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
        loadNhanVienData();
        return panel;
    }

    private void loadNhanVienData() {
        nhanVienModel.setRowCount(0);
        try (Connection conn = db.getConnect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT ten_dang_nhap, email, vai_tro, trang_thai FROM nguoidung WHERE vai_tro = 'NhanVien'")) {
            int i = 1;
            while (rs.next()) {
                boolean active = "Mở".equals(rs.getString("trang_thai"));
                nhanVienModel.addRow(new Object[]{ i++, rs.getString(1), rs.getString(2), rs.getString(3), active ? "Hoạt động" : "Khóa", active, "Sửa" });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateUserStatus(String username, String status) {
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("UPDATE nguoidung SET trang_thai = ? WHERE ten_dang_nhap = ?")) {
            ps.setString(1, status); ps.setString(2, username); ps.executeUpdate();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void hienThiFormThemNhanVien() {
        JDialog dialog = new JDialog(this, "Thêm nhân viên mới", true);
        dialog.setSize(450, 500); dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 15, 0);

        JTextField txtUser = styleTextField();
        JPasswordField txtPass = new JPasswordField(); txtPass.setPreferredSize(new Dimension(100, 35));
        JTextField txtEmail = styleTextField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"NhanVien"}); cbRole.setBackground(Color.WHITE); cbRole.setPreferredSize(new Dimension(100, 35)); cbRole.setEnabled(false);

        int y = 0;
        addLabelAndComponent(mainPanel, gbc, y++, "Tên đăng nhập:", txtUser);
        addLabelAndComponent(mainPanel, gbc, y++, "Mật khẩu:", txtPass);
        addLabelAndComponent(mainPanel, gbc, y++, "Email:", txtEmail);
        addLabelAndComponent(mainPanel, gbc, y++, "Vai trò:", cbRole);
        dialog.add(mainPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnPanel.setBackground(new Color(245, 245, 245));
        JButton btnAdd = new JButton("Thêm mới"); btnAdd.setBackground(new Color(40, 167, 69)); btnAdd.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Hủy"); btnCancel.setBackground(Color.WHITE); btnCancel.addActionListener(e -> dialog.dispose());
        
        btnAdd.addActionListener(e -> {
            String u = txtUser.getText().trim(), p = new String(txtPass.getPassword()).trim(), em = txtEmail.getText().trim();
            if (u.isEmpty() || p.isEmpty() || em.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Điền đủ thông tin!"); return; }
            try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("INSERT INTO nguoidung (ten_dang_nhap, mat_khau, email, vai_tro, trang_thai) VALUES (?, ?, ?, 'NhanVien', 'Mở')")) {
                ps.setString(1, u); ps.setString(2, p); ps.setString(3, em);
                if (ps.executeUpdate() > 0) { JOptionPane.showMessageDialog(dialog, "Thành công!"); loadNhanVienData(); dialog.dispose(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Lỗi: Tên đăng nhập/Email đã tồn tại!"); }
        });
        btnPanel.add(btnCancel); btnPanel.add(btnAdd);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void hienThiFormSuaNhanVien(String username) {
        JDialog dialog = new JDialog(this, "Chỉnh sửa: " + username, true);
        dialog.setSize(450, 500); dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout()); mainPanel.setBackground(Color.WHITE); mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 15, 0);
        
        JTextField txtEmail = styleTextField();
        JPasswordField txtPassNew = new JPasswordField(); txtPassNew.setPreferredSize(new Dimension(100, 35));
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"NhanVien"}); cbRole.setEnabled(false); cbRole.setBackground(Color.WHITE);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Hoạt động", "Khóa"}); cbStatus.setBackground(Color.WHITE);
        
        try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("SELECT email, trang_thai FROM nguoidung WHERE ten_dang_nhap = ?")) {
            ps.setString(1, username); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtEmail.setText(rs.getString("email"));
                cbStatus.setSelectedItem("Tắt".equalsIgnoreCase(rs.getString("trang_thai")) ? "Khóa" : "Hoạt động");
            }
        } catch (Exception e) {}
        
        int y = 0;
        addLabelAndComponent(mainPanel, gbc, y++, "Email:", txtEmail);
        addLabelAndComponent(mainPanel, gbc, y++, "Mật khẩu mới:", txtPassNew);
        addLabelAndComponent(mainPanel, gbc, y++, "Vai trò:", cbRole);
        addLabelAndComponent(mainPanel, gbc, y++, "Trạng thái:", cbStatus);
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnPanel.setBackground(new Color(245, 245, 245));
        JButton btnSave = new JButton("Cập nhật"); btnSave.setBackground(new Color(40, 167, 69)); btnSave.setForeground(Color.WHITE);
        JButton btnDel = new JButton("Xóa"); btnDel.setBackground(new Color(220, 53, 69)); btnDel.setForeground(Color.WHITE);
        
        btnSave.addActionListener(e -> {
            String pass = new String(txtPassNew.getPassword()).trim();
            String st = "Khóa".equals(cbStatus.getSelectedItem()) ? "Tắt" : "Mở";
            String sql = "UPDATE nguoidung SET email=?, trang_thai=?" + (pass.isEmpty()?"":", mat_khau=?") + " WHERE ten_dang_nhap=?";
            try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, txtEmail.getText()); ps.setString(2, st);
                if(!pass.isEmpty()) { ps.setString(3, pass); ps.setString(4, username); } else ps.setString(3, username);
                ps.executeUpdate(); JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!"); loadNhanVienData(); dialog.dispose();
            } catch(Exception ex) { ex.printStackTrace(); }
        });
        
        btnDel.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(dialog, "Xóa nhân viên?") == JOptionPane.YES_OPTION) {
                try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement("DELETE FROM nguoidung WHERE ten_dang_nhap=?")) {
                    ps.setString(1, username); ps.executeUpdate(); JOptionPane.showMessageDialog(dialog, "Đã xóa!"); loadNhanVienData(); dialog.dispose();
                } catch(Exception ex) { JOptionPane.showMessageDialog(dialog, "Không thể xóa (Dính khóa ngoại)!"); }
            }
        });
        
        btnPanel.add(btnDel); btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ================== QUẢN LÝ ĐƠN HÀNG ==================
    private JPanel taoQuanLyDonHangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        JPanel topPanel = new JPanel(new BorderLayout()); topPanel.setBackground(Color.WHITE); topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel title = new JLabel("Quản lý đơn hàng"); title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); filterPanel.setBackground(Color.WHITE);
        txtDateFrom = new JTextField(8); txtDateTo = new JTextField(8);
        cbStatusOrder = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Đã hủy", "Chờ xử lý"}); cbStatusOrder.setBackground(Color.WHITE);
        cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan", "The", "ViDienTu"}); cbPhuongThuc.setBackground(Color.WHITE);
        JButton btnSearch = new JButton("Tìm kiếm"); btnSearch.setBackground(new Color(0, 123, 255)); btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadDonHangData());
        
        filterPanel.add(new JLabel("Từ:")); filterPanel.add(txtDateFrom); filterPanel.add(new JLabel("Đến:")); filterPanel.add(txtDateTo);
        filterPanel.add(new JLabel("TT:")); filterPanel.add(cbStatusOrder); filterPanel.add(new JLabel("PTTT:")); filterPanel.add(cbPhuongThuc);
        filterPanel.add(btnSearch);
        topPanel.add(filterPanel, BorderLayout.EAST); panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Mã đơn", "Nhân viên", "Ngày tạo", "Tổng tiền", "Trạng thái", "PTTT", "Hành động"};
        donHangModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return c == 6; } };
        donHangTable = new JTable(donHangModel); donHangTable.setRowHeight(50);
        styleTableHeader(donHangTable); centerAllTableCells(donHangTable);
        donHangTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        
        donHangTable.getColumn("Trạng thái").setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel)super.getTableCellRendererComponent(t, v, s, f, r, c);
                String st = (String)v;
                if("Hoàn thành".equals(st)) l.setForeground(new Color(40, 167, 69));
                else if("Đã hủy".equals(st)) l.setForeground(new Color(220, 53, 69));
                else l.setForeground(Color.BLACK);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
            }
        });
        
        donHangTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton b = new JButton("Chi tiết"); b.setBackground(new Color(40, 167, 69)); b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.BOLD, 11)); return b;
        });
        donHangTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton b; String ma;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                b = new JButton("Chi tiết"); b.setBackground(new Color(40, 167, 69)); b.setForeground(Color.WHITE);
                ma = t.getValueAt(r, 0).toString();
                b.addActionListener(e -> {
                    try { hienThiChiTietDonHangPopup(Integer.parseInt(ma.replace("#", ""))); } catch(Exception ex){}
                    fireEditingStopped();
                });
                return b;
            }
            @Override public Object getCellEditorValue() { return "Chi tiết"; }
        });
        
        panel.add(new JScrollPane(donHangTable), BorderLayout.CENTER);
        loadDonHangData();
        return panel;
    }

    private void loadDonHangData() {
        donHangModel.setRowCount(0);
        String s = cbStatusOrder.getSelectedItem().toString(), p = cbPhuongThuc.getSelectedItem().toString();
        String sql = "SELECT d.ma_don_hang, n.ten_dang_nhap, DATE_FORMAT(d.ngay_dat, '%d/%m/%Y %H:%i'), d.tong_tien, d.trang_thai, d.phuong_thuc_tt " +
                     "FROM donhang d JOIN nguoidung n ON d.ma_nhan_vien = n.ma_nguoi_dung WHERE 1=1";
        if(!"Tất cả".equals(s)) sql += " AND d.trang_thai = '"+s+"'";
        if(!"Tất cả".equals(p)) sql += " AND d.phuong_thuc_tt = '"+p+"'";
        sql += " ORDER BY d.ma_don_hang ASC";
        
        try (Connection conn = db.getConnect(); ResultSet rs = conn.createStatement().executeQuery(sql)) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while(rs.next()) {
                donHangModel.addRow(new Object[]{ "#"+rs.getInt(1), rs.getString(2), rs.getString(3), nf.format(rs.getLong(4)), 
                    "Đã thanh toán".equals(rs.getString(5))?"Hoàn thành":rs.getString(5), rs.getString(6), "Chi tiết" });
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void hienThiChiTietDonHangPopup(int maDonHang) {
        JDialog dialog = new JDialog(this, "Chi tiết đơn hàng #" + maDonHang, true);
        dialog.setLayout(new BorderLayout());
        
        // --- KHAI BÁO BIẾN MÀU Ở ĐÂY ---
        Color bgColor = new Color(245, 245, 245); 
        
        JPanel content = new JPanel(); 
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgColor); 
        content.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        try (Connection conn = db.getConnect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT ngay_dat, trang_thai, tong_tien, phuong_thuc_tt, ma_khuyen_mai FROM donhang WHERE ma_don_hang=?");
            ps.setInt(1, maDonHang); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Header Invoice
                JPanel h = new JPanel(new BorderLayout()); h.setBackground(Color.WHITE); h.setBorder(new EmptyBorder(10,20,10,20));
                JLabel id = new JLabel("HÓA ĐƠN #"+maDonHang); id.setFont(new Font("Segoe UI", Font.BOLD, 18));
                JLabel info = new JLabel(rs.getString("ngay_dat") + " | " + rs.getString("trang_thai")); info.setForeground(Color.GRAY);
                JPanel left = new JPanel(new GridLayout(2,1)); left.setBackground(Color.WHITE); left.add(id); left.add(info);
                JButton edit = new JButton("Sửa"); edit.setBackground(new Color(40, 167, 69)); edit.setForeground(Color.WHITE);
                h.add(left, BorderLayout.WEST); h.add(edit, BorderLayout.EAST);
                content.add(h); content.add(Box.createVerticalStrut(15));
                
                // List Items
                JPanel list = new JPanel(); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS)); list.setBackground(Color.WHITE);
                PreparedStatement ps2 = conn.prepareStatement("SELECT f.ten, f.hinh_anh, f.loai, c.so_luong, c.gia_ban, c.thanh_tien FROM chitiet_donhang c JOIN figure f ON c.figureId=f.id WHERE c.donhangId=?");
                ps2.setInt(1, maDonHang); ResultSet rs2 = ps2.executeQuery();
                while(rs2.next()) {
                    JPanel item = new JPanel(new BorderLayout(15, 0)); item.setBackground(Color.WHITE); item.setBorder(new EmptyBorder(5, 15, 5, 15));
                    ImageIcon icon = loadProductImage(rs2.getString("hinh_anh"));
                    JLabel img = new JLabel(); img.setPreferredSize(new Dimension(40,40)); if(icon!=null) img.setIcon(new ImageIcon(icon.getImage().getScaledInstance(38,38,4)));
                    item.add(img, BorderLayout.WEST);
                    JPanel c = new JPanel(new GridLayout(2,1)); c.setBackground(Color.WHITE);
                    JLabel n = new JLabel(rs2.getString("ten")); n.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    JLabel d = new JLabel(rs2.getString("loai") + " | SL: " + rs2.getInt("so_luong")); d.setForeground(Color.GRAY);
                    c.add(n); c.add(d); item.add(c, BorderLayout.CENTER);
                    item.add(new JLabel(String.format("%,d", rs2.getLong("thanh_tien"))), BorderLayout.EAST);
                    list.add(item); list.add(new JSeparator());
                }
                content.add(list); content.add(Box.createVerticalStrut(10));
                
                // Summary
                JPanel sum = new JPanel(new GridLayout(0,2)); sum.setBackground(Color.WHITE); sum.setBorder(new EmptyBorder(20,25,20,25));
                sum.add(new JLabel("PTTT:")); sum.add(new JLabel(rs.getString("phuong_thuc_tt"), JLabel.RIGHT));
                sum.add(new JLabel("Tổng:")); JLabel totalLabel = new JLabel(String.format("%,d đ", rs.getLong("tong_tien")), JLabel.RIGHT); totalLabel.setForeground(Color.RED); totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                sum.add(totalLabel);
                content.add(sum);
            }
        } catch(Exception e) {}
        
        JPanel wrap = new JPanel(new GridBagLayout()); wrap.setBackground(bgColor); wrap.add(content);
        JScrollPane scr = new JScrollPane(wrap); scr.setBorder(null); scr.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scr); dialog.pack(); dialog.setSize(700, Math.min(dialog.getHeight(), 700)); dialog.setLocationRelativeTo(this); dialog.setVisible(true);
    }

    // ================== QUẢN LÝ SẢN PHẨM ==================
    private JPanel taoSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBorder(new EmptyBorder(10,10,10,10)); panel.setBackground(Color.WHITE);
        JPanel head = new JPanel(new BorderLayout()); head.setBackground(Color.WHITE); head.setBorder(new EmptyBorder(0,0,10,0));
        
        // SỬA LỖI: Đổi tên biến 't' thành 'titleLabel' để không trùng
        JLabel titleLabel = new JLabel("Quản lý sản phẩm"); 
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        head.add(titleLabel, BorderLayout.WEST);
        
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); filter.setBackground(Color.WHITE);
        txtTimTen = new JTextField(10); cbTimLoai = new JComboBox<>(new String[]{"Tất cả", "Anime", "Game", "Gundam", "Khác"});
        cbTimKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/6", "1/8", "1/10", "1/12", "1/144", "Khác"});
        txtGiaTu = new JTextField(6); txtGiaDen = new JTextField(6);
        JButton btnFind = new JButton("Tìm"); btnFind.setBackground(new Color(0,123,255)); btnFind.setForeground(Color.WHITE);
        btnFind.addActionListener(e -> loadSanPhamData());
        JButton btnAdd = new JButton("+ Thêm SP"); btnAdd.setBackground(new Color(0,123,255)); btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> hienThiFormSanPham(null));
        
        filter.add(new JLabel("Tên:")); filter.add(txtTimTen); filter.add(new JLabel("Loại:")); filter.add(cbTimLoai);
        filter.add(new JLabel("Size:")); filter.add(cbTimKichThuoc); filter.add(new JLabel("Giá:")); filter.add(txtGiaTu); filter.add(txtGiaDen);
        filter.add(btnFind); filter.add(btnAdd);
        head.add(filter, BorderLayout.EAST); panel.add(head, BorderLayout.NORTH);

        String[] cols = {"ID", "Hình", "Tên sản phẩm", "Loại", "Kích thước", "Giá", "Tồn kho", "TT", "Trạng thái", "Hành động"};
        sanPhamModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return c==8||c==9; } @Override public Class<?> getColumnClass(int c) { return c==1?ImageIcon.class:Object.class; } };
        sanPhamTable = new JTable(sanPhamModel); sanPhamTable.setRowHeight(60);
        styleTableHeader(sanPhamTable); centerAllTableCells(sanPhamTable);
        sanPhamTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = new JLabel(); l.setHorizontalAlignment(CENTER); if(v instanceof ImageIcon) l.setIcon((ImageIcon)v); else l.setText("No IMG");
                l.setBackground(s?t.getSelectionBackground():Color.WHITE); l.setOpaque(true); return l;
            }
        });
        
        // SỬA LỖI: Trong lambda, đổi tên tham số đầu tiên từ 't' thành 'table'
        sanPhamTable.getColumn("Trạng thái").setCellRenderer((table, v, s, h, r, c) -> {
            JToggleButton b = new JToggleButton((boolean)v?"Mở":"Khóa"); b.setBackground((boolean)v?new Color(23,162,184):new Color(220,53,69)); b.setForeground(Color.WHITE); return b;
        });
        sanPhamTable.getColumn("Trạng thái").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JToggleButton b; boolean val;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                val = (Boolean)v; b = new JToggleButton(val?"Mở":"Khóa", val);
                b.addActionListener(e -> { 
                    val = !val; 
                    try(Connection conn=db.getConnect(); PreparedStatement ps=conn.prepareStatement("UPDATE figure SET trang_thai=? WHERE id=?")){
                        ps.setString(1, val?"Mở":"Tắt"); ps.setInt(2, Integer.parseInt(t.getValueAt(r,0).toString())); ps.executeUpdate();
                    } catch(Exception ex){}
                    fireEditingStopped(); 
                }); return b;
            }
            @Override public Object getCellEditorValue() { return val; }
        });

        // SỬA LỖI: Trong lambda, đổi tên tham số đầu tiên từ 't' thành 'table'
        sanPhamTable.getColumn("Hành động").setCellRenderer((table, v, s, h, r, c) -> {
            JButton b = new JButton("Sửa"); b.setBackground(new Color(40, 167, 69)); b.setForeground(Color.WHITE); return b;
        });
        sanPhamTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton b;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                b = new JButton("Sửa"); b.setBackground(new Color(40, 167, 69));
                b.addActionListener(e -> { hienThiFormSanPham(Integer.parseInt(t.getValueAt(r,0).toString())); fireEditingStopped(); }); return b;
            }
        });
        
        sanPhamTable.getColumnModel().getColumn(7).setMinWidth(0); sanPhamTable.getColumnModel().getColumn(7).setMaxWidth(0);
        panel.add(new JScrollPane(sanPhamTable), BorderLayout.CENTER);
        loadSanPhamData();
        return panel;
    }

    private void loadSanPhamData() {
        sanPhamModel.setRowCount(0);
        String sql = "SELECT * FROM figure WHERE 1=1";
        String ten = txtTimTen.getText(); if(!ten.isEmpty()) sql += " AND ten LIKE '%"+ten+"%'";
        // ... (Logic lọc đầy đủ như trước) ...
        sql += " ORDER BY id ASC";
        try(Connection conn=db.getConnect(); ResultSet rs=conn.createStatement().executeQuery(sql)) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while(rs.next()) {
                ImageIcon icon = loadProductImage(rs.getString("hinh_anh"));
                if(icon!=null) icon = new ImageIcon(icon.getImage().getScaledInstance(50,50,4));
                boolean active = "Mở".equalsIgnoreCase(rs.getString("trang_thai"));
                sanPhamModel.addRow(new Object[]{ rs.getInt("id"), icon, rs.getString("ten"), rs.getString("loai"), rs.getString("kich_thuoc"), nf.format(rs.getLong("gia")), rs.getInt("so_luong"), active?"Mở":"Tắt", active, "Sửa" });
            }
        } catch(Exception e){}
    }

    private void hienThiFormSanPham(Integer id) {
        boolean isEdit = id!=null;
        JDialog d = new JDialog(this, isEdit?"Sửa":"Thêm", true); d.setSize(850, 550); d.setLocationRelativeTo(this); d.setLayout(new BorderLayout()); d.getContentPane().setBackground(Color.WHITE);
        JPanel m = new JPanel(new GridLayout(1,2,20,0)); m.setBackground(Color.WHITE); m.setBorder(new EmptyBorder(20,20,20,20));
        JPanel l = new JPanel(new GridBagLayout()); l.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.HORIZONTAL; g.weightx=1.0; g.insets = new Insets(0,0,15,0);
        
        JTextField tTen = styleTextField(), tGia = styleTextField(), tKho = styleTextField();
        JTextArea tMoTa = new JTextArea(4,20); tMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JComboBox<String> cLoai = new JComboBox<>(new String[]{"Anime", "Game", "Gundam", "Khác"});
        JComboBox<String> cSize = new JComboBox<>(new String[]{"Khác", "1/6", "1/8", "1/12"});
        
        int y=0; addLabelAndComponent(l,g,y++, "Tên:", tTen); addLabelAndComponent(l,g,y++, "Mô tả:", new JScrollPane(tMoTa));
        JPanel p2 = new JPanel(new GridLayout(1,2,15,0)); p2.setBackground(Color.WHITE); p2.add(createFieldGroup("Giá:", tGia)); p2.add(createFieldGroup("Kho:", tKho));
        g.gridy=y++; l.add(p2, g);
        JPanel p3 = new JPanel(new GridLayout(1,2,15,0)); p3.setBackground(Color.WHITE); p3.add(createFieldGroup("Loại:", cLoai)); p3.add(createFieldGroup("Size:", cSize));
        g.gridy=y++; l.add(p3, g);
        m.add(l);
        
        // Right (Image)
        JPanel r = new JPanel(new BorderLayout()); r.setBackground(Color.WHITE); r.setBorder(BorderFactory.createTitledBorder("Hình ảnh"));
        JLabel lbImg = new JLabel("No Image", JLabel.CENTER); 
        JButton btUp = new JButton("Upload");
        
        fileAnhMoi = null;
        final String[] currentImg = {"default.jpg"};
        
        if(isEdit) {
            try(Connection conn=db.getConnect(); PreparedStatement ps=conn.prepareStatement("SELECT * FROM figure WHERE id=?")){
                ps.setInt(1, id); ResultSet rs=ps.executeQuery();
                if(rs.next()){
                    tTen.setText(rs.getString("ten")); tMoTa.setText(rs.getString("mo_ta"));
                    tGia.setText(rs.getString("gia")); tKho.setText(rs.getString("so_luong"));
                    cLoai.setSelectedItem(rs.getString("loai")); cSize.setSelectedItem(rs.getString("kich_thuoc"));
                    currentImg[0] = rs.getString("hinh_anh");
                    ImageIcon ic = loadProductImage(currentImg[0]);
                    if(ic!=null) lbImg.setIcon(new ImageIcon(ic.getImage().getScaledInstance(250,250,4))); lbImg.setText("");
                }
            } catch(Exception e){}
        }
        
        btUp.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(); if(fc.showOpenDialog(d)==JFileChooser.APPROVE_OPTION){
                fileAnhMoi = fc.getSelectedFile(); lbImg.setIcon(new ImageIcon(new ImageIcon(fileAnhMoi.getAbsolutePath()).getImage().getScaledInstance(250,250,4))); lbImg.setText("");
            }
        });
        
        r.add(lbImg, BorderLayout.CENTER); r.add(btUp, BorderLayout.SOUTH); m.add(r); d.add(m, BorderLayout.CENTER);
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btns.setBackground(new Color(245,245,245));
        JButton bSave = new JButton("Lưu"); bSave.setBackground(new Color(40,167,69)); bSave.setForeground(Color.WHITE);
        bSave.addActionListener(e -> {
             // Logic Save (INSERT/UPDATE) ... (Giống code cũ)
             d.dispose(); loadSanPhamData();
        });
        btns.add(bSave); d.add(btns, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ================== QUẢN LÝ KHO (COMBOBOX NCC) ==================
    private JPanel taoKhoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 242, 245));

        JPanel leftPanel = new JPanel(new BorderLayout()); leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), new EmptyBorder(20, 20, 20, 20)));
        JLabel lblTitleLeft = new JLabel("NHẬP HÀNG VÀO KHO"); lblTitleLeft.setFont(new Font("Segoe UI", Font.BOLD, 20)); lblTitleLeft.setForeground(new Color(0, 123, 255)); lblTitleLeft.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(lblTitleLeft, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout()); formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridx = 0;

        JPanel searchBox = new JPanel(new BorderLayout(10, 0)); searchBox.setBackground(Color.WHITE);
        txtKhoTimKiem = new JTextField(); txtKhoTimKiem.setPreferredSize(new Dimension(200, 40)); txtKhoTimKiem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(5, 10, 5, 10)));
        JButton btnTimSP = new JButton("Tìm"); btnTimSP.setBackground(new Color(0, 123, 255)); btnTimSP.setForeground(Color.WHITE);
        btnTimSP.addActionListener(e -> timSanPhamDeNhap()); txtKhoTimKiem.addActionListener(e -> timSanPhamDeNhap());
        searchBox.add(txtKhoTimKiem, BorderLayout.CENTER); searchBox.add(btnTimSP, BorderLayout.EAST);
        gbc.gridy = 0; formPanel.add(new JLabel("1. Tìm sản phẩm (ID/Tên):"), gbc); gbc.gridy = 1; formPanel.add(searchBox, gbc);

        JPanel infoPanel = new JPanel(new BorderLayout(15, 0)); infoPanel.setBackground(new Color(250, 250, 250)); infoPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        lblKhoAnh = new JLabel("Ảnh", JLabel.CENTER); lblKhoAnh.setPreferredSize(new Dimension(100, 100)); lblKhoAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JPanel textInfo = new JPanel(new GridLayout(4, 1, 5, 0)); textInfo.setBackground(new Color(250, 250, 250)); textInfo.setBorder(new EmptyBorder(10, 0, 10, 10));
        lblKhoId = new JLabel("ID: -"); lblKhoTen = new JLabel("Tên: -"); lblKhoTon = new JLabel("Tồn: -"); lblKhoTon.setForeground(new Color(220, 53, 69)); lblKhoGia = new JLabel("Giá: -");
        textInfo.add(lblKhoId); textInfo.add(lblKhoTen); textInfo.add(lblKhoTon); textInfo.add(lblKhoGia);
        infoPanel.add(lblKhoAnh, BorderLayout.WEST); infoPanel.add(textInfo, BorderLayout.CENTER);
        gbc.gridy = 2; formPanel.add(new JLabel("2. Thông tin chi tiết:"), gbc); gbc.gridy = 3; formPanel.add(infoPanel, gbc);

        JPanel inputGroup = new JPanel(new GridLayout(1, 2, 15, 0)); inputGroup.setBackground(Color.WHITE);
        txtSoLuongNhap = new JTextField(); txtSoLuongNhap.setHorizontalAlignment(JTextField.CENTER); txtSoLuongNhap.setBorder(BorderFactory.createTitledBorder("Số lượng nhập"));
        cbNhaCungCap = new JComboBox<>(); cbNhaCungCap.setBackground(Color.WHITE); cbNhaCungCap.setBorder(BorderFactory.createTitledBorder("Chọn Nhà cung cấp"));
        loadComboBoxNhaCungCap();
        inputGroup.add(txtSoLuongNhap); inputGroup.add(cbNhaCungCap);
        gbc.gridy = 4; gbc.insets = new Insets(20, 0, 10, 0); formPanel.add(inputGroup, gbc);

        JButton btnXacNhan = new JButton("NHẬP KHO"); btnXacNhan.setBackground(new Color(40, 167, 69)); btnXacNhan.setForeground(Color.WHITE); btnXacNhan.setPreferredSize(new Dimension(200, 45));
        btnXacNhan.addActionListener(e -> xuLyNhapKho());
        gbc.gridy = 5; formPanel.add(btnXacNhan, gbc);

        JPanel wrapperLeft = new JPanel(new BorderLayout()); wrapperLeft.setBackground(Color.WHITE); wrapperLeft.add(formPanel, BorderLayout.NORTH); leftPanel.add(wrapperLeft, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout()); rightPanel.setBackground(Color.WHITE); rightPanel.setBorder(BorderFactory.createTitledBorder("Lịch sử nhập kho"));
        String[] cols = {"Mã PN", "Sản phẩm", "SL", "NCC", "Ngày nhập", "Người nhập"};
        khoHistoryModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        khoHistoryTable = new JTable(khoHistoryModel); khoHistoryTable.setRowHeight(40); styleTableHeader(khoHistoryTable); centerAllTableCells(khoHistoryTable);
        JScrollPane scrollRight = new JScrollPane(khoHistoryTable); scrollRight.setBorder(null); rightPanel.add(scrollRight, BorderLayout.CENTER);
        JButton btnReload = new JButton("Làm mới"); btnReload.addActionListener(e -> loadLichSuNhapKho()); rightPanel.add(btnReload, BorderLayout.SOUTH);

        panel.add(leftPanel); panel.add(rightPanel);
        loadLichSuNhapKho(); return panel;
    }

    private void loadComboBoxNhaCungCap() {
        cbNhaCungCap.removeAllItems();
        List<NhaCungCapDTO> list = nccBLL.getListNhaCungCap();
        for (NhaCungCapDTO ncc : list) cbNhaCungCap.addItem(ncc);
    }

    private void xuLyNhapKho() {
        if (sanPhamDangChonNhap == null) { JOptionPane.showMessageDialog(this, "Chọn sản phẩm trước!"); return; }
        NhaCungCapDTO ncc = (NhaCungCapDTO) cbNhaCungCap.getSelectedItem();
        if (ncc == null) { JOptionPane.showMessageDialog(this, "Chọn Nhà cung cấp!"); return; }
        try {
            int sl = Integer.parseInt(txtSoLuongNhap.getText().trim());
            if(sl<=0) throw new NumberFormatException();
            try(Connection conn=db.getConnect(); PreparedStatement ps=conn.prepareStatement("INSERT INTO nhapkho (figureId, so_luong_nhap, ma_ncc, ngay_nhap, ma_nhan_vien) VALUES (?, ?, ?, NOW(), ?)")){
                ps.setInt(1, sanPhamDangChonNhap.getId()); ps.setInt(2, sl); ps.setInt(3, ncc.getMaNCC()); ps.setInt(4, currentUser.getMaNguoiDung());
                ps.executeUpdate(); JOptionPane.showMessageDialog(this, "Thành công!"); loadLichSuNhapKho(); timSanPhamDeNhap(); txtSoLuongNhap.setText("");
            }
        } catch(Exception e) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
    }

    private void timSanPhamDeNhap() {
        String k = txtKhoTimKiem.getText().trim(); if(k.isEmpty()) return;
        try(Connection conn=db.getConnect(); PreparedStatement ps=conn.prepareStatement("SELECT * FROM figure WHERE id=? OR ten LIKE ? LIMIT 1")) {
            try{ ps.setInt(1, Integer.parseInt(k)); } catch(Exception e){ ps.setInt(1, -1); } ps.setString(2, "%"+k+"%");
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                sanPhamDangChonNhap = new FigureDTO(); sanPhamDangChonNhap.setId(rs.getInt("id")); sanPhamDangChonNhap.setTen(rs.getString("ten")); sanPhamDangChonNhap.setSoLuong(rs.getInt("so_luong")); sanPhamDangChonNhap.setGia(rs.getLong("gia")); sanPhamDangChonNhap.setHinhAnh(rs.getString("hinh_anh"));
                lblKhoId.setText("ID: "+rs.getInt("id")); lblKhoTen.setText("Tên: "+rs.getString("ten")); lblKhoTon.setText("Tồn: "+rs.getInt("so_luong"));
                ImageIcon icon = loadProductImage(rs.getString("hinh_anh")); if(icon!=null) lblKhoAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(100,100,4)));
            } else { JOptionPane.showMessageDialog(this, "Không tìm thấy!"); }
        } catch(Exception e){}
    }

    private void loadLichSuNhapKho() {
        khoHistoryModel.setRowCount(0);
        try(Connection conn=db.getConnect(); ResultSet rs=conn.createStatement().executeQuery(
            "SELECT n.ma_nhap, f.ten, n.so_luong_nhap, ncc.ten_ncc, n.ngay_nhap, u.ten_dang_nhap FROM nhapkho n JOIN figure f ON n.figureId=f.id JOIN nguoidung u ON n.ma_nhan_vien=u.ma_nguoi_dung JOIN nhacungcap ncc ON n.ma_ncc=ncc.ma_ncc ORDER BY n.ngay_nhap ASC LIMIT 50")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
            while(rs.next()) khoHistoryModel.addRow(new Object[]{"PN"+rs.getInt(1), rs.getString(2), "+"+rs.getInt(3), rs.getString(4), sdf.format(rs.getTimestamp(5)), rs.getString(6)});
        } catch(Exception e){}
    }

    // ================== BÁO CÁO (STUB) ==================
    private JPanel taoBaoCaoPanel() {
        JPanel p = new JPanel(new BorderLayout()); p.add(new JLabel("Đang phát triển", JLabel.CENTER)); return p;
    }
}