package pl.createcompetition.tournamentservice.kafka.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
//@FieldDefaults(level = PRIVATE, makeFinal = true)
@Service
public class MessageReadFacade {

    private final EventPublisher eventPublisher;

    public void sendEvent(final NotifyUserRequest notifyUserRequest) {
        eventPublisher.send(notifyUserRequest);
    }

}
