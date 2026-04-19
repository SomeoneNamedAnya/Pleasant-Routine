package org.app.auth.sevices;

import org.app.auth.domain.RefreshToken;
import org.app.auth.domain.UserInfo;
import org.app.auth.domain.UserPasswordInfo;
import org.app.auth.dto.AuthResponse;
import org.app.auth.dto.LoginRequest;
import org.app.auth.dto.RegisterRequest;
import org.app.auth.dto.RegistrationResponse;
import org.app.auth.repository.AuthUserInfoRepository;
import org.app.auth.repository.UserPasswordInfoRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserPasswordInfoRepository userPasswordInfoRepository;
    private final AuthUserInfoRepository authUserInfoRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserPasswordInfoRepository userPasswordInfoRepository, AuthUserInfoRepository authUserInfoRepository, JwtService jwtService, RefreshTokenService refreshService, PasswordEncoder passwordEncoder) {
        this.userPasswordInfoRepository = userPasswordInfoRepository;
        this.authUserInfoRepository = authUserInfoRepository;
        this.jwtService = jwtService;
        this.refreshService = refreshService;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void logout(String refreshTokenStr) {
        refreshService.deleteByToken(refreshTokenStr);
    }

    @Transactional
    public RegistrationResponse register(RegisterRequest req) throws BadRequestException {
        UserInfo userInfo = new UserInfo(req.name(), req.surname(), req.lastName(),
                req.dateOfBirth(), req.email(), req.educationId(), req.roomId(), req.role());
        authUserInfoRepository.save(userInfo);


        String password = UUID.randomUUID().toString();
        UserPasswordInfo passwordInfo = new UserPasswordInfo(req.email(), passwordEncoder.encode(password),
                false, userInfo, LocalDateTime.now(), LocalDateTime.now());
        userPasswordInfoRepository.save(passwordInfo);
        return new RegistrationResponse(password);

         /*
        userInfo.setEmail(req.email());
        userInfo.setName(req.name());
        userInfo.setSurname(req.surname());
        userInfo.setLastName(req.lastName());
        userInfo.setDateOfBirth(req.dateOfBirth() != null ? Date.valueOf(req.dateOfBirth()) : null);
        userInfo.setRole(StringUtils.hasText(req.role()) ? req.role() : "USER");
        userInfo.setEducationId(req.educationId());
        userInfo.setRoomId(req.roomId());


        authUserInfoRepository.save(userInfo);

        UserPasswordInfo passwordInfo = new UserPasswordInfo();
        passwordInfo.setUser(userInfo);
        passwordInfo.setEmail(req.email());
        passwordInfo.setHashPassword(passwordEncoder.encode(req.password()));

        userPasswordInfoRepository.save(passwordInfo);


          */



    }
    @Transactional
    public AuthResponse login(LoginRequest req) {
        System.out.println(req);
        System.out.println("****************************************");
        UserPasswordInfo passwordInfo = userPasswordInfoRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        System.out.println(passwordInfo);
        if (!passwordEncoder.matches(req.password(), passwordInfo.getHashPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }
        if (Boolean.TRUE.equals(passwordInfo.getHasToChangePassword())) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return new AuthResponse(null, null, true);
        }

        UserInfo info = authUserInfoRepository.findById(passwordInfo.getId()).orElseThrow();

        String access = jwtService.generateToken(passwordInfo, info.getRole());
        RefreshToken refreshEntity = refreshService.createRefreshToken(passwordInfo);

        return new AuthResponse(access, refreshEntity.getToken(), passwordInfo.getHasToChangePassword());
    }
    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken rt = refreshService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        System.out.println("aaadasdadaaaaaaaaaaaaaaaaaaaaaa");
        refreshService.verifyExpiration(rt);
        UserPasswordInfo user = rt.getUserPasswordInfo();
        UserInfo info = authUserInfoRepository.findById(user.getId()).orElseThrow();

        String newAccess = jwtService.generateToken(user, info.getRole());
        RefreshToken newRefresh = refreshService.createRefreshToken(user); // rotation

        return new AuthResponse(newAccess, newRefresh.getToken(), false);
    }
}