package pl.createcompetition.userservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.keycloak.common.Profile.Feature.UPDATE_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.createcompetition.userservice.user.KeyCloakService.UPDATE_PASSWORD;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.userservice.microserviceschanges.ExchangePasswordForTokenRequestRecord;
import pl.createcompetition.userservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.userservice.microserviceschanges.ValidJwtToken;
import pl.createcompetition.userservice.user.KeyCloakService;
import pl.createcompetition.userservice.user.UserController;
import pl.createcompetition.userservice.user.UserCreateRecord;
import pl.createcompetition.userservice.user.UserRegisteredRecord;
import pl.createcompetition.userservice.user.WebClientConfig;
import pl.createcompetition.unit.UnitTestJwtDecoderConfig;

@WebMvcTest(UserController.class)
@Import({KeyCloakService.class, UnitTestJwtDecoderConfig.class, WebClientConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerProperUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    Keycloak keycloak;

    @MockBean
    RealmResource realmResource;

    @MockBean
    UsersResource usersResource;

    @MockBean
    UserResource userResource;

    @Autowired
    ObjectMapper objectMapper;

    public static MockWebServer mockWebServer;

    static final String validUserName = "testUser";
    static final String validUserEmail = "test@example.com";
    static final String validUserPassword = "password";

    @BeforeAll
    static void mockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9090);
    }

    @AfterAll
    static void cleanUp() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    void testCreateUser() throws Exception {

        UserCreateRecord userCreateRecord = new UserCreateRecord(validUserName, validUserEmail, validUserPassword);
        UserRegisteredRecord registeredRecord = new UserRegisteredRecord(validUserName, validUserEmail);

        when(usersResource.create(any())).thenReturn(Response.status(201).build());

        String createUserJsonRequest = objectMapper.writeValueAsString(userCreateRecord);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/create")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJsonRequest))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        UserRegisteredRecord responseRecord = objectMapper.readValue(responseContent, UserRegisteredRecord.class);

        assertEquals(registeredRecord, responseRecord);
    }

    @Test
    void shouldGetCurrentUser() throws Exception {

        UserPrincipal userPrincipal = getUserPrincipal();

        SecurityContextHolder.getContext().setAuthentication(userPrincipal);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/me"))
            .andExpect(status().isOk())
            .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);

        assertTrue(responseJson.get("authorities").isArray());

        JsonNode authoritiesArray = responseJson.get("authorities");
        assertEquals(1, authoritiesArray.size());

        assertEquals(userPrincipal.getAuthorities().toArray()[0].toString(), authoritiesArray.get(0).get("authority").asText());

        assertTrue(responseJson.get("details").isEmpty());

        assertTrue(responseJson.get("authenticated").asBoolean());

        JsonNode principal = responseJson.get("principal");
        assertEquals(userPrincipal.getToken().getTokenValue(), principal.get("tokenValue").asText());

        JsonNode credentials = responseJson.get("credentials");
        assertEquals(userPrincipal.getToken().getTokenValue(), credentials.get("tokenValue").asText());

        JsonNode token = responseJson.get("token");
        assertEquals(userPrincipal.getToken().getTokenValue(), token.get("tokenValue").asText());

        assertEquals(userPrincipal.getName(), responseJson.get("name").asText());
        assertEquals(userPrincipal.getEmail(), responseJson.get("email").asText());
        assertEquals(userPrincipal.getName(), responseJson.get("userId").asText());

        JsonNode tokenAttributes = responseJson.get("tokenAttributes");
        assertEquals(userPrincipal.getName(), tokenAttributes.get("sub").asText());
        assertEquals(userPrincipal.getEmail(), tokenAttributes.get("email").asText());
    }


    @Test
    void whenCreatingUserShouldThrowExceptionThatUserNameAlreadyExists()
        throws Exception {

        UserCreateRecord userAlreadyExistsCreateRecord = new UserCreateRecord(validUserName, validUserEmail, validUserPassword);

        when(usersResource.create(any())).thenReturn(Response.status(409).entity("{\"errorMessage\":\"User exists with same username\"}").build());

        String createUserJsonRequest = objectMapper.writeValueAsString(userAlreadyExistsCreateRecord);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/create")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJsonRequest))
            .andExpect(status().isConflict())
            .andReturn();

        ResponseStatusException returnedException = (ResponseStatusException) mvcResult.getResolvedException();

        assertNotNull(returnedException);

        assertEquals("409 CONFLICT \"{\"errorMessage\":\"User exists with same username\"}\"", returnedException.getMessage());
        assertEquals(HttpStatus.CONFLICT, returnedException.getStatusCode());
        assertEquals("{\"errorMessage\":\"User exists with same username\"}", returnedException.getReason());
    }

    @Test
    void shouldExchangePasswordForToken() throws Exception {

        ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord = new ExchangePasswordForTokenRequestRecord(validUserName, validUserPassword);

        String exchangePasswordForTokenBody = objectMapper.writeValueAsString(exchangePasswordForTokenRequestRecord);

        String keycloakTokenResponse = "{\"access_token\":\"accessTokenSome\",\"expires_in\":299,\"refresh_expires_in\":1799,\"refresh_token\":\"refreshTokenSome\",\"token_type\":\"Bearer\",\"id_token\":\"idTokenSome\",\"not-before policy\":0,\"session_state\":\"sessionState\",\"scope\":\"openid profile email\"}";

        ValidJwtToken expectedToken = ValidJwtToken.builder()
            .accessToken("accessTokenSome")
            .expiresIn(299)
            .refreshExpiresIn(1799)
            .refreshToken("refreshTokenSome")
            .tokenType("Bearer")
            .idToken("idTokenSome")
            .notBeforePolicy(0)
            .session_state("sessionState")
            .scope("openid profile email")
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(keycloakTokenResponse)
            .addHeader("application", "x-www-form-urlencoded")
        );

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/login")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(exchangePasswordForTokenBody))
                .andExpect(status().isOk())
            .andReturn();


        String responseContent = mvcResult.getResponse().getContentAsString();
        ValidJwtToken responseToken = objectMapper.readValue(responseContent, ValidJwtToken.class);

        assertEquals(expectedToken, responseToken);
    }

    @Test
    void givingWrongPasswordWhenExchangeCredentialsForToken() throws Exception {

        ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord = new ExchangePasswordForTokenRequestRecord(validUserName, validUserPassword);

        String exchangePasswordForTokenBody = objectMapper.writeValueAsString(exchangePasswordForTokenRequestRecord);

        String keycloakTokenResponse = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid user credentials\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
            .setBody(keycloakTokenResponse)
            .addHeader("application", "x-www-form-urlencoded")
        );

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/login")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(exchangePasswordForTokenBody))
            .andExpect(status().isUnauthorized())
            .andReturn();

        ResponseStatusException responseStatusException = (ResponseStatusException) mvcResult.getResolvedException();

        assertNotNull(responseStatusException);
        assertEquals(HttpStatus.UNAUTHORIZED, responseStatusException.getStatusCode());
        assertEquals("401 UNAUTHORIZED \"Invalid user credentials\"", responseStatusException.getMessage());
        assertEquals("Invalid user credentials", responseStatusException.getReason());

    }

    @Test
    void shouldChangeEmail() throws Exception {

        UserPrincipal userPrincipal = getUserPrincipal();

        SecurityContextHolder.getContext().setAuthentication(userPrincipal);

        when(usersResource.get(any())).thenReturn(userResource);

        mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/change-mail")
                    .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());

        verify(userResource, times(1)).executeActionsEmail(List.of(UPDATE_EMAIL.name()));
    }

    @Test
    void shouldChangePassword() throws Exception {

        UserPrincipal userPrincipal = getUserPrincipal();

        SecurityContextHolder.getContext().setAuthentication(userPrincipal);

        when(usersResource.get(any())).thenReturn(userResource);

        mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/changePassword")
            .with(csrf())
        ).andExpect(status().isOk());

        verify(userResource, times(1)).executeActionsEmail(List.of("UPDATE_PASSWORD"));

    }

    @Test
    void shouldChangeUserName() throws Exception {

        UserPrincipal userPrincipal = getUserPrincipal();
        String newUserName = "newUserName";

        SecurityContextHolder.getContext().setAuthentication(userPrincipal);

        UserRepresentation userRepresentation = new UserRepresentation();

        when(usersResource.get("testUser")).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/change-username")
            .with(csrf())
            .content(newUserName)

        ).andExpect(status().isOk());

        verify(userResource, times(1)).update(userRepresentation);

        assertEquals(newUserName, userRepresentation.getUsername(), "New user name is not correct");

    }

    @Test
    void shouldSendVerificationEmail() throws Exception {

        UserPrincipal userPrincipal = getUserPrincipal();

        SecurityContextHolder.getContext().setAuthentication(userPrincipal);

        when(usersResource.get(validUserName)).thenReturn(userResource);

        mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/" + validUserName + "/send-verify-email")
            .with(csrf())
        ).andExpect(status().isOk());

        verify(userResource, times(1)).sendVerifyEmail();
    }

    @Test
    void shouldExecuteForgetPassword() throws Exception {

        UserPrincipal userPrincipal = getUserPrincipal();

        SecurityContextHolder.getContext().setAuthentication(userPrincipal);

        when(usersResource.get(validUserName)).thenReturn(userResource);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(validUserName);

        when(usersResource.searchByUsername(validUserName, true)).thenReturn(List.of(userRepresentation));

        mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/" + validUserName + "/forgot-password")
            .with(csrf())
        ).andExpect(status().isOk());

        verify(userResource, times(1)).executeActionsEmail(List.of(UPDATE_PASSWORD));
    }

    UserPrincipal getUserPrincipal() {

        String tokenValue = "dummyToken";
        String roleName = "ROLE_USER";

        Jwt jwtToken = Jwt.withTokenValue(tokenValue)
            .header("alg", "none")
            .claim("sub", validUserName)
            .claim("email", validUserEmail)
            .build();

        return  UserPrincipal.builder()
            .jwt(jwtToken)
            .authorities(Set.of(new SimpleGrantedAuthority(roleName)))
            .name(validUserName)
            .email(validUserEmail)
            .userId(validUserName)
            .build();

    }
}