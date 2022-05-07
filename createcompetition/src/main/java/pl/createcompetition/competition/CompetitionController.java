package pl.createcompetition.competition;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.competition.CompetitionService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    @ResponseBody
    public PagedResponseDto<?> searchUserDetail(@RequestParam(value = "search") @NotBlank String search,
                                                @Valid PaginationInfoRequest paginationInfoRequest) {
        return competitionService.searchCompetition(search, paginationInfoRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<?> addCompetition(@Valid @RequestBody Competition competition,
                                            @CurrentUser UserPrincipal userPrincipal) {

        return competitionService.addCompetition(competition, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{competitionName}")
    public ResponseEntity<?> updateCompetition(@Valid @RequestBody Competition competition,
                                               @CurrentUser UserPrincipal userPrincipal,
                                               @PathVariable String competitionName) {

        return competitionService.updateCompetition(competitionName, competition, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{competitionName}")
    public ResponseEntity<?> deleteCompetition(@PathVariable String competitionName,
                                               @CurrentUser UserPrincipal userPrincipal) {

        return competitionService.deleteCompetition(competitionName, userPrincipal);
    }
}