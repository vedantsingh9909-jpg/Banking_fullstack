package com.banking.banking.utils;


import com.banking.banking.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    /**
     * Mevcut authenticated kullanıcıyı döner.
     *
     * @return UserDetails veya kullanıcı objesi, yoksa null.
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return User.class.cast(principal);
    }
}