// FORM/ChangePasswordUI.java
package FORM;

import BLL.NguoiDungBLL;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordUI extends JFrame {
    private NguoiDungBLL bll = new NguoiDungBLL();
    private JTextField txtTen, txtEmail;
    private JPasswordField txtMKMoi;

    public ChangePasswordUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("MahiruShop - Đổi Mật Khẩu");
        setSize(500, 680);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(180, 0, 0));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
        panel.setPreferredSize(new Dimension(460, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // LOGO
        JLabel lblLogo = new JLabel("MahiruShop", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblLogo.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 40, 0);
        panel.add(lblLogo, gbc);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Đổi mật khẩu", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(180, 0, 0));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblTitle, gbc);

        // Tên đăng nhập
        JLabel lblTen = new JLabel("Tên đăng nhập");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(lblTen, gbc);

        txtTen = new JTextField("Nhập tên đăng nhập ...");
        txtTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTen.setForeground(Color.GRAY);
        txtTen.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        txtTen.setPreferredSize(new Dimension(340, 48));
        gbc.gridy = 3;
        panel.add(txtTen, gbc);

        txtTen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTen.getText().equals("Nhập tên đăng nhập ...")) {
                    txtTen.setText(""); txtTen.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtTen.getText().isEmpty()) {
                    txtTen.setText("Nhập tên đăng nhập ..."); txtTen.setForeground(Color.GRAY);
                }
            }
        });

        // Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 4; gbc.insets = new Insets(25, 0, 8, 0);
        panel.add(lblEmail, gbc);

        txtEmail = new JTextField("Nhập email ...");
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEmail.setForeground(Color.GRAY);
        txtEmail.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        txtEmail.setPreferredSize(new Dimension(340, 48));
        gbc.gridy = 5;
        panel.add(txtEmail, gbc);

        txtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtEmail.getText().equals("Nhập email ...")) {
                    txtEmail.setText(""); txtEmail.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtEmail.getText().isEmpty()) {
                    txtEmail.setText("Nhập email ..."); txtEmail.setForeground(Color.GRAY);
                }
            }
        });

        // Mật khẩu mới
        JLabel lblMKMoi = new JLabel("Mật khẩu mới");
        lblMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 6; gbc.insets = new Insets(25, 0, 8, 0);
        panel.add(lblMKMoi, gbc);

        txtMKMoi = new JPasswordField("Nhập mật khẩu mới ...");
        txtMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtMKMoi.setForeground(Color.GRAY);
        txtMKMoi.setEchoChar((char) 0);
        txtMKMoi.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        txtMKMoi.setPreferredSize(new Dimension(340, 48));
        gbc.gridy = 7;
        panel.add(txtMKMoi, gbc);

        txtMKMoi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                String pass = new String(txtMKMoi.getPassword());
                if (pass.equals("Nhập mật khẩu mới ...")) {
                    txtMKMoi.setText(""); txtMKMoi.setEchoChar('•'); txtMKMoi.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                String pass = new String(txtMKMoi.getPassword());
                if (pass.isEmpty()) {
                    txtMKMoi.setText("Nhập mật khẩu mới ..."); txtMKMoi.setEchoChar((char) 0); txtMKMoi.setForeground(Color.GRAY);
                }
            }
        });

        // NÚT XÁC NHẬN
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(Color.BLACK);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnXacNhan.setPreferredSize(new Dimension(340, 55));
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8; gbc.insets = new Insets(40, 0, 30, 0);
        panel.add(btnXacNhan, gbc);

        btnXacNhan.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            String email = txtEmail.getText().trim();
            String mkMoi = new String(txtMKMoi.getPassword());

            if (ten.isEmpty() || email.isEmpty() || mkMoi.isEmpty() ||
                ten.contains("Nhập") || email.contains("Nhập") || mkMoi.contains("Nhập")) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ!");
                return;
            }

            if (bll.kiemTraDanhTinh(ten, email)) {
                if (bll.doiMatKhau(ten, mkMoi)) {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                    dispose();
                    new LoginUI().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tên tài khoản hoặc email không khớp!");
            }
        });

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        add(panel, mainGbc);
        setVisible(true);
    }
}