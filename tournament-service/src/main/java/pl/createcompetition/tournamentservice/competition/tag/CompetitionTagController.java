package pl.createcompetition.tournamentservice.competition.tag;


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
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.Tag;

@AllArgsConstructor
@RestController
@RequestMapping("/competition/tags")
public class CompetitionTagController {

    private final CompetitionTagService competitionTagService;

    @RolesAllowed("user")
    @GetMapping()
    public List<Competition> getTagsToCompetition(@RequestBody String tagCompetition) {
        return competitionTagService.getCompetitionsTag(tagCompetition);

    }

    @RolesAllowed("user")
    @PostMapping("{competitionName}")
    public ResponseEntity<?> addTagsToCompetition(@RequestBody Set<Tag> tagCompetition,
                                                  @PathVariable String competitionName,
                                                  UserPrincipal userPrincipal) {

        return competitionTagService.addCompetitionTag(tagCompetition, competitionName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PutMapping("{competitionName}")
    public ResponseEntity<?> updateTagCompetition(@RequestBody Tag tagCompetition,
                                                  @PathVariable String competitionName,
                                                  UserPrincipal userPrincipal) {

        return competitionTagService.updateCompetitionTag(tagCompetition, competitionName, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @DeleteMapping("{competitionName}")
    public ResponseEntity<?> deleteTagCompetition(@RequestBody Tag tagCompetition,
                                                  @PathVariable String competitionName,
                                                  UserPrincipal userPrincipal) {

        return competitionTagService.deleteCompetitionTag(tagCompetition, competitionName, userPrincipal.getName());
    }


}
