package pl.createcompetition.authentication;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pl.createcompetition.payload.LoginRequest;
import pl.createcompetition.payload.SignUpRequest;
import pl.createcompetition.authentication.AuthenticationService;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticationUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authenticationService.registerUser(signUpRequest);
    }
}
