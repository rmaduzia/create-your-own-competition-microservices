package pl.createcompetition.tournamentservice.all.tournament;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.all.tournament.participation.FindTeamService;
import pl.createcompetition.tournamentservice.all.tournament.participation.Team;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;


@Service
@AllArgsConstructor
public class VerifyMethodsForServices {

    private final CompetitionRepository competitionRepository;
    private final FindTeamService findTeamService;

    public Team shouldFindTeam(String teamName, String teamOwner) {
        return findTeamService.findTeam(teamName, teamOwner);
    }

    public Team shouldFindTeam(String teamName) {
        return findTeamService.findTeam(teamName);
    }

    public Competition shouldFindCompetition(String competitionName) {
        return competitionRepository.findByCompetitionName(competitionName).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition not exists, Name: " +competitionName));
    }

    public void checkIfCompetitionBelongToUser(String competitionName, String userName) {
        if(!competitionName.equals(userName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You are not owner of this Competition");
        }
    }

}
