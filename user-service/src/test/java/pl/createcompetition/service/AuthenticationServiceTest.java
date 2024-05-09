package pl.createcompetition.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import pl.createcompetition.user.KeyCloakService;
import pl.createcompetition.user.UserRegisteredRecord;
import pl.createcompetition.user.UserCreateRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {


    String userName = "myName";
    String email = "myEmail";
    String password = "myPassword";

    UserCreateRecord userCreateRecord = new UserCreateRecord(userName, email, password);
    UserRegisteredRecord userRegisteredRecord = new UserRegisteredRecord(userName, email);

    AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
    Authentication authentication = Mockito.mock(Authentication.class);

    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
//    UserRepository userRepository = Mockito.mock(UserRepository.class);

    KeyCloakService keyCloakService = Mockito.mock(KeyCloakService.class);

    RealmResource realmResource = Mockito.mock(RealmResource.class);



//    TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
//    AuthenticationService authenticationService = new AuthenticationService(authenticationManager, userRepository, passwordEncoder, tokenProvider);

    @BeforeAll
    static void setUp() {
//        user = User.builder().password("Password").id(1L).provider(AuthProvider.local).email("grzesiek12@gmail.com").emailVerified(true).build();
//        signUpRequest = SignUpRequest.builder().email(user.getEmail()).password(user.getPassword()).build();
//        loginRequest = LoginRequest.builder().email(user.getEmail()).password(user.getPassword()).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));



    }

    @Test
    public void testKeycloakAuthentication() {
        // Mock Keycloak authentication response
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setToken("mocked_access_token");
//        when(realmResource.tokenManager().getAccessToken()).thenReturn(tokenResponse);

        // Perform your authentication logic and assert the result
    }

    @Test
    public void should_RegisterNewUser() {

//        when(userRepository.findByEmail(ArgumentMatchers.any(String.class))).thenReturn(Optional.empty());
//        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        when(keyCloakService.createUser(userCreateRecord)).thenReturn(userRegisteredRecord);

        assertEquals(keyCloakService.createUser(userCreateRecord), userRegisteredRecord);
    }

    @Test
    public void should_AuthenticateUser() {

//        when(tokenProvider.createToken(ArgumentMatchers.any(Authentication.class))).thenReturn("Token");
//        when(authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(authentication);

//        assertEquals(authenticationService.authenticationUser(loginRequest).getStatusCode(), HttpStatus.OK);

    }
}