package pl.createcompetition.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.microserviceschanges.ExchangePasswordForTokenRequestRecord;
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


}