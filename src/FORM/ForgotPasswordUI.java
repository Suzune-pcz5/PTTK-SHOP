// DÁN TOÀN BỘ CODE NÀY VÀO FILE MỚI: ForgotPasswordUI.java

package FORM;

import BLL.NguoiDungBLL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ForgotPasswordUI extends JFrame {
    private NguoiDungBLL bll = new NguoiDungBLL();
    private JTextField txtTen, txtEmail;
    private JFrame loginFrame; // Để lưu cửa sổ Login gốc

    public ForgotPasswordUI(JFrame loginFrame) {
        this.loginFrame = loginFrame;
        initComponents();
    }
    
    // Hàm quay lại
    private void quayLaiLogin() {
        this.dispose();
        if (loginFrame != null) {
            loginFrame.setVisible(true);
        }
    }

    private void initComponents() {
        setTitle("MahiruShop - Quên Mật Khẩu");
        setSize(500, 680); 
        setLocationRelativeTo(loginFrame);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(180, 0, 0)); 

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quayLaiLogin(); 
            }
        });

        // Panel trắng
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
        JLabel lblTitle = new JLabel("Quên mật khẩu", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(180, 0, 0));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblTitle, gbc);

        // 3. Tên đăng nhập
        JLabel lblTen = new JLabel("Tên đăng nhập");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblTen, gbc);

        txtTen = new JTextField();
        setupPlaceholder(txtTen, "Nhập tên đăng nhập ...");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(txtTen, gbc);

        // 4. Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 4; gbc.insets = new Insets(25, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblEmail, gbc);

        txtEmail = new JTextField();
        setupPlaceholder(txtEmail, "Nhập email ...");
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(txtEmail, gbc);
        
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

        btnXacNhan.addActionListener(e -> thucHienXacThuc());
        
        // Thêm panel trắng vào panel đỏ
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        add(panel, mainGbc);
        setVisible(true);
    }
    
    // Hàm tạo placeholder
    private void setupPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        textField.setPreferredSize(new Dimension(340, 48));

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

    // Hàm xử lý logic
    private void thucHienXacThuc() {
        String ten = txtTen.getText().trim();
        String email = txtEmail.getText().trim();

        if (ten.equals("Nhập tên đăng nhập ...") || email.equals("Nhập email ...") ||
            ten.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Gọi BLL (file 17:15)
            if (bll.kiemTraDanhTinh(ten, email)) {
                JOptionPane.showMessageDialog(this, "Xác thực thành công! Vui lòng đặt mật khẩu mới.");
                
                // Mở cửa sổ ResetPasswordUI
                ResetPasswordUI resetUI = new ResetPasswordUI(loginFrame, this, ten);
                resetUI.setVisible(true);
                
                // Ẩn cửa sổ này
                this.setVisible(false); 
                
            } else {
                JOptionPane.showMessageDialog(this, "Tên tài khoản hoặc email không khớp!");
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng: " + e.getMessage());
             e.printStackTrace();
        }
    }
}