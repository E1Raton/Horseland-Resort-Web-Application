package com.software_design.horseland.util;

import com.software_design.horseland.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${JWT_SECRET}")
    private String secretKey;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(User user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .issuer("demo-spring-boot-backend")
                .issuedAt(new Date(System.currentTimeMillis()))
                .claims(Map.of(
                        "userId", user.getId(),
                        "role", user.getRole()
                ))
                // the token will be expired in 15 minutes
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }
}
