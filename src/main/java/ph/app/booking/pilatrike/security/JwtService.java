package ph.app.booking.pilatrike.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ph.app.booking.pilatrike.entities.User;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    private static final String SECRET_KEY = "superSecretKeyForJWT";  // Ensure this is a long enough secret key (at least 256-bit)

    @Value("${jwt.secret}")
    private String base64Secret;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate the JWT token for the user
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())  // User ID as the subject
                .claim("type", user.getType().toString())  // Additional claim (user type)
                .setIssuedAt(new Date())  // Current date and time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract user ID from the JWT token
    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    // Extract claim from the token (e.g. type, issuedAt, expiration)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Signing key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Validate if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Check if the token is valid (not expired and matches the user)
    public boolean isTokenValid(String token, String userId) {
        final String extractedUserId = extractUserId(token).toString();
        return (extractedUserId.equals(userId) && !isTokenExpired(token));
    }

    // Validate the token (simplified version)
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);  // if this fails, it means the token is invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
