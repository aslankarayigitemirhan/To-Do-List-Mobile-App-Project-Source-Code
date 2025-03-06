package com.software.controller;

import com.software.DTO.RegisterRequest;
import com.software.DTO.UserDTO;
import com.software.model.User;
import com.software.service.UserService;
import com.software.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Kullanıcı girişi (login işlemi)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User user) {
        try {
            String token = userService.loginUser(user.getUsername(), user.getPassword());

            // Token ve expiration sürelerini kapsayan LoginResponse döndürülüyor.
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken("Bearer " + token);
            loginResponse.setExpiresIn(jwtUtil.getEXPIRATION_TIME());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Kullanıcı kaydı (register işlemi)
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequest user) {
        try {
            UserDTO createdUser = userService.registerUser(user.getName(), user.getSurname(),
                    user.getEmail(), user.getUsername(),
                    user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Kullanıcıyı silme işlemi
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            String result = userService.deleteUser(username);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // E-posta güncelleme işlemi
    @PutMapping("/update-email/{username}")
    public ResponseEntity<UserDTO> updateEmail(@PathVariable String username, @RequestBody String newEmail) {
        try {
            UserDTO updatedUser = userService.updateEmail(username, newEmail);
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Şifre güncelleme işlemi
    @PutMapping("/update-password/{username}")
    public ResponseEntity<String> updatePassword(@PathVariable String username,
                                                 @RequestParam String currentPassword,
                                                 @RequestParam String newPassword) {
        try {
            String result = userService.updatePassword(username, currentPassword, newPassword);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // Kullanıcı adı güncelleme işlemi
    @PutMapping("/update-username/{oldUsername}")
    public ResponseEntity<UserDTO> updateUsername(@PathVariable String oldUsername, @RequestBody String newUsername) {
        try {
            UserDTO updatedUser = userService.updateUsername(oldUsername, newUsername);
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Ad ve soyad güncelleme işlemi
    @PutMapping("/update-name/{username}")
    public ResponseEntity<UserDTO> updateNameAndSurname(@PathVariable String username,
                                                        @RequestBody UserDTO updatedInfo) {
        try {
            UserDTO updatedUser = userService.updateNameAndSurname(username,
                    updatedInfo.getName(),
                    updatedInfo.getSurname());
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
