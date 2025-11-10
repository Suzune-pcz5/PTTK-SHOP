// FORM/LoginUI.java
package FORM;

import BLL.NguoiDungBLL;
import DTO.NguoiDungDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginUI extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private NguoiDungBLL nguoiDungBLL = new NguoiDungBLL();

    public LoginUI() {
        initComponents();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Tùy chỉnh close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                moMainUIKhongDangNhap(); // Bấm X → mở MainUI (không đăng nhập)
            }
        });
    }

    private void initComponents() {
        setTitle("MahiruShop - Đăng Nhập");
        setSize(500, 680);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        getContentPane().setBackground(new Color(180, 0, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
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

        // Đăng nhập
        JLabel lblDangNhap = new JLabel("Đăng nhập", JLabel.CENTER);
        lblDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDangNhap.setForeground(new Color(180, 0, 0));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblDangNhap, gbc);

        // Tên đăng nhập
        JLabel lblTen = new JLabel("Tên đăng nhập");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(lblTen, gbc);

        txtTenDangNhap = new JTextField("Nhập tên đăng nhập ...");
        txtTenDangNhap.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTenDangNhap.setForeground(Color.GRAY);
        txtTenDangNhap.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        txtTenDangNhap.setPreferredSize(new Dimension(340, 48));
        gbc.gridy = 3;
        panel.add(txtTenDangNhap, gbc);

        txtTenDangNhap.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTenDangNhap.getText().equals("Nhập tên đăng nhập ...")) {
                    txtTenDangNhap.setText("");
                    txtTenDangNhap.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtTenDangNhap.getText().isEmpty()) {
                    txtTenDangNhap.setText("Nhập tên đăng nhập ...");
                    txtTenDangNhap.setForeground(Color.GRAY);
                }
            }
        });

        // Mật khẩu
        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 4; gbc.insets = new Insets(25, 0, 8, 0);
        panel.add(lblMatKhau, gbc);

        txtMatKhau = new JPasswordField("Nhập mật khẩu ...");
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtMatKhau.setForeground(Color.GRAY);
        txtMatKhau.setEchoChar((char) 0);
        txtMatKhau.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        txtMatKhau.setPreferredSize(new Dimension(340, 48));
        gbc.gridy = 5;
        panel.add(txtMatKhau, gbc);

        txtMatKhau.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                String pass = new String(txtMatKhau.getPassword());
                if (pass.equals("Nhập mật khẩu ...")) {
                    txtMatKhau.setText("");
                    txtMatKhau.setEchoChar('•');
                    txtMatKhau.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                String pass = new String(txtMatKhau.getPassword());
                if (pass.isEmpty()) {
                    txtMatKhau.setText("Nhập mật khẩu ...");
                    txtMatKhau.setEchoChar((char) 0);
                    txtMatKhau.setForeground(Color.GRAY);
                }
            }
        });

        // Quên mật khẩu
        JLabel lblQuenMK = new JLabel("<html><u>Quên mật khẩu ?</u></html>", JLabel.RIGHT);
        lblQuenMK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblQuenMK.setForeground(Color.BLUE);
        lblQuenMK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6; gbc.insets = new Insets(15, 0, 35, 0);
        panel.add(lblQuenMK, gbc);

        lblQuenMK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new ChangePasswordUI().setVisible(true); // MỞ ChangePasswordUI (KHÔNG TRUYỀN MAINUI NỮA)
            }
        });

        // NÚT ĐĂNG NHẬP
        JButton btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setBackground(Color.BLACK);
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnDangNhap.setPreferredSize(new Dimension(340, 55));
        btnDangNhap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(btnDangNhap, gbc);

        btnDangNhap.addActionListener(e -> dangNhap());

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        add(panel, mainGbc);
    }

    private void dangNhap() {
        String ten = txtTenDangNhap.getText().trim();
        String mk = new String(txtMatKhau.getPassword());

        if (ten.isEmpty() || mk.isEmpty() || ten.contains("Nhập") || mk.contains("Nhập")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ!");
            return;
        }

        NguoiDungDTO nd = nguoiDungBLL.dangNhap(ten, mk);
        if (nd != null) {
            dispose(); // ĐÓNG LOGINUI
            if ("NhanVien".equals(nd.getVaiTro())) {
                MainUI mainUI = new MainUI(nd); // MỞ MAINUI VỚI ND
                mainUI.setVisible(true);
            } else if ("Admin".equals(nd.getVaiTro())) {
                AdminUI adminUI = new AdminUI(nd); // MỞ ADMINUI VỚI ND
                adminUI.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    private void moMainUIKhongDangNhap() {
        dispose(); // ĐÓNG LOGINUI
        MainUI mainUI = new MainUI(null); // MỞ MAINUI KHÔNG ND
        mainUI.setVisible(true);
    }
}