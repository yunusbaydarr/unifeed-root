package app.unifeed.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    public JwtService(JwtEncoder encoder) { this.encoder = encoder; }
    public String issueAccessToken(UUID userId, String email, String globalRole) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("unifeed").issuedAt(now)
            .expiresAt(now.plus(15, ChronoUnit.MINUTES)).subject(userId.toString())
            .claim("userId", userId.toString()).claim("email", email).claim("globalRole", globalRole).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
