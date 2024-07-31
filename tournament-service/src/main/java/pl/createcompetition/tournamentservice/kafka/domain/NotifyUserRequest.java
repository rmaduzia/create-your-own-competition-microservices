package pl.createcompetition.tournamentservice.kafka.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class NotifyUserRequest implements InternalEvent{

    UUID id;
    String userName;
    String body;


    @Override
    public String getKey() {
        return userName;
    }
}
