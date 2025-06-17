package org.example._3_1_2_security.Controllers;


import jakarta.servlet.http.HttpServletRequest;

import org.example._3_1_2_security.Entity.User;
import org.example._3_1_2_security.repository.UserRepo;
import org.example._3_1_2_security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MainController {


    private UserRepo userRepository;
    private UserService userService;

    @Autowired
    public MainController(UserService userService, UserRepo userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping({"/"})
    public String welcome() {
        return "welcome";
    }

    @GetMapping({"/admin"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public String usersList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("message", authentication.getName());
        return "usersList";
    }

    @GetMapping("/admin/addUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showAddUserPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "addUser";
    }

    @PostMapping("/admin/addUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String saveUser(Model model, @ModelAttribute("user") User user, @RequestParam("roleString") String roleString) {
        System.out.println("Добавляем юзера");
        if (user.getName() != null && !user.getName().isEmpty() //
                && user.getLastName() != null && !user.getLastName().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && userService.addUser(user, roleString)) {
            return "redirect:/admin";
        }
        model.addAttribute("errorMessage", "Проверьте, чтобы все поля были заполнены и верно указана роль!");
        return "addUser";
    }

    @GetMapping("/admin/deleteUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUser(@RequestParam Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/editUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editUserForm(Model model, @RequestParam Long id) {
        Optional<User> oldUser = userRepository.findById(id);
        model.addAttribute("user", oldUser);
        return "editUser";
    }

    @PostMapping("/admin/editUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editUser(Model model, @ModelAttribute("user") User editUser) {
        Optional<User> oldUser = userRepository.findById(editUser.getId());
        if (oldUser.isPresent()) {
            oldUser.get().setName(editUser.getName());
            oldUser.get().setLastName(editUser.getLastName());
            oldUser.get().setEmail(editUser.getEmail());
            oldUser.get().setPassword(editUser.getPassword());
        }
        if (editUser.getName() != null && !editUser.getName().isEmpty() //
                && editUser.getLastName() != null && !editUser.getLastName().isEmpty()
                && editUser.getPassword() != null && !editUser.getPassword().isEmpty()
                && editUser.getEmail() != null && !editUser.getEmail().isEmpty()) {
            userService.editUser(oldUser.get());
            return "redirect:/admin";
        }
        model.addAttribute("errorMessage", "ВСЕ поля должны быть заполнены!");
        return "editUser";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public String showUserPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findByName(authentication.getName());
        model.addAttribute("user", user.get());
        model.addAttribute("message", authentication.getName());
        return "user";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            request.getSession().invalidate();
        }
        return "redirect:/";
    }
}
