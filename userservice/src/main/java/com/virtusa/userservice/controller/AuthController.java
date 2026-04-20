package com.virtusa.userservice.controller;

import com.virtusa.userservice.dto.AuthRequest;
import com.virtusa.userservice.dto.UserDTO;
import com.virtusa.userservice.repository.UserRepository;
//import com.virtusa.userservice.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    @Autowired
//    private JWTUtil jwtUtil;
    @Autowired
    private UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        Optional<UserDTO> user = userRepo.findByUsername(req.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(req.getPassword())) {
            return ResponseEntity.ok("Login Successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
    }
}
