package pl.createcompetition.tournamentservice.competition.match;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;

@AllArgsConstructor
@RestController
@RequestMapping("competition/{competitionName}")
public class MatchInCompetitionController {

    private final MatchInCompetitionService matchInCompetitionService;

    @RolesAllowed("user")
    @GetMapping()
    public PagedResponseDto<?> searchMatchInCompetition(@RequestParam(value = "search") @NotBlank String search,
                                                          @Valid PaginationInfoRequest paginationInfoRequest) {

        return matchInCompetitionService.searchMatchInCompetition(search, paginationInfoRequest);
    }

    @RolesAllowed("user")
    @PostMapping()
    public ResponseEntity<?> addMatchInCompetition(@Valid @RequestBody MatchInCompetition matchInCompetition,
                                                     @PathVariable String competitionName,
                                                     UserPrincipal userPrincipal) {

        return matchInCompetitionService.addMatchInCompetition(matchInCompetition, competitionName, userPrincipal);
    }

    @RolesAllowed("user")
    @PutMapping("{matchId}")
    public ResponseEntity<?> updateMatchInCompetition(@Valid  @RequestBody MatchInCompetition matchInCompetition,
                                                        @PathVariable Long matchId,
                                                       UserPrincipal userPrincipal) {

        return matchInCompetitionService.updateMatchInCompetition(matchInCompetition, matchId, userPrincipal);
    }

    @RolesAllowed("user")
    @DeleteMapping("{matchId}")
    public ResponseEntity<?> deleteMatchInCompetition(@PathVariable Long matchId,
                                                      UserPrincipal userPrincipal) {

        return matchInCompetitionService.deleteMatchInCompetition(matchId, userPrincipal);
    }

    @RolesAllowed("user")
    @PostMapping("matches/{matchId}/voting")
    public ResponseEntity<?> addVoteForWinnerTeam(@PathVariable Long matchId,
                                                  @RequestBody String winnerTeam,
                                                  UserPrincipal userPrincipal) {

        return matchInCompetitionService.addVoteForWinnerTeam(matchId, winnerTeam, userPrincipal);
    }

    @RolesAllowed("user")
    @PostMapping("matches/{matchId}/close")
    public ResponseEntity<?> closeMatch (@PathVariable Long matchId,
                                         UserPrincipal userPrincipal) {

        return matchInCompetitionService.closeMatch(matchId, userPrincipal);
    }
}