package com.altura.altura.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.altura.altura.Model.User;
import com.altura.altura.Repository.UserRepository;
import com.altura.altura.Exception.UnauthorizedException;
import com.altura.altura.Config.JwtUtil;
import com.altura.altura.DTO.LoginResponse;
import com.altura.altura.DTO.RegisterRequest;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Email atau password salah");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        return new LoginResponse(token, user.getRole().toString(), user.getFullname());
    }

    public LoginResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        User user = new User();
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password sebelum disimpan
        user.setPhone(request.getPhone());
        user.setRole(User.Role.USER);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        return new LoginResponse(token, user.getRole().toString(), user.getFullname());
    }
}