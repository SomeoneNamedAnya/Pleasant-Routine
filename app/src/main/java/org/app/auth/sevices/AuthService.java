package org.app.auth.sevices;

import org.app.auth.domain.RefreshToken;
import org.app.auth.domain.UserInfo;
import org.app.auth.domain.UserPasswordInfo;
import org.app.auth.dto.AuthResponse;
import org.app.auth.dto.LoginRequest;
import org.app.auth.dto.RegisterRequest;
import org.app.auth.repository.UserInfoRepository;
import org.app.auth.repository.UserPasswordInfoRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Date;

@Service
public class AuthService {
    private final UserPasswordInfoRepository userRepo;
    private final UserInfoRepository userInfoRepo;
    private final JwtService jwtService;
    private final RefreshTokenService refreshService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserPasswordInfoRepository userRepo, UserInfoRepository userInfoRepo, JwtService jwtService, RefreshTokenService refreshService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userInfoRepo = userInfoRepo;
        this.jwtService = jwtService;
        this.refreshService = refreshService;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void logout(String refreshTokenStr) {
        refreshService.deleteByToken(refreshTokenStr);
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) throws BadRequestException {

        if (userRepo.findByEmail(req.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }


        UserPasswordInfo passwordInfo = new UserPasswordInfo();

        passwordInfo.setEmail(req.email());
        passwordInfo.setHashPassword(passwordEncoder.encode(req.password()));


        UserInfo userInfo = new UserInfo();
        userInfo.setUserPasswordInfo(passwordInfo);

        userInfo.setId(passwordInfo.getId());

        userInfo.setEmail(req.email());
        userInfo.setName(req.name());
        userInfo.setSurname(req.surname());
        userInfo.setLastName(req.lastName());
        userInfo.setDateOfBirth(req.dateOfBirth() != null ? Date.valueOf(req.dateOfBirth()) : null);
        userInfo.setRole(StringUtils.hasText(req.role()) ? req.role() : "USER");
        userInfo.setEducationId(req.educationId());
        userInfo.setRoomId(req.roomId());

        passwordInfo.setUserInfo(userInfo); // Взаимная связь


        userRepo.save(passwordInfo);

        String accessToken = jwtService.generateToken(passwordInfo, userInfo.getRole());
        RefreshToken refreshEntity = refreshService.createRefreshToken(passwordInfo);

        return new AuthResponse(accessToken, refreshEntity.getToken());
    }
    @Transactional
    public AuthResponse login(LoginRequest req) {
        UserPasswordInfo user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(req.password(), user.getHashPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        UserInfo info = userInfoRepo.findById(user.getId()).orElseThrow();
        String access = jwtService.generateToken(user, info.getRole());
        RefreshToken refreshEntity = refreshService.createRefreshToken(user);

        return new AuthResponse(access, refreshEntity.getToken());
    }
    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken rt = refreshService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        System.out.println("aaadasdadaaaaaaaaaaaaaaaaaaaaaa");
        refreshService.verifyExpiration(rt);
        UserPasswordInfo user = rt.getUserPasswordInfo();
        UserInfo info = userInfoRepo.findById(user.getId()).orElseThrow();

        String newAccess = jwtService.generateToken(user, info.getRole());
        RefreshToken newRefresh = refreshService.createRefreshToken(user); // rotation

        return new AuthResponse(newAccess, newRefresh.getToken());
    }
}