package pl.createcompetition.tournamentservice.all.tournament.match;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.tournamentservice.all.tournament.TournamentService;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;


@AllArgsConstructor
@RestController
@RequestMapping("tournament/{tournamentName}")
public class MatchInTournamentController {

    private final TournamentService tournamentService;

    private final MatchInTournamentService matchInTournamentService;


    @RolesAllowed("user")
    @GetMapping
    public PagedResponseDto<?> searchMatchInTournament(@RequestParam(value ="search") @NotBlank String search,
                                                       @Valid PaginationInfoRequest paginationInfoRequest) {

        return matchInTournamentService.searchMatchInTournament(search, paginationInfoRequest);
    }

    @RolesAllowed("user")
    @PostMapping()
    public ResponseEntity<?> addMatchInTournament(@Valid @RequestBody MatchInTournament matchInTournament,
                                                  @PathVariable String tournamentName,
                                                  UserPrincipal userPrincipal) {

        return matchInTournamentService.addMatchInTournament(matchInTournament, tournamentName, userPrincipal);
    }

    @RolesAllowed("user")
    @PutMapping("{matchId}")
    public ResponseEntity<?> updateMatchInTournament(@Valid @RequestBody MatchInTournament matchInTournament,
                                                     @PathVariable Long matchId,
                                                     UserPrincipal userPrincipal) {

        return matchInTournamentService.updateMatchInTournament(matchInTournament, matchId, userPrincipal);
    }

    @RolesAllowed("user")
    @DeleteMapping("{matchId}")
    public ResponseEntity<?> deleteMatchInTournament(@PathVariable Long matchId,
                                                     UserPrincipal userPrincipal) {

        return matchInTournamentService.deleteMatchInTournament(matchId, userPrincipal);
    }

    @RolesAllowed("user")
    @PostMapping("matches")
    @PutMapping("matches")
    public ResponseEntity<?> setTheDatesOfTheTeamsMatches(@PathVariable String tournamentName,
                                                          @RequestBody Map<String, Date> dateMatch,
                                                          UserPrincipal userPrincipal) {

        return tournamentService.setTheDatesOfTheTeamsMatches(tournamentName, dateMatch, userPrincipal);

    }

    @RolesAllowed("user")
    @DeleteMapping("matches")
    public ResponseEntity<?> deleteDateOfTheTeamsMatches(@PathVariable String tournamentName,
                                                         @RequestBody String idMatch,
                                                         UserPrincipal userPrincipal) {

        return tournamentService.deleteDateOfTheTeamsMatches(tournamentName, idMatch, userPrincipal);
    }

    @RolesAllowed("user")
    @PostMapping("matches/{matchId}/voting")
    public ResponseEntity<?> addVoteForWinnerTeam(@PathVariable Long matchId,
                                                  @RequestBody String winnerTeam,
                                                  UserPrincipal userPrincipal) {

        return matchInTournamentService.addVoteForWinnerTeam(matchId, winnerTeam, userPrincipal);
    }

    @RolesAllowed("user")
    @PostMapping("matches/{matchId}/close")
    public ResponseEntity<?> closeMatch (@PathVariable Long matchId,
                                         UserPrincipal userPrincipal) {

        return matchInTournamentService.closeMatch(matchId, userPrincipal);
    }

}