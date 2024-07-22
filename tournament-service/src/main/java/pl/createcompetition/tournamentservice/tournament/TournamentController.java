package pl.createcompetition.tournamentservice.tournament;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;


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

    @RolesAllowed("user")
    @PostMapping
    public ResponseEntity<?> addTournament(@Valid @RequestBody TournamentCreateUpdateRequest tournament,
                                           UserPrincipal userPrincipal) {

        return tournamentService.addTournament(tournament,userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PutMapping("{tournamentName}")
    public ResponseEntity<?> updateTournament(@Valid @RequestBody TournamentCreateUpdateRequest tournament,
                                              UserPrincipal userPrincipal,
                                              @PathVariable String tournamentName) {

        return tournamentService.updateTournament(tournamentName, tournament, userPrincipal.getName());

    }

    @RolesAllowed("user")
    @DeleteMapping("{tournamentName}")
    public ResponseEntity<?> deleteTournament(@PathVariable String tournamentName,
                                              UserPrincipal userPrincipal) {

        return tournamentService.deleteTournament(tournamentName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @DeleteMapping("{tournamentName}/teams")
    public ResponseEntity<?> deleteTeamFromTournament(@PathVariable String tournamentName,
                                                      @RequestBody String teamName,
                                                      UserPrincipal userPrincipal) {

        return tournamentService.removeTeamFromTournament(tournamentName, teamName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PostMapping("{tournamentName}/start")
    public ResponseEntity<?> startTournament(@PathVariable String tournamentName,
                                             UserPrincipal userPrincipal) {

        return tournamentService.startTournament(tournamentName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PostMapping("draw_teams")
    public ResponseEntity<?> drawTeamsInTournament(@RequestBody String teamName,
                                                   @RequestParam Boolean matchWithEachOther,
                                                   UserPrincipal userPrincipal) {

        return tournamentService.drawTeamOptions(matchWithEachOther, teamName, userPrincipal.getName());
    }
}
