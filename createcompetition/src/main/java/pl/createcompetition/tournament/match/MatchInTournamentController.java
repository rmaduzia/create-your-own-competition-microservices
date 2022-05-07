package pl.createcompetition.tournament.match;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.tournament.TournamentService;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("tournament/{tournamentName}")
public class MatchInTournamentController {

    private final TournamentService tournamentService;

    private final MatchInTournamentService matchInTournamentService;


    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public PagedResponseDto<?> searchMatchInTournament(@RequestParam(value ="search") @NotBlank String search,
                                                       @Valid PaginationInfoRequest paginationInfoRequest) {

        return matchInTournamentService.searchMatchInTournament(search, paginationInfoRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<?> addMatchInTournament(@Valid @RequestBody MatchInTournament matchInTournament,
                                                  @PathVariable String tournamentName,
                                                  @CurrentUser UserPrincipal userPrincipal) {

        return matchInTournamentService.addMatchInTournament(matchInTournament, tournamentName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{matchId}")
    public ResponseEntity<?> updateMatchInTournament(@Valid @RequestBody MatchInTournament matchInTournament,
                                                     @PathVariable Long matchId,
                                                     @CurrentUser UserPrincipal userPrincipal) {

        return matchInTournamentService.updateMatchInTournament(matchInTournament, matchId, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{matchId}")
    public ResponseEntity<?> deleteMatchInTournament(@PathVariable Long matchId,
                                                     @CurrentUser UserPrincipal userPrincipal) {

        return matchInTournamentService.deleteMatchInTournament(matchId, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("matches")
    @PutMapping("matches")
    public ResponseEntity<?> setTheDatesOfTheTeamsMatches(@PathVariable String tournamentName,
                                                          @RequestBody Map<String, Date> dateMatch,
                                                          @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.setTheDatesOfTheTeamsMatches(tournamentName, dateMatch, userPrincipal);

    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("matches")
    public ResponseEntity<?> deleteDateOfTheTeamsMatches(@PathVariable String tournamentName,
                                                         @RequestBody String idMatch,
                                                         @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.deleteDateOfTheTeamsMatches(tournamentName, idMatch, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("matches/{matchId}/voting")
    public ResponseEntity<?> addVoteForWinnerTeam(@PathVariable Long matchId,
                                                  @RequestBody String winnerTeam,
                                                  @CurrentUser UserPrincipal userPrincipal) {

        return matchInTournamentService.addVoteForWinnerTeam(matchId, winnerTeam, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("matches/{matchId}/close")
    public ResponseEntity<?> closeMatch (@PathVariable Long matchId,
                                         @CurrentUser UserPrincipal userPrincipal) {

        return matchInTournamentService.closeMatch(matchId, userPrincipal);
    }

}