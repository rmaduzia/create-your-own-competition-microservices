package pl.createcompetition.tournamentservice.tournament.participation;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;


@AllArgsConstructor
@RestController
@RequestMapping("team")
public class TournamentTeamController {

    private final TournamentTeamService tournamentService;

    @RolesAllowed("user")
    @PostMapping("{teamName}/joinTournament")
    public ResponseEntity<?> joinTeamToTournament(@RequestBody String recruitName,
                                                  UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return tournamentService.teamJoinTournament(teamName, recruitName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PostMapping("{teamName}leaveTournament")
    public ResponseEntity<?> teamLeaveTournament(@RequestBody String recruitName,
                                                 UserPrincipal userPrincipal,
                                                 @PathVariable String teamName) {

        return tournamentService.teamLeaveTournament(teamName, recruitName, userPrincipal.getName());
    }
}
