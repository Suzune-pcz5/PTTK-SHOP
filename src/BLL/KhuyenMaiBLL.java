    package BLL;

    import DAL.KhuyenMaiDAL;
    import DTO.KhuyenMaiDTO;

    import java.util.List;

    public class KhuyenMaiBLL {
        private KhuyenMaiDAL dal = new KhuyenMaiDAL();

        public List<KhuyenMaiDTO> layTatCa() {
            return dal.layTatCa();
        }

        public boolean themKhuyenMai(KhuyenMaiDTO km) {
            return dal.themKhuyenMai(km);
        }

        public boolean xoaKhuyenMai(String ma) {
            return dal.xoaKhuyenMai(ma);
        }
    }