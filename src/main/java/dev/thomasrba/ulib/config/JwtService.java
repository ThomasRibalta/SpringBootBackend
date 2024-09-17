package dev.thomasrba.ulib.config;

import dev.thomasrba.ulib.entity.User;
import dev.thomasrba.ulib.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class JwtService {

    private final String SECRET_KEY = "990c6fb70c1fc92a56e2c91146ff47137b71f6c8abe4da3793bb502d5fca8d51";
    private UserRepository userRepository;

    public String extractMail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.
                parserBuilder().
                setSigningKey(getKey()).
                build().
                parseClaimsJws(token).
                getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public Map<String, String> generate(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User userEntity = user.get();
        return this.generateJwt(userEntity);
    }

    private Map<String, String> generateJwt(User user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 15 * 60 * 1000;
        final Map<String, String> claims =  Map.of(
                "email" , user.getEmail()
        );

        final String bearer = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(user.getEmail())
                .signWith(this.getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer" , bearer);
    }

    private Key getKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String email = extractMail(token);
        return email.equals(userDetails.getUsername()) && !isExpiredToken(token);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isExpiredToken(String token) {
        return extractExpiration(token).before(new Date());
    }

}
