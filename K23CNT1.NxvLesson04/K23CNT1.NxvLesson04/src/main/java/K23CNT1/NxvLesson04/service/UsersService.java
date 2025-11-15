package K23CNT1.NxvLesson04.service;

import K23CNT1.NxvLesson04.dto.UserDTO;
import K23CNT1.NxvLesson04.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {
    List<User> userList = new ArrayList<>();

    public UsersService() {
        // (Code constructor này sẽ chạy đúng
        //  nếu bạn đã thêm @AllArgsConstructor vào file User.java)
        userList.add(new User(1L, "john.doe", "pass1", "John Doe",
                LocalDate.parse("1990-01-01"), "john.doe@example.com", "1234567890", 34, true));
        userList.add(new User(2L, "jane.smith", "pass2", "Jane Smith",
                LocalDate.parse("1992-05-15"), "jane.smith@example.com", "0987654321", 32, false));
        userList.add(new User(3L, "alice.johnson", "pass3", "Alice Johnson",
                LocalDate.parse("1988-11-23"), "alice.johnson@example.com", "1122334455", 36, true));
        userList.add(new User(4L, "bob.brown", "pass4", "Bob Brown",
                LocalDate.parse("1985-03-10"), "bob.brown@example.com", "6677889900", 39, true));
        userList.add(new User(5L, "charlie.white", "pass5", "Charlie White",
                LocalDate.parse("1995-07-30"), "charlie.white@example.com", "5433221100", 29, false));
    }

    public List<User> findAll() {
        return userList;
    }

    public Boolean create(UserDTO userDTO) {
        try {
            User user = new User();

            // Gán ID tự động (logic đơn giản cho list)
            // (Trong hình là ...count() + 1, nhưng nó sẽ trả về long,
            //  chúng ta cần Long. Dùng (long)userList.size() + 1L sẽ an toàn hơn)
            user.setId((long) userList.size() + 1L);

            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());
            user.setFullName(userDTO.getFullName());
            // LƯU Ý: tài liệu bị thiếu setPhone và setAge
            user.setAge(userDTO.getAge()); // (Tôi tự bổ sung)
            user.setPhone(userDTO.getPhone()); // (Tôi tự bổ sung)
            user.setBirthDay(userDTO.getBirthDay());
            user.setStatus(userDTO.getStatus());
            userList.add(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}