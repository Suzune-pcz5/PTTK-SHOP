package BLL;

import DAL.NhaCungCapDAL;
import DTO.NhaCungCapDTO;
import java.util.List;

public class NhaCungCapBLL {
    private NhaCungCapDAL dal = new NhaCungCapDAL();

    public List<NhaCungCapDTO> getListNhaCungCap() {
        return dal.layDanhSachNCC();
    }
}