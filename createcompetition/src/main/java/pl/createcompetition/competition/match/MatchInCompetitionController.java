package pl.createcompetition.competition.match;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RestController
@RequestMapping("competition/{competitionName}")
public class MatchInCompetitionController {

    private final MatchInCompetitionService matchInCompetitionService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public PagedResponseDto<?> searchMatchInCompetition(@RequestParam(value = "search") @NotBlank String search,
                                                          @Valid PaginationInfoRequest paginationInfoRequest) {

        return matchInCompetitionService.searchMatchInCompetition(search, paginationInfoRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<?> addMatchInCompetition(@Valid @RequestBody MatchInCompetition matchInCompetition,
                                                     @PathVariable String competitionName,
                                                     @CurrentUser UserPrincipal userPrincipal) {

        return matchInCompetitionService.addMatchInCompetition(matchInCompetition, competitionName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{matchId}")
    public ResponseEntity<?> updateMatchInCompetition(@Valid  @RequestBody MatchInCompetition matchInCompetition,
                                                        @PathVariable Long matchId,
                                                        @CurrentUser UserPrincipal userPrincipal) {

        return matchInCompetitionService.updateMatchInCompetition(matchInCompetition, matchId, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{matchId}")
    public ResponseEntity<?> deleteMatchInCompetition(@PathVariable Long matchId,
                                                        @CurrentUser UserPrincipal userPrincipal) {

        return matchInCompetitionService.deleteMatchInCompetition(matchId, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("matches/{matchId}/voting")
    public ResponseEntity<?> addVoteForWinnerTeam(@PathVariable Long matchId,
                                                  @RequestBody String winnerTeam,
                                                  @CurrentUser UserPrincipal userPrincipal) {

        return matchInCompetitionService.addVoteForWinnerTeam(matchId, winnerTeam, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("matches/{matchId}/close")
    public ResponseEntity<?> closeMatch (@PathVariable Long matchId,
                                         @CurrentUser UserPrincipal userPrincipal) {

        return matchInCompetitionService.closeMatch(matchId, userPrincipal);
    }
}