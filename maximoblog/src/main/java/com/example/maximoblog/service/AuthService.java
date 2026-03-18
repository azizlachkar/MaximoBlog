package com.example.maximoblog.service;

import com.example.maximoblog.dto.*;
import com.example.maximoblog.entity.*;
import com.example.maximoblog.repository.PasswordResetTokenRepository;
import com.example.maximoblog.repository.UserRepository;
import com.example.maximoblog.repository.VerificationTokenRepository;
import com.example.maximoblog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // ── Register ────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email is already registered"));
        }

        // Save user (disabled until email is verified)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(false)
                .build();

        userRepository.save(user);

        // Create verification token (24h expiry)
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(user, token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageResponse.builder()
                        .message("Registration successful! Please check your email to verify your account.")
                        .build());
    }

    // ── Verify Email ────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> verifyEmail(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElse(null);

        if (verificationToken == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid verification token"));
        }

        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(Map.of("error", "Verification token has expired. Please register again."));
        }

        // Activate user
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // Delete used token (prevent reuse)
        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok(MessageResponse.builder()
                .message("Email verified successfully! You can now log in.")
                .build());
    }

    // ── Login ───────────────────────────────────────────────────

    public ResponseEntity<?> login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (DisabledException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account not verified. Please check your email."));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtUtil.generateToken(user);

        AuthResponse response = AuthResponse.builder()
                .token(jwtToken)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }

    // ── Forgot Password ─────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // Always return success to prevent email enumeration
        if (user == null) {
            return ResponseEntity.ok(MessageResponse.builder()
                    .message("If an account exists with that email, a reset link has been sent.")
                    .build());
        }

        // Delete any existing reset token for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Create new reset token (15 min expiry)
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send reset email
        emailService.sendPasswordResetEmail(user, token);

        return ResponseEntity.ok(MessageResponse.builder()
                .message("If an account exists with that email, a reset link has been sent.")
                .build());
    }

    // ── Reset Password ──────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElse(null);

        if (resetToken == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid reset token"));
        }

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(Map.of("error", "Reset token has expired. Please request a new one."));
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Delete used token (prevent reuse)
        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok(MessageResponse.builder()
                .message("Password reset successfully! You can now log in with your new password.")
                .build());
    }
}
