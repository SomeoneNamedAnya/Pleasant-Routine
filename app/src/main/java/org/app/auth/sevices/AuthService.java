package org.app.auth.sevices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.auth.domain.RefreshToken;
import org.app.auth.domain.UserInfo;
import org.app.auth.domain.UserPasswordInfo;
import org.app.auth.dto.*;
import org.app.auth.repository.AuthUserInfoRepository;
import org.app.auth.repository.UserPasswordInfoRepository;
import jakarta.transaction.Transactional;
import org.app.email.service.EmailService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserPasswordInfoRepository userPasswordInfoRepository;
    private final AuthUserInfoRepository authUserInfoRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public RegistrationResponse register(RegisterRequest req) {


        UserInfo userInfo = new UserInfo(
                req.name(), req.surname(), req.lastName(),
                req.dateOfBirth(), req.email(),
                req.educationId(), req.roomId(), req.role()
        );
        authUserInfoRepository.save(userInfo);


        String tempPassword = generateTempPassword();


        UserPasswordInfo passwordInfo = new UserPasswordInfo(
                req.email(),
                passwordEncoder.encode(tempPassword),
                true,
                userInfo,
                Instant.now(),
                Instant.now()
        );
        userPasswordInfoRepository.save(passwordInfo);


        String fullName = req.surname() + " " + req.name();
        emailService.sendTemporaryPassword(req.email(), fullName, tempPassword);

        log.info("User registered: {}, temp password sent to email", req.email());


        return new RegistrationResponse(tempPassword);
    }


    @Transactional
    public AuthResponse login(LoginRequest req) {

        UserPasswordInfo passwordInfo = userPasswordInfoRepository
                .findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(req.password(), passwordInfo.getHashPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }


        if (Boolean.TRUE.equals(passwordInfo.getHasToChangePassword())) {
            return new AuthResponse(null, null, true);
        }

        UserInfo info = authUserInfoRepository
                .findById(passwordInfo.getUser().getId())
                .orElseThrow(() -> new RuntimeException("UserInfo not found"));

        String access = jwtService.generateToken(passwordInfo, info.getRole());
        RefreshToken refreshEntity = refreshService.createRefreshToken(passwordInfo);

        return new AuthResponse(access, refreshEntity.getToken(), false);
    }


    @Transactional
    public AuthResponse changePassword(ChangePasswordRequest req) {


        UserPasswordInfo passwordInfo = userPasswordInfoRepository
                .findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(req.oldPassword(), passwordInfo.getHashPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }


        validateNewPassword(req.newPassword());


        passwordInfo.setHashPassword(passwordEncoder.encode(req.newPassword()));
        passwordInfo.setHasToChangePassword(false);
        passwordInfo.setUpdatedAt(Instant.now());
        userPasswordInfoRepository.save(passwordInfo);


        UserInfo info = authUserInfoRepository
                .findById(passwordInfo.getUser().getId())
                .orElseThrow(() -> new RuntimeException("UserInfo not found"));

        String fullName = info.getSurname() + " " + info.getName();
        emailService.sendPasswordChangedNotification(req.email(), fullName);

        String access = jwtService.generateToken(passwordInfo, info.getRole());
        RefreshToken refreshEntity = refreshService.createRefreshToken(passwordInfo);

        log.info("Password changed for user: {}", req.email());

        return new AuthResponse(access, refreshEntity.getToken(), false);
    }


    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken rt = refreshService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshService.verifyExpiration(rt);

        UserPasswordInfo user = rt.getUserPasswordInfo();
        UserInfo info = authUserInfoRepository
                .findById(user.getUser().getId())
                .orElseThrow(() -> new RuntimeException("UserInfo not found"));

        String newAccess = jwtService.generateToken(user, info.getRole());
        RefreshToken newRefresh = refreshService.createRefreshToken(user);

        return new AuthResponse(newAccess, newRefresh.getToken(), false);
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshService.deleteByToken(refreshTokenStr);
    }


    @Transactional
    public void resetPasswordByAdmin(String email) {
        UserPasswordInfo passwordInfo = userPasswordInfoRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String tempPassword = generateTempPassword();
        passwordInfo.setHashPassword(passwordEncoder.encode(tempPassword));
        passwordInfo.setHasToChangePassword(true);
        passwordInfo.setUpdatedAt(Instant.now());
        userPasswordInfoRepository.save(passwordInfo);

        UserInfo info = authUserInfoRepository
                .findById(passwordInfo.getUser().getId())
                .orElseThrow();
        String fullName = info.getSurname() + " " + info.getName();
        emailService.sendTemporaryPassword(email, fullName, tempPassword);

        log.info("Password reset by admin for user: {}", email);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString()
                .toUpperCase();
    }

    private void validateNewPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (password.length() > 128) {
            throw new IllegalArgumentException("Password is too long");
        }
    }
}