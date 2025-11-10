package com.example.nxvlesson03.controller;

import com.example.nxvlesson03.entity.Khoa;
import com.example.nxvlesson03.service.KhoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KhoaController {

    @Autowired
    private KhoaService khoaService;

    // API Lấy toàn bộ danh sách
    @GetMapping("/khoa-list")
    public List<Khoa> getAllKhoa() {
        return khoaService.getAllKhoa();
    }

    // API Lấy theo makh
    @GetMapping("/khoa/{makh}")
    public Khoa getKhoaById(@PathVariable String makh) {
        return khoaService.getKhoaByMaKh(makh);
    }

    // API Thêm mới
    @PostMapping("/khoa-add")
    public Khoa addKhoa(@RequestBody Khoa khoa) {
        return khoaService.addKhoa(khoa);
    }

    // API Sửa
    @PutMapping("/khoa/{makh}")
    public Khoa updateKhoa(@PathVariable String makh, @RequestBody Khoa khoa) {
        return khoaService.updateKhoa(makh, khoa);
    }

    // API Xóa
    @DeleteMapping("/khoa/{makh}")
    public boolean deleteKhoa(@PathVariable String makh) {
        return khoaService.deleteKhoa(makh);
    }
}