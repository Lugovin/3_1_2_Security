package org.example._3_1_2_security.service;


import jakarta.transaction.Transactional;
import org.example._3_1_2_security.Entity.Role;
import org.example._3_1_2_security.Entity.User;
import org.example._3_1_2_security.repository.RoleRepo;
import org.example._3_1_2_security.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    private UserRepo userRepository;
    private RoleRepo roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepository, RoleRepo roleRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByNameWithRoles(username);
    }

    @Transactional
    public boolean addUser(User user, String roleName) {
        Role newRole = new Role();
        User hasUser = userRepository.findByNameWithRoles(user.getUsername());
        if (!(hasUser == null)) {
            return false;
        }
        for (Role role : roleRepository.findAll()) {
            if (role.getRoleName().equals(roleName)) {
                newRole = role;
                break;
            }
        }
        if (newRole != null) {
            user.setRole(newRole);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void editUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
