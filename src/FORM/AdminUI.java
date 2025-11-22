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
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Map; //
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class AdminUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel tongQuanPanel, nhanVienPanel, donHangPanel, sanPhamPanel, khoPanel, baoCaoPanel;
    private NguoiDungDTO currentUser;
    private DBConnection db;
    private BLL.DonHangBLL donHangBLL = new BLL.DonHangBLL(); // <--- THÊM DÒNG NÀY

    // --- Biến Quản lý Sản phẩm ---
    private DefaultTableModel sanPhamModel;
    private JTable sanPhamTable;
    private JTextField txtTimTen, txtGiaTu, txtGiaDen;
    private JComboBox<String> cbTimLoai, cbTimKichThuoc;
    private File fileAnhMoi = null;
    private JComboBox<NhaCungCapDTO> cbTimNCC; // <--- THÊM MỚI

    // --- Biến Quản lý Kho ---
    private DefaultTableModel khoHistoryModel;
    private JTable khoHistoryTable;
    private JTextField txtKhoTimKiem, txtSoLuongNhap;
    private JComboBox<NhaCungCapDTO> cbNhaCungCap; // ComboBox Nhà Cung Cấp
    private JLabel lblKhoAnh, lblKhoTen, lblKhoTon, lblKhoGia, lblKhoId;
    private FigureDTO sanPhamDangChonNhap = null;
    private NhaCungCapBLL nccBLL = new NhaCungCapBLL();
    private JTextField txtHistTen, txtHistDateFrom, txtHistDateTo;
    private JComboBox<NhaCungCapDTO> cbHistNCC;
    private JPanel nccPanel;
    private DefaultTableModel nccModel;
    private JTable nccTable;
    private JTextField txtGiaNhap; // <--- BỔ SUNG
    private JLabel lblTongTienNhap; // <--- BỔ SUNG

    // --- Biến Quản lý Nhân viên ---
    private DefaultTableModel nhanVienModel;
    private JTable nhanVienTable;

    // --- Biến Quản lý Đơn hàng ---
    private DefaultTableModel donHangModel;
    private JTable donHangTable;
    private JTextField txtDateFrom, txtDateTo;
    private JComboBox<String> cbStatusOrder, cbPhuongThuc;
    
    // --- Biến Thống kê ---
    private DAL.ThongKeDAL tkDAL = new DAL.ThongKeDAL();
    
    // --- Biến Khuyến mãi ---
    private JPanel khuyenMaiPanel;
    private DAL.KhuyenMaiDAL kmDAL = new DAL.KhuyenMaiDAL();
    private DefaultTableModel kmModel;
    private JTable kmTable;
    private JTextField txtKMMa, txtKMPT, txtKMHan, txtKMMoTa;
    
    private BLL.PhieuDieuChinhBLL kkeBLL = new BLL.PhieuDieuChinhBLL(); // <--- THÊM DÒNG NÀY
    // -------------------------

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

        // 1. [MỚI] Nút Bán hàng (POS) - Chỉ hiện cho Admin
        JButton btnPOS = new JButton("Bán hàng (POS)");
        btnPOS.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPOS.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnPOS.setForeground(Color.WHITE);
        btnPOS.setFocusPainted(false);
        btnPOS.setPreferredSize(new Dimension(150, 40));
        btnPOS.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPOS.addActionListener(e -> {
            // Ẩn AdminUI, mở MainUI
            this.setVisible(false);
            new MainUI(currentUser).setVisible(true);
        });
        rightHeader.add(btnPOS, gbc); // Thêm vào header
        
        gbc.gridx++;
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
        nccPanel = taoNhaCungCapPanel(); // Hàm tạo giao diện NCC
        tabbedPane.addTab("Tổng quan", tongQuanPanel);
        tabbedPane.addTab("Quản lý nhân viên", nhanVienPanel);
        tabbedPane.addTab("Quản lý đơn hàng", donHangPanel);
        tabbedPane.addTab("Quản lý sản phẩm", sanPhamPanel);
        tabbedPane.addTab("Quản lý kho", khoPanel);
        tabbedPane.addTab("Quản lý nhà cung cấp", nccPanel); // Thêm vào Tab
        tabbedPane.addTab("Quản lý khuyến mãi", taoKhuyenMaiPanel());
        tabbedPane.addTab("Báo cáo thống kê", baoCaoPanel);

        // --- [SỬA LẠI] SỰ KIỆN CHUYỂN TAB -> LOAD LẠI DỮ LIỆU ---
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: // Tổng quan
                    // Thay vì xóa panel, ta gọi lại hàm tạo và set lại component cho tab 0
                    tongQuanPanel = taoTongQuanPanel(); 
                    tabbedPane.setComponentAt(0, tongQuanPanel);
                    break;
                case 1: // Nhân viên
                    loadNhanVienData();
                    break;
                case 2: // Đơn hàng
                    loadDonHangData();
                    break;
                case 3: // Sản phẩm
                    loadSanPhamData();
                    break;
                case 4: // Kho
                    loadLichSuNhapKho();
                    loadComboBoxNhaCungCap(); 
                    break;
                case 5: // Nhà cung cấp
                    loadNhaCungCapData();
                    break;
                case 6: // Khuyến mãi
                     loadKhuyenMaiList();
                     break;
                // Case 7 Báo cáo: Khi bấm nút Xem nó mới load nên ko cần auto
            }
        });
        // -------------------------------------------------------------------
        
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
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Tổng quan hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        // --- PHẦN THỐNG KÊ ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 20, 0));
        statsPanel.setMaximumSize(new Dimension(1800, 120));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        try (Connection conn = db.getConnect()) {
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
        panel.add(Box.createVerticalStrut(40));

        // --- PHẦN SẢN PHẨM BÁN CHẠY ---
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
                 // [FIX LỖI]: Sử dụng CASE WHEN để chỉ cộng dồn số lượng của đơn 'Đã thanh toán'
                 "SELECT f.id, f.ten, f.hinh_anh, f.gia, " +
                 "COALESCE(SUM(CASE WHEN d.trang_thai = 'Đã thanh toán' THEN c.so_luong ELSE 0 END), 0) AS sl " +
                 "FROM figure f " +
                 "LEFT JOIN chitiet_donhang c ON f.id = c.figureId " +
                 "LEFT JOIN donhang d ON c.donhangId = d.ma_don_hang " +
                 "GROUP BY f.id, f.ten, f.hinh_anh, f.gia " +
                 "ORDER BY sl DESC LIMIT 5")) {

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
            
            // Thêm ô trống (nếu chưa đủ 5)
            while (count < 5) {
                topProductsPanel.add(taoTopProductCard(-1, "Chưa có dữ liệu", null, "", ""));
                count++;
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
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. Ảnh sản phẩm
        JLabel lblImg = new JLabel();
        lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (icon != null) {
             Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
             lblImg.setIcon(new ImageIcon(img));
        } else {
             lblImg.setText("No Image");
             lblImg.setPreferredSize(new Dimension(150, 150));
             lblImg.setHorizontalAlignment(JLabel.CENTER);
        }
        card.add(lblImg);
        card.add(Box.createVerticalStrut(15)); 

        // 2. Tên sản phẩm
        JLabel lblTen = new JLabel("<html><center>" + ten + "</center></html>");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTen.setHorizontalAlignment(JLabel.CENTER);
        lblTen.setPreferredSize(new Dimension(180, 45)); 
        card.add(lblTen);
        card.add(Box.createVerticalStrut(5));

        // 3. Giá tiền
        JLabel lblGia = new JLabel(gia);
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGia.setForeground(new Color(220, 53, 69));
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

        // 5. Nút Sửa (Chỉ hiện nếu có ID hợp lệ)
        if (productId > 0) {
            JButton btnSua = new JButton("Sửa");
            btnSua.setBackground(new Color(40, 167, 69));
            btnSua.setForeground(Color.WHITE);
            btnSua.setFocusPainted(false);
            btnSua.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // [FIX]: Gọi hàm hiển thị form sửa
            btnSua.addActionListener(e -> hienThiFormSanPham(productId));
            
            card.add(btnSua);
        } else {
            card.add(Box.createVerticalStrut(30)); // Khoảng trống cho ô placeholder
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
    // ================== QUẢN LÝ ĐƠN HÀNG (CÓ NÚT HỦY) ==================
    private JPanel taoQuanLyDonHangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel topPanel = new JPanel(new BorderLayout()); 
        topPanel.setBackground(Color.WHITE); 
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel title = new JLabel("Quản lý đơn hàng"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);
        
        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); 
        filterPanel.setBackground(Color.WHITE);
        
        txtDateFrom = new JTextField(8); txtDateFrom.setToolTipText("yyyy-mm-dd");
        txtDateTo = new JTextField(8); txtDateTo.setToolTipText("yyyy-mm-dd");
        
        cbStatusOrder = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Đã hủy"}); 
        cbStatusOrder.setBackground(Color.WHITE);
        
        cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan", "The", "ViDienTu"}); 
        cbPhuongThuc.setBackground(Color.WHITE);
        
        JButton btnSearch = new JButton("Tìm kiếm"); 
        btnSearch.setBackground(new Color(0, 123, 255)); 
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadDonHangData());
        
        filterPanel.add(new JLabel("Từ:")); filterPanel.add(txtDateFrom); 
        filterPanel.add(new JLabel("Đến:")); filterPanel.add(txtDateTo);
        filterPanel.add(new JLabel("TT:")); filterPanel.add(cbStatusOrder); 
        filterPanel.add(new JLabel("PTTT:")); filterPanel.add(cbPhuongThuc);
        filterPanel.add(btnSearch);
        
        topPanel.add(filterPanel, BorderLayout.EAST); 
        panel.add(topPanel, BorderLayout.NORTH);

        // Bảng dữ liệu: Thêm cột "Hủy đơn" (Index 7)
        String[] cols = {"Mã đơn", "Nhân viên", "Ngày tạo", "Tổng tiền", "Trạng thái", "PTTT", "Chi tiết", "Hủy đơn"};
        donHangModel = new DefaultTableModel(cols, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return c == 6 || c == 7; } // Click được cột 6, 7
        };
        
        donHangTable = new JTable(donHangModel); 
        donHangTable.setRowHeight(50);
        styleTableHeader(donHangTable); 
        centerAllTableCells(donHangTable);
        donHangTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        
        // Renderer Trạng thái (Màu sắc)
        donHangTable.getColumn("Trạng thái").setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel)super.getTableCellRendererComponent(t, v, s, f, r, c);
                String st = (String)v;
                if("Đã thanh toán".equals(st)) l.setForeground(new Color(40, 167, 69));
                else if("Đã hủy".equals(st)) l.setForeground(new Color(220, 53, 69));
                else l.setForeground(Color.BLACK);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
            }
        });
        
        // Renderer/Editor Nút Chi tiết (Cột 6)
        donHangTable.getColumn("Chi tiết").setCellRenderer((t, v, s, h, r, c) -> {
            JButton b = new JButton("Xem"); b.setBackground(new Color(23, 162, 184)); b.setForeground(Color.WHITE); return b;
        });
        donHangTable.getColumn("Chi tiết").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton b; String ma;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                b = new JButton("Xem"); b.setBackground(new Color(23, 162, 184)); b.setForeground(Color.WHITE);
                ma = t.getValueAt(r, 0).toString();
                b.addActionListener(e -> {
                    try { hienThiChiTietDonHangPopup(Integer.parseInt(ma.replace("#", ""))); } catch(Exception ex){}
                    fireEditingStopped();
                });
                return b;
            }
        });

        // Renderer/Editor Nút Hủy đơn (Cột 7 - MỚI)
        donHangTable.getColumn("Hủy đơn").setCellRenderer((t, v, s, h, r, c) -> {
            String st = (String)t.getModel().getValueAt(r, 4); // Cột trạng thái
            JButton b = new JButton("Hủy"); b.setFont(new Font("Segoe UI", Font.BOLD, 11));
            if("Đã hủy".equals(st)) { b.setEnabled(false); b.setText("-"); b.setBackground(Color.LIGHT_GRAY); }
            else { b.setBackground(new Color(220, 53, 69)); b.setForeground(Color.WHITE); }
            return b;
        });
        
        donHangTable.getColumn("Hủy đơn").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton b;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                String st = (String)t.getModel().getValueAt(r, 4);
                b = new JButton("Hủy");
                if(!"Đã hủy".equals(st)) {
                    b.setBackground(new Color(220, 53, 69)); b.setForeground(Color.WHITE);
                    b.addActionListener(e -> {
                        String ma = t.getValueAt(r, 0).toString();
                        xuLyHuyDonHang(Integer.parseInt(ma.replace("#", "")));
                        fireEditingStopped();
                    });
                } else { b.setEnabled(false); b.setText("-"); }
                return b;
            }
        });
        
        panel.add(new JScrollPane(donHangTable), BorderLayout.CENTER);
        loadDonHangData();
        return panel;
    }

    private void loadDonHangData() {
        donHangModel.setRowCount(0);
        String s = cbStatusOrder.getSelectedItem().toString();
        String p = cbPhuongThuc.getSelectedItem().toString();
        String dFrom = txtDateFrom.getText().trim();
        String dTo = txtDateTo.getText().trim();

        StringBuilder sql = new StringBuilder(
            "SELECT d.ma_don_hang, n.ten_dang_nhap, DATE_FORMAT(d.ngay_dat, '%Y-%m-%d %H:%i'), d.tong_tien, d.trang_thai, d.phuong_thuc_tt " +
            "FROM donhang d JOIN nguoidung n ON d.ma_nhan_vien = n.ma_nguoi_dung WHERE 1=1");
        
        if(!"Tất cả".equals(s)) sql.append(" AND d.trang_thai = '").append(s).append("'");
        if(!"Tất cả".equals(p)) sql.append(" AND d.phuong_thuc_tt = '").append(p).append("'");
        if(!dFrom.isEmpty()) sql.append(" AND DATE(d.ngay_dat) >= '").append(dFrom).append("'");
        if(!dTo.isEmpty()) sql.append(" AND DATE(d.ngay_dat) <= '").append(dTo).append("'");
        
        sql.append(" ORDER BY d.ma_don_hang ASC");
        
        try (Connection conn = db.getConnect(); ResultSet rs = conn.createStatement().executeQuery(sql.toString())) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while(rs.next()) {
                donHangModel.addRow(new Object[]{ 
                    "#" + rs.getInt(1), 
                    rs.getString(2), 
                    rs.getString(3), 
                    nf.format(rs.getLong(4)), 
                    rs.getString(5), 
                    rs.getString(6), 
                    "Xem", 
                    "Hủy" 
                });
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void hienThiChiTietDonHangPopup(int maDonHang) {
        JDialog dialog = new JDialog(this, "Chi tiết đơn hàng #" + maDonHang, true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Dùng JEditorPane để hiển thị HTML cho đẹp
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);

        try (Connection conn = db.getConnect()) {
            // 1. Lấy thông tin chung đơn hàng (JOIN để lấy tên nhân viên)
            String sqlInfo = "SELECT d.ngay_dat, d.trang_thai, d.tong_tien, d.phuong_thuc_tt, n.ten_dang_nhap " +
                             "FROM donhang d " +
                             "JOIN nguoidung n ON d.ma_nhan_vien = n.ma_nguoi_dung " +
                             "WHERE d.ma_don_hang = ?";
            
            PreparedStatement ps = conn.prepareStatement(sqlInfo);
            ps.setInt(1, maDonHang);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String ngay = rs.getString("ngay_dat"); // Hoặc format lại nếu muốn đẹp hơn
                String thuNgan = rs.getString("ten_dang_nhap");
                String pttt = rs.getString("phuong_thuc_tt");
                long tongTien = rs.getLong("tong_tien");

                // --- BUILD HTML ---
                StringBuilder html = new StringBuilder();
                html.append("<html><body style='font-family: Segoe UI, sans-serif; padding: 20px;'>");
                
                // Header
                html.append("<div style='text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px;'>");
                html.append("<h1 style='color: #007bff; margin: 0;'>MAHIRU SHOP</h1>");
                html.append("<p style='font-size: 10px; color: gray;'>HÓA ĐƠN LƯU TRỮ (ADMIN VIEW)</p></div>");
                
                // Info
                html.append("<div style='margin-top: 20px;'><table style='width: 100%; font-size: 12px;'>");
                html.append("<tr><td><b>Mã HĐ:</b> #").append(maDonHang).append("</td>");
                html.append("<td style='text-align: right;'><b>Ngày:</b> ").append(ngay).append("</td></tr>");
                html.append("<tr><td><b>Thu ngân:</b> ").append(thuNgan).append("</td>");
                html.append("<td style='text-align: right;'><b>PTTT:</b> ").append(pttt).append("</td></tr>");
                html.append("</table></div>");

                // Table Items
                html.append("<br><table style='width: 100%; border-collapse: collapse; font-size: 12px;'>");
                html.append("<tr style='background-color: #f2f2f2; text-align: left;'><th style='padding: 8px; border-bottom: 1px solid #ddd;'>Sản phẩm</th><th style='padding: 8px; border-bottom: 1px solid #ddd; text-align: center;'>SL</th><th style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>Đơn giá</th><th style='padding: 8px; border-bottom: 1px solid #ddd; text-align: right;'>T.Tiền</th></tr>");

                // 2. Lấy chi tiết sản phẩm
                String sqlItems = "SELECT f.ten, c.so_luong, c.gia_ban, c.thanh_tien " +
                                  "FROM chitiet_donhang c " +
                                  "JOIN figure f ON c.figureId = f.id " +
                                  "WHERE c.donhangId = ?";
                PreparedStatement ps2 = conn.prepareStatement(sqlItems);
                ps2.setInt(1, maDonHang);
                ResultSet rs2 = ps2.executeQuery();
                
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                long tongHang = 0;

                while (rs2.next()) {
                    String tenSP = rs2.getString("ten");
                    int sl = rs2.getInt("so_luong");
                    long gia = rs2.getLong("gia_ban");
                    long thanhTien = rs2.getLong("thanh_tien");
                    tongHang += thanhTien;

                    html.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee;'>").append(tenSP).append("</td>");
                    html.append("<td style='padding: 8px; border-bottom: 1px solid #eee; text-align: center;'>").append(sl).append("</td>");
                    html.append("<td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>").append(nf.format(gia)).append("</td>");
                    html.append("<td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>").append(nf.format(thanhTien)).append("</td></tr>");
                }

                html.append("</table>");

                // Footer Totals
                long giamGia = tongHang - tongTien;
                html.append("<div style='margin-top: 15px; text-align: right;'>");
                html.append("<p style='margin: 5px;'>Tổng tiền hàng: <b>").append(nf.format(tongHang)).append("</b></p>");
                if (giamGia > 0) {
                    html.append("<p style='margin: 5px; color: green;'>Giảm giá: -").append(nf.format(giamGia)).append("</p>");
                }
                html.append("<h2 style='color: #dc3545; margin-top: 10px;'>TỔNG CỘNG: ").append(nf.format(tongTien)).append("</h2></div>");
                
                html.append("</body></html>");
                editorPane.setText(html.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            editorPane.setText("<html><h2 style='color:red'>Lỗi tải dữ liệu hóa đơn!</h2></html>");
        }

        dialog.add(new JScrollPane(editorPane), BorderLayout.CENTER);

        // Nút In & Đóng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton btnPrint = new JButton("🖨 In lại Hóa đơn");
        btnPrint.setBackground(new Color(0, 123, 255));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        
        btnPrint.addActionListener(e -> {
            try {
                boolean complete = editorPane.print();
                if (complete) {
                    JOptionPane.showMessageDialog(dialog, "Đã gửi lệnh in / Xuất PDF thành công!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi in: " + ex.getMessage());
            }
        });

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void xuLyHuyDonHang(int maDonHang) {
        if (JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn hủy đơn hàng #" + maDonHang + "?\nKho hàng sẽ được hoàn trả tự động.", 
            "Xác nhận hủy", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            if (donHangBLL.huyDonHang(maDonHang)) {
                JOptionPane.showMessageDialog(this, "Đã hủy đơn hàng thành công!");
                
                // Cập nhật lại bảng và báo cho MainUI
                loadDonHangData();
                loadSanPhamData(); // Cập nhật lại tồn kho bên Admin
                triggerRealTimeUpdate(); // Bắn tín hiệu sang MainUI
            } else {
                JOptionPane.showMessageDialog(this, "Không thể hủy đơn hàng này (Có thể đã hủy rồi)!");
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
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Quản lý sản phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);

        // Panel Bộ lọc bên phải
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setBackground(Color.WHITE);

        txtTimTen = new JTextField(10);
        cbTimLoai = new JComboBox<>(new String[]{"Tất cả", "Anime", "Game", "Gundam", "Khác"});
        cbTimLoai.setBackground(Color.WHITE);
        
        cbTimKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/6", "1/8", "1/10", "1/12", "1/144", "Khác"});
        cbTimKichThuoc.setBackground(Color.WHITE);

        // [MỚI] ComboBox lọc theo NCC
        cbTimNCC = new JComboBox<>();
        cbTimNCC.setBackground(Color.WHITE);
        cbTimNCC.addItem(new NhaCungCapDTO(0, "Tất cả NCC", "", "", "", "")); // Item mặc định
        for(NhaCungCapDTO ncc : nccBLL.getListNhaCungCap()) cbTimNCC.addItem(ncc);

        txtGiaTu = new JTextField(5); txtGiaDen = new JTextField(5);

        JButton btnTim = new JButton("Tìm kiếm");
        btnTim.setBackground(new Color(0, 123, 255)); 
        btnTim.setForeground(Color.WHITE);
        btnTim.setFocusPainted(false);
        btnTim.addActionListener(e -> loadSanPhamData());

        JButton btnThem = new JButton("+ Thêm SP");
        btnThem.setBackground(new Color(0, 123, 255)); 
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnThem.setFocusPainted(false);
        btnThem.addActionListener(e -> hienThiFormSanPham(null));

        filterPanel.add(new JLabel("Tên:"));  filterPanel.add(txtTimTen);
        filterPanel.add(new JLabel("NCC:"));  filterPanel.add(cbTimNCC); // Thêm vào giao diện
        filterPanel.add(new JLabel("Loại:")); filterPanel.add(cbTimLoai);
        filterPanel.add(new JLabel("Size:")); filterPanel.add(cbTimKichThuoc);
        filterPanel.add(new JLabel("Giá:"));  filterPanel.add(txtGiaTu); filterPanel.add(new JLabel("-")); filterPanel.add(txtGiaDen);
        filterPanel.add(btnTim);
        filterPanel.add(Box.createHorizontalStrut(5));
        filterPanel.add(btnThem);

        topPanel.add(filterPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- B. BẢNG DỮ LIỆU (THÊM CỘT NCC) ---
        String[] cols = {"ID", "Hình", "Tên sản phẩm", "Nhà cung cấp", "Loại", "Kích thước", "Giá", "Số lượng", "TT", "Trạng thái", "Hành động"};
        
        sanPhamModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 9 || c == 10; } // Cột 9 (Toggle), 10 (Sửa)
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
        };

        sanPhamTable = new JTable(sanPhamModel);
        sanPhamTable.setRowHeight(60);
        
        sanPhamTable.getColumnModel().getColumn(0).setPreferredWidth(30); 
        sanPhamTable.getColumnModel().getColumn(1).setPreferredWidth(60); 
        sanPhamTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        sanPhamTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Cột NCC
        
        // Ẩn cột Text trạng thái (Cột 8)
        sanPhamTable.getColumnModel().getColumn(8).setMinWidth(0);
        sanPhamTable.getColumnModel().getColumn(8).setMaxWidth(0);
        
        styleTableHeader(sanPhamTable);
        centerAllTableCells(sanPhamTable);
        
        // Renderer Ảnh
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

        setupProductToggle(sanPhamTable, 9); // Cột 9 là nút gạt

        // Nút Sửa
        sanPhamTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton b = new JButton("Sửa"); b.setBackground(new Color(40, 167, 69)); b.setForeground(Color.WHITE); return b;
        });
        sanPhamTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                JButton b = new JButton("Sửa"); b.setBackground(new Color(40, 167, 69));
                b.addActionListener(e -> {
                    int id = Integer.parseInt(t.getValueAt(r, 0).toString());
                    hienThiFormSanPham(id);
                    fireEditingStopped();
                }); return b;
            }
        });

        panel.add(new JScrollPane(sanPhamTable), BorderLayout.CENTER);
        loadSanPhamData();
        return panel;
    }

    private void loadSanPhamData() {
        try {
            if (sanPhamModel == null) return;
            sanPhamModel.setRowCount(0);
            
            String ten = (txtTimTen != null) ? txtTimTen.getText().trim() : "";
            String loai = (cbTimLoai != null) ? cbTimLoai.getSelectedItem().toString() : "Tất cả";
            String size = (cbTimKichThuoc != null) ? cbTimKichThuoc.getSelectedItem().toString() : "Tất cả";
            String giaTu = (txtGiaTu != null) ? txtGiaTu.getText().trim() : "";
            String giaDen = (txtGiaDen != null) ? txtGiaDen.getText().trim() : "";
            
            // Lọc theo NCC
            int maNCCLoc = 0;
            if (cbTimNCC != null && cbTimNCC.getSelectedIndex() > 0) {
                NhaCungCapDTO nccSel = (NhaCungCapDTO) cbTimNCC.getSelectedItem();
                maNCCLoc = nccSel.getMaNCC();
            }

            // SQL JOIN để lấy tên NCC
            StringBuilder sql = new StringBuilder(
                "SELECT f.*, n.ten_ncc FROM figure f LEFT JOIN nhacungcap n ON f.ma_ncc = n.ma_ncc WHERE 1=1");
            
            if (!ten.isEmpty()) sql.append(" AND f.ten LIKE '%").append(ten).append("%'");
            if (!"Tất cả".equals(loai)) sql.append(" AND f.loai = '").append(loai).append("'");
            if (!"Tất cả".equals(size)) sql.append(" AND f.kich_thuoc = '").append(size).append("'");
            if (maNCCLoc > 0) sql.append(" AND f.ma_ncc = ").append(maNCCLoc); 
            if (!giaTu.isEmpty()) sql.append(" AND f.gia >= ").append(giaTu);
            if (!giaDen.isEmpty()) sql.append(" AND f.gia <= ").append(giaDen);

            sql.append(" ORDER BY f.id ASC");

            try (Connection conn = db.getConnect(); ResultSet rs = conn.createStatement().executeQuery(sql.toString())) {
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                while (rs.next()) {
                    ImageIcon icon = loadProductImage(rs.getString("hinh_anh"));
                    if (icon != null) icon = new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

                    String stDB = rs.getString("trang_thai");
                    boolean isActive = (stDB != null && stDB.equalsIgnoreCase("Mở"));
                    String tenNCC = rs.getString("ten_ncc"); // Lấy tên NCC từ kết quả JOIN

                    sanPhamModel.addRow(new Object[]{
                        rs.getInt("id"), icon, rs.getString("ten"),
                        tenNCC != null ? tenNCC : "Chưa rõ", // Hiển thị NCC
                        rs.getString("loai"), rs.getString("kich_thuoc"),
                        nf.format(rs.getLong("gia")), rs.getInt("so_luong"),
                        isActive ? "Mở" : "Tắt", isActive, "Sửa"
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void hienThiFormSanPham(Integer idSanPham) {
        boolean isEdit = (idSanPham != null);
        JDialog dialog = new JDialog(this, isEdit ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới", true);
        dialog.setSize(850, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // --- PANEL CHÍNH (2 CỘT) ---
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel leftPanel = new JPanel(new GridBagLayout()); leftPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(0, 0, 10, 0); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0; 
        gbc.gridx = 0;

        // --- KHAI BÁO CÁC Ô NHẬP ---
        JTextField txtTen = styleTextField();
        JTextArea txtMoTa = new JTextArea(3, 20); 
        txtMoTa.setLineWrap(true); 
        txtMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JTextField txtGia = styleTextField();
        
        // [QUAN TRỌNG] Ô Tồn kho: Mặc định là 0 và KHÔNG CHO SỬA
        JTextField txtSoLuong = styleTextField();
        txtSoLuong.setText("0"); 
        txtSoLuong.setEditable(false); // Khóa lại
        txtSoLuong.setBackground(new Color(240, 240, 240)); // Màu xám nhẹ để biết là read-only
        
        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"Anime", "Game", "Gundam", "Khác"}); 
        cbLoai.setBackground(Color.WHITE);
        
        JComboBox<String> cbKichThuoc = new JComboBox<>(new String[]{"Khác", "1/6", "1/8", "1/10", "1/12", "1/144"}); 
        cbKichThuoc.setBackground(Color.WHITE);
        
        // [MỚI] ComboBox chọn Nhà Cung Cấp
        JComboBox<NhaCungCapDTO> cbNCC = new JComboBox<>();
        cbNCC.setBackground(Color.WHITE);
        // Load danh sách NCC đang hợp tác vào ComboBox
        for(NhaCungCapDTO ncc : nccBLL.getListNhaCungCap()) {
            cbNCC.addItem(ncc);
        }

        // --- LAYOUT CỘT TRÁI ---
        int y = 0;
        addLabelAndComponent(leftPanel, gbc, y++, "Tên sản phẩm:", txtTen);
        addLabelAndComponent(leftPanel, gbc, y++, "Mô tả:", new JScrollPane(txtMoTa));
        
        // Thêm NCC vào Form
        addLabelAndComponent(leftPanel, gbc, y++, "Nhà cung cấp (Nguồn nhập):", cbNCC);

        JPanel p2 = new JPanel(new GridLayout(1, 2, 15, 0)); p2.setBackground(Color.WHITE); 
        p2.add(createFieldGroup("Giá bán (VNĐ):", txtGia)); 
        p2.add(createFieldGroup("Tồn kho (Read-only):", txtSoLuong));
        gbc.gridy = y++; leftPanel.add(p2, gbc);
        
        JPanel p3 = new JPanel(new GridLayout(1, 2, 15, 0)); p3.setBackground(Color.WHITE); 
        p3.add(createFieldGroup("Loại:", cbLoai)); 
        p3.add(createFieldGroup("Kích thước:", cbKichThuoc));
        gbc.gridy = y++; leftPanel.add(p3, gbc);

        // --- CỘT PHẢI: ẢNH ---
        JPanel rightPanel = new JPanel(new BorderLayout()); 
        rightPanel.setBackground(Color.WHITE); 
        rightPanel.setBorder(BorderFactory.createTitledBorder("Hình ảnh"));
        
        JLabel lblImg = new JLabel("No Image", JLabel.CENTER); 
        JButton btnUp = new JButton("Tải ảnh lên");
        fileAnhMoi = null; 
        final String[] curImg = {"default.jpg"};
        
        // --- LOGIC LOAD DỮ LIỆU CŨ (NẾU LÀ SỬA) ---
        if (isEdit) { 
            try (Connection conn = db.getConnect(); 
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM figure WHERE id=?")) {
                ps.setInt(1, idSanPham); 
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtTen.setText(rs.getString("ten")); 
                    txtMoTa.setText(rs.getString("mo_ta"));
                    txtGia.setText(String.valueOf(rs.getInt("gia"))); 
                    txtSoLuong.setText(String.valueOf(rs.getInt("so_luong"))); // Load tồn kho hiện tại
                    cbLoai.setSelectedItem(rs.getString("loai")); 
                    cbKichThuoc.setSelectedItem(rs.getString("kich_thuoc"));
                    curImg[0] = rs.getString("hinh_anh");
                    
                    ImageIcon ic = loadProductImage(curImg[0]);
                    if(ic!=null) {
                        lblImg.setIcon(new ImageIcon(ic.getImage().getScaledInstance(250,250,Image.SCALE_SMOOTH))); 
                        lblImg.setText("");
                    }
                    
                    // Set lại NCC cũ
                    int dbMaNCC = rs.getInt("ma_ncc");
                    for(int i=0; i<cbNCC.getItemCount(); i++) {
                        if(cbNCC.getItemAt(i).getMaNCC() == dbMaNCC) { 
                            cbNCC.setSelectedIndex(i); 
                            break; 
                        }
                    }
                    // Nếu đang sửa, có thể chọn khóa luôn NCC nếu muốn (tùy logic nghiệp vụ)
                    // cbNCC.setEnabled(false); 
                }
            } catch (Exception e) {}
        }
        
        // Sự kiện chọn ảnh
        btnUp.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(); 
            if(fc.showOpenDialog(dialog)==JFileChooser.APPROVE_OPTION) {
                fileAnhMoi = fc.getSelectedFile();
                lblImg.setIcon(new ImageIcon(new ImageIcon(fileAnhMoi.getAbsolutePath()).getImage().getScaledInstance(250,250,Image.SCALE_SMOOTH))); 
                lblImg.setText("");
            }
        });
        
        JPanel btnWrap = new JPanel(); btnWrap.setBackground(Color.WHITE); btnWrap.add(btnUp);
        rightPanel.add(lblImg, BorderLayout.CENTER); 
        rightPanel.add(btnWrap, BorderLayout.SOUTH);

        mainPanel.add(leftPanel); 
        mainPanel.add(rightPanel);
        dialog.add(mainPanel, BorderLayout.CENTER);
        
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bot.setBackground(new Color(245,245,245));

        // --- NÚT XÓA SẢN PHẨM ---
        JButton btnXoa = new JButton("Xóa SP");
        btnXoa.setBackground(new Color(220, 53, 69)); // Đỏ
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setVisible(isEdit); // Chỉ hiện khi đang sửa

        btnXoa.addActionListener(e -> {
            // 1. Hỏi xác nhận
            if (JOptionPane.showConfirmDialog(dialog, 
                "Bạn có chắc muốn xóa sản phẩm này?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            // 2. Thử Xóa vĩnh viễn (Gọi BLL)
            // Lưu ý: Bạn cần đảm bảo BLL/DAL có hàm xoaSanPham trả về boolean
            // Nếu DAL chưa có, nó sẽ nhảy vào else bên dưới (giả lập lỗi) hoặc bạn cần thêm hàm xóa vào DAL.
            // Giả sử bll.xoaSanPham(id) đã được viết ở các bước trước.
            if (new BLL.FigureBLL().xoaSanPham(idSanPham)) { 
                JOptionPane.showMessageDialog(dialog, "Đã xóa hoàn toàn khỏi hệ thống!");
                triggerRealTimeUpdate();
                dialog.dispose();
            } else {
                // 3. Nếu không xóa được (do dính khóa ngoại), gợi ý Chuyển trạng thái
                int choice = JOptionPane.showConfirmDialog(dialog, 
                    "Không thể xóa vĩnh viễn vì sản phẩm này đã có lịch sử giao dịch!\n" +
                    "Bạn có muốn chuyển trạng thái sang 'Tắt' (Ngừng kinh doanh) không?", 
                    "Không thể xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    try (Connection conn = db.getConnect(); 
                         PreparedStatement ps = conn.prepareStatement("UPDATE figure SET trang_thai = 'Tắt' WHERE id = ?")) {
                        ps.setInt(1, idSanPham);
                        ps.executeUpdate();
                        
                        JOptionPane.showMessageDialog(dialog, "Đã chuyển trạng thái sang 'Tắt'!");
                        triggerRealTimeUpdate();
                        dialog.dispose();
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        });
        
        // --- NÚT LƯU ---
        JButton btnSave = new JButton("Lưu sản phẩm"); 
        btnSave.setBackground(new Color(40,167,69)); 
        btnSave.setForeground(Color.WHITE);
        
        btnSave.addActionListener(e -> {
            try {
                String n = txtTen.getText().trim(); 
                if(n.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Tên không được để trống!"); return; }

                int g = Integer.parseInt(txtGia.getText()); 
                // Không cần lấy txtSoLuong vì nó bị khóa và tự động tính
                
                String img = (fileAnhMoi!=null) ? fileAnhMoi.getName() : curImg[0];
                NhaCungCapDTO selNCC = (NhaCungCapDTO) cbNCC.getSelectedItem();
                if(selNCC == null) { JOptionPane.showMessageDialog(dialog, "Vui lòng chọn Nhà cung cấp!"); return; }

                // SQL INSERT/UPDATE (Có thêm ma_ncc)
                // Lưu ý: Khi INSERT, ta để số lượng = 0 (mặc định)
                String sql = isEdit 
                    ? "UPDATE figure SET ten=?, mo_ta=?, gia=?, loai=?, kich_thuoc=?, hinh_anh=?, ma_ncc=? WHERE id=?"
                    : "INSERT INTO figure (ten, mo_ta, gia, so_luong, loai, kich_thuoc, hinh_anh, ma_ncc, trang_thai) VALUES (?, ?, ?, 0, ?, ?, ?, ?, 'Mở')";
                
                try(Connection conn=db.getConnect(); PreparedStatement ps=conn.prepareStatement(sql)) {
                    ps.setString(1, n); 
                    ps.setString(2, txtMoTa.getText()); 
                    ps.setInt(3, g); 
                    // ps.setInt(4, s); -> BỎ qua tham số số lượng khi Update, và Hardcode 0 khi Insert
                    
                    if(isEdit) {
                        // UPDATE: ten, mo_ta, gia, loai, kich_thuoc, hinh_anh, ma_ncc WHERE id
                        ps.setString(4, cbLoai.getSelectedItem().toString()); 
                        ps.setString(5, cbKichThuoc.getSelectedItem().toString());
                        ps.setString(6, img); 
                        ps.setInt(7, selNCC.getMaNCC()); 
                        ps.setInt(8, idSanPham);
                    } else {
                        // INSERT: ten, mo_ta, gia, 0, loai, kich_thuoc, hinh_anh, ma_ncc, 'Mở'
                        ps.setString(4, cbLoai.getSelectedItem().toString()); 
                        ps.setString(5, cbKichThuoc.getSelectedItem().toString());
                        ps.setString(6, img); 
                        ps.setInt(7, selNCC.getMaNCC());
                    }
                    
                    ps.executeUpdate(); 
                    JOptionPane.showMessageDialog(dialog, "Lưu thành công!"); 
                    
                    triggerRealTimeUpdate(); // <--- CHÈN VÀO ĐÂY
                    
                    loadSanPhamData(); 
                    dialog.dispose();
                }
            } catch(NumberFormatException ex) { 
                JOptionPane.showMessageDialog(dialog, "Giá bán phải là số hợp lệ!"); 
            } catch(Exception ex) { 
                ex.printStackTrace(); 
                JOptionPane.showMessageDialog(dialog, "Lỗi lưu dữ liệu!"); 
            }
        });
        
        bot.add(btnXoa); // <--- Add nút Xóa
        bot.add(btnSave); 
        dialog.add(bot, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    // --- HÀM HỖ TRỢ TẠO NÚT GẠT CHO SẢN PHẨM ---
    private void setupProductToggle(JTable table, int colIndex) {
        // 1. Renderer (Hiển thị màu sắc)
        table.getColumnModel().getColumn(colIndex).setCellRenderer((t, v, s, h, r, c) -> {
            boolean active = (Boolean) v;
            JToggleButton btn = new JToggleButton(active ? "Mở" : "Khóa");
            // Mở = Xanh Cyan (#17a2b8), Khóa = Đỏ (#dc3545)
            btn.setBackground(active ? new Color(23, 162, 184) : new Color(220, 53, 69)); 
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        // 2. Editor (Xử lý sự kiện click)
        table.getColumnModel().getColumn(colIndex).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JToggleButton btn;
            private boolean currState;
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currState = (Boolean) value;
                btn = new JToggleButton(currState ? "Mở" : "Khóa", currState);
                
                btn.addActionListener(e -> {
                    currState = !currState; // Đảo trạng thái
                    int id = Integer.parseInt(table.getValueAt(row, 0).toString());
                    
                    // Cập nhật Database
                    try (Connection conn = db.getConnect(); 
                         PreparedStatement ps = conn.prepareStatement("UPDATE figure SET trang_thai=? WHERE id=?")) {
                        ps.setString(1, currState ? "Mở" : "Tắt");
                        ps.setInt(2, id);
                        ps.executeUpdate();
                        
                        triggerRealTimeUpdate(); // <--- CHÈN VÀO ĐÂY
                        
                    } catch (Exception ex) { 
                        ex.printStackTrace(); 
                    }
                    
                    // Cập nhật lại giá trị trong bảng để Renderer vẽ lại màu đúng
                    table.setValueAt(currState, row, column);
                    fireEditingStopped();
                });
                return btn;
            }
            
            @Override
            public Object getCellEditorValue() {
                return currState;
            }
        });
    }

    // ================== QUẢN LÝ KHO (COMBOBOX NCC) ==================
    // ================== QUẢN LÝ KHO (CÓ THÊM NÚT KIỂM KÊ) ==================
    private JPanel taoKhoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // --- 1. HEADER (TIÊU ĐỀ & BỘ LỌC LỊCH SỬ) ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Quản lý kho");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setBackground(Color.WHITE);

        txtHistTen = new JTextField(10); txtHistTen.setToolTipText("Tên SP...");
        cbHistNCC = new JComboBox<>(); cbHistNCC.setBackground(Color.WHITE);
        cbHistNCC.addItem(new NhaCungCapDTO(0, "Tất cả NCC", "", "", "", ""));
        for(NhaCungCapDTO ncc : nccBLL.getListNhaCungCap()) cbHistNCC.addItem(ncc);
        
        txtHistDateFrom = new JTextField(8); txtHistDateFrom.setToolTipText("yyyy-mm-dd");
        txtHistDateTo = new JTextField(8);   txtHistDateTo.setToolTipText("yyyy-mm-dd");

        JButton btnFilter = new JButton("Lọc Lịch Sử");
        btnFilter.setBackground(new Color(0, 123, 255)); btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> loadLichSuNhapKho()); 

        filterPanel.add(new JLabel("SP:"));   filterPanel.add(txtHistTen);
        filterPanel.add(new JLabel("NCC:"));  filterPanel.add(cbHistNCC);
        filterPanel.add(new JLabel("Từ:"));   filterPanel.add(txtHistDateFrom);
        filterPanel.add(new JLabel("Đến:"));  filterPanel.add(txtHistDateTo);
        filterPanel.add(btnFilter);

        topPanel.add(filterPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- 2. NỘI DUNG CHÍNH ---
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(new Color(240, 242, 245));
        mainContent.setBorder(new EmptyBorder(10, 0, 0, 0));

        // === CỘT TRÁI: NHẬP HÀNG & KIỂM KÊ ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), new EmptyBorder(15, 15, 15, 15)));
        
        JLabel lblSubTitle = new JLabel("TÁC VỤ KHO HÀNG", JLabel.CENTER); // Đổi tên tiêu đề cho hợp lý
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubTitle.setForeground(new Color(0, 123, 255));
        lblSubTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        leftPanel.add(lblSubTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridx = 0;

        // A. Tìm kiếm
        JPanel searchBox = new JPanel(new BorderLayout(10, 0)); searchBox.setBackground(Color.WHITE);
        txtKhoTimKiem = new JTextField(); txtKhoTimKiem.setPreferredSize(new Dimension(0, 35));
        txtKhoTimKiem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(0, 10, 0, 10)));
        JButton btnTimSP = new JButton("Tìm"); btnTimSP.setBackground(new Color(0, 123, 255)); btnTimSP.setForeground(Color.WHITE);
        btnTimSP.addActionListener(e -> timSanPhamDeNhap()); txtKhoTimKiem.addActionListener(e -> timSanPhamDeNhap());
        searchBox.add(txtKhoTimKiem, BorderLayout.CENTER); searchBox.add(btnTimSP, BorderLayout.EAST);
        gbc.gridy = 0; formPanel.add(new JLabel("1. Tìm sản phẩm (ID/Tên):"), gbc);
        gbc.gridy = 1; formPanel.add(searchBox, gbc);

        // B. Thông tin
        JPanel infoPanel = new JPanel(new BorderLayout(15, 0)); infoPanel.setBackground(new Color(250, 250, 250));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        lblKhoAnh = new JLabel("Ảnh", JLabel.CENTER); lblKhoAnh.setPreferredSize(new Dimension(90, 90));
        lblKhoAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JPanel txtInfo = new JPanel(new GridLayout(4, 1, 2, 0)); txtInfo.setBackground(new Color(250, 250, 250)); txtInfo.setBorder(new EmptyBorder(5, 0, 5, 5));
        lblKhoId = new JLabel("ID: -"); lblKhoTen = new JLabel("Tên: -"); lblKhoTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblKhoTon = new JLabel("Tồn: -"); lblKhoTon.setForeground(new Color(220, 53, 69)); lblKhoGia = new JLabel("Giá bán: -");
        txtInfo.add(lblKhoId); txtInfo.add(lblKhoTen); txtInfo.add(lblKhoTon); txtInfo.add(lblKhoGia);
        infoPanel.add(lblKhoAnh, BorderLayout.WEST); infoPanel.add(txtInfo, BorderLayout.CENTER);
        gbc.gridy = 2; formPanel.add(new JLabel("2. Thông tin chi tiết:"), gbc); gbc.gridy = 3; formPanel.add(infoPanel, gbc);

        // C. Nhập liệu
        JPanel inputGroup = new JPanel(new GridLayout(2, 2, 10, 10));
        inputGroup.setBackground(Color.WHITE);
        inputGroup.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        cbNhaCungCap = new JComboBox<>(); cbNhaCungCap.setBackground(Color.WHITE); cbNhaCungCap.setBorder(BorderFactory.createTitledBorder("Nhà cung cấp")); loadComboBoxNhaCungCap();
        lblTongTienNhap = new JLabel("0 VND", JLabel.CENTER); lblTongTienNhap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTienNhap.setForeground(new Color(220, 53, 69)); lblTongTienNhap.setBorder(BorderFactory.createTitledBorder("Thành tiền"));
        txtSoLuongNhap = new JTextField(); txtSoLuongNhap.setHorizontalAlignment(JTextField.CENTER); txtSoLuongNhap.setBorder(BorderFactory.createTitledBorder("SL Nhập"));
        txtGiaNhap = new JTextField(); txtGiaNhap.setHorizontalAlignment(JTextField.CENTER); txtGiaNhap.setBorder(BorderFactory.createTitledBorder("Giá Nhập (VND)"));
        
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTongTienNhap(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTongTienNhap(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTongTienNhap(); }
        };
        txtSoLuongNhap.getDocument().addDocumentListener(dl); txtGiaNhap.getDocument().addDocumentListener(dl);
        
        inputGroup.add(cbNhaCungCap); inputGroup.add(lblTongTienNhap); inputGroup.add(txtSoLuongNhap); inputGroup.add(txtGiaNhap);
        gbc.gridy = 4; formPanel.add(inputGroup, gbc);

        // D. Panel chứa Nút: [NHẬP KHO] [KIỂM KÊ]
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnXacNhan = new JButton("NHẬP KHO");
        btnXacNhan.setBackground(new Color(40, 167, 69)); 
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setPreferredSize(new Dimension(0, 45));
        btnXacNhan.addActionListener(e -> xuLyNhapKho());
        
        // [MỚI] Nút Kiểm Kê
        JButton btnKiemKe = new JButton("KIỂM KÊ KHO");
        btnKiemKe.setBackground(new Color(255, 193, 7)); // Vàng cam
        btnKiemKe.setForeground(Color.BLACK);
        btnKiemKe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKiemKe.addActionListener(e -> hienThiFormKiemKe());

        btnPanel.add(btnXacNhan);
        btnPanel.add(btnKiemKe);
        
        gbc.gridy = 5; formPanel.add(btnPanel, gbc); // Thay thế add btnXacNhan cũ
        
        JPanel wrapForm = new JPanel(new BorderLayout()); wrapForm.setBackground(Color.WHITE); wrapForm.add(formPanel, BorderLayout.NORTH);
        leftPanel.add(wrapForm, BorderLayout.CENTER);


        // === CỘT PHẢI: LỊCH SỬ ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), new EmptyBorder(15, 15, 15, 15)));
        JLabel subTitleRight = new JLabel("LỊCH SỬ GIAO DỊCH", JLabel.CENTER);
        subTitleRight.setFont(new Font("Segoe UI", Font.BOLD, 16)); subTitleRight.setBorder(new EmptyBorder(0, 0, 15, 0));
        rightPanel.add(subTitleRight, BorderLayout.NORTH);

        String[] cols = {"Mã PN", "Sản phẩm", "SL", "Giá nhập", "Tổng tiền", "NCC", "Ngày", "Trạng thái", "Hủy"};
        khoHistoryModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 8; } // Chỉ cột Hủy
        };
        
        khoHistoryTable = new JTable(khoHistoryModel);
        khoHistoryTable.setRowHeight(40);
        styleTableHeader(khoHistoryTable);
        centerAllTableCells(khoHistoryTable);
        
        khoHistoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        khoHistoryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        khoHistoryTable.getColumnModel().getColumn(6).setPreferredWidth(110);

        // Renderer Trạng thái
        khoHistoryTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel)super.getTableCellRendererComponent(t, v, s, f, r, c);
                String st = (String)v;
                if("Hoàn thành".equals(st)) l.setForeground(new Color(40, 167, 69)); else l.setForeground(Color.RED);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
            }
        });

        // Renderer/Editor Nút Hủy
        khoHistoryTable.getColumnModel().getColumn(8).setCellRenderer((t, v, s, h, r, c) -> {
            String st = (String)t.getModel().getValueAt(r, 7);
            JButton b = new JButton("Hủy"); b.setFont(new Font("Segoe UI", Font.BOLD, 11));
            if("Đã hủy".equals(st)) { b.setEnabled(false); b.setText("Đã hủy"); } 
            else { b.setBackground(new Color(220, 53, 69)); b.setForeground(Color.WHITE); }
            return b;
        });
        
        khoHistoryTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton b;
            @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                String st = (String)t.getModel().getValueAt(r, 7);
                b = new JButton("Hủy");
                if(!"Đã hủy".equals(st)) {
                    b.setBackground(new Color(220, 53, 69)); b.setForeground(Color.WHITE);
                    b.addActionListener(e -> {
                        String ma = t.getValueAt(r, 0).toString();
                        xuLyHuyPhieuNhap(Integer.parseInt(ma.replace("PN", "")));
                        fireEditingStopped();
                    });
                } else { b.setEnabled(false); b.setText("Đã hủy"); }
                return b;
            }
        });

        JScrollPane scR = new JScrollPane(khoHistoryTable); scR.setBorder(null);
        rightPanel.add(scR, BorderLayout.CENTER);
        JButton btnRe = new JButton("Làm mới dữ liệu"); btnRe.addActionListener(e -> loadLichSuNhapKho());
        rightPanel.add(btnRe, BorderLayout.SOUTH);

        mainContent.add(leftPanel); mainContent.add(rightPanel);
        panel.add(mainContent, BorderLayout.CENTER);
        
        loadLichSuNhapKho();
        return panel;
    }

    private void loadComboBoxNhaCungCap() {
        cbNhaCungCap.removeAllItems();
        List<NhaCungCapDTO> list = nccBLL.getListNhaCungCap();
        for (NhaCungCapDTO ncc : list) cbNhaCungCap.addItem(ncc);
    }

    private void xuLyNhapKho() {
        if (sanPhamDangChonNhap == null) { JOptionPane.showMessageDialog(this, "Chọn sản phẩm trước!"); return; }
        NhaCungCapDTO ncc = (NhaCungCapDTO) cbNhaCungCap.getSelectedItem();
        if (ncc == null) { JOptionPane.showMessageDialog(this, "Lỗi: Không xác định được NCC!"); return; }
        
        try {
            int sl = Integer.parseInt(txtSoLuongNhap.getText().trim());
            long gia = Long.parseLong(txtGiaNhap.getText().trim());
            
            if(sl <= 0 || gia < 0) { JOptionPane.showMessageDialog(this, "Số lượng và Giá phải hợp lệ!"); return; }
            
            long tong = sl * gia;
            
            // Insert vào DB (có giá và tổng tiền)
            String sql = "INSERT INTO nhapkho (figureId, so_luong_nhap, don_gia_nhap, tong_tien_nhap, ma_ncc, ngay_nhap, ma_nhan_vien) VALUES (?, ?, ?, ?, ?, NOW(), ?)";
            
            try(Connection conn=db.getConnect(); PreparedStatement ps=conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                ps.setInt(1, sanPhamDangChonNhap.getId());
                ps.setInt(2, sl);
                ps.setLong(3, gia);
                ps.setLong(4, tong);
                ps.setInt(5, ncc.getMaNCC());
                ps.setInt(6, currentUser.getMaNguoiDung());
                
                ps.executeUpdate();
                
                // Lấy ID phiếu vừa tạo để in hóa đơn
                ResultSet rs = ps.getGeneratedKeys();
                int maPhieu = 0;
                if(rs.next()) maPhieu = rs.getInt(1);
                
                JOptionPane.showMessageDialog(this, "Nhập kho thành công!");
                
                triggerRealTimeUpdate(); // <--- CHÈN VÀO ĐÂY

                loadSanPhamData(); // Tự refresh bảng Admin
                // Hiện phiếu nhập
                hienThiPhieuNhapPopup(maPhieu, sanPhamDangChonNhap, ncc.getTenNCC(), sl, gia, tong);
                
                loadLichSuNhapKho(); 
                timSanPhamDeNhap(); 
                resetFormNhap();
            }
        } catch(NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!"); 
        } catch(Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage()); 
        }
    }
    
    private void resetFormNhap() {
        lblKhoId.setText("ID: -"); lblKhoTen.setText("Tên: -"); 
        lblKhoTon.setText("Tồn: -"); lblKhoGia.setText("Giá: -");
        lblKhoAnh.setIcon(null); lblKhoAnh.setText("Ảnh");
        txtSoLuongNhap.setText("");
        txtGiaNhap.setText(""); // Reset giá
        sanPhamDangChonNhap = null;
        
        cbNhaCungCap.setSelectedIndex(-1);
        cbNhaCungCap.setEnabled(false); // Mặc định khóa, chỉ mở khi tìm SP (nhưng logic mới là luôn khóa theo SP nên để false luôn cũng được, hoặc true để chờ tìm)
        // Thực ra nên để true để clear, nhưng khi tìm thấy SP nó sẽ tự khóa lại đúng NCC.
    }

    private void timSanPhamDeNhap() {
        String k = txtKhoTimKiem.getText().trim(); 
        if (k.isEmpty()) return;
        
        resetFormNhap(); // Reset form trước khi tìm mới

        try(Connection conn = db.getConnect(); 
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM figure WHERE id=? OR ten LIKE ? LIMIT 1")) {
            
            // Xử lý input: Nếu là số thì tìm theo ID, nếu chữ thì tìm theo tên
            try { 
                ps.setInt(1, Integer.parseInt(k)); 
            } catch(Exception e) { 
                ps.setInt(1, -1); // Nếu không phải số thì ID = -1 (không tìm thấy theo ID)
            }
            ps.setString(2, "%"+k+"%");
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                sanPhamDangChonNhap = new FigureDTO();
                sanPhamDangChonNhap.setId(rs.getInt("id"));
                sanPhamDangChonNhap.setTen(rs.getString("ten"));
                sanPhamDangChonNhap.setSoLuong(rs.getInt("so_luong"));
                sanPhamDangChonNhap.setGia(rs.getLong("gia"));
                sanPhamDangChonNhap.setHinhAnh(rs.getString("hinh_anh"));
                sanPhamDangChonNhap.setMaNCC(rs.getInt("ma_ncc")); 

                // Fill UI Info
                lblKhoId.setText("ID: " + rs.getInt("id"));
                lblKhoTen.setText("<html>" + rs.getString("ten") + "</html>"); // Bọc html để tự xuống dòng nếu tên dài
                lblKhoTon.setText("Tồn: " + rs.getInt("so_luong"));
                lblKhoGia.setText("Giá bán: " + String.format("%,d VND", rs.getLong("gia")));
                
                ImageIcon icon = loadProductImage(rs.getString("hinh_anh"));
                if(icon != null) lblKhoAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(90,90,Image.SCALE_SMOOTH)));
                else { lblKhoAnh.setIcon(null); lblKhoAnh.setText("No IMG"); }

                // [QUAN TRỌNG] Tự động chọn NCC và KHÓA lại để đảm bảo nhập đúng nguồn
                int maNCC = rs.getInt("ma_ncc");
                boolean foundNCC = false;
                for(int i=0; i<cbNhaCungCap.getItemCount(); i++) {
                    NhaCungCapDTO ncc = (NhaCungCapDTO) cbNhaCungCap.getItemAt(i);
                    if(ncc.getMaNCC() == maNCC) {
                        cbNhaCungCap.setSelectedIndex(i);
                        foundNCC = true;
                        break;
                    }
                }
                
                cbNhaCungCap.setEnabled(false); // Khóa không cho đổi NCC
                if(!foundNCC) JOptionPane.showMessageDialog(this, "Lưu ý: Nhà cung cấp của sản phẩm này đã bị ngừng hợp tác hoặc bị xóa!");
                
                txtSoLuongNhap.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void loadLichSuNhapKho() {
        khoHistoryModel.setRowCount(0);
        
        String tenSP = txtHistTen.getText().trim();
        String dateFrom = txtHistDateFrom.getText().trim();
        String dateTo = txtHistDateTo.getText().trim();
        
        int maNCC = 0;
        if (cbHistNCC != null && cbHistNCC.getSelectedItem() instanceof NhaCungCapDTO) {
            NhaCungCapDTO ncc = (NhaCungCapDTO) cbHistNCC.getSelectedItem();
            maNCC = ncc.getMaNCC();
        }

        StringBuilder sql = new StringBuilder(
            "SELECT n.ma_nhap, f.ten, n.so_luong_nhap, n.don_gia_nhap, n.tong_tien_nhap, ncc.ten_ncc, n.ngay_nhap, n.trang_thai " +
            "FROM nhapkho n " +
            "JOIN figure f ON n.figureId = f.id " +
            "JOIN nhacungcap ncc ON n.ma_ncc = ncc.ma_ncc " +
            "WHERE 1=1 "
        );

        if (!tenSP.isEmpty()) sql.append(" AND f.ten LIKE '%").append(tenSP).append("%'");
        if (maNCC > 0) sql.append(" AND n.ma_ncc = ").append(maNCC);
        if (!dateFrom.isEmpty()) sql.append(" AND DATE(n.ngay_nhap) >= '").append(dateFrom).append("'");
        if (!dateTo.isEmpty()) sql.append(" AND DATE(n.ngay_nhap) <= '").append(dateTo).append("'");

        sql.append(" ORDER BY n.ngay_nhap ASC LIMIT 100"); // Limit 100 để không load quá nặng

        try (Connection conn = db.getConnect(); ResultSet rs = conn.createStatement().executeQuery(sql.toString())) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                khoHistoryModel.addRow(new Object[]{
                    "PN" + rs.getInt("ma_nhap"),
                    rs.getString("ten"),
                    "+" + rs.getInt("so_luong_nhap"),
                    nf.format(rs.getLong("don_gia_nhap")),
                    nf.format(rs.getLong("tong_tien_nhap")),
                    rs.getString("ten_ncc"),
                    sdf.format(rs.getTimestamp("ngay_nhap")),
                    rs.getString("trang_thai"),
                    "Hủy"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void hienThiPhieuNhapPopup(int maPhieu, FigureDTO sp, String tenNCC, int sl, long gia, long tong) {
        JDialog d = new JDialog(this, "Phiếu Nhập Kho", true);
        d.setSize(450, 550);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(Color.WHITE);
        d.setLayout(new BorderLayout());
        
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Header
        JLabel title = new JLabel("PHIẾU NHẬP KHO", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(title); p.add(Box.createVerticalStrut(5));
        
        JLabel sub = new JLabel("Mã phiếu: PN" + maPhieu, JLabel.CENTER);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(sub); p.add(Box.createVerticalStrut(20));
        
        // Info NCC
        p.add(new JLabel("Nhà cung cấp: " + tenNCC));
        p.add(new JLabel("Người nhập: " + currentUser.getTenDangNhap()));
        p.add(new JLabel("Ngày nhập: " + new java.util.Date().toString()));
        p.add(Box.createVerticalStrut(10));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(10));
        
        // Item Details
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        p.add(new JLabel("Sản phẩm: " + sp.getTen()));
        p.add(Box.createVerticalStrut(5));
        
        JPanel row = new JPanel(new GridLayout(1, 2)); row.setBackground(Color.WHITE);
        row.add(new JLabel("Số lượng: " + sl));
        row.add(new JLabel("Đơn giá: " + nf.format(gia)));
        p.add(row);
        
        p.add(Box.createVerticalStrut(20));
        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(10));
        
        // Total
        JLabel lTotal = new JLabel("TỔNG TIỀN: " + nf.format(tong));
        lTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lTotal.setForeground(new Color(40, 167, 69));
        lTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        p.add(lTotal);
        
        d.add(p, BorderLayout.CENTER);
        
        JButton bClose = new JButton("Đóng");
        bClose.addActionListener(e -> d.dispose());
        JPanel bP = new JPanel(); bP.setBackground(Color.WHITE); bP.add(bClose);
        d.add(bP, BorderLayout.SOUTH);
        
        d.setVisible(true);
    }
    
    private void xuLyHuyPhieuNhap(int maPhieu) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn hủy phiếu nhập này?\nSố lượng hàng sẽ bị trừ lại khỏi kho.", 
            "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = db.getConnect()) {
                // 1. Lấy thông tin phiếu nhập để biết trừ bao nhiêu
                String sqlGet = "SELECT figureId, so_luong_nhap FROM nhapkho WHERE ma_nhap = ? AND trang_thai != 'Đã hủy'";
                PreparedStatement psGet = conn.prepareStatement(sqlGet);
                psGet.setInt(1, maPhieu);
                ResultSet rs = psGet.executeQuery();
                
                if (rs.next()) {
                    int idSP = rs.getInt("figureId");
                    int slNhap = rs.getInt("so_luong_nhap");
                    
                    // 2. Cập nhật trạng thái phiếu nhập thành "Đã hủy"
                    String sqlUpdate = "UPDATE nhapkho SET trang_thai = 'Đã hủy' WHERE ma_nhap = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                    psUpdate.setInt(1, maPhieu);
                    psUpdate.executeUpdate();
                    
                    // 3. Trừ tồn kho (Database Trigger có thể đã làm việc này nếu bạn có trigger update, 
                    // nhưng để chắc chắn, ta trừ thủ công ở đây hoặc dựa vào Trigger 'tru_kho_khi_huy_nhap' bạn đã tạo)
                    // Vì bạn đã có Trigger `tru_kho_khi_huy_nhap` trong SQL, ta KHÔNG cần code trừ kho ở đây nữa.
                    
                    JOptionPane.showMessageDialog(this, "Đã hủy phiếu nhập thành công!");
                    
                    triggerRealTimeUpdate(); // <--- CHÈN VÀO ĐÂY
                    
                    loadLichSuNhapKho(); // Load lại bảng lịch sử
                    timSanPhamDeNhap();  // Load lại thông tin sản phẩm để thấy tồn kho giảm
                } else {
                    JOptionPane.showMessageDialog(this, "Phiếu nhập không tồn tại hoặc đã bị hủy trước đó.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy phiếu: " + e.getMessage());
            }
        }
    }
    
    private void updateTongTienNhap() {
        try {
            long sl = Long.parseLong(txtSoLuongNhap.getText().trim());
            long gia = Long.parseLong(txtGiaNhap.getText().trim());
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            lblTongTienNhap.setText(nf.format(sl * gia));
        } catch (Exception e) {
            lblTongTienNhap.setText("0 VND");
        }
    }
    
    private void hienThiFormKiemKe() {
        // 1. Kiểm tra xem đã chọn sản phẩm chưa
        if (sanPhamDangChonNhap == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm và chọn một sản phẩm để kiểm kê!", "Chưa chọn SP", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Tạo Popup
        JDialog d = new JDialog(this, "Kiểm kê: " + sanPhamDangChonNhap.getTen(), true);
        d.setSize(400, 350);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());
        d.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;

        // Hiển thị thông tin hiện tại
        JLabel lblCurrent = new JLabel("Tồn kho hệ thống: " + sanPhamDangChonNhap.getSoLuong());
        lblCurrent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCurrent.setForeground(new Color(0, 123, 255));
        
        JTextField txtThucTe = new JTextField();
        txtThucTe.setBorder(BorderFactory.createTitledBorder("Số lượng thực tế (Kiểm đếm được)"));
        
        JTextArea txtLyDo = new JTextArea(3, 20);
        txtLyDo.setBorder(BorderFactory.createTitledBorder("Lý do điều chỉnh (nếu có chênh lệch)"));
        
        JButton btnSave = new JButton("Xác nhận & Cập nhật kho");
        btnSave.setBackground(new Color(255, 193, 7));
        
        // Add components
        g.gridx=0; g.gridy=0; d.add(lblCurrent, g);
        g.gridy=1; d.add(txtThucTe, g);
        g.gridy=2; d.add(new JScrollPane(txtLyDo), g);
        g.gridy=3; d.add(btnSave, g);

        // Logic xử lý
        btnSave.addActionListener(e -> {
            try {
                String strThucTe = txtThucTe.getText().trim();
                if (strThucTe.isEmpty()) { JOptionPane.showMessageDialog(d, "Nhập số lượng thực tế!"); return; }
                
                int slThucTe = Integer.parseInt(strThucTe);
                int slHeThong = sanPhamDangChonNhap.getSoLuong();
                
                if (slThucTe < 0) { JOptionPane.showMessageDialog(d, "Số lượng không được âm!"); return; }
                
                // Nếu có chênh lệch, bắt buộc nhập lý do
                if (slThucTe != slHeThong && txtLyDo.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Có chênh lệch! Vui lòng nhập lý do (VD: Hư hỏng, mất mát...)", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Xác nhận lần cuối
                if (slThucTe != slHeThong) {
                    int confirm = JOptionPane.showConfirmDialog(d, 
                        "Hệ thống: " + slHeThong + "\nThực tế: " + slThucTe + "\nChênh lệch: " + (slThucTe - slHeThong) + "\n\nBạn chắc chắn muốn cập nhật kho?", 
                        "Xác nhận điều chỉnh", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) return;
                } else {
                     JOptionPane.showMessageDialog(d, "Số lượng khớp! Không cần điều chỉnh.");
                     d.dispose();
                     return;
                }

                // Gọi BLL
                if (kkeBLL.taoPhieuKiemKe(sanPhamDangChonNhap.getId(), slHeThong, slThucTe, currentUser.getMaNguoiDung(), txtLyDo.getText())) {
                    JOptionPane.showMessageDialog(d, "Kiểm kê hoàn tất! Kho đã được cập nhật.");
                    
                    // Refresh giao diện
                    triggerRealTimeUpdate(); 
                    timSanPhamDeNhap(); // Load lại thông tin SP để thấy số mới
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Lỗi hệ thống!");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Vui lòng nhập số nguyên!");
            }
        });
        
        d.setVisible(true);
    }
    // ================== QUẢN LÝ NHÀ CUNG CẤP ==================
    private JPanel taoNhaCungCapPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Quản lý Nhà cung cấp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnAdd = new JButton("+ Thêm NCC");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(130, 35));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> hienThiFormNCC(null)); 

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- CẤU TRÚC CỘT MỚI ---
        // Cũ: ID, Tên, SĐT, Email, Địa chỉ, Trạng thái(Text), Hợp tác(Toggle), Hành động
        // Mới: ID, Tên, SĐT, Email, Địa chỉ, Trạng thái(Toggle), Chi tiết(Button), Hành động(Button)
        String[] cols = {"ID", "Tên Nhà Cung Cấp", "SĐT", "Email", "Địa chỉ", "Trạng thái", "Chi tiết", "Hành động"};
        
        nccModel = new DefaultTableModel(cols, 0) {
            // Cho phép sửa cột 5 (Toggle), 6 (Chi tiết), 7 (Sửa)
            @Override public boolean isCellEditable(int r, int c) { return c >= 5; }
        };

        nccTable = new JTable(nccModel);
        nccTable.setRowHeight(50);
        styleTableHeader(nccTable);
        centerAllTableCells(nccTable);
        
        // Căn chỉnh độ rộng
        nccTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        nccTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        nccTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        // 1. Renderer Nút Gạt (Cột 5 - Trạng thái)
        nccTable.getColumn("Trạng thái").setCellRenderer((t, v, s, h, r, c) -> {
            boolean active = (Boolean) v;
            JToggleButton btn = new JToggleButton(active ? "Hợp tác" : "Ngừng");
            btn.setBackground(active ? new Color(23, 162, 184) : new Color(220, 53, 69));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nccTable.getColumn("Trạng thái").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JToggleButton btn; boolean currState;
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                currState = (Boolean) v;
                btn = new JToggleButton(currState ? "Hợp tác" : "Ngừng", currState);
                btn.addActionListener(e -> {
                    currState = !currState;
                    int id = Integer.parseInt(t.getValueAt(r, 0).toString());
                    if(nccBLL.doiTrangThai(id, currState ? "Hợp tác" : "Ngừng")) {
                         loadComboBoxNhaCungCap(); 
                    }
                    fireEditingStopped();
                });
                return btn;
            }
            @Override public Object getCellEditorValue() { return currState; }
        });

        // 2. Renderer Nút Chi Tiết (Cột 6 - MỚI)
        nccTable.getColumn("Chi tiết").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Xem SP");
            btn.setBackground(new Color(0, 123, 255)); // Màu xanh dương
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            return btn;
        });
        
        nccTable.getColumn("Chi tiết").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                JButton btn = new JButton("Xem SP");
                btn.setBackground(new Color(0, 123, 255));
                btn.addActionListener(e -> {
                    int id = Integer.parseInt(t.getValueAt(r, 0).toString());
                    String tenNCC = t.getValueAt(r, 1).toString();
                    hienThiChiTietNCC(id, tenNCC); // Gọi hàm hiển thị Popup
                    fireEditingStopped();
                });
                return btn;
            }
        });

        // 3. Renderer Nút Sửa (Cột 7 - Hành động)
        nccTable.getColumn("Hành động").setCellRenderer((t, v, s, h, r, c) -> {
            JButton btn = new JButton("Sửa");
            btn.setBackground(new Color(40, 167, 69)); // Màu xanh lá
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return btn;
        });

        nccTable.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                JButton btn = new JButton("Sửa");
                btn.setBackground(new Color(40, 167, 69));
                btn.addActionListener(e -> {
                    NhaCungCapDTO ncc = new NhaCungCapDTO();
                    ncc.setMaNCC(Integer.parseInt(t.getValueAt(r, 0).toString()));
                    ncc.setTenNCC(t.getValueAt(r, 1).toString());
                    ncc.setSdt(t.getValueAt(r, 2).toString());
                    ncc.setEmail(t.getValueAt(r, 3).toString());
                    ncc.setDiaChi(t.getValueAt(r, 4).toString());
                    // Lưu ý: Cột 5 giờ là Boolean, cần convert lại String cho DTO nếu cần
                    boolean st = (Boolean) t.getValueAt(r, 5);
                    ncc.setTrangThai(st ? "Hợp tác" : "Ngừng");
                    
                    hienThiFormNCC(ncc);
                    fireEditingStopped();
                });
                return btn;
            }
        });

        panel.add(new JScrollPane(nccTable), BorderLayout.CENTER);
        loadNhaCungCapData();
        return panel;
    }

    private void loadNhaCungCapData() {
        nccModel.setRowCount(0);
        List<NhaCungCapDTO> list = nccBLL.layDanhSachTatCa(); 
        for (NhaCungCapDTO ncc : list) {
            boolean isActive = "Hợp tác".equals(ncc.getTrangThai());
            nccModel.addRow(new Object[]{
                ncc.getMaNCC(),
                ncc.getTenNCC(),
                ncc.getSdt(),
                ncc.getEmail(),
                ncc.getDiaChi(),
                isActive, // Cột 5: Boolean cho nút gạt Trạng thái
                "Xem SP", // Cột 6: Nút Chi tiết
                "Sửa"     // Cột 7: Nút Sửa
            });
        }
    }

    private void hienThiFormNCC(NhaCungCapDTO nccEditing) {
        boolean isEdit = (nccEditing != null);
        JDialog dialog = new JDialog(this, isEdit ? "Sửa Nhà Cung Cấp" : "Thêm Nhà Cung Cấp", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 15, 0);

        JTextField txtTen = styleTextField();
        JTextField txtSDT = styleTextField();
        JTextField txtEmail = styleTextField();
        JTextArea txtDiaChi = new JTextArea(3, 20);
        txtDiaChi.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        txtDiaChi.setLineWrap(true);

        // Fill dữ liệu nếu là Sửa
        if (isEdit) {
            txtTen.setText(nccEditing.getTenNCC());
            txtSDT.setText(nccEditing.getSdt());
            txtEmail.setText(nccEditing.getEmail());
            txtDiaChi.setText(nccEditing.getDiaChi());
        }

        int y = 0;
        addLabelAndComponent(form, gbc, y++, "Tên nhà cung cấp:", txtTen);
        addLabelAndComponent(form, gbc, y++, "Số điện thoại:", txtSDT);
        addLabelAndComponent(form, gbc, y++, "Email:", txtEmail);
        addLabelAndComponent(form, gbc, y++, "Địa chỉ:", new JScrollPane(txtDiaChi));

        dialog.add(form, BorderLayout.CENTER);
        
        // --- NÚT XÓA NHÀ CUNG CẤP ---
        JButton btnXoa = new JButton("Xóa NCC");
        btnXoa.setBackground(new Color(220, 53, 69));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setPreferredSize(new Dimension(100, 35));
        btnXoa.setVisible(isEdit);

        btnXoa.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(dialog, "Bạn muốn xóa Nhà cung cấp này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            // Thử xóa vĩnh viễn
            // Giả sử nccBLL.xoaNhaCungCap(id) đã có. Nếu chưa, nó trả về false.
            if (nccBLL.xoaNhaCungCap(nccEditing.getMaNCC())) {
                JOptionPane.showMessageDialog(dialog, "Đã xóa NCC khỏi hệ thống!");
                triggerRealTimeUpdate();
                dialog.dispose();
            } else {
                // Nếu không xóa được -> Gợi ý Ngừng hợp tác
                int choice = JOptionPane.showConfirmDialog(dialog, 
                    "Không thể xóa vì NCC này đang có liên kết với sản phẩm hoặc lịch sử nhập!\n" +
                    "Bạn có muốn chuyển trạng thái sang 'Ngừng' hợp tác không?", 
                    "Cảnh báo ràng buộc", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    try (Connection conn = db.getConnect(); 
                         PreparedStatement ps = conn.prepareStatement("UPDATE nhacungcap SET trang_thai = 'Ngừng' WHERE ma_ncc = ?")) {
                        ps.setInt(1, nccEditing.getMaNCC());
                        ps.executeUpdate();
                        
                        JOptionPane.showMessageDialog(dialog, "Đã chuyển trạng thái sang 'Ngừng'!");
                        triggerRealTimeUpdate();
                        dialog.dispose();
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        });

        // --- NÚT LƯU NHÀ CUNG CẤP ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 245, 245));
        JButton btnSave = new JButton("Lưu thông tin");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(140, 35));
        
        btnSave.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Tên không được để trống!"); return; }
            
            NhaCungCapDTO newNCC = new NhaCungCapDTO();
            newNCC.setTenNCC(ten);
            newNCC.setSdt(txtSDT.getText());
            newNCC.setEmail(txtEmail.getText());
            newNCC.setDiaChi(txtDiaChi.getText());
            newNCC.setTrangThai("Hợp tác"); // Mặc định

            boolean kq;
            if (isEdit) {
                newNCC.setMaNCC(nccEditing.getMaNCC());
                newNCC.setTrangThai(nccEditing.getTrangThai()); // Giữ nguyên trạng thái cũ
                kq = nccBLL.suaNhaCungCap(newNCC);
            } else {
                kq = nccBLL.themNhaCungCap(newNCC);
            }

            if (kq) {
                JOptionPane.showMessageDialog(dialog, "Thành công!");
                loadNhaCungCapData();      // Refresh bảng quản lý
                loadComboBoxNhaCungCap();  // Refresh combobox nhập kho
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Thất bại!");
            }
        });

        btnPanel.add(btnXoa); // <--- Add nút Xóa vào trước nút Lưu
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void hienThiChiTietNCC(int maNCC, String tenNCC) {
        JDialog dialog = new JDialog(this, "Danh sách sản phẩm của: " + tenNCC, true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // Header
        JLabel lblTitle = new JLabel("SẢN PHẨM CUNG CẤP", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 123, 255));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        dialog.add(lblTitle, BorderLayout.NORTH);

        // Bảng sản phẩm
        String[] cols = {"ID", "Tên sản phẩm", "Loại", "Giá bán", "Tồn kho"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(40);
        styleTableHeader(table); // Dùng lại hàm style có sẵn
        centerAllTableCells(table);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        // Query lấy sản phẩm theo ma_ncc
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement("SELECT id, ten, loai, gia, so_luong FROM figure WHERE ma_ncc = ?")) {
            ps.setInt(1, maNCC);
            ResultSet rs = ps.executeQuery();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ten"),
                    rs.getString("loai"),
                    nf.format(rs.getLong("gia")),
                    rs.getInt("so_luong")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Nếu không có sản phẩm
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"-", "Chưa cung cấp sản phẩm nào", "-", "-", "-"});
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 20, 20));
        scroll.getViewport().setBackground(Color.WHITE);
        dialog.add(scroll, BorderLayout.CENTER);

        // Nút đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.setBackground(new Color(220, 53, 69));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        btnPanel.add(btnClose);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    // ================== BÁO CÁO THỐNG KÊ (NÂNG CẤP) ==================
    private JPanel taoBaoCaoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 242, 245));

        // --- CỘT TRÁI: THỐNG KÊ DOANH THU (DẠNG BẢNG) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(null, "Thống kê Doanh thu", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                new Font("Segoe UI", Font.BOLD, 16), new Color(0, 123, 255)));
        
        // Bộ lọc ngày
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        // Mặc định lấy tháng hiện tại
        java.time.LocalDate now = java.time.LocalDate.now();
        String firstDay = now.withDayOfMonth(1).toString();
        String lastDay = now.withDayOfMonth(now.lengthOfMonth()).toString();

        JTextField tFrom = new JTextField(8); tFrom.setText(firstDay); tFrom.setToolTipText("yyyy-mm-dd");
        JTextField tTo = new JTextField(8); tTo.setText(lastDay);   tTo.setToolTipText("yyyy-mm-dd");
        
        JButton bLoc = new JButton("Xem");
        bLoc.setBackground(new Color(0, 123, 255)); bLoc.setForeground(Color.WHITE);
        
        filterPanel.add(new JLabel("Từ:")); filterPanel.add(tFrom);
        filterPanel.add(new JLabel("Đến:")); filterPanel.add(tTo);
        filterPanel.add(bLoc);
        leftPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Bảng Doanh thu (Thay vì Text)
        DefaultTableModel dtModel = new DefaultTableModel(new String[]{"STT", "Ngày", "Doanh thu"}, 0);
        JTable dtTable = new JTable(dtModel);
        styleTableHeader(dtTable);
        centerAllTableCells(dtTable);
        dtTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        
        // Footer tổng tiền
        JLabel lblTongDoanhThu = new JLabel("TỔNG CỘNG: 0 đ", JLabel.RIGHT);
        lblTongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongDoanhThu.setForeground(Color.RED);
        lblTongDoanhThu.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        leftPanel.add(new JScrollPane(dtTable), BorderLayout.CENTER);
        leftPanel.add(lblTongDoanhThu, BorderLayout.SOUTH);
        
        // Logic Xem Báo Cáo
        Runnable loadDoanhThu = () -> {
            dtModel.setRowCount(0);
            java.util.Map<String, Long> data = tkDAL.getDoanhThuTheoNgay(tFrom.getText(), tTo.getText());
            long total = 0;
            int stt = 1;
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            for (String date : data.keySet()) {
                long val = data.get(date);
                total += val;
                dtModel.addRow(new Object[]{stt++, date, nf.format(val)});
            }
            lblTongDoanhThu.setText("TỔNG CỘNG: " + nf.format(total));
        };
        
        bLoc.addActionListener(e -> loadDoanhThu.run());

        // --- CỘT PHẢI: TOP BÁN CHẠY & CẢNH BÁO ---
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        rightPanel.setOpaque(false);

        // 1. Top 10 Bán chạy (Thêm STT)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(null, "Top 10 Bán Chạy", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                new Font("Segoe UI", Font.BOLD, 14)));
        
        DefaultTableModel topModel = new DefaultTableModel(new String[]{"STT", "Sản phẩm", "SL Bán"}, 0);
        JTable topTable = new JTable(topModel);
        styleTableHeader(topTable);
        centerAllTableCells(topTable);
        topTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        
        // Load data Top 10
        int sttTop = 1;
        for (Object[] row : tkDAL.getTopBanChay()) {
            // row: [Ten, SL] -> Thêm STT vào đầu
            topModel.addRow(new Object[]{sttTop++, row[0], row[1]});
        }
        topPanel.add(new JScrollPane(topTable));

        // 2. Cảnh báo Tồn kho
        JPanel lowPanel = new JPanel(new BorderLayout());
        lowPanel.setBackground(Color.WHITE);
        lowPanel.setBorder(BorderFactory.createTitledBorder(null, "Cảnh báo Sắp hết hàng (<10)", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                new Font("Segoe UI", Font.BOLD, 14), Color.RED));
        
        DefaultTableModel lowModel = new DefaultTableModel(new String[]{"ID", "Sản phẩm", "Tồn"}, 0);
        JTable lowTable = new JTable(lowModel);
        styleTableHeader(lowTable);
        centerAllTableCells(lowTable);
        
        lowTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setForeground(Color.RED);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setHorizontalAlignment(JLabel.CENTER);
                if (column == 2) l.setText(value + " ⚠️");
                return l;
            }
        });

        for (Object[] row : tkDAL.getCanhBaoTonKho()) {
            lowModel.addRow(row);
        }
        lowPanel.add(new JScrollPane(lowTable));

        rightPanel.add(topPanel);
        rightPanel.add(lowPanel);

        panel.add(leftPanel);
        panel.add(rightPanel);
        
        // [QUAN TRỌNG] Tự động load dữ liệu khi mở
        loadDoanhThu.run(); 
        
        return panel;
    }
    
    // ================== GIAO DIỆN KHUYẾN MÃI (ĐÃ SỬA LỖI HIỂN THỊ) ==================
    private JPanel taoKhuyenMaiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // 1. Header & Form
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        
        JLabel title = new JLabel("Quản lý Khuyến mãi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 10, 0)); // Cách xuống dưới 1 chút
        topPanel.add(title, BorderLayout.NORTH);

        // --- PANEL FORM NHẬP LIỆU ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        // Tạo border có tiêu đề
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin mã giảm giá",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
            
        GridBagConstraints gbc = new GridBagConstraints();
        // Insets(top, left, bottom, right): Khoảng cách giữa các component
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Khởi tạo TextField bằng style chuẩn của app
        txtKMMa = styleTextField();
        txtKMPT = styleTextField();
        txtKMHan = styleTextField(); txtKMHan.setToolTipText("yyyy-mm-dd");
        txtKMMoTa = styleTextField();

        // --- Hàng 1 ---
        gbc.gridy = 0;
        
        // Cột 1: Label Mã
        gbc.gridx = 0; gbc.weightx = 0; // Label không giãn
        formPanel.add(new JLabel("Mã Code:"), gbc);
        
        // Cột 2: Input Mã
        gbc.gridx = 1; gbc.weightx = 1.0; // Input giãn hết mức có thể
        formPanel.add(txtKMMa, gbc);
        
        // Cột 3: Label %
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("% Giảm:"), gbc);
        
        // Cột 4: Input %
        gbc.gridx = 3; gbc.weightx = 1.0;
        formPanel.add(txtKMPT, gbc);
        
        // --- Hàng 2 ---
        gbc.gridy = 1;
        
        // Cột 1: Label Hạn
        gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Hạn dùng (yyyy-mm-dd):"), gbc);
        
        // Cột 2: Input Hạn
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtKMHan, gbc);
        
        // Cột 3: Label Mô tả
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        // Cột 4: Input Mô tả
        gbc.gridx = 3; gbc.weightx = 1.0;
        formPanel.add(txtKMMoTa, gbc);

        // --- PANEL NÚT BẤM ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnThem = new JButton("Thêm"); btnThem.setBackground(new Color(40, 167, 69)); btnThem.setForeground(Color.WHITE);
        JButton btnSua = new JButton("Sửa");    btnSua.setBackground(new Color(255, 193, 7));
        JButton btnXoa = new JButton("Xóa");    btnXoa.setBackground(new Color(220, 53, 69)); btnXoa.setForeground(Color.WHITE);
        JButton btnLamMoi = new JButton("Làm mới"); btnLamMoi.setBackground(new Color(23, 162, 184)); btnLamMoi.setForeground(Color.WHITE);

        // Thêm style cho nút bấm to hơn chút
        Dimension btnSize = new Dimension(100, 35);
        btnThem.setPreferredSize(btnSize); btnSua.setPreferredSize(btnSize);
        btnXoa.setPreferredSize(btnSize); btnLamMoi.setPreferredSize(btnSize);

        btnThem.addActionListener(e -> xuLyKhuyenMai("them"));
        btnSua.addActionListener(e -> xuLyKhuyenMai("sua"));
        btnXoa.addActionListener(e -> xuLyKhuyenMai("xoa"));
        
        btnLamMoi.addActionListener(e -> {
            loadKhuyenMaiList(); 
            txtKMMa.setText(""); txtKMPT.setText(""); txtKMHan.setText(""); txtKMMoTa.setText("");
        });

        btnPanel.add(btnThem); btnPanel.add(btnSua); btnPanel.add(btnXoa); btnPanel.add(btnLamMoi);

        // Add Form và Button vào TopPanel
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // 2. Bảng dữ liệu
        String[] cols = {"STT", "Mã Code", "% Giảm", "Hạn sử dụng", "Mô tả", "Trạng thái"};
        kmModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        kmTable = new JTable(kmModel);
        kmTable.setRowHeight(40);
        styleTableHeader(kmTable);
        centerAllTableCells(kmTable);
        kmTable.getColumnModel().getColumn(0).setPreferredWidth(40); 

        // Tô màu trạng thái & Căn giữa
        kmTable.getColumn("Trạng thái").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                // Gọi super để khởi tạo các thuộc tính cơ bản
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                
                // --- [THÊM DÒNG NÀY ĐỂ CĂN GIỮA] ---
                l.setHorizontalAlignment(JLabel.CENTER); 
                
                // Logic tô màu như cũ
                String st = (String) v;
                if ("Hết hạn".equals(st)) {
                    l.setForeground(Color.RED);
                } else {
                    l.setForeground(new Color(40, 167, 69)); // Xanh lá
                }
                
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return l;
            }
        });

        // Sự kiện click bảng để đổ dữ liệu lên form
        kmTable.getSelectionModel().addListSelectionListener(e -> {
            int r = kmTable.getSelectedRow();
            if (r >= 0) {
                txtKMMa.setText(kmTable.getValueAt(r, 1).toString());
                String pt = kmTable.getValueAt(r, 2).toString().replace("%", "");
                txtKMPT.setText(pt);
                txtKMHan.setText(kmTable.getValueAt(r, 3).toString());
                Object moTa = kmTable.getValueAt(r, 4);
                txtKMMoTa.setText(moTa != null ? moTa.toString() : "");
            }
        });

        panel.add(new JScrollPane(kmTable), BorderLayout.CENTER);
        
        loadKhuyenMaiList(); // Load lần đầu
        
        return panel;
    }


    // 3. HÀM XỬ LÝ LOGIC THÊM/SỬA/XÓA
    private void xuLyKhuyenMai(String action) {
        String ma = txtKMMa.getText().trim();
        String han = txtKMHan.getText().trim();
        String mota = txtKMMoTa.getText().trim();
        int pt = 0;
        
        if (ma.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Khuyến Mãi!"); return; }
        
        if (!"xoa".equals(action)) {
            try { 
                pt = Integer.parseInt(txtKMPT.getText().trim()); 
                if (pt <= 0 || pt > 100) throw new Exception();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "% Giảm phải là số từ 1-100!"); return; }
            
            if (!han.matches("\\d{4}-\\d{2}-\\d{2}")) { JOptionPane.showMessageDialog(this, "Hạn dùng sai định dạng: yyyy-MM-dd"); return; }
        }

        boolean kq = false;
        if ("them".equals(action)) kq = kmDAL.them(ma, pt, han, mota);
        else if ("sua".equals(action)) kq = kmDAL.sua(ma, pt, han, mota);
        else if ("xoa".equals(action)) {
            if (JOptionPane.showConfirmDialog(this, "Xóa mã này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                kq = kmDAL.xoa(ma);
            } else return;
        }

        if (kq) {
            JOptionPane.showMessageDialog(this, "Thao tác thành công!");
            loadKhuyenMaiList(); // Gọi hàm load mới
            txtKMMa.setText(""); txtKMPT.setText(""); txtKMHan.setText(""); txtKMMoTa.setText("");
            triggerRealTimeUpdate(); 
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thất bại!");
        }
    }
    
    // ================== HÀM PHỤ TRỢ CHO TAB KHUYẾN MÃI ==================
    private void loadKhuyenMaiList() {
        // 1. Kiểm tra an toàn
        if (kmModel == null) return;
        
        // 2. Xóa dữ liệu cũ để tránh bị trùng lặp khi reload
        kmModel.setRowCount(0);

        String sql = "SELECT ma, phan_tram_giam, han_dung, mo_ta FROM khuyenmai ORDER BY han_dung ASC";

        try (Connection conn = db.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int stt = 1; // Biến đếm số thứ tự bắt đầu từ 1
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while (rs.next()) {
                // Xử lý logic ngày tháng
                java.sql.Date hanDung = rs.getDate("han_dung");
                String trangThai = (hanDung != null && hanDung.before(today)) ? "Hết hạn" : "Còn hạn";
                String hanDungStr = (hanDung != null) ? hanDung.toString() : "";

                // --- [QUAN TRỌNG] ADD DÒNG MỚI ĐÚNG THỨ TỰ CỘT ---
                // Cấu trúc bảng: 0.STT | 1.Mã | 2.% Giảm | 3.Hạn SD | 4.Mô tả | 5.Trạng thái
                kmModel.addRow(new Object[]{
                    stt++,                              // Cột 0: STT (Tự tăng)
                    rs.getString("ma"),                 // Cột 1: Mã Code
                    rs.getInt("phan_tram_giam") + "%",  // Cột 2: % Giảm (Thêm dấu %)
                    hanDungStr,                         // Cột 3: Hạn sử dụng
                    rs.getString("mo_ta"),              // Cột 4: Mô tả
                    trangThai                           // Cột 5: Trạng thái (Logic màu sắc sẽ do Renderer xử lý)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khuyến mãi: " + e.getMessage());
        }
    }
    
    // Hàm bắn tín hiệu sang MainUI
    private void triggerRealTimeUpdate() {
        // 1. Cập nhật chính giao diện Admin (để Admin thấy số nhảy)
        loadSanPhamData();
        loadLichSuNhapKho();
        loadNhaCungCapData();
        // loadDonHangData(); // Nếu cần
        
        // 2. Bắn tín hiệu ép buộc MainUI cập nhật ngay lập tức
        try {
            // Gọi hàm static bên MainUI
            FORM.MainUI.forceUpdateData(); 
        } catch (Exception e) {
            System.out.println("MainUI chưa mở hoặc không tìm thấy.");
        }
    }
}