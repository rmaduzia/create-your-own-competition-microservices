package pl.createcompetition.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.createcompetition.user.KeyCloakService;
import pl.createcompetition.user.UserController;
import pl.createcompetition.user.UserCreateRecord;
import pl.createcompetition.user.UserRegisteredRecord;


@WebMvcTest(UserController.class)
@Import({KeyCloakService.class, UnitTestJwtDecoderConfig.class})
public class UserControllerProperUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    KeyCloakService keyCloakService;

    @MockBean
    Keycloak keycloak;

    @MockBean
    RealmResource realmResource;

    @MockBean
    UsersResource usersResource;

    @Test
    public void testCreateUser() throws Exception {

        UserCreateRecord userCreateRecord = new UserCreateRecord("testuser", "test@example.com", "password");
        UserRegisteredRecord registeredRecord = new UserRegisteredRecord("testuser", "test@example.com");

        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        when(usersResource.create(any())).thenReturn(Response.status(201).build());

        ObjectMapper objectMapper = new ObjectMapper();
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
}