package FORM;

import com.toedter.calendar.JDateChooser;
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

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setOpaque(false);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2, true));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });

        rightHeader.add(logoutBtn);
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
                ImageIcon icon = loadImage(rs.getString("hinh_anh"));
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

    private ImageIcon loadImage(String filename) {
        if (filename == null || filename.trim().isEmpty()) return null;
        String[] paths = { "/images/" + filename, "/img/" + filename, filename };
        for (String p : paths) {
            try {
                BufferedImage img = ImageIO.read(getClass().getResource(p));
                if (img != null) return new ImageIcon(img);
            } catch (Exception ignored) {}
        }
        return null;
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

    // ================== NHÂN VIÊN ==================
    private JPanel taoNhanVienPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Quản lý người dùng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"#", "Tên đăng nhập", "Email", "Vai trò", "Trạng thái", "Khóa", "Hành động"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return c == 5; } };
        JTable table = new JTable(model);
        table.setRowHeight(40);
        styleTableHeader(table);
        centerAllTableCells(table);

        try (Connection conn = db.getConnect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT ten_dang_nhap, email, vai_tro, trang_thai FROM nguoidung")) {
            int i = 1;
            while (rs.next()) {
                boolean active = "Active".equals(rs.getString("trang_thai"));
                model.addRow(new Object[]{ i++, rs.getString(1), rs.getString(2), rs.getString(3), active ? "Hoạt động" : "Khóa", active, "Sửa" });
            }
        } catch (Exception e) { e.printStackTrace(); }

        table.getColumn("Khóa").setCellRenderer((t, v, s, h, r, c) -> {
            JToggleButton btn = new JToggleButton((Boolean)v ? "Mở" : "Khóa", (Boolean)v);
            btn.addActionListener(e -> {
                int row = table.convertRowIndexToModel(r);
                String username = (String) model.getValueAt(row, 1);
                String newStatus = btn.isSelected() ? "Active" : "Locked";
                try (Connection conn = db.getConnect(); PreparedStatement ps = conn.prepareStatement(
                        "UPDATE nguoidung SET trang_thai = ? WHERE ten_dang_nhap = ?")) {
                    ps.setString(1, newStatus);
                    ps.setString(2, username);
                    ps.executeUpdate();
                } catch (Exception ex) { ex.printStackTrace(); }
            });
            return btn;
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

    // ================== QUẢN LÝ ĐƠN HÀNG - ĐÃ KHÔI PHỤC HOÀN TOÀN ==================
    private JPanel taoQuanLyDonHangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

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
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Đã hủy"});
        JComboBox<String> cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan"});
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

        String[] cols = {"Mã đơn", "Nhân viên", "Ngày", "Tổng tiền", "Trạng thái", "Phương thức", "Hành động"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        table.setRowHeight(50);
        styleTableHeader(table);
        centerAllTableCells(table);

        table.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Chi tiết");
            btn.setBackground(new Color(40, 167, 69));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> {
                int row = table.convertRowIndexToModel(r);
                String maDon = model.getValueAt(row, 0).toString();
                int id = Integer.parseInt(maDon.substring(1));
                hienThiChiTietDonHangPopup(id);
            });
            return btn;
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

        sql.append(" ORDER BY d.ngay_dat DESC");

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

    private void hienThiChiTietDonHangPopup(int id) {
        JDialog dialog = new JDialog(this, "Chi tiết đơn hàng #" + id, true);
        dialog.setSize(1000, 650);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Chi tiết đơn hàng #" + id, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Bảng chi tiết
        String[] cols = {"Sản phẩm", "Hình ảnh", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(60);
        styleTableHeader(table);
        centerAllTableCells(table);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT f.ten, f.hinh_anh, c.so_luong, c.don_gia, c.thanh_tien " +
                 "FROM chitiet_donhang c JOIN figure f ON c.figureId = f.id WHERE c.donhangId = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while (rs.next()) {
                ImageIcon icon = loadImage(rs.getString("hinh_anh"));
                model.addRow(new Object[]{
                    rs.getString("ten"),
                    icon,
                    rs.getInt("so_luong"),
                    nf.format(rs.getLong("don_gia")),
                    nf.format(rs.getLong("thanh_tien"))
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

        dialog.add(panel);
        dialog.setVisible(true);
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
                ImageIcon icon = loadImage(rs.getString("hinh_anh"));
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
                ImageIcon icon = loadImage(rs.getString(2));
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
}