package pl.createcompetition.tournamentservice.all.tournament.participation;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.all.tournament.Tournament;
import pl.createcompetition.tournamentservice.all.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.all.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;

@AllArgsConstructor
@Service
public class TournamentTeamService {

//    private final NotificationMessagesToUsersService notificationMessagesToUsersService;
    private final VerifyMethodsForServices verifyMethodsForServices;
    private final TournamentRepository tournamentRepository;

    public ResponseEntity<?> teamJoinTournament(String teamName, String tournamentName,String userName) {

        TeamDto foundTeamDto = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeamDto, userName);

        Tournament findTournament = getTournament(tournamentName);

        if (findTournament.getMaxAmountOfTeams() == findTournament.getTeams().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"There is already the maximum number of teams");
        }

        findTournament.addTeamToTournament(teamName);

        // Send notification to Team Members
     //   for (UserDetail userDetail: foundTeam.getUserDetails()) {
   //         notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","joined tournament: ", tournamentName);
    //    }


        return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> teamLeaveTournament(String teamName, String tournamentName,String userName) {

        TeamDto foundTeamDto = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeamDto, userName);

        Tournament findTournament = getTournament(tournamentName);

        boolean isTeamRemovedFromTournament = findTournament.deleteTeamFromTournament(teamName);

        // Send notification to Team Members
//        for (UserDetail userDetail: foundTeam.getUserDetails()) {
//            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","left tournament: ", tournamentName);
//        }

        return ResponseEntity.ok().build();

    }


    private void checkIfTeamBelongToUser(TeamDto teamDto, String userName) {
            if (!teamDto.getTeamOwner().equals(userName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "User named: " + userName + " is not owner of team named: " + teamDto.getTeamName());
            }
    }

    private Tournament getTournament(String tournamentName) {
        return tournamentRepository.findByTournamentName(tournamentName).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND ,"Tournament not exists. Name: " + tournamentName));
    }

    public ResponseEntity<?> setTheDatesOfTheTeamsMatches(String tournamentName, Map<String, LocalDateTime> dateMatch, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        foundTournament.setMatchTimes(dateMatch);

        return ResponseEntity.ok(tournamentRepository.save(foundTournament));
    }

    public ResponseEntity<?> deleteDateOfTheTeamsMatches(String tournamentName, String idDateMatch, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        foundTournament.getMatchTimes().remove(idDateMatch);
        tournamentRepository.save(foundTournament);

        return ResponseEntity.noContent().build();
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, UserPrincipal userPrincipal) {
        if (!tournament.getTournamentOwner().equals(userPrincipal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament named: " + tournament.getTournamentName() + " does not belong to: " + userPrincipal.getName());
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByTournamentNameAndTournamentOwner(tournamentName, tournamentOwner).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND ,"Tournament : " + tournamentName + "not exists"));
    }


}