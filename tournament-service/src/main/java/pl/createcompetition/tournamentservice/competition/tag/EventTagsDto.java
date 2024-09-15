package pl.createcompetition.tournamentservice.competition.tag;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventTagsDto {

    private String eventName;
    private List<String> tags;

}