package pl.createcompetition.teamservice.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyCloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;


    public UserRepresentation getUserByUserName(String username) {
        return getUsersResource().searchByUsername(username, true).stream().findFirst().orElse(null);
    }

    private UsersResource getUsersResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.users();


    }


}
