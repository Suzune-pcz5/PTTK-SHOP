package pttk.shop.main;

import FORM.LoginUI;  // Form đăng nhập (nếu có)
import FORM.MainUI;   // Form chính (nếu có)
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PTTKSHOPMain {
    public static void main(String[] args) {
        // === THÊM ĐOẠN NÀY ĐỂ KÍCH HOẠT NIMBUS ===
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nếu Nimbus không có, dùng L&F mặc định
             System.out.println("Không thể thiết lập Nimbus L&F: " + e.getMessage());
        }
        // === KẾT THÚC THÊM ===
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Nếu có form đăng nhập thì chạy form đăng nhập
                    LoginUI login = new LoginUI();
                    login.setVisible(true);

                    // Nếu không có FrmLogin thì mở thẳng form chính:
                    // FrmMain main = new FrmMain();
                    // main.setVisible(true);
                } catch (Exception e) {
                    System.out.println("Lỗi khi chạy ứng dụng: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
