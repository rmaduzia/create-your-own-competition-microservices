package pl.createcompetition.authorizationserver.newpackages.user;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.authorizationserver.newpackages.security.CurrentUser;
import pl.createcompetition.authorizationserver.newpackages.security.UserPrincipal;


@AllArgsConstructor
@RestController
public class UserController {

    final private UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userService.getCurrentUser(userPrincipal);
    }


}
