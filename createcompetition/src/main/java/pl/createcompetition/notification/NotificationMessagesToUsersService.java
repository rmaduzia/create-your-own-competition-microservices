package pl.createcompetition.notification;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Service;
import pl.createcompetition.model.websockets.UserNotification;

@AllArgsConstructor
@Service
public class NotificationMessagesToUsersService {

    //private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    //private final RabbitTemplate rabbitTemplate;


    @MessageMapping("/notifications")
    @SendToUser("/queue/notifications")
    public void notificationMessageToUser(String recipientUserName, String subject, String action, String event) {

        String content = notificationBuilderContent(subject, action, event);

        UserNotification userNotification = UserNotification.builder().recipient(recipientUserName).content(content).build();
        //notificationRepository.save(userNotification);
        System.out.println("to sie wykonuje");
        System.out.println(userNotification);
        //simpMessagingTemplate.convertAndSendToUser(recipientUserName, "/queue/notifications", userNotification);
        simpMessagingTemplate.convertAndSendToUser("odbiorca", "/queue/notifications", "zawartosc");

     //   rabbitTemplate.convertAndSend("amq.topic", "/queue/notifications", "zawartosc");


        //simpMessagingTemplate.convertAndSend("/queue/notifications", "zawartosc"); /  test wysłania do kolejki

    }

    public String notificationBuilderContent(String Subject,String action,  String event) {
        return Subject +" "+ action + " "+ event;
    }

}
