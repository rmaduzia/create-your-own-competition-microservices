package pl.createcompetition.tournamentservice.tournament.participation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.kafka.domain.MessageSendFacade;
import pl.createcompetition.tournamentservice.kafka.domain.NotifyTeamMembersRequest;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;

@AllArgsConstructor
@Service
public class TournamentTeamService {

    private final VerifyMethodsForServices verifyMethodsForServices;
    private final TournamentRepository tournamentRepository;
    private final MessageSendFacade messageSendFacade;

    public ResponseEntity<?> teamJoinTournament(String teamName, String tournamentName,String userName) {

        TeamDto foundTeamDto = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeamDto, userName);

        Tournament findTournament = getTournament(tournamentName);

        if (findTournament.getMaxAmountOfTeams() == findTournament.getTeams().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"There is already the maximum number of teams");
        }

        findTournament.addTeamToTournament(teamName);

        NotifyTeamMembersRequest notifyTeamMembersRequest = NotifyTeamMembersRequest.builder()
            .id(UUID.randomUUID())
            .teamName(teamName)
            .body("Your team: " + teamName + " joined tournament: " + tournamentName)
            .build();

        messageSendFacade.sendEvent(notifyTeamMembersRequest);

        return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> teamLeaveTournament(String teamName, String tournamentName,String userName) {

        TeamDto foundTeamDto = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeamDto, userName);

        Tournament findTournament = getTournament(tournamentName);

        boolean isTeamRemovedFromTournament = findTournament.deleteTeamFromTournament(teamName);

        if (!isTeamRemovedFromTournament) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Issue while removing team from tournament");
        }

        NotifyTeamMembersRequest notifyTeamMembersRequest = NotifyTeamMembersRequest.builder()
            .id(UUID.randomUUID())
            .teamName(teamName)
            .body("Your team: " + teamName + " left tournament: " + tournamentName)
            .build();

        messageSendFacade.sendEvent(notifyTeamMembersRequest);

        return ResponseEntity.ok().build();
    }


    private void checkIfTeamBelongToUser(TeamDto teamDto, String userName) {
            if (!teamDto.getTeamOwner().equals(userName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "User named: " + userName + " is not owner of team named: " + teamDto.getTeamName());
            }
    }

    private Tournament getTournament(String tournamentName) {
        return tournamentRepository.findByEventName(tournamentName).orElseThrow(() ->
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
        if (!tournament.getEventOwner().equals(userPrincipal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament named: " + tournament.getEventName() + " does not belong to: " + userPrincipal.getName());
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByEventNameAndEventOwner(tournamentName, tournamentOwner).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND ,"Tournament : " + tournamentName + "not exists"));
    }


}