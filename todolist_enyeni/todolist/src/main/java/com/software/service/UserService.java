package com.software.service;

import com.software.DTO.UserDTO;
import com.software.model.User;
import com.software.repository.UserRepository;
import com.software.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Bağımlılık döngüsü kırıldı

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    // ✅ Kullanıcıyı username ile al
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Kullanıcı girişi (Login)
    public String loginUser(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtil.generateToken(username);
    }

    // ✅ Kullanıcı kaydı (Register)
    public UserDTO registerUser(String name, String surname, String email, String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        System.out.println("Şu anda şifreleniyor...");
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Şifre : " + encodedPassword);
        User user = new User(name, surname, email, username, encodedPassword);
        try {
            User savedUser = this.userRepository.save(user);
            return new UserDTO(savedUser.getName(), savedUser.getSurname(), savedUser.getEmail(), savedUser.getUsername());
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            throw new RuntimeException("Error during registration");
        }
    }

    // ✅ Kullanıcıyı sil
    public String deleteUser(String username) {
        User user = getUserByUsername(username);
        userRepository.delete(user);
        return "User " + username + " deleted successfully.";
    }

    // ✅ E-posta güncelleme
    public UserDTO updateEmail(String username, String newEmail) {
        User user = getUserByUsername(username);
        user.setEmail(newEmail);
        userRepository.save(user);
        return new UserDTO(user.getName(), user.getSurname(), newEmail, user.getUsername());
    }

    // ✅ Şifre güncelleme (Mevcut şifre doğrulama ile)
    public String updatePassword(String username, String currentPassword, String newPassword) {
        User user = getUserByUsername(username);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password updated successfully";
    }

    // ✅ Kullanıcı adı güncelleme
    public UserDTO updateUsername(String oldUsername, String newUsername) {
        User user = getUserByUsername(oldUsername);
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("New username is already taken");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
        return new UserDTO(user.getName(), user.getSurname(), user.getEmail(), newUsername);
    }

    // ✅ Ad ve soyad güncelleme
    public UserDTO updateNameAndSurname(String username, String newName, String newSurname) {
        User user = getUserByUsername(username);
        user.setName(newName);
        user.setSurname(newSurname);
        userRepository.save(user);
        return new UserDTO(newName, newSurname, user.getEmail(), user.getUsername());
    }
}
