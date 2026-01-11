package pl.createcompetition.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.createcompetition.notification_service.dto.UserNotification;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpleMessageTemplate;

    public void globalNotify(UserNotification userNotification) {

        //TODO dodac logowanie - wszystko do ELK
        simpleMessageTemplate.convertAndSendToUser(userNotification.userName(), "global", userNotification.message());
    }


}
