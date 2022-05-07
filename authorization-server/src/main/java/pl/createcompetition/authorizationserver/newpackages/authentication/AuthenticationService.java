package pl.createcompetition.authorizationserver.newpackages.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.createcompetition.authorizationserver.newpackages.payload.AuthResponse;
import pl.createcompetition.authorizationserver.newpackages.payload.LoginRequest;
import pl.createcompetition.authorizationserver.newpackages.security.TokenProvider;
import pl.createcompetition.authorizationserver.newpackages.user.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthenticationService {

    final private AuthenticationManager authenticationManager;
    final private UserRepository userDao;
    final private PasswordEncoder passwordEncoder;
    final private TokenProvider tokenProvider;

    public ResponseEntity<?> authenticationUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }



}
