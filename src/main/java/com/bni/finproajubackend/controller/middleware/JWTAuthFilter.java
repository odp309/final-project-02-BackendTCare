package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTInterface jwtService;
    private final ObjectMapper mapper;
    private TokenRevocationListService tokenRevocationListService;
    private TemplateResInterface responseService;

    public JWTAuthFilter(JWTService jwtUtil, JWTInterface jwtService, ObjectMapper mapper, TokenRevocationListService tokenRevocationListService, TemplateResInterface responseService) {
        this.jwtService = jwtService;
        this.mapper = mapper;
        this.tokenRevocationListService = tokenRevocationListService;
        this.responseService = responseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> errorDetails = new HashMap<>();

        try {
            System.out.println(request);
            String accessToken = jwtService.resolveToken(request);
            System.out.println(accessToken);

            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (tokenRevocationListService.isTokenRevoked(accessToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                errorDetails.put("message", "Token Expired");
                mapper.writeValue(response.getWriter(), responseService.apiFailed(errorDetails));
                return;
            }

            if (!jwtService.isTokenValid(accessToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                errorDetails.put("message", "Token Expired");
                mapper.writeValue(response.getWriter(), responseService.apiFailed(errorDetails));
                return;
            }

            Claims claims = jwtService.extractAllClaims(accessToken);
            String username = claims.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, "", new ArrayList<>());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            errorDetails.put("message", "Authentication Error");
            errorDetails.put("details", e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(response.getWriter(), responseService.apiFailed(errorDetails));
        }
    }
}
