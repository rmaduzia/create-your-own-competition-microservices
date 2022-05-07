package pl.createcompetition.competition;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.model.Tag;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.competition.CompetitionTagService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/competition/tags")
public class CompetitionTagController {

    private final CompetitionTagService competitionTagService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<?> getTagsToCompetition(@RequestBody List<String> tagCompetition) {
        return competitionTagService.getCompetitionTag(tagCompetition);

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("{competitionName}")
    public ResponseEntity<?> addTagsToCompetition(@Valid @RequestBody Set<Tag> tagCompetition,
                                                  @PathVariable String competitionName,
                                                  @CurrentUser UserPrincipal userPrincipal) {

        return competitionTagService.addCompetitionTag(tagCompetition, competitionName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{competitionName}")
    public ResponseEntity<?> updateTagCompetition(@Valid @RequestBody Tag tagCompetition,
                                                  @PathVariable String competitionName,
                                                  @CurrentUser UserPrincipal userPrincipal) {

        return competitionTagService.updateCompetitionTag(tagCompetition, competitionName, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{competitionName}")
    public ResponseEntity<?> deleteTagCompetition(@RequestBody Tag tagCompetition,
                                                  @PathVariable String competitionName,
                                                  @CurrentUser UserPrincipal userPrincipal) {

        return competitionTagService.deleteCompetitionTag(tagCompetition, competitionName, userPrincipal);
    }


}
