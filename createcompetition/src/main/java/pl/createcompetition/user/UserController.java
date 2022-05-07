package pl.createcompetition.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import pl.createcompetition.user.User;
import pl.createcompetition.payload.ChangeMailRequest;
import pl.createcompetition.payload.ChangePasswordRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.user.UserService;

@AllArgsConstructor
@RestController
public class UserController {

    final private UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userService.getCurrentUser(userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("changeMail")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeMailRequest changeMail){
        return userService.changeEmail(changeMail);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("changePassword")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest changePassword){
        return userService.changePassword(changePassword);
    }

}
