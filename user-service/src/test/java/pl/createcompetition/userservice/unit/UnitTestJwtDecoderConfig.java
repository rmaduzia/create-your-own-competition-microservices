package pl.createcompetition.unit;

import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class UnitTestJwtDecoderConfig {

    static final String AUTH0_TOKEN = "token";
    static final String SUB = "sub";
    static final String AUTH0ID = "randomAuthId";

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> jwt();
    }

    public Jwt jwt() {

        Map<String, Object> claims = Map.of(
            SUB, AUTH0ID
        );
        return new Jwt(
            AUTH0_TOKEN,
            Instant.now(),
            Instant.now().plusSeconds(30),
            Map.of("alg", "none"),
            claims
        );
    }
}