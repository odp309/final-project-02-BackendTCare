package com.bni.finproajubackend.aspect;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Aspect
@Component
public class PermissionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);

    private final UserRepository userRepository;
    private final ObjectMapper mapper;
    private final TemplateResInterface responseService;

    public PermissionAspect(UserRepository userRepository, ObjectMapper mapper, TemplateResInterface responseService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.responseService = responseService;
    }

    @Pointcut("@annotation(requiresPermission)")
    public void callAt(RequiresPermission requiresPermission) {
    }

    @Before(value = "callAt(requiresPermission)", argNames = "joinPoint,requiresPermission")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) throws IOException {
        logger.info("Entering checkPermission method for {}", joinPoint.getSignature().getName());
        try {
            String requiredPermission = requiresPermission.value();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Authentication is null or not authenticated");
                throw new SecurityException("Unauthorized");
            }

            Object principal = authentication.getPrincipal();
            String username = extractUsername(principal);
            if (username == null) {
                logger.warn("Invalid authentication principal");
                throw new SecurityException("Invalid authentication principal.");
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                logger.warn("User not found: {}", username);
                throw new SecurityException("User Not Found");
            }

            if (!userHasPermission(user, requiredPermission)) {
                logger.warn("User does not have permission: {}", username);
                throw new SecurityException("User Not Permitted to access this feature");
            }

            logger.info("User {} has the required permission {}", username, requiredPermission);
        } catch (SecurityException e) {
            String errorMessage = switch (e.getMessage()) {
                case "Unauthorized" -> "Unauthorized";
                case "Invalid authentication principal." -> "Invalid authentication principal.";
                case "User Not Found" -> "User Not Found";
                default -> "User not Permitted";
            };
            logger.warn("Security exception occurred: {}", errorMessage);
            throw new SecurityException(errorMessage);
        } catch (Exception e) {
            logger.error("An error occurred during permission check: {}", e.getMessage());
            throw new IOException("Error during permission check");
        }
    }

    private String extractUsername(Object principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    private boolean userHasPermission(User user, String requiredPermission) {
        return user.getAdmin().getRole().getRoleName().equals(requiredPermission);
    }
}
