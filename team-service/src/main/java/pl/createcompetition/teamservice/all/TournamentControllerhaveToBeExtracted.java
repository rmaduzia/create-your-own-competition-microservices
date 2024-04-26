package pl.createcompetition.teamservice.all;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.teamservice.microserviceschanges.UserPrincipal;


@AllArgsConstructor
@RestController
@RequestMapping("team")
public class TournamentControllerhaveToBeExtracted {

    private final TournamentServiceHaveToBeExtracted tournamentServiceHaveToBeExtracted;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/joinTournament")
    public ResponseEntity<?> joinTeamToTournament(@RequestBody String recruitName,
                                                  UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return tournamentServiceHaveToBeExtracted.teamJoinTournament(teamName, recruitName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}leaveTournament")
    public ResponseEntity<?> teamLeaveTournament(@RequestBody String recruitName,
                                                 UserPrincipal userPrincipal,
                                                 @PathVariable String teamName) {

        return tournamentServiceHaveToBeExtracted.teamLeaveTournament(teamName, recruitName, userPrincipal.getName());
    }


    //TODO IMPLEMENT METHOD
    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/joinCompetition")
    public ResponseEntity<?> joinCompetition(@RequestBody String competitionName,
                                             UserPrincipal userPrincipal,
                                             @PathVariable String teamName) {

        return tournamentServiceHaveToBeExtracted.teamJoinCompetition(teamName, competitionName, userPrincipal.getName());
    }

    //TODO IMPLEMENT METHOD
    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/leaveCompetition")
    public ResponseEntity<?> rejectionCompetition(@RequestBody String competitionName,
                                                  UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return tournamentServiceHaveToBeExtracted.teamLeaveCompetition(teamName, competitionName, userPrincipal.getName());
    }
}
