// DÁN TOÀN BỘ CODE NÀY VÀO FILE LoginUI.java

package FORM;

import BLL.NguoiDungBLL;
import DTO.NguoiDungDTO;
import Database.DBConnection;    // <--- THÊM DÒNG NÀY (Kiểm tra lại tên class kết nối CSDL của bạn)
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.sql.*;          // <--- THÊM DÒNG NÀY
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginUI extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    // private JCheckBox chkShowPass; // Bỏ CheckBox
    private NguoiDungBLL nguoiDungBLL = new NguoiDungBLL();
    private DBConnection db = new DBConnection(); // <--- THÊM DÒNG NÀY để dùng kết nối CSDL
    
    public LoginUI() {
        initComponents();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                moMainUIKhongDangNhap(); 
            }
        });
    }

    private void initComponents() {
        setTitle("MahiruShop - Đăng Nhập");
        setSize(500, 680);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(180, 0, 0));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        panel.setPreferredSize(new Dimension(460, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // LOGO
        JLabel lblLogo = new JLabel("MahiruShop", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblLogo.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.insets = new Insets(30, 0, 40, 0);
        panel.add(lblLogo, gbc);

        // Đăng nhập
        JLabel lblDangNhap = new JLabel("Đăng nhập", JLabel.CENTER);
        lblDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDangNhap.setForeground(new Color(180, 0, 0));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblDangNhap, gbc);

        // Tên đăng nhập
        JLabel lblTen = new JLabel("Tên đăng nhập");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST; 
        panel.add(lblTen, gbc);

        txtTenDangNhap = new JTextField();
        setupPlaceholder(txtTenDangNhap, "Nhập tên đăng nhập ...");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER; 
        panel.add(txtTenDangNhap, gbc);

        // Mật khẩu
        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 4; gbc.insets = new Insets(25, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblMatKhau, gbc);

        // === SỬA LỖI: TẠO PANEL CHỨA MẬT KHẨU VÀ NÚT CON MẮT ===
        // Khởi tạo txtMatKhau ở đây
        txtMatKhau = new JPasswordField();
        JPanel passPanel = createPasswordPanel(txtMatKhau, "Nhập mật khẩu ...");
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(passPanel, gbc);
        // === KẾT THÚC SỬA ===
        
        // Bỏ CheckBox
        // gbc.gridy = 6; ...

        // Quên mật khẩu
        JLabel lblQuenMK = new JLabel("<html><u>Quên mật khẩu ?</u></html>", JLabel.RIGHT);
        lblQuenMK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblQuenMK.setForeground(Color.BLUE);
        lblQuenMK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7; gbc.insets = new Insets(10, 0, 20, 0); // Sửa lề
        gbc.anchor = GridBagConstraints.EAST; 
        panel.add(lblQuenMK, gbc);

        lblQuenMK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setVisible(false); 
                ForgotPasswordUI forgotUI = new ForgotPasswordUI(LoginUI.this); 
                forgotUI.setVisible(true);
            }
        });

        // NÚT ĐĂNG NHẬP
        JButton btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setBackground(Color.BLACK);
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnDangNhap.setPreferredSize(new Dimension(340, 55));
        btnDangNhap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER; 
        panel.add(btnDangNhap, gbc);

        btnDangNhap.addActionListener(e -> dangNhap());

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        add(panel, mainGbc);
    }
    
    // === THÊM HÀM MỚI: TẠO PANEL MẬT KHẨU VỚI ICON ===
    private JPanel createPasswordPanel(JPasswordField passField, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // 1. Ô nhập mật khẩu
        setupPlaceholder(passField, placeholder); // Dùng hàm cũ để style
        panel.add(passField, BorderLayout.CENTER);

        // 2. Nút "con mắt"
        JToggleButton btnShowPass = new JToggleButton();
        try {
            // === SỬA LỖI TẢI ICON TẠI ĐÂY ===
            // Dùng getResource để tải từ classpath, /Resources/ là đường dẫn tuyệt đối từ root
            Image imgClosed = ImageIO.read(getClass().getResource("/Resources/icon_images/eye_close.png"));
            Image imgOpen = ImageIO.read(getClass().getResource("/Resources/icon_images/eye_open.png"));

            ImageIcon iconClosed = new ImageIcon(imgClosed.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            ImageIcon iconOpen = new ImageIcon(imgOpen.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            // === KẾT THÚC SỬA ===
            
            btnShowPass.setIcon(iconClosed);
            btnShowPass.setSelectedIcon(iconOpen);
            
        } catch (Exception e) {
            btnShowPass.setText("👁"); // Fallback nếu không có icon
            System.err.println("Không thể load icon con mắt: " + e.getMessage());
            // In ra lỗi chi tiết để debug
             e.printStackTrace(); 
        }

        btnShowPass.setPreferredSize(new Dimension(40, 40)); // Kích thước nút
        btnShowPass.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền
        btnShowPass.setContentAreaFilled(false); // Nền trong suốt
        btnShowPass.setFocusPainted(false);
        btnShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm sự kiện cho nút
        btnShowPass.addActionListener(e -> {
            String pass = new String(passField.getPassword());
            if (!pass.equals(placeholder)) {
                if (btnShowPass.isSelected()) {
                    passField.setEchoChar((char) 0); // Hiện
                } else {
                    passField.setEchoChar('•'); // Ẩn
                }
            }
        });
        
        // Panel bọc nút (để căn lề)
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonWrapper.setBackground(Color.WHITE);
        buttonWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); // Giống viền
        buttonWrapper.add(btnShowPass);
        
        panel.add(buttonWrapper, BorderLayout.EAST);
        
        // Cần đảm bảo placeholder hoạt động với nút
        passField.addFocusListener(new FocusAdapter() {
             @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(passField.getPassword());
                if (pass.equals(placeholder)) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    if (!btnShowPass.isSelected()) {
                        passField.setEchoChar('•');
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                String pass = new String(passField.getPassword());
                if (pass.isEmpty()) {
                    passField.setForeground(Color.GRAY);
                    passField.setText(placeholder);
                    passField.setEchoChar((char) 0);
                }
            }
        });
        
        return panel;
    }
    
    // Hàm setupPlaceholder (Sửa lại chỉ còn style)
    private void setupPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        textField.setPreferredSize(new Dimension(340, 48));

        if (textField instanceof JPasswordField) {
            ((JPasswordField) textField).setEchoChar((char) 0); 
        }
        
        // (Xóa FocusListener ở đây, vì createPasswordPanel sẽ tự xử lý)
        // Chỉ giữ lại cho txtTenDangNhap
        if (!(textField instanceof JPasswordField)) {
             textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (textField.getForeground() == Color.GRAY) {
                        textField.setText("");
                        textField.setForeground(Color.BLACK);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (textField.getText().isEmpty()) {
                        textField.setForeground(Color.GRAY);
                        textField.setText(placeholder);
                    }
                }
            });
        }
    }
    
    // (Bỏ hàm addShowHideListener)

    private void dangNhap() {
        String ten = txtTenDangNhap.getText().trim();
        String mk = new String(txtMatKhau.getPassword());

        // 1. Kiểm tra rỗng
        if (ten.isEmpty() || mk.isEmpty() || ten.equals("Nhập tên đăng nhập ...") || mk.equals("Nhập mật khẩu ...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // 2. LOGIC KIỂM TRA ĐĂNG NHẬP CHI TIẾT
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM nguoidung WHERE ten_dang_nhap = ? AND mat_khau = ?")) {

            ps.setString(1, ten);
            ps.setString(2, mk);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // --- TRƯỜNG HỢP A: Tài khoản và Mật khẩu ĐÚNG ---
                
                // Lấy trạng thái ra kiểm tra
                String status = rs.getString("trang_thai");
                String role = rs.getString("vai_tro");
                
                // Kiểm tra xem có phải là "Mở" (hoặc "Active") hay không
                if (status != null && (status.equalsIgnoreCase("Mở") || status.equalsIgnoreCase("Active"))) {
                    
                    // ==> ĐĂNG NHẬP THÀNH CÔNG
                    // Tạo DTO thủ công để truyền sang form khác (vì không dùng BLL ở đây)
                    NguoiDungDTO nd = new NguoiDungDTO();
                    // --- [BỔ SUNG DÒNG QUAN TRỌNG NÀY] ---
                    nd.setMaNguoiDung(rs.getInt("ma_nguoi_dung")); // <--- Lấy ID từ SQL bỏ vào DTO
                    // -------------------------------------
                    nd.setTenDangNhap(ten);
                    nd.setMatKhau(mk);
                    nd.setVaiTro(role);
                    nd.setTrangThai(status);
                    // Set thêm các trường khác nếu cần (email...)
                    
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Xin chào " + role);
                    this.dispose(); // Đóng form Login
                    
                    // Chuyển màn hình
                    if ("Admin".equalsIgnoreCase(role)) {
                        new AdminUI(nd).setVisible(true);
                    } else {
                        new MainUI(nd).setVisible(true);
                    }
                    
                } else {
                    // ==> TRƯỜNG HỢP B: Đúng mật khẩu nhưng BỊ KHÓA
                    JOptionPane.showMessageDialog(this, 
                        "Tài khoản của bạn hiện đang bị KHÓA.\nVui lòng liên hệ Admin để mở lại!", 
                        "Thông báo", 
                        JOptionPane.WARNING_MESSAGE);
                }

            } else {
                // --- TRƯỜNG HỢP C: Không tìm thấy User hoặc sai Pass ---
                JOptionPane.showMessageDialog(this, 
                    "Sai tên đăng nhập hoặc mật khẩu!", 
                    "Lỗi đăng nhập", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    private void moMainUIKhongDangNhap() {
        dispose(); 
        MainUI mainUI = new MainUI(null); 
        mainUI.setVisible(true);
    }
}