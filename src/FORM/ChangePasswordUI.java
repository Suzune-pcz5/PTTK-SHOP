package FORM;

import BLL.NguoiDungBLL;
import DTO.NguoiDungDTO;
import Database.DBConnection; // <--- ĐÃ SỬA ĐÚNG ĐƯỜNG DẪN
import java.sql.*;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChangePasswordUI extends JFrame {
    private NguoiDungBLL bll = new NguoiDungBLL();
    private DBConnection db = new DBConnection(); // Để check pass cũ

    // Khai báo 3 ô mật khẩu
    private JPasswordField txtMKCu, txtMKMoi, txtXacNhanMKMoi;
    
    private JFrame mainFrame; 
    private NguoiDungDTO nguoiDung; 

    public ChangePasswordUI(JFrame mainFrame, NguoiDungDTO nguoiDung) {
        this.mainFrame = mainFrame;
        this.nguoiDung = nguoiDung;
        initComponents();
    }
    
    private void dongCuaSo() {
        this.dispose();
        if (mainFrame != null) {
            mainFrame.setEnabled(true); 
            mainFrame.toFront(); 
        }
    }

    private void initComponents() {
        setTitle("MahiruShop - Đổi Mật Khẩu");
        setSize(500, 750); // Tăng chiều cao để chứa thêm ô nhập
        setLocationRelativeTo(mainFrame); 
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(180, 0, 0)); 
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dongCuaSo(); 
            }
        });
        
        if(mainFrame != null) {
            mainFrame.setEnabled(false);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        panel.setPreferredSize(new Dimension(460, 680));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. LOGO
        JLabel lblLogo = new JLabel("MahiruShop", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblLogo.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.insets = new Insets(30, 0, 30, 0);
        panel.add(lblLogo, gbc);

        // 2. Tiêu đề
        JLabel lblTitle = new JLabel("Đổi mật khẩu", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(180, 0, 0));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(lblTitle, gbc);

        // --- 3. MẬT KHẨU CŨ (MỚI THÊM) ---
        JLabel lblMKCu = new JLabel("Mật khẩu cũ");
        lblMKCu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblMKCu, gbc);

        txtMKCu = new JPasswordField();
        JPanel passPanelCu = createPasswordPanel(txtMKCu, "Nhập mật khẩu hiện tại ...");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(passPanelCu, gbc);

        // 4. Mật khẩu mới
        JLabel lblMKMoi = new JLabel("Mật khẩu mới");
        lblMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 4; gbc.insets = new Insets(20, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblMKMoi, gbc);

        txtMKMoi = new JPasswordField();
        JPanel passPanelMoi = createPasswordPanel(txtMKMoi, "Nhập mật khẩu mới ...");
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(passPanelMoi, gbc);

        // 5. Xác nhận Mật khẩu mới
        JLabel lblXacNhanMKMoi = new JLabel("Xác nhận mật khẩu mới");
        lblXacNhanMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 6; gbc.insets = new Insets(20, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblXacNhanMKMoi, gbc);

        txtXacNhanMKMoi = new JPasswordField();
        JPanel passPanelXacNhan = createPasswordPanel(txtXacNhanMKMoi, "Nhập lại mật khẩu mới ...");
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(passPanelXacNhan, gbc);
        
        // 6. NÚT XÁC NHẬN
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(Color.BLACK);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnXacNhan.setPreferredSize(new Dimension(340, 55)); 
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8; gbc.insets = new Insets(40, 0, 30, 0); 
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnXacNhan, gbc);

        btnXacNhan.addActionListener(e -> thucHienDoiMatKhau());
        
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        add(panel, mainGbc);
        setVisible(true);
    }
    
    // === HÀM TẠO PANEL MẬT KHẨU (CÓ ICON CON MẮT) ===
    private JPanel createPasswordPanel(JPasswordField passField, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // 1. Ô nhập mật khẩu
        setupPlaceholder(passField, placeholder); 
        panel.add(passField, BorderLayout.CENTER);

        // 2. Nút "con mắt"
        JToggleButton btnShowPass = new JToggleButton();
        try {
            // Dùng getResource để tải từ classpath
            Image imgClosed = ImageIO.read(getClass().getResource("/Resources/icon_images/eye_close.png"));
            Image imgOpen = ImageIO.read(getClass().getResource("/Resources/icon_images/eye_open.png"));

            ImageIcon iconClosed = new ImageIcon(imgClosed.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            ImageIcon iconOpen = new ImageIcon(imgOpen.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            
            btnShowPass.setIcon(iconClosed);
            btnShowPass.setSelectedIcon(iconOpen);
            
        } catch (Exception e) {
            btnShowPass.setText("👁"); // Fallback
        }

        btnShowPass.setPreferredSize(new Dimension(40, 40)); 
        btnShowPass.setBorder(BorderFactory.createEmptyBorder()); 
        btnShowPass.setContentAreaFilled(false); 
        btnShowPass.setFocusPainted(false);
        btnShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sự kiện hiện/ẩn pass
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
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonWrapper.setBackground(Color.WHITE);
        buttonWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); 
        buttonWrapper.add(btnShowPass);
        
        panel.add(buttonWrapper, BorderLayout.EAST);
        
        // Logic Placeholder
        passField.addFocusListener(new FocusAdapter() {
             @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(passField.getPassword());
                if (pass.equals(placeholder)) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    if (!btnShowPass.isSelected()) passField.setEchoChar('•');
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
    
    // === LOGIC XỬ LÝ ĐỔI MẬT KHẨU ===
    private void thucHienDoiMatKhau() {
        String mkCu = new String(txtMKCu.getPassword()).trim();
        String mkMoi = new String(txtMKMoi.getPassword()).trim();
        String xacNhanMK = new String(txtXacNhanMKMoi.getPassword()).trim();

        // 1. Kiểm tra rỗng và placeholder
        if (mkCu.isEmpty() || mkCu.equals("Nhập mật khẩu hiện tại ...") ||
            mkMoi.isEmpty() || mkMoi.equals("Nhập mật khẩu mới ...") ||
            xacNhanMK.isEmpty() || xacNhanMK.equals("Nhập lại mật khẩu mới ...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Kiểm tra mật khẩu cũ (QUAN TRỌNG: Check từ DB)
        if (!checkOldPassword(nguoiDung.getTenDangNhap(), mkCu)) {
             JOptionPane.showMessageDialog(this, "Mật khẩu cũ không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        // 3. Mật khẩu mới không được trùng cũ
        if (mkMoi.equals(mkCu)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới không được trùng mật khẩu cũ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Xác nhận mật khẩu
        if (!mkMoi.equals(xacNhanMK)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (bll.doiMatKhau(this.nguoiDung.getTenDangNhap(), mkMoi)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại.");
                dongCuaSo();
                
                // Đăng xuất bắt buộc
                if (mainFrame != null) {
                    mainFrame.dispose();
                    new LoginUI().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
             e.printStackTrace();
        }
    }
    
    // Hàm kiểm tra mật khẩu cũ trực tiếp từ Database
    private boolean checkOldPassword(String username, String oldPass) {
        try (Connection conn = db.getConnect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM nguoidung WHERE ten_dang_nhap = ? AND mat_khau = ?")) {
            ps.setString(1, username);
            ps.setString(2, oldPass);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // True nếu tìm thấy user + pass khớp
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}