package pl.createcompetition.teamservice.all;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.createcompetition.teamservice.microserviceschanges.UserPrincipal;


@AllArgsConstructor
@RestController
@RequestMapping("team")
public class TeamController {

    private final TeamService teamService;


    @GetMapping("test-endpoint")
    public UserPrincipal testEndpoint(UserPrincipal userPrincipal) {

        System.out.println("Principal: " + userPrincipal);

        return userPrincipal;
    }

    @GetMapping
    @ResponseBody
    public PagedResponseDto<?> searchTeam(@RequestParam(value = "search") @NotBlank String search,
                                          @Valid PaginationInfoRequest paginationInfoRequest) {
        return teamService.searchTeam(search, paginationInfoRequest);

    }


    @PostMapping
    @RolesAllowed("user")
    public ResponseEntity<?> addTeam(@RequestBody CreateTeamRequest createTeamRequest,
                                     UserPrincipal userPrincipal) {

        return teamService.addTeam(createTeamRequest, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @PutMapping("{teamName}")
    public ResponseEntity<?> updateTeam(@Valid @RequestBody Team team,
                                        UserPrincipal userPrincipal,
                                        @PathVariable String teamName) {

        return teamService.updateTeam(teamName, team, userPrincipal.getName());
    }

    @RolesAllowed("user")
    @DeleteMapping("{teamName}")
    public ResponseEntity<?> deleteTeam(@PathVariable String teamName,
                                        UserPrincipal userPrincipal) {

        return teamService.deleteTeam(teamName, userPrincipal.getName());
    }


    @RolesAllowed("user")
    @PostMapping("{teamName}/addRecruit")
    public ResponseEntity<?> addRecruitToTeam(@RequestParam String recruitName,
                                              UserPrincipal userPrincipal,
                                              @PathVariable String teamName) {

        return teamService.addRecruitToTeam(teamName, recruitName,userPrincipal.getName());
    }

    @RolesAllowed("user")
    @DeleteMapping("{teamName}/removeRecruit")
    public ResponseEntity<?> removeMemberFromTeam(@RequestParam String recruitName,
                                                  UserPrincipal userPrincipal,
                                                  @PathVariable String teamName) {

        return teamService.removeMemberFromTeam(teamName, recruitName, userPrincipal);
    }

    @GetMapping("team-members/{teamName}")
    @RolesAllowed("user")
    public List<String> getListOfTeamMembers(@RequestBody String teamName) {
        return teamService.getListOfTeamMembers(teamName);
    }


}
