package pl.createcompetition.tournamentservice.kafka;


import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.tournamentservice.kafka.domain.MessageSendFacade;
import pl.createcompetition.tournamentservice.kafka.domain.NotifyTeamMembersRequest;

@RestController
@RequestMapping("kafka")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KafkaTestingController {

    MessageSendFacade messageSendFacade;

    @PostMapping("send")
    public ResponseEntity<?> sendEvent() {

        NotifyTeamMembersRequest notifyTeamMembersRequest = NotifyTeamMembersRequest.builder()
            .id(UUID.randomUUID())
            .teamName("firstTeam")
            .body("some body")
            .build();

        messageSendFacade.sendEvent(notifyTeamMembersRequest);

        return ResponseEntity.ok().build();

    }

}
