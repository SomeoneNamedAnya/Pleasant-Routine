package org.app.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.app.auth.CustomUserDetails;
import org.app.auth.sevices.JwtService;
import org.app.auth.domain.UserPasswordInfo;
import org.app.auth.repository.UserPasswordInfoRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserPasswordInfoRepository userRepo;
    private static final List<String> PUBLIC_URLS = List.of(
            "/auth/login",
            "/refresh"
    );
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_URLS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        Long userId = jwtService.extractUserId(jwt);
        String role = jwtService.extractUserRole(jwt);
        System.out.println(userId);

        if (userId != null && role != null && jwtService.isTokenValid(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails = new CustomUserDetails(userId, role);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()

            );
                SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        chain.doFilter(request, response);
    }


}