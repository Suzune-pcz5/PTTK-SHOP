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
import java.sql.PreparedStatement; // Thêm import
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class MainUI extends JFrame {
    // 1. Biến tĩnh để lưu chính bản thân MainUI đang chạy
    public static MainUI instance;

    public MainUI(NguoiDungDTO nd) {
        instance = this; // Gán instance
        this.nguoiDungHienTai = nd;
        initComponents();
    }

    // 2. Hàm "Cực đoan": Cho phép bên ngoài ép buộc MainUI cập nhật
    public static void forceUpdateData() {
        if (instance != null) {
            System.out.println("Admin bắt buộc cập nhật dữ liệu...");
            
            // Nếu đang ở Tab Bán hàng (Index 0)
            if (instance.mainTabs.getSelectedIndex() == 0) {
                int selectedRow = instance.tblDanhSach.getSelectedRow();
                int selectedId = -1;
                if (selectedRow >= 0) {
                    try { selectedId = Integer.parseInt(instance.tblDanhSach.getValueAt(selectedRow, 0).toString()); } catch(Exception e){}
                }

                instance.taiDanhSach(); 
                instance.capNhatGioHang(); 
                instance.loadKhuyenMaiData();

                if (selectedId != -1) {
                    for (int i = 0; i < instance.tblDanhSach.getRowCount(); i++) {
                        int id = Integer.parseInt(instance.tblDanhSach.getValueAt(i, 0).toString());
                        if (id == selectedId) {
                            instance.tblDanhSach.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }
            } 
            // Nếu đang ở Tab Lịch sử (Index 1)
            else if (instance.mainTabs.getSelectedIndex() == 1) {
                instance.loadEmployeeOrderHistory();
            }
        }
    }

    // --- KHAI BÁO BIẾN GIAO DIỆN ---
    private JTabbedPane mainTabs; 
    private JTable tblDanhSach, tblGioHang;
    private JTextField txtMinGia, txtMaxGia, txtTenTimKiem;
    private JComboBox<String> cbLoai, cbKichThuoc;
    private JSplitPane splitPane;
    private JComboBox<NhaCungCapDTO> cbLocNCC; 
    private NhaCungCapBLL nccBLL = new NhaCungCapBLL();
    
    // --- BIẾN TAB LỊCH SỬ ĐƠN HÀNG ---
    private DefaultTableModel modelLichSuDon;
    private JTable tblLichSuDon;
    private JTextField txtDateFrom, txtDateTo;
    private JComboBox<String> cbStatusLoc, cbPtttLoc;
    
    // Header components
    private JLabel lblTenNguoiDung;
    private JPopupMenu userMenuPopup;

    // --- BIẾN UI THANH TOÁN ---
    private JComboBox<String> cbPhuongThucTT;
    private JComboBox<String> cbKhuyenMai; 
    private JTextField txtTienKhachDua;    
    private JLabel lblTienThua;            
    private JLabel lblTongTien;            
    
    // --- BIẾN LOGIC ---
    private FigureBLL bll = new FigureBLL();
    private NguoiDungDTO nguoiDungHienTai = null;
    private List<FigureDTO> danhSachHienTai; 
    
    private double phanTramGiam = 0;
    private long tongTienHienTai = 0;       
    private BLL.DonHangBLL donHangBLL = new BLL.DonHangBLL(); 

    // =========================================================================
    // 1. INIT COMPONENT
    // =========================================================================
    private void initComponents() {
        setTitle("MAHIRU. - Quản Lý Bán Hàng");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));

        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header chung
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // --- [TABBED PANE] ---
        mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Tab 1: Bán hàng (POS)
        splitPane = createSplitContent(); 
        mainTabs.addTab("Bán hàng (POS)", splitPane);

        // Tab 2: Lịch sử đơn hàng
        mainTabs.addTab("Tra cứu đơn hàng", createOrderHistoryPanel());
        
        // Sự kiện chuyển tab
        mainTabs.addChangeListener(e -> {
            if (mainTabs.getSelectedIndex() == 1) {
                loadEmployeeOrderHistory();
            } else {
                taiDanhSach(); 
            }
        });

        mainPanel.add(mainTabs, BorderLayout.CENTER);
        add(mainPanel);
        
        // Load dữ liệu ban đầu
        taiDanhSach();
        capNhatGioHang();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.65));
    }

    // =========================================================================
    // 2. HEADER PANEL
    // =========================================================================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logo = new JLabel("MAHIRU.");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setOpaque(false);
        int h = 35; 

        searchPanel.add(createLabelWhite("Tên:"));
        txtTenTimKiem = new JTextField(12); txtTenTimKiem.setPreferredSize(new Dimension(120, h));
        searchPanel.add(txtTenTimKiem);

        searchPanel.add(createLabelWhite("NCC:"));
        cbLocNCC = new JComboBox<>();
        cbLocNCC.setPreferredSize(new Dimension(100, h));
        cbLocNCC.addItem(new NhaCungCapDTO(0, "Tất cả", "", "", "", ""));
        if (nccBLL != null) {
            List<NhaCungCapDTO> list = nccBLL.getListNhaCungCap();
            if (list != null) for(NhaCungCapDTO ncc : list) cbLocNCC.addItem(ncc);
        }
        searchPanel.add(cbLocNCC);

        searchPanel.add(createLabelWhite("Loại:"));
        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Gundam", "Anime", "Game", "Khác"});
        cbLoai.setPreferredSize(new Dimension(90, h));
        searchPanel.add(cbLoai);
        
        searchPanel.add(createLabelWhite("KT:"));
        cbKichThuoc = new JComboBox<>(new String[]{"Tất cả", "1/6", "1/8", "1/10", "1/12", "Khác"});
        cbKichThuoc.setPreferredSize(new Dimension(70, h));
        searchPanel.add(cbKichThuoc);

        JButton btnTimKiem = createRedButton("Tìm");
        btnTimKiem.setPreferredSize(new Dimension(70, h));
        btnTimKiem.addActionListener(e -> timKiemNangCao());
        searchPanel.add(btnTimKiem);

        // Panel User
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        
        if (lblTenNguoiDung == null) {
             lblTenNguoiDung = new JLabel();
             lblTenNguoiDung.setFont(new Font("Segoe UI", Font.BOLD, 16));
             lblTenNguoiDung.setForeground(Color.WHITE);
        }
        lblTenNguoiDung.setText(nguoiDungHienTai != null
          ? "<html>" + nguoiDungHienTai.getTenDangNhap() + " ▼</html>" 
          : "Xin chào, Khách");

        if (nguoiDungHienTai != null) {
            userMenuPopup = new JPopupMenu();
            userMenuPopup.setBackground(new Color(50, 50, 50));
            userMenuPopup.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

            JMenuItem itemChangePass = new JMenuItem("Đổi mật khẩu");
            styleDarkMenuItem(itemChangePass);
            itemChangePass.addActionListener(e -> new ChangePasswordUI(MainUI.this, nguoiDungHienTai).setVisible(true));
            userMenuPopup.add(itemChangePass);
            
            JSeparator sep = new JSeparator(); sep.setForeground(Color.GRAY);
            userMenuPopup.add(sep);

            JMenuItem itemLogout = new JMenuItem("Đăng xuất");
            styleDarkMenuItem(itemLogout);
            itemLogout.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose(); new LoginUI().setVisible(true);
                }
            });
            userMenuPopup.add(itemLogout);

            lblTenNguoiDung.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblTenNguoiDung.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    userMenuPopup.show(evt.getComponent(), 
                            evt.getComponent().getWidth() - userMenuPopup.getPreferredSize().width, 
                            evt.getComponent().getHeight());
                }
            });
        }
        right.add(lblTenNguoiDung);

        panel.add(logo, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private void styleDarkMenuItem(JMenuItem item) {
        item.setFont(new Font("Segoe UI", Font.BOLD, 14));
        item.setForeground(Color.WHITE);
        item.setBackground(new Color(50, 50, 50));
        item.setOpaque(true);
        item.setBorder(new EmptyBorder(10, 15, 10, 15));
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(new Color(80, 80, 80)); }
            public void mouseExited(MouseEvent e) { item.setBackground(new Color(50, 50, 50)); }
        });
    }

    private JLabel createLabelWhite(String t) { JLabel l = new JLabel(t); l.setForeground(Color.WHITE); l.setFont(new Font("Segoe UI", Font.BOLD, 14)); return l; }
    private JButton createRedButton(String t) { JButton b = new JButton(t); b.setBackground(new Color(220, 53, 69)); b.setForeground(Color.WHITE); return b; }
    private JButton createGreenButton(String t) { JButton b = new JButton(t); b.setBackground(new Color(40, 167, 69)); b.setForeground(Color.WHITE); return b; }


    // =========================================================================
    // 3. TAB 1: POS (BÁN HÀNG)
    // =========================================================================
    private JSplitPane createSplitContent() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        return splitPane;
    }

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
        tblDanhSach.setRowHeight(70);
        
        JScrollPane scroll = new JScrollPane(tblDanhSach);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel(" GIỎ HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 123, 255));
        title.setBorder(new EmptyBorder(15, 10, 10, 10));
        panel.add(title, BorderLayout.NORTH);

        tblGioHang = new JTable();
        tblGioHang.setRowHeight(60);
        JScrollPane scroll = new JScrollPane(tblGioHang);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel payPanel = new JPanel(new GridBagLayout());
        payPanel.setBackground(new Color(248, 249, 250));
        payPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 15);
        Font fontBig = new Font("Segoe UI", Font.BOLD, 22);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel l1 = new JLabel("Khuyến mãi:"); l1.setFont(fontLabel);
        payPanel.add(l1, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        cbKhuyenMai = new JComboBox<>();
        cbKhuyenMai.addItem("Không áp dụng");
        loadKhuyenMaiData(); 
        cbKhuyenMai.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object selected = cbKhuyenMai.getSelectedItem();
                cbKhuyenMai.removeAllItems();
                cbKhuyenMai.addItem("Không áp dụng");
                loadKhuyenMaiData();
                if (selected != null) cbKhuyenMai.setSelectedItem(selected);
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
        cbKhuyenMai.addActionListener(e -> capNhatTongTien()); 
        payPanel.add(cbKhuyenMai, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Thanh toán:"); l2.setFont(fontLabel);
        payPanel.add(l2, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        cbPhuongThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử"});
        payPanel.add(cbPhuongThucTT, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("TỔNG CỘNG:"); l3.setFont(fontBold);
        payPanel.add(l3, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        lblTongTien = new JLabel("0 đ");
        lblTongTien.setFont(fontBig);
        lblTongTien.setForeground(new Color(220, 53, 69));
        lblTongTien.setHorizontalAlignment(JLabel.RIGHT);
        payPanel.add(lblTongTien, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        payPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel l4 = new JLabel("Khách đưa:"); l4.setFont(fontLabel);
        payPanel.add(l4, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtTienKhachDua = new JTextField();
        txtTienKhachDua.setFont(fontBold);
        txtTienKhachDua.setHorizontalAlignment(JTextField.RIGHT);
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhTienThua(); }
            public void removeUpdate(DocumentEvent e) { tinhTienThua(); }
            public void changedUpdate(DocumentEvent e) { tinhTienThua(); }
        });
        payPanel.add(txtTienKhachDua, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel l5 = new JLabel("Tiền thừa:"); l5.setFont(fontLabel);
        payPanel.add(l5, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        lblTienThua = new JLabel("0 đ");
        lblTienThua.setFont(fontBold);
        lblTienThua.setForeground(new Color(40, 167, 69));
        lblTienThua.setHorizontalAlignment(JLabel.RIGHT);
        payPanel.add(lblTienThua, gbc);

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
    // 4. TAB 2: LỊCH SỬ ĐƠN HÀNG (MỚI)
    // =========================================================================
    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // 1. Bộ lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm đơn hàng"));

        txtDateFrom = new JTextField(8); txtDateFrom.setToolTipText("yyyy-mm-dd");
        txtDateTo = new JTextField(8);   txtDateTo.setToolTipText("yyyy-mm-dd");
        
        cbStatusLoc = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Đã hủy"});
        cbPtttLoc = new JComboBox<>(new String[]{"Tất cả", "TienMat", "ChuyenKhoan", "The", "ViDienTu"});
        
        JButton btnLoc = new JButton("Tìm kiếm");
        btnLoc.setBackground(new Color(0, 123, 255)); 
        btnLoc.setForeground(Color.WHITE);
        btnLoc.addActionListener(e -> loadEmployeeOrderHistory());

        filterPanel.add(new JLabel("Từ:")); filterPanel.add(txtDateFrom);
        filterPanel.add(new JLabel("Đến:")); filterPanel.add(txtDateTo);
        filterPanel.add(new JLabel("Trạng thái:")); filterPanel.add(cbStatusLoc);
        filterPanel.add(new JLabel("PTTT:")); filterPanel.add(cbPtttLoc);
        filterPanel.add(btnLoc);

        panel.add(filterPanel, BorderLayout.NORTH);

        // 2. Bảng
        String[] cols = {"Mã đơn", "Ngày tạo", "Tổng tiền", "Trạng thái", "PTTT", "Chi tiết"};
        modelLichSuDon = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        tblLichSuDon = new JTable(modelLichSuDon);
        tblLichSuDon.setRowHeight(45);
        styleTable(tblLichSuDon);
        
        // Renderer
        tblLichSuDon.getColumn("Trạng thái").setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel)super.getTableCellRendererComponent(t, v, s, f, r, c);
                String st = (String)v;
                if("Đã thanh toán".equals(st)) l.setForeground(new Color(40, 167, 69));
                else if("Đã hủy".equals(st)) l.setForeground(new Color(220, 53, 69));
                else l.setForeground(Color.BLACK);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
            }
        });

        // Nút Xem
        tblLichSuDon.getColumn("Chi tiết").setCellRenderer((t, v, s, h, r, c) -> {
            JButton b = new JButton("Xem"); b.setBackground(new Color(23, 162, 184)); b.setForeground(Color.WHITE); return b;
        });
        
        tblLichSuDon.getColumn("Chi tiết").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
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
            @Override public Object getCellEditorValue() { return "Xem"; }
        });

        panel.add(new JScrollPane(tblLichSuDon), BorderLayout.CENTER);
        return panel;
    }
    
    private void loadEmployeeOrderHistory() {
        if (modelLichSuDon == null) return;
        modelLichSuDon.setRowCount(0);
        
        String s = cbStatusLoc.getSelectedItem().toString();
        String p = cbPtttLoc.getSelectedItem().toString();
        String dFrom = txtDateFrom.getText().trim();
        String dTo = txtDateTo.getText().trim();

        StringBuilder sql = new StringBuilder(
            "SELECT d.ma_don_hang, DATE_FORMAT(d.ngay_dat, '%Y-%m-%d %H:%i'), d.tong_tien, d.trang_thai, d.phuong_thuc_tt " +
            "FROM donhang d WHERE 1=1");
        
        if(!"Tất cả".equals(s)) sql.append(" AND d.trang_thai = '").append(s).append("'");
        if(!"Tất cả".equals(p)) sql.append(" AND d.phuong_thuc_tt = '").append(p).append("'");
        if(!dFrom.isEmpty()) sql.append(" AND DATE(d.ngay_dat) >= '").append(dFrom).append("'");
        if(!dTo.isEmpty()) sql.append(" AND DATE(d.ngay_dat) <= '").append(dTo).append("'");
        
        // Chỉ hiện đơn của nhân viên đang đăng nhập (Option B)
        if (nguoiDungHienTai != null) {
            sql.append(" AND d.ma_nhan_vien = ").append(nguoiDungHienTai.getMaNguoiDung());
        }

        sql.append(" ORDER BY d.ma_don_hang DESC");
        
        try (Connection conn = new DBConnection().getConnect(); 
             ResultSet rs = conn.createStatement().executeQuery(sql.toString())) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            while(rs.next()) {
                modelLichSuDon.addRow(new Object[]{ 
                    "#" + rs.getInt(1), rs.getString(2), nf.format(rs.getLong(3)), rs.getString(4), rs.getString(5), "Xem"
                });
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    // Popup chi tiết cho MainUI (Chỉ Xem, không Sửa)
    private void hienThiChiTietDonHangPopup(int maDonHang) {
        JDialog dialog = new JDialog(this, "Chi tiết đơn hàng #" + maDonHang, true);
        dialog.setLayout(new BorderLayout());
        JPanel content = new JPanel(); content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(245, 245, 245)); content.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        try (Connection conn = new DBConnection().getConnect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT ngay_dat, trang_thai, tong_tien, phuong_thuc_tt FROM donhang WHERE ma_don_hang=?");
            ps.setInt(1, maDonHang); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JPanel h = new JPanel(new BorderLayout()); h.setBackground(Color.WHITE); h.setBorder(new EmptyBorder(10,20,10,20));
                JLabel id = new JLabel("HÓA ĐƠN #"+maDonHang); id.setFont(new Font("Segoe UI", Font.BOLD, 18));
                JLabel info = new JLabel(rs.getString("ngay_dat") + " | " + rs.getString("trang_thai")); info.setForeground(Color.GRAY);
                JPanel left = new JPanel(new GridLayout(2,1)); left.setBackground(Color.WHITE); left.add(id); left.add(info);
                h.add(left, BorderLayout.WEST);
                content.add(h); content.add(Box.createVerticalStrut(15));
                
                JPanel list = new JPanel(); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS)); list.setBackground(Color.WHITE);
                PreparedStatement ps2 = conn.prepareStatement("SELECT f.ten, f.hinh_anh, f.loai, c.so_luong, c.gia_ban, c.thanh_tien FROM chitiet_donhang c JOIN figure f ON c.figureId=f.id WHERE c.donhangId=?");
                ps2.setInt(1, maDonHang); ResultSet rs2 = ps2.executeQuery();
                while(rs2.next()) {
                    JPanel item = new JPanel(new BorderLayout(15, 0)); item.setBackground(Color.WHITE); item.setBorder(new EmptyBorder(5, 15, 5, 15));
                    ImageIcon icon = loadResizedIcon(rs2.getString("hinh_anh"), 40, 40);
                    JLabel img = new JLabel(); img.setPreferredSize(new Dimension(40,40)); if(icon!=null) img.setIcon(icon);
                    item.add(img, BorderLayout.WEST);
                    JPanel c = new JPanel(new GridLayout(2,1)); c.setBackground(Color.WHITE);
                    JLabel n = new JLabel(rs2.getString("ten")); n.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    JLabel d = new JLabel(rs2.getString("loai") + " | SL: " + rs2.getInt("so_luong")); d.setForeground(Color.GRAY);
                    c.add(n); c.add(d); item.add(c, BorderLayout.CENTER);
                    item.add(new JLabel(String.format("%,d", rs2.getLong("thanh_tien"))), BorderLayout.EAST);
                    list.add(item); list.add(new JSeparator());
                }
                content.add(list); content.add(Box.createVerticalStrut(10));
                
                JPanel sum = new JPanel(new GridLayout(0,2)); sum.setBackground(Color.WHITE); sum.setBorder(new EmptyBorder(20,25,20,25));
                sum.add(new JLabel("PTTT:")); sum.add(new JLabel(rs.getString("phuong_thuc_tt"), JLabel.RIGHT));
                sum.add(new JLabel("Tổng:")); JLabel totalLabel = new JLabel(String.format("%,d đ", rs.getLong("tong_tien")), JLabel.RIGHT); totalLabel.setForeground(Color.RED); totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                sum.add(totalLabel);
                content.add(sum);
            }
        } catch(Exception e) {}
        
        JScrollPane scr = new JScrollPane(content); scr.setBorder(null); scr.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scr); dialog.pack(); dialog.setSize(400, 550); dialog.setLocationRelativeTo(this); dialog.setVisible(true);
    }

    // Hàm hiển thị chi tiết sản phẩm (Dùng cho nút Chi tiết ở bảng bên trái)
    private void moChiTiet(FigureDTO f) {
        JDialog d = new JDialog(MainUI.this, "Chi tiết sản phẩm", true);
        d.setSize(650, 500); 
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(Color.WHITE);
        d.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20); 
        
        // --- 1. ẢNH (BÊN TRÁI) ---
        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(250, 250));
        lblImg.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = loadResizedIcon(f.getHinhAnh(), 240, 240); 
        if(icon != null) lblImg.setIcon(icon);
        else lblImg.setText("No Image");

        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.gridheight = 8; 
        gbc.anchor = GridBagConstraints.NORTH; 
        p.add(lblImg, gbc);

        // --- 2. THÔNG TIN (BÊN PHẢI) ---
        gbc.gridx = 1; 
        gbc.gridheight = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        
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
        
        // Nhà cung cấp
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

        // Mô tả (Text Area)
        gbc.gridy++;
        JTextArea txtMoTa = new JTextArea(f.getMoTa());
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setEditable(false);
        
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollMoTa.setPreferredSize(new Dimension(280, 100)); 
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.weighty = 1.0; 
        p.add(scrollMoTa, gbc);

        d.add(p, BorderLayout.CENTER);
        d.setVisible(true);
    }
    
    // =========================================================================
    // 5. LOGIC & HELPER (Giữ nguyên)
    // =========================================================================
    // ... (styleTable, loadKhuyenMaiData, taiDanhSach, capNhatBangDanhSach, capNhatGioHang, 
    // capNhatTongTien, tinhTienThua, themVaoGio, xoaKhoiGio, thanhToan, hienThiPopupHoaDon, 
    // moChiTiet, loadResizedIcon, timKiemNangCao, parseDouble, createStyledMenuItem, 
    // styleDarkMenuItem, createLabelWhite, createRedButton, createGreenButton, Inner Classes...)
    
    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setOpaque(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 5, 0, 0));

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 2 && table.getColumnCount() > 6) table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            else table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadKhuyenMaiData() {
        try (Connection conn = new DBConnection().getConnect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT ma, phan_tram_giam FROM khuyenmai WHERE han_dung >= CURDATE()")) {
            while (rs.next()) cbKhuyenMai.addItem(rs.getString("ma") + " - " + rs.getInt("phan_tram_giam") + "%");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void taiDanhSach() {
        this.danhSachHienTai = bll.layTatCa();
        capNhatBangDanhSach(this.danhSachHienTai);
    }

    private void capNhatBangDanhSach(List<FigureDTO> list) {
        String[] cols = {"ID", "Hình", "Tên Figure", "Loại", "Giá", "Kích thước", "Số lượng", "Nhà cung cấp", "Chi tiết", "Thêm"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c >= 8; } 
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
        };
        this.danhSachHienTai = list;
        for (FigureDTO f : list) {
            ImageIcon icon = loadResizedIcon(f.getHinhAnh(), 60, 60); 
            model.addRow(new Object[]{ f.getId(), icon, f.getTen(), f.getLoai(), String.format("%,.0f", f.getGia()), f.getKichThuoc(), f.getSoLuong(), f.getTenNCC(), "Chi tiết", "Thêm" });
        }
        tblDanhSach.setModel(model);
        tblDanhSach.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblDanhSach.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblDanhSach.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblDanhSach.getColumnModel().getColumn(7).setPreferredWidth(150);
        styleTable(tblDanhSach);
        
        tblDanhSach.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = new JLabel(); l.setHorizontalAlignment(JLabel.CENTER);
                if (v instanceof ImageIcon) l.setIcon((ImageIcon) v); else l.setText("No IMG");
                l.setOpaque(true); l.setBackground(s ? t.getSelectionBackground() : Color.WHITE);
                return l;
            }
        });
        
        TableColumn colDetail = tblDanhSach.getColumnModel().getColumn(8);
        colDetail.setCellRenderer(new DetailButtonRenderer());
        colDetail.setCellEditor(new DetailButtonEditor(new JCheckBox())); 
        TableColumn colAdd = tblDanhSach.getColumnModel().getColumn(9);
        colAdd.setCellRenderer(new AddButtonRenderer());
        colAdd.setCellEditor(new AddButtonEditor(new JCheckBox())); 
    }

    private void capNhatGioHang() {
        String[] cols = {"ID", "Hình", "Tên SP", "SL", "Thành tiền", "Xóa"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
        };
        for (GioHangItemDTO i : bll.getGioHang()) {
            ImageIcon icon = loadResizedIcon(i.getFigure().getHinhAnh(), 50, 50);
            model.addRow(new Object[]{ i.getFigure().getId(), icon, i.getFigure().getTen(), i.getSoLuong(), String.format("%,.0f", i.getThanhTien()), "Xóa" });
        }
        tblGioHang.setModel(model);
        tblGioHang.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblGioHang.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblGioHang.getColumnModel().getColumn(2).setPreferredWidth(150);
        styleTable(tblGioHang); 
        
        tblGioHang.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = new JLabel(); l.setHorizontalAlignment(JLabel.CENTER);
                if (v instanceof ImageIcon) l.setIcon((ImageIcon) v); else l.setText("No IMG");
                l.setOpaque(true); l.setBackground(s ? t.getSelectionBackground() : Color.WHITE);
                return l;
            }
        });
        
        tblGioHang.getColumnModel().getColumn(5).setCellRenderer(new DeleteButtonRenderer());
        tblGioHang.getColumnModel().getColumn(5).setCellEditor(new DeleteButtonEditor(new JCheckBox())); 
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        long tongGoc = 0;
        for (GioHangItemDTO i : bll.getGioHang()) tongGoc += i.getThanhTien();
        int phanTram = 0;
        String kmSelect = (String) cbKhuyenMai.getSelectedItem();
        if (kmSelect != null && !kmSelect.equals("Không áp dụng")) {
            try { String[] parts = kmSelect.split(" - "); if (parts.length > 1) phanTram = Integer.parseInt(parts[1].replace("%", "")); } catch(Exception e) {}
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
            if (text.isEmpty()) { lblTienThua.setText("0 đ"); return; }
            long tienKhach = Long.parseLong(text);
            long tienThua = tienKhach - tongTienHienTai;
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            lblTienThua.setText(nf.format(tienThua));
            if (tienThua < 0) lblTienThua.setForeground(Color.RED); else lblTienThua.setForeground(new Color(40, 167, 69));
        } catch (NumberFormatException e) { lblTienThua.setText("0 đ"); }
    }

    private void themVaoGio(int id, int soLuong) {
        if (bll.themVaoGio(id, soLuong)) {
            capNhatGioHang();
            for (FigureDTO fig : this.danhSachHienTai) { if (fig.getId() == id) { fig.setSoLuong(fig.getSoLuong() - soLuong); break; } }
            capNhatBangDanhSach(this.danhSachHienTai);
        } else { JOptionPane.showMessageDialog(this, "Không đủ hàng hoặc lỗi kho!"); }
    }

    private void xoaKhoiGio(int id) {
        int slTra = 0;
        for(GioHangItemDTO i : bll.getGioHang()) if(i.getFigureId()==id) slTra=i.getSoLuong();
        if (bll.xoaKhoiGio(id)) {
            capNhatGioHang();
            for (FigureDTO fig : this.danhSachHienTai) { if (fig.getId() == id) { fig.setSoLuong(fig.getSoLuong() + slTra); break; } }
            capNhatBangDanhSach(this.danhSachHienTai);
        }
    }

    private void thanhToan() {
        if (nguoiDungHienTai == null) { JOptionPane.showMessageDialog(this, "Chưa đăng nhập!"); return; }
        if (nguoiDungHienTai.getMaNguoiDung() <= 0) { JOptionPane.showMessageDialog(this, "Phiên làm việc lỗi, vui lòng đăng nhập lại!", "Lỗi", JOptionPane.ERROR_MESSAGE); this.dispose(); new LoginUI().setVisible(true); return; }
        if (bll.getGioHang().isEmpty()) { JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng!"); return; }
        String pttt = (String) cbPhuongThucTT.getSelectedItem();
        if ("Tiền mặt".equals(pttt)) {
            try {
                String textTien = txtTienKhachDua.getText().replace(".", "").replace(",", "").trim();
                if (textTien.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khách đưa!"); return; }
                long khachDua = Long.parseLong(textTien);
                if (khachDua < tongTienHienTai) { JOptionPane.showMessageDialog(this, "Khách đưa chưa đủ tiền!"); return; }
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Tiền nhập không hợp lệ!"); return; }
        }
        String maKM = null;
        Object itemSelect = cbKhuyenMai.getSelectedItem();
        if (itemSelect != null && !itemSelect.toString().equals("Không áp dụng")) {
            try { String raw = itemSelect.toString(); maKM = raw.contains("-") ? raw.split("-")[0].trim() : raw.trim(); } catch (Exception e) {}
        }
        if (maKM != null && maKM.isEmpty()) maKM = null;
        String ptttDB = switch(pttt) { case "Tiền mặt" -> "TienMat"; case "Chuyển khoản" -> "ChuyenKhoan"; case "Thẻ" -> "The"; default -> "ViDienTu"; };
        DonHangDTO donHang = bll.thanhToan(nguoiDungHienTai.getMaNguoiDung(), ptttDB, maKM);
        if (donHang != null) {
            capNhatGioHang(); taiDanhSach(); txtTienKhachDua.setText(""); lblTienThua.setText("0 đ"); cbKhuyenMai.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!"); hienThiPopupHoaDon(donHang);
        } else { JOptionPane.showMessageDialog(this, "Lỗi thanh toán!"); }
    }

    private void hienThiPopupHoaDon(DonHangDTO donHang) {
        JDialog d = new JDialog(this, "HÓA ĐƠN THANH TOÁN", true); d.setSize(400, 500); d.setLocationRelativeTo(this);
        JTextArea txt = new JTextArea(); txt.setFont(new Font("Consolas", Font.PLAIN, 13)); txt.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("        MAHIRU SHOP\n"); sb.append("---------------------------\n");
        sb.append("Mã ĐH: ").append(donHang.getMaDonHang()).append("\n"); sb.append("Ngày: ").append(donHang.getNgayDat()).append("\n"); sb.append("---------------------------\n");
        for(GioHangItemDTO i : donHang.getGioHang()) {
            sb.append(String.format("%-20s x%d\n", i.getFigure().getTen().length()>20?i.getFigure().getTen().substring(0,18)+"..":i.getFigure().getTen(), i.getSoLuong()));
            sb.append(String.format("%25s\n", String.format("%,.0f", i.getThanhTien())));
        }
        sb.append("---------------------------\n"); sb.append("TỔNG TIỀN: ").append(String.format("%,.0f VND", donHang.getTongTien())).append("\n"); sb.append("\nCảm ơn quý khách!");
        txt.setText(sb.toString()); d.add(new JScrollPane(txt)); d.setVisible(true);
    }

    private ImageIcon loadResizedIcon(String filename, int w, int h) {
         if (filename == null || filename.isEmpty()) return null;
         try { URL url = getClass().getResource("/Resources/figure_images/" + filename); if (url != null) { BufferedImage img = ImageIO.read(url); return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)); } } catch(Exception e) {}
         return null;
    }
    
    private void timKiemNangCao() {
        String ten = txtTenTimKiem.getText().trim();
        String loai = "Tất cả".equals(cbLoai.getSelectedItem()) ? null : (String) cbLoai.getSelectedItem();
        Double min = parseDouble(txtMinGia.getText());
        Double max = parseDouble(txtMaxGia.getText());
        String kt = "Tất cả".equals(cbKichThuoc.getSelectedItem()) ? null : (String) cbKichThuoc.getSelectedItem();
        Integer maNCC = 0;
        if (cbLocNCC != null && cbLocNCC.getSelectedIndex() > 0) { NhaCungCapDTO ncc = (NhaCungCapDTO) cbLocNCC.getSelectedItem(); maNCC = ncc.getMaNCC(); }
        this.danhSachHienTai = bll.timKiemNangCao(ten, loai, min, max, kt, maNCC); capNhatBangDanhSach(this.danhSachHienTai);
    }
    
    private Double parseDouble(String s) { try { return Double.parseDouble(s); } catch(Exception e) { return null; } }
    
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text); menuItem.setFont(new Font("Segoe UI", Font.BOLD, 14)); menuItem.setBackground(new Color(50, 50, 50)); menuItem.setForeground(Color.WHITE); menuItem.setOpaque(true); menuItem.setBorder(new EmptyBorder(10, 15, 10, 15));
        menuItem.addMouseListener(new MouseAdapter() { public void mouseEntered(MouseEvent e) { menuItem.setBackground(new Color(80, 80, 80)); } public void mouseExited(MouseEvent e) { menuItem.setBackground(new Color(50, 50, 50)); } });
        return menuItem;
    }

    // --- INNER CLASSES ---
    private class DetailButtonRenderer extends JButton implements TableCellRenderer {
        public DetailButtonRenderer() { setText("Chi tiết"); setBackground(new Color(23, 162, 184)); setForeground(Color.WHITE); }
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
        public AddButtonRenderer() { setText("Thêm"); setBackground(new Color(40, 167, 69)); setForeground(Color.WHITE); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return this; }
    }
    private class AddButtonEditor extends DefaultCellEditor {
        JButton b;
        public AddButtonEditor(JCheckBox cb) {
            super(cb); b = new JButton("Thêm");
            b.addActionListener(e -> {
                int selectedRow = tblDanhSach.getSelectedRow();
                if (selectedRow != -1) {
                    int id = Integer.parseInt(tblDanhSach.getValueAt(selectedRow, 0).toString());
                    String sl = JOptionPane.showInputDialog("Nhập số lượng:");
                    try { if(sl!=null && !sl.trim().isEmpty()) { int s = Integer.parseInt(sl.trim()); if(s > 0) themVaoGio(id, s); } } catch(Exception ex){}
                }
                fireEditingStopped();
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int c) { return b; }
        public Object getCellEditorValue() { return "Thêm"; }
    }
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() { setText("Xóa"); setBackground(new Color(220, 53, 69)); setForeground(Color.WHITE); }
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