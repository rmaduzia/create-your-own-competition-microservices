package pl.createcompetition.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import pl.createcompetition.user.UserService;
import pl.createcompetition.util.email.Mail;
import pl.createcompetition.util.email.MailService;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.AuthProvider;
import pl.createcompetition.user.User;
import pl.createcompetition.payload.ApiResponse;
import pl.createcompetition.payload.ChangeMailRequest;
import pl.createcompetition.payload.ChangePasswordRequest;
import pl.createcompetition.user.UserRepository;
import pl.createcompetition.security.UserPrincipal;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitWebConfig
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;
    @InjectMocks
    private UserService userService;

    private User user;
    private User mockUser;
    private UserPrincipal userPrincipal;
    private ChangePasswordRequest passwordRequest;
    private ChangeMailRequest mailRequest;

    @BeforeEach
    void setUp() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .imageUrl("test Url")
                .emailVerified(true)
                .password("Password")
                .provider(AuthProvider.local)
                .providerId("id").build();

        mockUser = user.getClass().getConstructor().newInstance();
        userPrincipal = UserPrincipal.create(user);
        passwordRequest = new ChangePasswordRequest("NewPassword", user.getId(), "Password");
        mailRequest = new ChangeMailRequest("test2@test.com", user.getId(), "Password");

        when(userRepository.save(this.user)).thenReturn(mockUser);
        when(userRepository.findByIdAndPassword(user.getId(), user.getPassword())).thenReturn(Optional.of(this.user));
    }

    @Test
    public void should_GetCurrentUser() {
        when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(user));
        assertEquals(userService.getCurrentUser(userPrincipal).getEmail(),user.getEmail());
    }

    @Test
    public void should_ChangeEmail() {

        mockUser.setEmail("test2@test.com");

        ResponseEntity<ApiResponse> isChanged = userService.changeEmail(mailRequest);
        User user = userRepository.findByIdAndPassword(this.user.getId(), "Password").get();

        assertTrue(Objects.requireNonNull(isChanged.getBody()).isSuccess());
        assertEquals( "test2@test.com", user.getEmail());
        verify(userRepository, times(1)).save(this.user);
        verify(mailService, times(1)).send(any(Mail.class));
    }

    @Test
    public void should_NotChangeEmail() {
        when(userRepository.findByIdAndPassword(this.user.getId(), "Password")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->{
            userService.changeEmail(mailRequest);
        });
    }

    @Test
    public void should_ChangePassword() {

        mockUser.setPassword("NewPassword");

        ResponseEntity<ApiResponse> isChanged = userService.changePassword(passwordRequest);

        assertTrue(Objects.requireNonNull(isChanged.getBody()).isSuccess());
        verify(userRepository, times(1)).save(this.user);
        verify(mailService, times(1)).send(any(Mail.class));
    }

    @Test
    public void should_NotChangePassword() {

        when(userRepository.findByIdAndPassword(user.getId(), user.getPassword())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->{
            userService.changePassword(passwordRequest);
        });
    }
}