package pl.createcompetition.authorizationserver.newpackages.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.authorizationserver.newpackages.exception.ResourceNotFoundException;
import pl.createcompetition.authorizationserver.newpackages.payload.ApiResponse;
import pl.createcompetition.authorizationserver.newpackages.payload.interfaces.ChangeRequest;
import pl.createcompetition.authorizationserver.newpackages.security.CurrentUser;
import pl.createcompetition.authorizationserver.newpackages.security.UserPrincipal;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    final private UserRepository userDao;

    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userDao.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    public User save(User user) {
        return userDao.save(user);
    }

    private User getUserForChange(ChangeRequest changeRequest){
        return userDao.findByIdAndPassword(changeRequest.getUserId(), changeRequest.getPassword())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", changeRequest.getUserId()));
    }

}
