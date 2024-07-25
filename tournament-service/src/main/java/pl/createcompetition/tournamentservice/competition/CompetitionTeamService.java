package pl.createcompetition.tournamentservice.competition;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;

@AllArgsConstructor
@Service
public class CompetitionTeamService {

//    private final NotificationMessagesToUsersService notificationMessagesToUsersService;
    private final VerifyMethodsForServices verifyMethodsForServices;
    private final CompetitionRepository competitionRepository;

    public ResponseEntity<?> teamJoinCompetition(String teamName, String competitionName, String userName) {

        TeamDto foundTeamDto = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeamDto, userName);

        Competition foundCompetition = getCompetition(competitionName);

        if (foundCompetition.getMaxAmountOfTeams() == foundCompetition.getTeams().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"There is already the maximum number of teams");
        }

        boolean isTeamAdded = foundCompetition.addTeam(teamName);

        if (!isTeamAdded) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Did not add team: " + teamName + " to competition: " + competitionName);
        }


//        // Send notification to Team Members
//        for (UserDetail userDetail: foundTeam.getUserDetails()) {
//            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","joined competition: ", competitionName);
//        }

        return ResponseEntity.ok("Added team: " + teamName + " to competition: " + competitionName);

    }

    public ResponseEntity<?> teamLeaveCompetition(String teamName, String competitionName, String userName) {

        TeamDto foundTeamDto = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeamDto, userName);

        Competition foundCompetition = getCompetition(competitionName);

        boolean isTeamRemoved = foundCompetition.removeTeam(teamName);

        if (!isTeamRemoved) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Did not find team: " + teamName + " in competition: " + competitionName);
        }

        // Send notification to Team Members
//        for (UserDetail userDetail: foundTeam.getUserDetails()) {
//            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","left tournament: ", competitionName);
//        }

        competitionRepository.save(foundCompetition);
        return ResponseEntity.ok("Removed team: " + teamName + " from competition: " + competitionName);

    }

    private void checkIfTeamBelongToUser(TeamDto teamDto, String userName) {
            if (!teamDto.getTeamOwner().equals(userName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "User named: " + userName + " is not owner of team named: " + teamDto.getTeamName());
            }
    }

    private Competition getCompetition(String competitionName) {
        return competitionRepository.findByEventName(competitionName).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND ,"Competition not exists, Name: " +competitionName));
    }
}