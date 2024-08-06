package pl.createcompetition.tournamentservice.kafka.domain;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class NotifyTeamMembersRequest implements InternalEvent{

    String teamName;
    String body;
    UUID id;


    @Override
    public String getKey() {
        return teamName;
    }
}
