package pl.createcompetition.tournament;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.tournament.Tournament;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.tournament.TournamentService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RestController
@RequestMapping("tournament")
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    @ResponseBody
    public PagedResponseDto<?> searchTournament(@RequestParam(value = "search") @NotBlank String search,
                                                @Valid PaginationInfoRequest paginationInfoRequest) {
        return tournamentService.searchTournament(search, paginationInfoRequest);

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> addTournament(@Valid @RequestBody Tournament tournament,
                                           @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.addTournament(tournament,userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{tournamentName}")
    public ResponseEntity<?> updateTournament(@Valid @RequestBody Tournament tournament,
                                              @CurrentUser UserPrincipal userPrincipal,
                                              @PathVariable String tournamentName) {

        return tournamentService.updateTournament(tournamentName, tournament, userPrincipal);

    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{tournamentName}")
    public ResponseEntity<?> deleteTournament(@PathVariable String tournamentName,
                                              @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.deleteTournament(tournamentName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{tournamentName}/teams")
    public ResponseEntity<?> deleteTeamFromTournament(@PathVariable String tournamentName,
                                                      @RequestBody String teamName,
                                                      @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.removeTeamFromTournament(tournamentName, teamName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("{tournamentName}/start")
    public ResponseEntity<?> startTournament(@PathVariable String tournamentName,
                                             @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.startTournament(tournamentName, userPrincipal);
    }

    @PreAuthorize("hasROle('USER')")
    @PostMapping("draw_teams")
    public ResponseEntity<?> drawTeamsInTournament(@RequestBody String teamName,
                                                   @RequestParam Boolean matchWithEachOther,
                                                   @CurrentUser UserPrincipal userPrincipal) {

        return tournamentService.drawTeamOptions(matchWithEachOther, teamName, userPrincipal);
    }
}
