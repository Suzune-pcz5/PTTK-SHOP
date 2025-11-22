package BLL;

import DAL.PhieuDieuChinhDAL;

public class PhieuDieuChinhBLL {
    private PhieuDieuChinhDAL dal = new PhieuDieuChinhDAL();

    public boolean taoPhieuKiemKe(int idSP, int slHeThong, int slThucTe, int maNV, String lyDo) {
        int chenhLech = slThucTe - slHeThong;
        
        // Nếu không có chênh lệch thì không cần lưu (tùy logic, nhưng thường là vậy)
        if (chenhLech == 0) return true; 

        return dal.luuPhieuDieuChinh(idSP, slHeThong, slThucTe, chenhLech, maNV, lyDo);
    }
}