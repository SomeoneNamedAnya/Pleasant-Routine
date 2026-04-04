package org.app.auth;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContext {
    public static Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new RuntimeException("Unauthorized");
        }

        return user.getUserId();
    }
}
