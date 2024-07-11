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
public class CompetitionTeamController {

    private final CompetitionTeamService competitionTeamService;

    @RolesAllowed("user")
    @PostMapping("{teamName}/joinCompetition")
    public ResponseEntity<?> joinCompetition(@RequestBody String competitionName,
                                             UserPrincipal userPrincipal,
                                             @PathVariable String teamName) {

        return competitionTeamService.teamJoinCompetition(teamName, competitionName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PostMapping("{teamName}/leaveCompetition")
    public ResponseEntity<?> rejectionCompetition(@RequestBody String competitionName,
                                                  UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return competitionTeamService.teamLeaveCompetition(teamName, competitionName, userPrincipal.getName());
    }
}
