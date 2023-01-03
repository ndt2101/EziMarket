package com.ndt2101.ezimarket.utils;

import com.ndt2101.ezimarket.exception.ApplicationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JwtUtils {
    @Autowired
    private SecretKey secretKey;

    @Value("${jwt.time_expiration}")
    private Long timeExpiration;

    public Optional<String> getJwtFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter((bearerToken) -> bearerToken.startsWith("Bearer "))
                .map((bearerToken) -> bearerToken.substring(7));
    }

    public Boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            parser.parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getLoginNameFromToken(String token) throws ApplicationException {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        String loginName = parser.parseClaimsJws(token).getBody().getSubject();
        if (loginName != null && !loginName.isEmpty()) {
            return loginName;
        }
        throw new ApplicationException(HttpStatus.BAD_REQUEST, "invalid token");
    }

    public String generateToken(String loginName) {
        return Jwts.builder()
                .setSubject(loginName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + timeExpiration))
                .signWith(secretKey,SignatureAlgorithm.HS256)
                .compact();
    }
}
