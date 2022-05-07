package pl.createcompetition.team;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.team.Team;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.CurrentUser;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.team.TeamService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RestController
@RequestMapping("team")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @ResponseBody
    public PagedResponseDto<?> searchTeam(@RequestParam(value = "search") @NotBlank String search,
                                          @Valid PaginationInfoRequest paginationInfoRequest) {
        return teamService.searchTeam(search, paginationInfoRequest);

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> addTeam(@Valid @RequestBody Team team,
                                     @CurrentUser UserPrincipal userPrincipal) {

        return teamService.addTeam(team, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("{teamName}")
    public ResponseEntity<?> updateTeam(@Valid @RequestBody Team team,
                                        @CurrentUser UserPrincipal userPrincipal,
                                        @PathVariable String teamName) {

        return teamService.updateTeam(teamName, team, userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("{teamName}")
    public ResponseEntity<?> deleteTeam(@PathVariable String teamName,
                                        @CurrentUser UserPrincipal userPrincipal) {

        return teamService.deleteTeam(teamName, userPrincipal);
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/addRecruit")
    public ResponseEntity<?> addRecruitToTeam(@RequestBody String recruitName,
                                              @CurrentUser UserPrincipal userPrincipal,
                                              @PathVariable String teamName) {

        return teamService.addRecruitToTeam(teamName, recruitName,userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/deleteRecruit")
    public ResponseEntity<?> deleteMemberFromTeam(@RequestBody String recruitName,
                                                  @CurrentUser UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return teamService.deleteMemberFromTeam(teamName, recruitName,userPrincipal);
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/joinTournament")
    public ResponseEntity<?> joinTeamToTournament(@RequestBody String recruitName,
                                                  @CurrentUser UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return teamService.teamJoinTournament(teamName, recruitName,userPrincipal);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}leaveTournament")
    public ResponseEntity<?> teamLeaveTournament(@RequestBody String recruitName,
                                                 @CurrentUser UserPrincipal userPrincipal,
                                                 @PathVariable String teamName) {

        return teamService.teamLeaveTournament(teamName, recruitName,userPrincipal);
    }


    //TODO IMPLEMENT METHOD
    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/joinCompetition")
    public ResponseEntity<?> joinCompetition(@RequestBody String competitionName,
                                             @CurrentUser UserPrincipal userPrincipal,
                                             @PathVariable String teamName) {

        return teamService.teamJoinCompetition(teamName, competitionName, userPrincipal);
    }

    //TODO IMPLEMENT METHOD
    @PreAuthorize("hasRole('USER')")
    @PostMapping("{teamName}/leaveCompetition")
    public ResponseEntity<?> rejectionCompetition(@RequestBody String competitionName,
                                                  @CurrentUser UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return teamService.teamLeaveCompetition(teamName, competitionName, userPrincipal);
    }
}
