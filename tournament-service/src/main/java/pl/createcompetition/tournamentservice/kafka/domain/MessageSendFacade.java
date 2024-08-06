package pl.createcompetition.tournamentservice.kafka.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageSendFacade {

    private final EventPublisher eventPublisher;

    public void sendEvent(final NotifyTeamMembersRequest notifyTeamMembersRequest) {
        eventPublisher.send(notifyTeamMembersRequest);
    }

}
