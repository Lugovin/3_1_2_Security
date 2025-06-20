package org.example._3_1_2_security.service;


import org.example._3_1_2_security.model.User;
import org.example._3_1_2_security.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    private UserRepo userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByNameWithRoles(username);
    }

    @Transactional
    public boolean addUser(User user) {
        User hasUser = userRepository.findUserByNameWithRoles(user.getUsername());
        if (!(hasUser == null)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void editUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsersWithRoles() {
        return userRepository.findAllUsersWithRoles();
    }

    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public User findUserByNameWithRoles(String name) {
        return userRepository.findUserByNameWithRoles(name);
    }
}
