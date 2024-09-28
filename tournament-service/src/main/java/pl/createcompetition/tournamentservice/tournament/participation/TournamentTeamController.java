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
@RequestMapping("tournament")
public class TournamentTeamController {

    private final TournamentTeamService tournamentService;

    @RolesAllowed("user")
    @PostMapping("/{tournamentName}/team/joinTournament")
    public ResponseEntity<String> joinTeamToTournament(@RequestBody String teamName,
                                                  UserPrincipal userPrincipal,
                                                  @PathVariable String tournamentName) {

        return tournamentService.teamJoinTournament(tournamentName, teamName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PostMapping("/{tournamentName}/team/leaveTournament")
    public ResponseEntity<?> teamLeaveTournament(@RequestBody String teamName,
                                                 UserPrincipal userPrincipal,
                                                 @PathVariable String tournamentName) {

        return tournamentService.teamLeaveTournament(tournamentName, teamName, userPrincipal.getName());
    }
}
