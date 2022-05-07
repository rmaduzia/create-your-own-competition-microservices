package pl.createcompetition.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.payload.ApiResponse;
import pl.createcompetition.payload.ChangeMailRequest;
import pl.createcompetition.payload.ChangePasswordRequest;
import pl.createcompetition.user.UserController;
import pl.createcompetition.user.UserService;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    UserService userService;
    @InjectMocks
    UserController userController;

    Gson gson = new Gson();
    MockMvc mockMvc;

    ResponseEntity<ApiResponse> response = ResponseEntity.ok(new ApiResponse(true, "Test Ok"));
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("Password 2", 1L, "Password");
    ChangeMailRequest changeMailRequest = new ChangeMailRequest("test@tes.com", 1L, "Password");

    String changePasswordJson = gson.toJson(changePasswordRequest);
    String changeEmailJson = gson.toJson(changeMailRequest);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void should_ChangePasswordCorrect() throws Exception {
        when(userService.changePassword(any(ChangePasswordRequest.class))).thenReturn(response);

        correctMvcPerform("/changePassword", changePasswordJson);
        verify(userService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    void should_ChangePasswordInValid() throws Exception {
        when(userService.changePassword(any(ChangePasswordRequest.class))).thenThrow(ResourceNotFoundException.class);

        inValidMvcPerform("/changePassword", changePasswordJson);
        verify(userService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    void should_ChangeMailCorrect() throws Exception {
        when(userService.changeEmail(any(ChangeMailRequest.class))).thenReturn(response);

        correctMvcPerform("/changeMail", changeEmailJson);
        verify(userService, times(1)).changeEmail(any(ChangeMailRequest.class));
    }

    @Test
    void should_NotChangeMail() throws Exception {
        when(userService.changeEmail(any(ChangeMailRequest.class))).thenThrow(ResourceNotFoundException.class);

        inValidMvcPerform("/changeMail", changeEmailJson);
        verify(userService, times(1)).changeEmail((any(ChangeMailRequest.class)));
    }

    private MvcResult correctMvcPerform(String urlPath, String jsonObject) throws Exception {
        return mockMvc.perform(post(urlPath)
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content(jsonObject))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is(true)))
                .andExpect(jsonPath("message", is("Test Ok"))).andReturn();
    }

    private MvcResult inValidMvcPerform(String urlPath, String jsonObject) throws Exception {
        return mockMvc.perform(post(urlPath)
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content(jsonObject))
                .andExpect(status().is4xxClientError()).andReturn();
    }
}
