package pl.createcompetition.notification;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pl.createcompetition.model.websockets.UserNotification;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.notification.NotificationMessagesToUsersService;


@AllArgsConstructor
@Controller
public class WebSocketMessagingController {

    private final SimpMessageSendingOperations messagingTemplate;
    private Gson gson = new Gson();
    private final NotificationMessagesToUsersService notificationMessagesToUsersService;

    @MessageMapping("/news")
    @SendTo("/topic/news")
    public String broadcastNews(@Payload String message) {
        return message;
    }



    /* Test Code
    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(
            @Payload String message,
            Principal principal) throws Exception {
        return gson
                .fromJson(message, Map.class)
                .get("name").toString();
    }
*/

    @MessageMapping("/notifications")
    @SendToUser("/queue/notifications")
    public UserNotification send(@Payload UserNotification userNotification, @CurrentUser UserPrincipal userPrincipal) {
        return userNotification;
    }


    @GetMapping("/klop")
    public ResponseEntity<?> execute() {

        notificationMessagesToUsersService.notificationMessageToUser("name rekruta", "Team","invite","nazwa teamu");
        return ResponseEntity.ok("costam");
    }







}
