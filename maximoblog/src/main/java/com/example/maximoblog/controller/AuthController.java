package com.example.maximoblog.controller;

import com.example.maximoblog.dto.ForgotPasswordRequest;
import com.example.maximoblog.dto.LoginRequest;
import com.example.maximoblog.dto.RegisterRequest;
import com.example.maximoblog.dto.ResetPasswordRequest;
import com.example.maximoblog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
}
