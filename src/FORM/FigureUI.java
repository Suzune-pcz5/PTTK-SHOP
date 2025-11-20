package FORM;

import BLL.FigureBLL;
import BLL.NguoiDungBLL;
import DTO.FigureDTO;
import DTO.GioHangItemDTO;
import DTO.NguoiDungDTO;
import DTO.DonHangDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

public class FigureUI extends JFrame {
    private JTable tblDanhSach, tblGioHang;
    private JTextField txtSoLuong, txtMaKM, txtTenDangNhap, txtMatKhau, txtTenDangKy, txtMatKhauDangKy;
    private JTextField txtMinGia, txtMaxGia;
    private JTextField txtTenTimKiem;
    private JComboBox<String> cbLoai, cbKichThuoc;
    private List<FigureDTO> danhSachHienTai;
    private JLabel lblTongTien;
    private JTextArea txtKetQua;
    private FigureBLL bll = new FigureBLL();
    private NguoiDungBLL nguoiDungBLL = new NguoiDungBLL();
    private double phanTramGiam = 0;
    private NguoiDungDTO nguoiDungHienTai = null;
    private JDialog loginDialog;

    public FigureUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("🧸 Cửa Hàng Figure");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        add(createPanelTimKiem(), BorderLayout.NORTH);
        add(createPanelDanhSach(), BorderLayout.WEST);
        add(createPanelChucNang(), BorderLayout.CENTER);
        add(createPanelKetQua(), BorderLayout.SOUTH);

        taiDanhSach();
        capNhatGioHang();
        hienThiDangNhap();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createPanelTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Tìm Kiếm Figure"));
        panel.setBackground(new Color(245, 245, 220));

        panel.add(new JLabel("Tên:"));
        txtTenTimKiem = new JTextField(10); // Thêm ô tìm theo tên
        panel.add(txtTenTimKiem);
        
        panel.add(new JLabel("Loại:"));
        String[] loaiOptions = {"Tất cả", "Teddy", "Unicorn", "Panda", "Khác"};
        cbLoai = new JComboBox<>(loaiOptions);
        cbLoai.setPreferredSize(new Dimension(100, 25));
        panel.add(cbLoai);

        panel.add(new JLabel("Giá từ:"));
        txtMinGia = new JTextField(5);
        panel.add(txtMinGia);

        panel.add(new JLabel("đến:"));
        txtMaxGia = new JTextField(5);
        panel.add(txtMaxGia);

        panel.add(new JLabel("Kích thước:"));
        String[] kichThuocOptions = {"Tất cả", "Nhỏ", "Vừa", "Lớn"};
        cbKichThuoc = new JComboBox<>(kichThuocOptions);
        cbKichThuoc.setPreferredSize(new Dimension(100, 25));
        panel.add(cbKichThuoc);

        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(new Color(0, 191, 255));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.addActionListener(e -> timKiemNangCao());
        panel.add(btnTimKiem);

        return panel;
    }

    private void timKiemNangCao() {
        String ten = txtTenTimKiem.getText().trim();
        if (ten.isEmpty()) {
            ten = null;
        }
        
        String loai = (String) cbLoai.getSelectedItem();
        if ("Tất cả".equals(loai)) loai = null;

        Double minGia = null;
        try {
            if (!txtMinGia.getText().isEmpty()) minGia = Double.parseDouble(txtMinGia.getText());
        } catch (NumberFormatException ignored) {}

        Double maxGia = null;
        try {
            if (!txtMaxGia.getText().isEmpty()) maxGia = Double.parseDouble(txtMaxGia.getText());
        } catch (NumberFormatException ignored) {}

        String kichThuoc = (String) cbKichThuoc.getSelectedItem();
        if ("Tất cả".equals(kichThuoc)) kichThuoc = null;

        this.danhSachHienTai = bll.timKiemNangCao(ten, loai, minGia, maxGia, kichThuoc, 0); 
        capNhatBangDanhSach(this.danhSachHienTai);
    }

    private JPanel createPanelDanhSach() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📋 Danh Sách Figure"));
        panel.setPreferredSize(new Dimension(400, 0));
        panel.setBackground(new Color(240, 255, 240));

        tblDanhSach = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblDanhSach);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPanelChucNang() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🛒 Giỏ Hàng"));
        panel.setBackground(new Color(255, 228, 225));

        tblGioHang = new JTable();
        JScrollPane scrollGioHang = new JScrollPane(tblGioHang);
        panel.add(scrollGioHang, BorderLayout.CENTER);

        JPanel panelTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongTien = new JLabel("Tổng tiền: 0 VND");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        panelTongTien.add(lblTongTien);
        panel.add(panelTongTien, BorderLayout.SOUTH);

        JPanel panelChucNang = new JPanel(new GridLayout(4, 1, 5, 5));
        panelChucNang.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnThemGio = new JButton("Thêm vào giỏ");
        btnThemGio.setBackground(new Color(50, 205, 50));
        btnThemGio.setForeground(Color.WHITE);
        btnThemGio.addActionListener(e -> themVaoGio());
        panelChucNang.add(btnThemGio);

        JButton btnXoaGio = new JButton("Xóa khỏi giỏ");
        btnXoaGio.setBackground(new Color(255, 69, 0));
        btnXoaGio.setForeground(Color.WHITE);
        btnXoaGio.addActionListener(e -> xoaKhoiGio());
        panelChucNang.add(btnXoaGio);

        JButton btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setBackground(new Color(0, 128, 0));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.addActionListener(e -> thanhToan());
        panelChucNang.add(btnThanhToan);

        panel.add(panelChucNang, BorderLayout.EAST);

        JPanel panelMaKM = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMaKM.add(new JLabel("Mã KM:"));
        txtMaKM = new JTextField(10);
        panelMaKM.add(txtMaKM);
        JButton btnApDung = new JButton("Áp dụng");
        btnApDung.addActionListener(e -> apDungMaKM());
        panelMaKM.add(btnApDung);
        panel.add(panelMaKM, BorderLayout.NORTH);

        return panel;
    }

    private void apDungMaKM() {
        String ma = txtMaKM.getText().trim();
        if (!ma.isEmpty()) {
            double giam = bll.kiemTraMaKhuyenMai(ma);
            if (giam > 0) {
                phanTramGiam = giam;
                JOptionPane.showMessageDialog(this, "Áp dụng thành công! Giảm " + giam + "%");
                capNhatTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Mã khuyến mãi không hợp lệ hoặc hết hạn.");
            }
        }
    }

    private JPanel createPanelKetQua() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📝 Kết Quả"));
        panel.setPreferredSize(new Dimension(0, 150));
        panel.setBackground(new Color(230, 230, 250));

        txtKetQua = new JTextArea();
        txtKetQua.setEditable(false);
        JScrollPane scrollKetQua = new JScrollPane(txtKetQua);
        panel.add(scrollKetQua, BorderLayout.CENTER);

        return panel;
    }

    private void taiDanhSach() {
        this.danhSachHienTai = bll.layTatCa();
        capNhatBangDanhSach(this.danhSachHienTai);
    }

    private void capNhatBangDanhSach(List<FigureDTO> danhSach) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên", "Loại", "Giá", "Kích thước", "Số lượng", "Xem"}, 0){
        @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho phép sửa cột "Xem" (cột 6)
            }
        };
        
        for (FigureDTO gb : danhSach) {
            model.addRow(new Object[]{gb.getId(), gb.getTen(), gb.getLoai(), gb.getGia(), gb.getKichThuoc(), gb.getSoLuong(), "Xem"});
        }
        tblDanhSach.setModel(model);

        TableColumn buttonColumn = tblDanhSach.getColumnModel().getColumn(6);
        buttonColumn.setCellRenderer(new ButtonRenderer());
        buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        
        tblDanhSach.revalidate();
        tblDanhSach.repaint();
        
    }

    private void hienThiChiTiet(FigureDTO gb) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(gb.getId()).append("\n");
        sb.append("Tên: ").append(gb.getTen()).append("\n");
        sb.append("Loại: ").append(gb.getLoai()).append("\n");
        sb.append("Giá: ").append(gb.getGia()).append(" VND\n");
        sb.append("Kích thước: ").append(gb.getKichThuoc()).append("\n");
        sb.append("Số lượng: ").append(gb.getSoLuong()).append("\n");
        sb.append("Mô tả: ").append(gb.getMoTa()).append("\n");
        txtKetQua.setText(sb.toString());
    }

    private void capNhatGioHang() {
        DefaultTableModel modelGio = new DefaultTableModel(new String[]{"ID", "Tên", "Số lượng", "Thành tiền"}, 0);
        for (GioHangItemDTO item : bll.getGioHang()) {
            modelGio.addRow(new Object[]{item.getFigure().getId(), item.getFigure().getTen(), item.getSoLuong(), item.getThanhTien()});
        }
        tblGioHang.setModel(modelGio);
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        double tong = bll.tinhTongTien();
        double tongSauGiam = tong * (1 - phanTramGiam / 100);
        lblTongTien.setText(String.format("Tổng tiền: %.2f VND (Giảm %.0f%%)", tongSauGiam, phanTramGiam));
    }

    private void themVaoGio() {
        System.out.println("--- 1. Bấm nút Thêm ---");
        int row = tblDanhSach.getSelectedRow();
        
        if (row >= 0) {
            int id = this.danhSachHienTai.get(row).getId();
            System.out.println("--- 2. Lấy được ID: " + id + " tại hàng: " + row + " ---");
            
            String input = JOptionPane.showInputDialog(this, "Nhập số lượng:");
            try {
                int soLuong = Integer.parseInt(input);
                if (soLuong <= 0) {
                    System.out.println("--- LỖI: Số lượng <= 0 ---"); // DEBUG
                    return;
                }
                
                System.out.println("--- 3. Gọi BLL.themVaoGio với ID: " + id + ", SL: " + soLuong + " ---"); // DEBUG
                if (bll.themVaoGio(id, soLuong)) {
                    
                    System.out.println("--- 4. BLL trả về TRUE. Bắt đầu cập nhật UI ---"); // DEBUG
                    capNhatGioHang();
                    boolean daTruSoLuong = false; // Biến kiểm tra
                    for (FigureDTO fig : this.danhSachHienTai) {
                        if (fig.getId() == id) {
                            int slTruoc = fig.getSoLuong();
                            fig.setSoLuong(slTruoc - soLuong);
                            System.out.println("--- 5. Đã trừ SL tạm. ID: " + id + ". Tồn kho từ " + slTruoc + " -> " + fig.getSoLuong() + " ---"); // DEBUG
                            daTruSoLuong = true;
                            break; 
                        }
                    }
                    
                    if (!daTruSoLuong) {
                         System.out.println("--- LỖI: Không tìm thấy ID " + id + " trong danhSachHienTai ---"); // DEBUG
                    }

                    System.out.println("--- 6. Gọi capNhatBangDanhSach ---"); // DEBUG
                    capNhatBangDanhSach(this.danhSachHienTai);
                    
                } else {
                    System.out.println("--- LỖI: BLL trả về FALSE (Không đủ hàng) ---"); // DEBUG
                    JOptionPane.showMessageDialog(this, "Số lượng không đủ hoặc lỗi.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("--- LỖI: Nhập số lượng không hợp lệ ---"); // DEBUG
            }
        } else {
            System.out.println("--- LỖI: Chưa chọn hàng (row = -1) ---"); // DEBUG
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm.");
        }
    }

    private void xoaKhoiGio() {
        int row = tblGioHang.getSelectedRow();
        if (row >= 0) {
            int id = (int) tblGioHang.getValueAt(row, 0);
            int soLuongTraLai = 0;
            for(GioHangItemDTO item : bll.getGioHang()) {
                if(item.getFigureId() == id) {
                    soLuongTraLai = item.getSoLuong();
                    break;
                }
            }
            if (bll.xoaKhoiGio(id)) {
                capNhatGioHang();
                for (FigureDTO fig : this.danhSachHienTai) {
                    if (fig.getId() == id) {
                        fig.setSoLuong(fig.getSoLuong() + soLuongTraLai);
                        break; // Thoát vòng lặp
                    }
                }
                
                capNhatBangDanhSach(this.danhSachHienTai);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa khỏi giỏ.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ.");
        }
    }

    private void thanhToan() {
        if (nguoiDungHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để thanh toán.");
            return;
        }

        // Lấy giỏ hàng từ BLL để kiểm tra
        List<GioHangItemDTO> gioHangHienTai = bll.getGioHang();
        if (gioHangHienTai == null || gioHangHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng. Không thể thanh toán.");
            return;
        }

        int maNhanVien = nguoiDungHienTai.getMaNguoiDung();
        String phuongThucTT = "TienMat";
        String maKhuyenMai = txtMaKM.getText().trim();
        if (phanTramGiam == 0 || maKhuyenMai.isEmpty()) {
            maKhuyenMai = null;
        }

        // GỌI HÀM BLL VỚI ĐÚNG THAM SỐ
        DonHangDTO donHang = bll.thanhToan(maNhanVien, phuongThucTT, maKhuyenMai);

        if (donHang != null) {
            
            // 1. Hiển thị thông báo (Bạn đã có)
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            
            // 2. Cập nhật ô Kết quả/Hóa đơn
            StringBuilder sb = new StringBuilder("--- HÓA ĐƠN ĐÃ LƯU ---\n");
            sb.append("Mã đơn: ").append(donHang.getMaDonHang()).append("\n");
            sb.append("Ngày đặt: ").append(donHang.getNgayDat().toString()).append("\n");
            sb.append("Tổng tiền: ").append(String.format("%,.0f VND", donHang.getTongTien())).append("\n");
            sb.append("Trạng thái: Đã thanh toán.\n");
            txtKetQua.setText(sb.toString());

            // 3. Cập nhật lại giao diện (để xóa giỏ hàng và tải lại số lượng)
            capNhatGioHang(); // Giỏ hàng (FigureBLL đã tự xóa, giờ UI cập nhật)
            taiDanhSach();    // Tải lại danh sách figure (với số lượng mới)
            
            // 4. Reset các trường
            phanTramGiam = 0;
            txtMaKM.setText("");
            // capNhatTongTien() đã được gọi bên trong capNhatGioHang()

        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại.");
        }
    }

    private void hienThiDangNhap() {
        loginDialog = new JDialog(this, "Đăng Nhập / Đăng Ký", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLayout(new GridLayout(5, 2, 5, 5));
        loginDialog.setLocationRelativeTo(this);

        loginDialog.add(new JLabel("Tên đăng nhập:"));
        txtTenDangNhap = new JTextField();
        loginDialog.add(txtTenDangNhap);

        loginDialog.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JPasswordField();
        loginDialog.add(txtMatKhau);

        JButton btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.addActionListener(e -> dangNhap());
        loginDialog.add(btnDangNhap);

        JButton btnDangKy = new JButton("Đăng ký");
        btnDangKy.addActionListener(e -> hienThiDangKy());
        loginDialog.add(btnDangKy);

        loginDialog.setVisible(true);
    }

    private void dangNhap() {
        String ten = txtTenDangNhap.getText();
        String mk = txtMatKhau.getText();
        NguoiDungDTO nd = nguoiDungBLL.dangNhap(ten, mk);
        if (nd != null) {
            nguoiDungHienTai = nd;
            loginDialog.dispose();
            if ("NhanVien".equals(nd.getVaiTro())) {
                hienThiGiaoDienAdmin();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Đăng nhập thất bại.");
        }
    }

    private void hienThiDangKy() {
        JDialog dangKyDialog = new JDialog(this, "Đăng Ký", true);
        dangKyDialog.setSize(300, 150);
        dangKyDialog.setLayout(new GridLayout(3, 2, 5, 5));

        dangKyDialog.add(new JLabel("Tên đăng nhập:"));
        txtTenDangKy = new JTextField();
        dangKyDialog.add(txtTenDangKy);

        dangKyDialog.add(new JLabel("Mật khẩu:"));
        txtMatKhauDangKy = new JPasswordField();
        dangKyDialog.add(txtMatKhauDangKy);

        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.addActionListener(e -> dangKy());
        dangKyDialog.add(btnXacNhan);

        dangKyDialog.setLocationRelativeTo(this);
        dangKyDialog.setVisible(true);
    }

    private void dangKy() {
        String ten = txtTenDangKy.getText();
        String mk = txtMatKhauDangKy.getText();
        NguoiDungDTO nd = new NguoiDungDTO(0, ten, mk, "KhachHang");
        if (nguoiDungBLL.dangKy(nd)) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            txtTenDangNhap.setText(ten);
            txtMatKhau.setText(mk);
           
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại. Tên đăng nhập đã tồn tại.");
        }
    }

    private void hienThiGiaoDienAdmin() {
        // Code cho giao diện admin (AdminUI) ở đây
        // Ví dụ: new AdminUI().setVisible(true);
        // this.dispose(); // Đóng giao diện khách hàng
    }

    private class ButtonRenderer implements TableCellRenderer {
        private final JButton button = new JButton("Xem");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Xem");
            button.setBackground(new Color(135, 206, 250));
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Lấy FigureDTO từ danh sách TẠI HÀNG ĐANG BẤM
                    // Bằng cách lấy ID từ model
                    try {
                        int id = (int) tblDanhSach.getModel().getValueAt(currentRow, 0);
                        
                        // Tìm FigureDTO trong danhSachHienTai
                        FigureDTO currentFigure = null;
                        for(FigureDTO fig : danhSachHienTai) {
                            if(fig.getId() == id) {
                                currentFigure = fig;
                                break;
                            }
                        }
                        
                        if (currentFigure != null) {
                            hienThiChiTiet(currentFigure);
                        } else {
                            System.out.println("Không tìm thấy FigureDTO cho id: " + id);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    fireEditingStopped(); // Kết thúc quá trình chỉnh sửa
                }
            });
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Lưu lại hàng đang được chọn
            this.currentRow = row; 
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            // Trả về chính giá trị "Xem" (hoặc null)
            return "Xem";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FigureUI());
    }
}