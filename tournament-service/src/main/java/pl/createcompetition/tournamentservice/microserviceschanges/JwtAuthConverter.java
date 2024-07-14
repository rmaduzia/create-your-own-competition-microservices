package pl.createcompetition.tournamentservice.microserviceschanges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return UserPrincipal.builder()
            .jwt(jwt)
            .authorities(new HashSet<>(extractResourceRoles(jwt)))
            .email(jwt.getClaimAsString("email"))
            .userId(jwt.getClaimAsString("sub"))
            .name(jwt.getClaimAsString("preferred_username"))
            .build();

    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        Collection<String> allRoles = new ArrayList<>();
        Collection<String> resourceRoles;
        Collection<String> realmRoles;

        if (resourceAccess != null && resourceAccess.get("account") != null) {

            Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");
            if (account.containsKey("roles")) {
                resourceRoles = (Collection<String>) account.get("roles");
                allRoles.addAll(resourceRoles);
            }
        }

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            realmRoles = (Collection<String>) realmAccess.get("roles");
            allRoles.addAll(realmRoles);
        }

        return allRoles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toSet());
    }
}
