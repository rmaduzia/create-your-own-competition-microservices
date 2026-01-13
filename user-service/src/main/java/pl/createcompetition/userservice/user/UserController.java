package pl.createcompetition.userservice.user;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.userservice.microserviceschanges.ExchangePasswordForTokenRequestRecord;
import pl.createcompetition.userservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.userservice.microserviceschanges.ValidJwtToken;

@AllArgsConstructor
@RestController
public class UserController {

    final private KeyCloakService keyCloakService;

    @RolesAllowed("user")
    @GetMapping("/user/me")
    public UserPrincipal getCurrentUser(UserPrincipal userPrincipal) {
        return userPrincipal;
    }

    @PostMapping("/keycloak/user/create")
    public UserRegisteredRecord createUser(@RequestBody UserCreateRecord userCreateRecord) {
        return keyCloakService.createUser(userCreateRecord);
    }

    @PostMapping("keycloak/user/login")
    public ValidJwtToken exchangePasswordForToken(@RequestBody ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord) {
        return keyCloakService.exchangePasswordForToken(exchangePasswordForTokenRequestRecord);
    }

    @RolesAllowed("user")
    @PostMapping("/keycloak/user/change-mail")
    public void changeEmail(UserPrincipal userPrincipal){
        keyCloakService.changeEmail(userPrincipal.getUserId());
    }

    @RolesAllowed("user")
    @PostMapping("/keycloak/user/changePassword")
    public void changePassword(UserPrincipal userPrincipal){
        keyCloakService.changePassword(userPrincipal.getUserId());
    }

    @RolesAllowed("user")
    @PostMapping("keycloak/user/change-username")
    public String changeUserName(@RequestBody String userName, UserPrincipal userPrincipal) {
        return keyCloakService.changeUserName(userPrincipal.getUserId(), userName);
    }

    @PostMapping("/keycloak/{userId}/send-verify-email")
    public void sendVerificationEmail(@PathVariable String userId) {
        keyCloakService.sendEmailVerification(userId);
    }

    @PostMapping("keycloak/user/{userId}/forgot-password")
    public void forgotPassword(@PathVariable String userId) {
        keyCloakService.forgotPassword(userId);
    }


}