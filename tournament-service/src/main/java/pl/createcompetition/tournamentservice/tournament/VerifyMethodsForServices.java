package pl.createcompetition.tournamentservice.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.tournament.participation.FindTeamService;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;


@Service
@RequiredArgsConstructor
public class VerifyMethodsForServices {

    private final CompetitionRepository competitionRepository;
    private final FindTeamService findTeamService;
    private final TournamentRepository tournamentRepository;

    public TeamDto shouldFindTeam(String teamName, String teamOwner) {

        TeamDto foundTeam = findTeamService.findTeam(teamName, teamOwner);

        if (foundTeam == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found, Team Name: " + teamName + " Team owner: " + teamOwner);
        }

        return foundTeam;
    }

    public TeamDto shouldFindTeam(String teamName) {
        return findTeamService.findTeam(teamName);
    }

    public Competition shouldFindCompetition(String competitionName) {
        return competitionRepository.findByEventName(competitionName).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition not exists, Name: " +competitionName));
    }

    public Tournament shouldFindTournament(String competitionName) {
        return tournamentRepository.findByEventName(competitionName).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition not exists, Name: " +competitionName));
    }

    public void checkIfCompetitionBelongToUser(String competitionName, String userName) {
        if(!competitionName.equals(userName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You are not owner of this Competition");
        }
    }

}
