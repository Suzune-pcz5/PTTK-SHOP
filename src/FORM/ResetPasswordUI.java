// DÁN TOÀN BỘ CODE NÀY VÀO FILE ResetPasswordUI.java

package FORM;

import BLL.NguoiDungBLL;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ResetPasswordUI extends JFrame {
    private NguoiDungBLL bll = new NguoiDungBLL();
    private JPasswordField txtMKMoi, txtXacNhanMKMoi;
    // private JCheckBox chkShowMKMoi, chkShowXacNhan; // Bỏ CheckBox
    
    private JFrame loginFrame; 
    private JFrame forgotFrame; 
    private String tenDangNhap; 

    public ResetPasswordUI(JFrame loginFrame, JFrame forgotFrame, String tenDangNhap) {
        this.loginFrame = loginFrame;
        this.forgotFrame = forgotFrame;
        this.tenDangNhap = tenDangNhap;
        initComponents();
    }
    
    private void quayLaiLogin() {
        this.dispose(); 
        this.forgotFrame.dispose(); 
        if (loginFrame != null) {
            loginFrame.setVisible(true);
        }
    }

    private void initComponents() {
        setTitle("MahiruShop - Đặt Lại Mật Khẩu");
        setSize(500, 680); 
        setLocationRelativeTo(forgotFrame);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(180, 0, 0)); 

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quayLaiLogin(); 
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        panel.setPreferredSize(new Dimension(460, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. LOGO
        JLabel lblLogo = new JLabel("MahiruShop", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblLogo.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.insets = new Insets(30, 0, 40, 0);
        panel.add(lblLogo, gbc);

        // 2. Tiêu đề
        JLabel lblTitle = new JLabel("Đặt mật khẩu mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(180, 0, 0));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblTitle, gbc);

        // 3. Mật khẩu mới
        JLabel lblMKMoi = new JLabel("Mật khẩu mới");
        lblMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblMKMoi, gbc);

        txtMKMoi = new JPasswordField();
        JPanel passPanelMoi = createPasswordPanel(txtMKMoi, "Nhập mật khẩu mới ...");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(passPanelMoi, gbc);

        // 4. Xác nhận Mật khẩu mới
        JLabel lblXacNhanMKMoi = new JLabel("Xác nhận mật khẩu mới");
        lblXacNhanMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 4; gbc.insets = new Insets(25, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblXacNhanMKMoi, gbc);

        txtXacNhanMKMoi = new JPasswordField();
        JPanel passPanelXacNhan = createPasswordPanel(txtXacNhanMKMoi, "Nhập lại mật khẩu mới ...");
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(passPanelXacNhan, gbc);
        
        // 5. NÚT XÁC NHẬN
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(Color.BLACK);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnXacNhan.setPreferredSize(new Dimension(340, 55)); 
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6; gbc.insets = new Insets(40, 0, 30, 0); 
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnXacNhan, gbc);

        btnXacNhan.addActionListener(e -> thucHienDatLaiMatKhau());
        
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        add(panel, mainGbc);
        setVisible(true);
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
    }
    
    // (Bỏ hàm addShowHideListener)

    // Hàm xử lý logic
    private void thucHienDatLaiMatKhau() {
        String mkMoi = new String(txtMKMoi.getPassword()).trim();
        String xacNhanMK = new String(txtXacNhanMKMoi.getPassword()).trim();

        if (mkMoi.equals("Nhập mật khẩu mới ...") || xacNhanMK.equals("Nhập lại mật khẩu mới ...") ||
            mkMoi.isEmpty() || xacNhanMK.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!mkMoi.equals(xacNhanMK)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (bll.doiMatKhau(this.tenDangNhap, mkMoi)) {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                quayLaiLogin(); 
            } else {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thất bại! (Lỗi BLL/DAL)");
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng: " + e.getMessage());
             e.printStackTrace();
        }
    }
}