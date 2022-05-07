package pl.createcompetition.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.util.email.Mail;
import pl.createcompetition.util.email.MailService;
import pl.createcompetition.util.email.TemplateValues;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.payload.ApiResponse;
import pl.createcompetition.payload.ChangeMailRequest;
import pl.createcompetition.payload.ChangePasswordRequest;
import pl.createcompetition.payload.interfaces.ChangeRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;


@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    final private UserRepository userDao;
    final private MailService mailService;

    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userDao.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    public ResponseEntity<ApiResponse> changeEmail(ChangeMailRequest request) {
        User user = getUserForChange(request);
        Mail mail = sendInformationEmile(user,"email","https://www.google.pl/", request.getEmail());
        user.setEmail(request.getEmail());
        user = userDao.save(user);
        return changeResponse(request.getEmail().equals(user.getEmail()),"Email", mail);
    }

    public ResponseEntity<ApiResponse> changePassword(ChangePasswordRequest request) {
        User user = getUserForChange(request);
        Mail mail = sendInformationEmile(user,"password","https://www.google.pl/",request.getNewPassword());
        user.setPassword(request.getNewPassword());
        user = userDao.save(user);
        return changeResponse(request.getNewPassword().equals(user.getPassword())," Password", mail);
    }

    public User save(User user) {
        return userDao.save(user);
    }

    private User getUserForChange(ChangeRequest changeRequest){
        return userDao.findByIdAndPassword(changeRequest.getUserId(), changeRequest.getPassword())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", changeRequest.getUserId()));
    }

    private ResponseEntity<ApiResponse> changeResponse(boolean changeCondition, String parameter, Mail mail) {
        if (changeCondition) {
            if (mail != null){
                mailService.send(mail);
            }
            return ResponseEntity.ok(new ApiResponse(true,parameter + " has change"));
        }
        throw new RuntimeException(parameter + " hasn't change");
    }

    private Mail sendInformationEmile(User user, String data, String link, String value) {
        TemplateValues values = TemplateValues.builder()
                .changedData(data)
                .changeDataLink(link)
                .dataValue(user.getEmail())
                .name(value).build();

        return new Mail(user.getEmail(),values);
    }
}
