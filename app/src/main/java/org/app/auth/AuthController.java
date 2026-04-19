package org.app.auth;

import org.app.auth.dto.AuthResponse;
import org.app.auth.dto.LoginRequest;
import org.app.auth.dto.RefreshRequest;
import org.app.auth.dto.RegisterRequest;
import org.app.auth.sevices.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        System.out.println(req);
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (BadRequestException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email already exists"));
        }

    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest req) {
        authService.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            AuthResponse authResponse = authService.login(req);
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }


    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
        try {
            AuthResponse authResponse = authService.refresh(req.refreshToken());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }

    }
}