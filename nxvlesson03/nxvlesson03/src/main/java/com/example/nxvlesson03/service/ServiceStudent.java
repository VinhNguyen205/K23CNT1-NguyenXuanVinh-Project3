package com.example.nxvlesson03.service;

import com.example.nxvlesson03.entity.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class: StudentService
 * <p>Lớp dịch vụ thực hiện các chức năng thao tác với List Object Student</p>
 *
 * @author Chung Trinh (theo tài liệu)
 * @version 1.0
 */
@Service
public class ServiceStudent {
    List<Student> students = new ArrayList<>();

    public ServiceStudent() {
        // --- LƯU Ý ---
        // Dữ liệu mẫu trong file PDF của bạn không khớp với cấu trúc
        // class Student (id, name, age, gender, address, phone, email)
        // Tôi sẽ tạo dữ liệu mẫu bằng cách dùng setters để đảm bảo code chạy được.

        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("Nguyễn Văn A");
        s1.setAge(20);
        s1.setGender("Nam");
        s1.setAddress("Hà Nội");
        s1.setPhone("0978611888");
        s1.setEmail("nguyenvana@example.com");

        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("Trần Thị B");
        s2.setAge(22);
        s2.setGender("Nữ");
        s2.setAddress("Đà Nẵng");
        s2.setPhone("0978611889");
        s2.setEmail("tranthib@example.com");

        Student s3 = new Student();
        s3.setId(3L);
        s3.setName("Lê Văn C");
        s3.setAge(25);
        s3.setGender("Nam");
        s3.setAddress("TP. Hồ Chí Minh");
        s3.setPhone("0978611887");
        s3.setEmail("levanc@example.com");

        // Thêm vào danh sách
        students.addAll(Arrays.asList(s1, s2, s3));
    }

    /**
     * Lấy toàn bộ danh sách sinh viên
     * @return List<Student>
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Lấy sinh viên theo id
     * @param id
     * @return Student (hoặc null nếu không tìm thấy)
     */
    public Student getStudent(Long id) {
        return students.stream()
                .filter(student -> student.getId().equals(id))
                .findFirst().orElse(null);
    }

    /**
     * Thêm mới một sinh viên
     * @param student
     * @return Student (sinh viên đã được thêm)
     */
    public Student addStudent(Student student) {
        students.add(student);
        return student;
    }

    /**
     * Cập nhật thông tin sinh viên
     * @param id
     * @param student
     * @return Student (sinh viên đã cập nhật)
     */
    public Student updateStudent(Long id, Student student) {
        Student check = getStudent(id);
        if (check == null) {
            return null; // Không tìm thấy sinh viên để cập nhật
        }

        students.forEach(item -> {
            // Sửa lỗi logic: Dùng .equals() để so sánh Long, không dùng ==
            if (item.getId().equals(id)) {
                item.setName(student.getName());
                item.setAddress(student.getAddress());
                item.setEmail(student.getEmail());
                item.setPhone(student.getPhone());
                item.setAge(student.getAge());
                item.setGender(student.getGender());
            }
        });
        return student;
    }

    /**
     * Xóa thông tin sinh viên
     * @param id
     * @return boolean (true nếu xóa thành công)
     */
    public boolean deleteStudent(Long id) {
        Student check = getStudent(id);
        // Phương thức .remove() của List sẽ trả về true nếu xóa thành công
        return students.remove(check);
    }
}