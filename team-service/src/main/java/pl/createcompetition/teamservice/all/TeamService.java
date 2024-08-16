package pl.createcompetition.teamservice.all;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.teamservice.exception.ResourceAlreadyExistException;
import pl.createcompetition.teamservice.exception.ResourceNotFoundException;
//import pl.createcompetition.teamservice.notification.NotificationMessagesToUsersService;
import pl.createcompetition.teamservice.keycloak.KeyCloakService;
import pl.createcompetition.teamservice.query.GetQueryImplService;
import pl.createcompetition.teamservice.microserviceschanges.UserPrincipal;

@AllArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;

//    private final NotificationMessagesToUsersService notificationMessagesToUsersService;
    private final GetQueryImplService<Team,?> queryTeamService;
    private final VerifyMethodsForServices verifyMethodsForServices;
    private final KeyCloakService keyCloakService;

    public PagedResponseDto<?> searchTeam(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryTeamService.execute(Team.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<Team> addTeam (CreateTeamRequest createTeamRequest, String userName) {

        if (!teamRepository.existsTeamByTeamNameIgnoreCase(createTeamRequest.getTeamName())) {
            Team newTeam = Team.builder()
                .teamOwner(userName)
                .teamName(createTeamRequest.getTeamName())
                .city(createTeamRequest.getCity())
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(teamRepository.save(newTeam));
        } else{
            throw new ResourceAlreadyExistException("Team", "Name", createTeamRequest.getTeamName());
        }
    }

    public ResponseEntity<?> updateTeam (String teamName, Team team, String userName) {

        if (!team.getTeamName().equals(teamName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name doesn't match with Team object");
        }

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(team.getTeamName(), userName);
        checkIfTeamBelongToUser(foundTeam, userName);

        return ResponseEntity.ok(teamRepository.save(team));

    }

    public ResponseEntity<?> deleteTeam (String teamName, String userName) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeam, userName);

        teamRepository.deleteById(foundTeam.getId());

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> addRecruitToTeam(String teamName, String recruitName, String userName) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeam, userName);

        // SHOULD VERIFY WITH KEYCLOAK IS RECRUIT NAME IS CORRECT
        if (keyCloakService.getUserByUserName(recruitName) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name: " + recruitName + " does not exists");
        }

        foundTeam.addRecruit(recruitName);

        teamRepository.save(foundTeam);

//        notificationMessagesToUsersService.notificationMessageToUser(recruitName, "Team","invite", foundTeam.getTeamName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> removeMemberFromTeam(String teamName, String userNameToDelete, UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getName());
        checkIfTeamBelongToUser(foundTeam, userPrincipal.getName());

        boolean isRemoved = foundTeam.removeRecruit(userNameToDelete);

        if (!isRemoved)
            throw new ResourceNotFoundException("UserName: " + userNameToDelete
                +  " does not belong to team: " + teamName);


        teamRepository.save(foundTeam);
//        notificationMessagesToUsersService.notificationMessageToUser(userNameToDelete, "Have been","deleted", foundTeam.getTeamName());

        return ResponseEntity.ok().build();
    }



    private void checkIfTeamBelongToUser(Team team, String userName) {
            if (!team.getTeamOwner().equals(userName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team named: " + team.getTeamName() + " does not belong tu: " + userName);
            }
        }

    private void checkIfUserIsMemberOfTeam(Team team, String username) {
        if(!team.getTeamMembers().contains(username)) {
            throw new ResourceNotFoundException("User named: " + username, "Team", team.getTeamName() + " not found in Team");
        }
    }


    public List<String> getListOfTeamMembers(String teamName) {
        return teamRepository.findTeamMembersByTeamName(teamName);
    }
}