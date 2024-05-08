package pl.createcompetition.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.microserviceschanges.ExchangePasswordForTokenRequestRecord;
import pl.createcompetition.microserviceschanges.JwtTokenDto;
import pl.createcompetition.microserviceschanges.ValidJwtToken;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KeyCloakService {

    private static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";
    private static final String UPDATE_EMAIL = "UPDATE_EMAIL";
    private static final String VERIFY_EMAIL = "VERIFY_EMAIL";

    @Value("${keycloak.domain}")
    private String authServerUrl;

    @Value("${keycloak-client.clientId}")
    private String CLIENT_ID;

    @Value("${keycloak-client.clientSecret}")
    private String CLIENT_SECRET;

    @Value("${keycloak-client.redirectUri}")
    private String REDIRECT_URI;

    private static final String SCOPES = "openid profile";

    private static final String GRANT_TYPE = "password";

    @Value("${keycloak.realm}")
    private String realm;

    Logger logger = LoggerFactory.getLogger(getClass());

    private final Keycloak keycloak;

    private final ObjectMapper objectMapper;

    public UserRegisteredRecord createUser(UserCreateRecord userCreateRecord) {

        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setUsername(userCreateRecord.userName());
        user.setEmail(userCreateRecord.email());
        user.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userCreateRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(List.of(credentialRepresentation));

        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);

        if (Objects.equals(201, response.getStatus())) {

            List<UserRepresentation> representationList = usersResource.searchByUsername(
                userCreateRecord.userName(), true);

            if (!representationList.isEmpty()) {

                UserRepresentation userRepresentation = representationList.stream().filter(savedUser -> Objects.equals(false, savedUser.isEmailVerified())).findFirst().orElse(null);
                assert userRepresentation != null;
                sendEmailVerification(userRepresentation.getId());
                logger.info("Email was sent to user id {}", userRepresentation.getId());

            }
            return new UserRegisteredRecord(userCreateRecord.userName(), userCreateRecord.email());


        }

        if (response.getStatus() == 409)
            throw new ResponseStatusException(HttpStatusCode.valueOf(response.getStatus()), response.readEntity(String.class));

        logger.error("Error while creating user, reason: " + response.getStatusInfo().getReasonPhrase());
        logger.error("Error while creating user, reason: " + response.readEntity(String.class));

        throw new ResponseStatusException(HttpStatusCode.valueOf(response.getStatus()), "Issue happened while creating user:" + userCreateRecord.userName() + " reason: " + response.getStatusInfo().getReasonPhrase());
    }

    public void forgotPassword(String userName) {

        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentationList = usersResource.searchByUsername(userName, true);

        UserRepresentation userRepresentation = userRepresentationList.stream().findFirst().orElse(null);

        if (userRepresentation != null) {

            UserResource userResource = usersResource.get(userRepresentation.getId());
            userResource.executeActionsEmail(List.of(UPDATE_PASSWORD));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Username not found: " + userName);
        }
    }

    public String changeUserName(String userId, String userName) {

        if (isUserNameExist(userName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exist: " + userName);
        }

        UsersResource usersResource = getUsersResource();
        UserRepresentation userRepresentation = usersResource.get(userId).toRepresentation();

        if (userRepresentation == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username not found: " + userName);
        }

        userRepresentation.setUsername(userName);

        UserResource userResource = getUserResource(userId);
        userResource.update(userRepresentation);

        return userName;
    }

    public void changePassword(String userId) {

        UserResource userResource = getUserResource(userId);

        userResource.executeActionsEmail(List.of(UPDATE_PASSWORD));
    }

    public void changeEmail(String userId) {

        UserResource userResource = getUserResource(userId);
        userResource.executeActionsEmail(List.of(UPDATE_EMAIL));
    }



    private boolean isUserNameExist(String userName) {

        return getUsersResource().searchByUsername(userName, true).size() == 1;


    }


    private UserResource getUserResource(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);

    }


    public void sendEmailVerification(String userId) {

        UsersResource usersResource = getUsersResource();

        try {
            usersResource.get(userId).sendVerifyEmail();
        } catch (Exception exception) {
            logger.error("Error while sending email to user id: {}", userId);
        }

    }

    private UsersResource getUsersResource() {

        RealmResource realmResource = keycloak.realm(realm);

        return realmResource.users();
    }


    public ValidJwtToken exchangePasswordForToken(
        ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord) {

        URI authorizationUri;

        try {
            authorizationUri = new URIBuilder("http://" + authServerUrl  + "/realms/appdevelopercompetition/protocol/openid-connect/token").build();
        } catch (URISyntaxException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Issue while login in. Please use form to contact with us");
        }

        WebClient webClient = WebClient.builder().build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", GRANT_TYPE);
        formData.add("username", exchangePasswordForTokenRequestRecord.username());
        formData.add("password", exchangePasswordForTokenRequestRecord.password());
        formData.add("client_id", CLIENT_ID);
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("scope", SCOPES);

        String keycloakAuthenticationResponse = webClient.post()
          .uri(authorizationUri)
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(BodyInserters.fromFormData(formData))
          .retrieve()
          .onStatus(HttpStatusCode::isError, clientResponse ->
            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                JsonObject errorJson = JsonParser.parseString(errorBody).getAsJsonObject();
                String errorDescription = errorJson.get("error_description").getAsString();

                logger.error("Error response from keycloak: " + errorBody);
                throw new ResponseStatusException(clientResponse.statusCode(), errorDescription);
            }))
          .bodyToMono(String.class)
          .block();

        JwtTokenDto jwtTokenDto = null;

        try {
            jwtTokenDto = objectMapper.readValue(keycloakAuthenticationResponse, JwtTokenDto.class);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Issue while login in. Please use form to contact with us");
        }

        return new ValidJwtToken(jwtTokenDto.getAccessToken(), jwtTokenDto.getExpiresIn(),
          jwtTokenDto.getRefreshExpiresIn(), jwtTokenDto.getRefreshToken(),
          jwtTokenDto.getTokenType(), jwtTokenDto.getIdToken(),
          jwtTokenDto.getNotBeforePolicy(), jwtTokenDto.getSessionState(),
          jwtTokenDto.getScope());

    }
}
