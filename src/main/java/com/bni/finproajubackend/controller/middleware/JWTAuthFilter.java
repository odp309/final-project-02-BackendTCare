package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.TokenRevocationListInterface;
import com.bni.finproajubackend.service.JWTService;
import com.bni.finproajubackend.service.TokenRevocationListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTInterface jwtService;
    private final ObjectMapper mapper;
    private final TokenRevocationListInterface tokenRevocationListService;
    private final TemplateResInterface responseService;

    public JWTAuthFilter(JWTService jwtUtil, JWTInterface jwtService, ObjectMapper mapper, TokenRevocationListService tokenRevocationListService, TemplateResInterface responseService) {
        this.jwtService = jwtService;
        this.mapper = mapper;
        this.tokenRevocationListService = tokenRevocationListService;
        this.responseService = responseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = jwtService.resolveToken(request);
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (tokenRevocationListService.isTokenRevoked(accessToken) || !jwtService.isTokenValid(accessToken)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                mapper.writeValue(response.getWriter(), responseService.apiUnauthorized(null, "Token Expired"));
                return;
            }

            Claims claims = jwtService.extractAllClaims(accessToken);
            String username = claims.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, "", new ArrayList<>());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (SecurityException e) {
            handleException(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage() != null ? e.getMessage() : "Unauthorized");
        } catch (Exception e) {
            handleException(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage() != null ? e.getMessage() : "Something Went Wrong");
        }
    }

    private void handleException(HttpServletResponse response, int status, String errorMessage) throws IOException {
        String message;
        if (errorMessage != null && errorMessage.toLowerCase().contains("jwt expired")) {
            status = HttpStatus.UNAUTHORIZED.value();
            message = "Token Expired";
        } else if (errorMessage != null && errorMessage.toLowerCase().contains("user not permitted")) {
            status = HttpStatus.FORBIDDEN.value();
            message = "User not Permitted";
        } else {
            message = errorMessage != null ? errorMessage : "Something Went Wrong";
        }

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (status == HttpStatus.UNAUTHORIZED.value() || status == HttpStatus.FORBIDDEN.value()) {
            mapper.writeValue(response.getWriter(), responseService.apiUnauthorized(null, message));
        } else {
            mapper.writeValue(response.getWriter(), responseService.apiFailed(null, message));
        }
    }


}
