package com.shipkart.Shipkart.Controller;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.shipkart.Shipkart.DTO.UserDto;
import com.shipkart.Shipkart.Entity.User;
import com.shipkart.Shipkart.JwtUtil.JwtUtil;
import com.shipkart.Shipkart.Repo.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "User Authorization", description = "User Sign up, Sign in, Sign out")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    
    
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    
    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@RequestBody User user) {
        Optional<User> dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser.isPresent() && passwordEncoder.matches(user.getPassword(), dbUser.get().getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            UserDto userDto = new UserDto();
            userDto.setToken(token);
            userDto.setUsername(dbUser.get().getUsername());
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.status(401).body("Invalid username or password!");
    }
    

    
    @PostMapping("/signout")
    public ResponseEntity<String> signout(HttpServletRequest request) {
    	
    	String token = null;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extract the token part after "Bearer "
        }
    	jwtUtil.blacklistToken(token);
        return ResponseEntity.ok("User signed out successfully!");
    }
    
}

