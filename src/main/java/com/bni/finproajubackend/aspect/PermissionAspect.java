package com.bni.finproajubackend.aspect;

import com.bni.finproajubackend.annotation.RequirePermission;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.UserRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Aspect
@Component
public class PermissionAspect {

    private UserRepository userRepository;

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requiresPermission) throws AccessDeniedException {
        String requiredPermission = requiresPermission.value();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        boolean hasPermission = user.getPerson().getAdmin().getRole().getPermissions().stream()
                .anyMatch(permission
                        -> permission.getPermissionName().equals(requiredPermission)
                        || permission.getPermissionName().equals("all"));

        if (!hasPermission) {
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }
    }
}
