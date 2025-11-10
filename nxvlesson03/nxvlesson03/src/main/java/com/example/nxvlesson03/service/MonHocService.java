package com.example.nxvlesson03.service;

import com.example.nxvlesson03.entity.MonHoc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MonHocService {
    private List<MonHoc> listMonHoc = new ArrayList<>();

    public MonHocService() {
        listMonHoc.addAll(Arrays.asList(
                new MonHoc("Java", "Lập trình Java", 40),
                new MonHoc("C#", "Lập trình C# .NET", 40),
                new MonHoc("SQL", "Cơ sở dữ liệu SQL Server", 30),
                new MonHoc("WEB", "Thiết kế web", 35),
                new MonHoc("PM", "Quản lý dự án", 20)
        ));
    }

    // Lấy toàn bộ danh sách
    public List<MonHoc> getAllMonHoc() {
        return listMonHoc;
    }

    // Lấy theo mamh
    public MonHoc getMonHocByMaMh(String mamh) {
        return listMonHoc.stream()
                .filter(mh -> mh.getMamh().equals(mamh))
                .findFirst().orElse(null);
    }

    // Thêm mới
    public MonHoc addMonHoc(MonHoc monHoc) {
        listMonHoc.add(monHoc);
        return monHoc;
    }

    // Sửa đổi
    public MonHoc updateMonHoc(String mamh, MonHoc mhDetails) {
        MonHoc monHoc = getMonHocByMaMh(mamh);
        if (monHoc != null) {
            monHoc.setTenmh(mhDetails.getTenmh());
            monHoc.setSotiet(mhDetails.getSotiet());
            return monHoc;
        }
        return null;
    }

    // Xóa
    public boolean deleteMonHoc(String mamh) {
        MonHoc monHoc = getMonHocByMaMh(mamh);
        if (monHoc != null) {
            return listMonHoc.remove(monHoc);
        }
        return false;
    }
}