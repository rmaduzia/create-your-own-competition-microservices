package pl.createcompetition.tournamentservice.competition.tag;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventTagsDto {

    private String eventName;
    private List<String> tags;

}