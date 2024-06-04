package pl.createcompetition.tournamentservice.microserviceschanges;

import java.util.Collection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserPrincipal extends JwtAuthenticationToken {

    private String email;
    private String userId;

    @Builder
    public UserPrincipal(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String name, String email, String userId) {
        super(jwt, authorities, name);
        this.email = email;
        this.userId = userId;
    }

}
