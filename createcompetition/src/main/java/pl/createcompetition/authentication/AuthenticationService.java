package pl.createcompetition.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.model.AuthProvider;
import pl.createcompetition.user.User;
import pl.createcompetition.payload.ApiResponse;
import pl.createcompetition.payload.AuthResponse;
import pl.createcompetition.payload.LoginRequest;
import pl.createcompetition.payload.SignUpRequest;
import pl.createcompetition.user.UserRepository;
import pl.createcompetition.security.TokenProvider;

import java.net.URI;

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

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {
        if(userDao.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email address already in use.");
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userDao.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }



}
