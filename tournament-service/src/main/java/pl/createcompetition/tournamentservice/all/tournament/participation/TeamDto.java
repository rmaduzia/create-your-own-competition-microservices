package pl.createcompetition.tournamentservice.all.tournament.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.createcompetition.tournamentservice.all.tournament.participation.TeamDto.TeamDtoOutput;
import pl.createcompetition.tournamentservice.query.QueryDtoInterface;

@AllArgsConstructor
@Builder
@Data
public class TeamDto implements QueryDtoInterface<TeamDtoOutput> {

    private String teamName;
    private String teamOwner;

    @Override
    public TeamDtoOutput map() {
        return new TeamDtoOutput(teamName,teamOwner);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamDtoOutput {
        private String teamName;
        private String teamOwner;
    }

}
