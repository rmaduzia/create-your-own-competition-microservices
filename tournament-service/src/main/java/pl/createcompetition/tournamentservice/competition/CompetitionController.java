package pl.createcompetition.tournamentservice.competition;

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
@RequestMapping("competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    @ResponseBody
    public PagedResponseDto<?> searchUserDetail(@RequestParam(value = "search") @NotBlank String search,
                                                @Valid PaginationInfoRequest paginationInfoRequest) {
        return competitionService.searchCompetition(search, paginationInfoRequest);
    }

    @PostMapping
    @RolesAllowed("user")
    public ResponseEntity<?> createCompetition(@Valid @RequestBody CompetitionCreateUpdateRequest competitionCreateUpdateRequest,
                                            UserPrincipal userPrincipal) {

        return competitionService.addCompetition(competitionCreateUpdateRequest, userPrincipal.getName());
    }

    @PutMapping("{competitionName}")
    @RolesAllowed("user")
    public ResponseEntity<?> updateCompetition(@Valid @RequestBody CompetitionCreateUpdateRequest competitionCreateUpdateRequest,
                                               UserPrincipal userPrincipal,
                                               @PathVariable String competitionName) {

        return competitionService.updateCompetition(competitionName, competitionCreateUpdateRequest, userPrincipal.getName());
    }

    @DeleteMapping("{competitionName}")
    @RolesAllowed("user")
    public ResponseEntity<?> deleteCompetition(@PathVariable String competitionName,
                                               UserPrincipal userPrincipal) {

        return competitionService.deleteCompetition(competitionName, userPrincipal.getName());
    }
}