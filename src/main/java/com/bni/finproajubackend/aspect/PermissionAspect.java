package com.bni.finproajubackend.aspect;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.nio.file.AccessDeniedException;

@Aspect
@Component
public class PermissionAspect {

    private final UserRepository userRepository;

    public PermissionAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Pointcut("@annotation(requiresPermission)")
    public void callAt(RequiresPermission requiresPermission) {
    }

    @Before("callAt(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        String requiredPermission = requiresPermission.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required.");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new AccessDeniedException("Invalid authentication principal.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new AccessDeniedException("User not found.");
        }

        boolean hasPermission = user.getPerson().getAdmin().getRole().getPermissions().stream()
                .anyMatch(permission -> permission.getPermissionName().equals(requiredPermission))
                || user.getPerson().getAdmin().getRole().getPermissions().stream()
                .anyMatch(permission -> permission.getPermissionName().equals("all"));

        if (!hasPermission) {
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }
    }
}
