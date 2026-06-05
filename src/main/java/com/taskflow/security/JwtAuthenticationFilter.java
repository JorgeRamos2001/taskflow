package com.taskflow.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(7);
        String email = null;

        try {
            email = jwtService.extractEmail(jwt);
        }catch (ExpiredJwtException expiredJwtException) {
            log.warn("Expired JWT token: {}", expiredJwtException.getMessage());
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("Token has expired. Please log in again to obtain a new token."));
            return;
        }catch (MalformedJwtException malformedJwtException) {
            log.warn("Malformed JWT token: {}", malformedJwtException.getMessage());
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("Invalid token. Please log in again."));
            return;
        }catch (UnsupportedJwtException unsupportedJwtException) {
            log.warn("Unsupported JWT token: {}", unsupportedJwtException.getMessage());
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("Unsupported token type. Please log in again."));
            return;
        }catch (IllegalArgumentException illegalArgumentException) {
            log.warn("JWT claims string is empty: {}", illegalArgumentException.getMessage());
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("Invalid token. Please log in again."));
            return;
        }catch (SignatureException signatureException) {
            log.warn("Invalid JWT signature: {}", signatureException.getMessage());
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("Invalid token. Please log in again."));
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
