package pl.createcompetition.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.keycloak.common.Profile.Feature.UPDATE_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.microserviceschanges.ExchangePasswordForTokenRequestRecord;
import pl.createcompetition.microserviceschanges.UserPrincipal;
import pl.createcompetition.microserviceschanges.ValidJwtToken;
import pl.createcompetition.user.KeyCloakService;
import pl.createcompetition.user.UserController;
import pl.createcompetition.user.UserCreateRecord;
import pl.createcompetition.user.UserRegisteredRecord;
import reactor.core.publisher.Mono;

@WebMvcTest(UserController.class)
@Import({KeyCloakService.class, UnitTestJwtDecoderConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerProperUnitTest {

    @Autowired
    MockMvc mockMvc;

//    @Autowired
//    KeyCloakService keyCloakService;

    @MockBean
    Keycloak keycloak;

    @MockBean
    RealmResource realmResource;

    @MockBean
    UsersResource usersResource;

    @MockBean
    UserResource userResource;

    @MockBean
//    @Autowired
    WebClient webClient;

    @MockBean
    WebClient.Builder webClientBuilder;

    @Autowired
    ObjectMapper objectMapper;


    @MockBean
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    public static MockWebServer mockWebServer;

    @BeforeAll
    static void mockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void cleanUp() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);


        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
    }

    @Test
    void testCreateUser() throws Exception {

        UserCreateRecord userCreateRecord = new UserCreateRecord("testuser", "test@example.com", "password");
        UserRegisteredRecord registeredRecord = new UserRegisteredRecord("testuser", "test@example.com");

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

        UserCreateRecord userAlreadyExistsCreateRecord = new UserCreateRecord("testuser", "test@example.com", "password");

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

        String userName = "test";
        String password = "test";

        ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord = new ExchangePasswordForTokenRequestRecord(userName, password);

        String exchangePasswordForTokenBody = objectMapper.writeValueAsString(exchangePasswordForTokenRequestRecord);





//        when(webClient.post()).thenReturn("fgfgfg");

        String mockResponse = "{\"accessToken\":\"mockAccessToken\",\"expiresIn\":300,\"refreshExpiresIn\":1800,\"refreshToken\":\"mockRefreshToken\",\"tokenType\":\"Bearer\",\"idToken\":\"mockIdToken\",\"notBeforePolicy\":0,\"session_state\":\"mockSessionState\",\"scope\":\"openid profile email\"}";



        ValidJwtToken validJwtToken = ValidJwtToken.builder()
            .accessToken("acesss token")
            .build();

        mockWebServer.enqueue(new MockResponse()
            .setBody(objectMapper.writeValueAsString(validJwtToken))
            .addHeader("Content-Type", "application/json")
        );


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/keycloak/user/login")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(exchangePasswordForTokenBody))
                .andExpect(status().isOk())
            .andReturn();

        System.out.println("result: " + mvcResult.getResponse().getContentAsString());

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


    UserPrincipal getUserPrincipal() {

        String tokenValue = "dummyToken";
        String username = "testUser";
        String userEmail = "test@test.pl";
        String roleName = "ROLE_USER";

        Jwt jwtToken = Jwt.withTokenValue(tokenValue)
            .header("alg", "none")
            .claim("sub", username)
            .claim("email", userEmail)
            .build();


        return  UserPrincipal.builder()
            .jwt(jwtToken)
            .authorities(Set.of(new SimpleGrantedAuthority(roleName)))
            .name(username)
            .email(userEmail)
            .userId(username)
            .build();

    }




}