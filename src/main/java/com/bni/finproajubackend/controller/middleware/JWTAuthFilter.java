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
    private TokenRevocationListInterface tokenRevocationListService;
    private TemplateResInterface responseService;

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
        } catch (Exception e) {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            String errorMessage = e.getMessage() != null ? e.getMessage() :"Something Went Wrong";

            if (errorMessage != null && errorMessage.toLowerCase().contains("jwt expired")) {
                statusCode = HttpStatus.UNAUTHORIZED.value();
                errorMessage = "Token Expired";
            }

            response.setStatus(statusCode);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            if (statusCode == HttpStatus.UNAUTHORIZED.value())
                mapper.writeValue(response.getWriter(), responseService.apiUnauthorized(null, errorMessage));
            else
                mapper.writeValue(response.getWriter(), responseService.apiFailed(null, errorMessage));

        }

    }
}
