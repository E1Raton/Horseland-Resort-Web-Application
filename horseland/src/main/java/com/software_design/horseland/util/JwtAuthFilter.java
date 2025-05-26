package com.software_design.horseland.util;

import com.software_design.horseland.model.AuthToken;
import com.software_design.horseland.repository.AuthTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${JWT_SECRET}")
    private String secretKey;

    private final AuthTokenRepository authTokenRepository;

    public JwtAuthFilter(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean checkClaims(String token){
        Claims claims = getAllClaimsFromToken(token);

        // check issuer
        if (!"demo-spring-boot-backend".equals(claims.getIssuer())) {
            log.error("Invalid token issuer");
            return false;
        }

        // check expiration
        if (claims.getExpiration().before(new Date())) {
            log.error("Token has expired");
            return false;
        }

        // check iat
        if (claims.getIssuedAt() == null || claims.getIssuedAt().after(new Date())) {
            log.error("Token issued at date is invalid");
            return false;
        }
        // check claims
        if (claims.get("userId") == null || claims.get("role") == null) {
            log.error("Token claims are invalid: does not contain userId and role");
            return false;
        }
        log.info("Token is valid. User ID: {}, Role: {}",
                claims.get("userId"), claims.get("role"));
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Allow OPTIONS requests and public auth endpoints
        if ("OPTIONS".equalsIgnoreCase(method)
                || "/auth/login".equals(path)
                || "/auth/request-reset".equals(path)
                || "/auth/verify-code".equals(path)) {
            log.info("Skipping JWT filter for path: {} and method: {}", path, method);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Authorization header is missing or does not start with 'Bearer '");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        try {
            boolean isValid = checkClaims(token);
            if (!isValid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            AuthToken authToken = authTokenRepository.findByToken(token);
            if (authToken == null) {
                log.error("Token not found in database (probably logged out)");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (authToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                log.error("Token in database has expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Claims claims = getAllClaimsFromToken(token);

            String username = claims.getSubject();
            String role = claims.get("role").toString();
            String userId = claims.get("userId").toString();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, List.of(() -> "ROLE_" + role));
            authentication.setDetails(userId);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            // Token is invalid, log the error and set the response status
            log.error("Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
