package pl.createcompetition.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.createcompetition.notification.NotificationMessagesToUsersService;
import pl.createcompetition.notification.NotificationRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SendNotificationToUserTest {

    String rabbitmqPort = "5679";

    @MockBean
    private NotificationMessagesToUsersService notificationMessagesToUsersService;


    private NotificationRepository notificationRepository;
    private SimpMessagingTemplate simpMessagingTemplate;

    String receiverName = "user1";


}
