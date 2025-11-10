package com.example.nxvlesson03.service;

import com.example.nxvlesson03.entity.Khoa;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class KhoaService {
    private List<Khoa> listKhoa = new ArrayList<>();

    public KhoaService() {
        // Tạo 5 phần tử mẫu
        listKhoa.addAll(Arrays.asList(
                new Khoa("CNTT", "Công nghệ thông tin"),
                new Khoa("KT", "Kế toán"),
                new Khoa("QTKD", "Quản trị kinh doanh"),
                new Khoa("NN", "Ngôn ngữ Anh"),
                new Khoa("CK", "Cơ khí")
        ));
    }

    // Lấy toàn bộ danh sách
    public List<Khoa> getAllKhoa() {
        return listKhoa;
    }

    // Lấy theo makh
    public Khoa getKhoaByMaKh(String makh) {
        return listKhoa.stream()
                .filter(khoa -> khoa.getMakh().equals(makh))
                .findFirst().orElse(null);
    }

    // Thêm mới
    public Khoa addKhoa(Khoa khoa) {
        listKhoa.add(khoa);
        return khoa;
    }

    // Sửa đổi
    public Khoa updateKhoa(String makh, Khoa khoaDetails) {
        Khoa khoa = getKhoaByMaKh(makh);
        if (khoa != null) {
            khoa.setTenkh(khoaDetails.getTenkh());
            // (Nếu có các trường khác thì set ở đây)
            return khoa;
        }
        return null;
    }

    // Xóa
    public boolean deleteKhoa(String makh) {
        Khoa khoa = getKhoaByMaKh(makh);
        if (khoa != null) {
            return listKhoa.remove(khoa);
        }
        return false;
    }
}