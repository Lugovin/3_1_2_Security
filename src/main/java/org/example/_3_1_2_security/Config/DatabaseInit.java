package org.example._3_1_2_security.Config;


import jakarta.annotation.PostConstruct;

import org.example._3_1_2_security.Entity.Role;
import org.example._3_1_2_security.Entity.User;
import org.example._3_1_2_security.repository.RoleRepo;
import org.example._3_1_2_security.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



@Component
public class DatabaseInit {

    public Role adminRole = new Role(1L, "ADMIN");
    public Role userRole = new Role(2L, "USER");

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInit(UserRepo userRepository, RoleRepo roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        User user1 = new User("Bill", "1111", "Gates", "faja@google.com");
        user1.setPassword(passwordEncoder.encode(user1.getPassword()));
        User user2 = new User("John", "0000", "Smith", "ddd@mail.ru");
        user2.setPassword(passwordEncoder.encode(user2.getPassword()));
        User user3 = new User("Николай", "9999", "Луговин", "lugovin@gmail.com");
        user3.setPassword(passwordEncoder.encode(user3.getPassword()));

        user1.setRole(adminRole);
        user2.setRole(userRole);
        user3.setRole(userRole);
        user3.setRole(adminRole);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        System.out.println("База данных заполнена начальными данными");
    }
}
