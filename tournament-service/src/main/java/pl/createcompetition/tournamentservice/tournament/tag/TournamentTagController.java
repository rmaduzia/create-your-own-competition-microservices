package pl.createcompetition.tournamentservice.tournament.tag;


import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.tag.EventTagsDto;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.tournament.Tournament;

@AllArgsConstructor
@RestController
@RequestMapping("/tournament/tags")
public class TournamentTagController {

    private final TournamentTagService tournamentTagService;

    @RolesAllowed("user")
    @GetMapping()
    public List<Tournament> getTournamentsByTag(@RequestBody String tagTournament) {
        return tournamentTagService.getTournamentsByTag(tagTournament);

    }

    @RolesAllowed("user")
    @PostMapping("{tournamentName}")
    public ResponseEntity<EventTagsDto> addTagsToTournament(@RequestBody Set<String> tagCompetition,
                                                  @PathVariable String tournamentName,
                                                  UserPrincipal userPrincipal) {

        return tournamentTagService.addTournamentTag(tagCompetition, tournamentName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PutMapping("{tournamentName}")
    public ResponseEntity<EventTagsDto> updateTagTournament(@RequestBody String tagCompetition,
                                                  @PathVariable String tournamentName,
                                                  UserPrincipal userPrincipal) {

        return tournamentTagService.updateTournamentTag(tagCompetition, tournamentName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @DeleteMapping("{tournamentName}")
    public ResponseEntity<?> deleteTagTournament(@RequestBody String tagCompetition,
                                                  @PathVariable String tournamentName,
                                                  UserPrincipal userPrincipal) {

        return tournamentTagService.deleteTournamentTag(tagCompetition, tournamentName, userPrincipal.getName());
    }


}
