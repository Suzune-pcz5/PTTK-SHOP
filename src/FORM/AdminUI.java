// FORM/AdminUI.java
package FORM;

import DTO.NguoiDungDTO;
import javax.swing.JFrame;

public class AdminUI extends JFrame {
    public AdminUI(NguoiDungDTO nd) {
        setTitle("Giao Diện Quản Trị - " + nd.getTenDangNhap());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new javax.swing.JLabel("Chào mừng đến trang Admin!", javax.swing.SwingConstants.CENTER));
    }
}