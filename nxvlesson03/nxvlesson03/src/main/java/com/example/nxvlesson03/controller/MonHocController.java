package com.example.nxvlesson03.controller;

import com.example.nxvlesson03.entity.MonHoc;
import com.example.nxvlesson03.service.MonHocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    // API Lấy toàn bộ danh sách
    @GetMapping("/monhoc-list")
    public List<MonHoc> getAllMonHoc() {
        return monHocService.getAllMonHoc();
    }

    // API Lấy theo mamh
    @GetMapping("/monhoc/{mamh}")
    public MonHoc getMonHocById(@PathVariable String mamh) {
        return monHocService.getMonHocByMaMh(mamh);
    }

    // API Thêm mới
    @PostMapping("/monhoc-add")
    public MonHoc addMonHoc(@RequestBody MonHoc monHoc) {
        return monHocService.addMonHoc(monHoc);
    }

    // API Sửa
    @PutMapping("/monhoc/{mamh}")
    public MonHoc updateMonHoc(@PathVariable String mamh, @RequestBody MonHoc monHoc) {
        return monHocService.updateMonHoc(mamh, monHoc);
    }

    // API Xóa
    @DeleteMapping("/monhoc/{mamh}")
    public boolean deleteMonHoc(@PathVariable String mamh) {
        return monHocService.deleteMonHoc(mamh);
    }
}