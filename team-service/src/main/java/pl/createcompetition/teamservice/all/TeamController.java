package pl.createcompetition.teamservice.all;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.teamservice.exception.ResourceAlreadyExistException;
import pl.createcompetition.teamservice.exception.ResourceNotFoundException;
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

    @GetMapping("testing-endpoint")
    public ResponseEntity<?> testingEndpoint() {

        String userNameToDelete = "userNameToDelete";
        String teamName = "teamName";

//        throw new ResourceNotFoundException("UserName: " + userNameToDelete +  "does not belong to team: " + teamName);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserName999999999999999999999999999");
//        throw new ResourceNotFoundException("UserName7777777777777777777777777");

//        throw new ResourceNotFoundException("99999999999999999999");


    }

}
