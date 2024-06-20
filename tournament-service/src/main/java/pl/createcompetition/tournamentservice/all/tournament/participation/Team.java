package pl.createcompetition.tournamentservice.all.tournament.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.createcompetition.tournamentservice.all.tournament.participation.Team.TeamDto;
import pl.createcompetition.tournamentservice.query.QueryDtoInterface;

@AllArgsConstructor
@Builder
@Data
public class Team implements QueryDtoInterface<TeamDto> {

    private String teamName;
    private String teamOwner;

    @Override
    public TeamDto map() {
        return new TeamDto(teamName,teamOwner);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamDto {
        private String teamName;
        private String teamOwner;
    }

}
