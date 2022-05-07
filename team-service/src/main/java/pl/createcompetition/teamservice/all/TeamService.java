package pl.createcompetition.teamservice.all;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.teamservice.exception.BadRequestException;
import pl.createcompetition.teamservice.exception.ResourceAlreadyExistException;
import pl.createcompetition.teamservice.exception.ResourceNotFoundException;
import pl.createcompetition.teamservice.notification.NotificationMessagesToUsersService;
import pl.createcompetition.teamservice.query.GetQueryImplService;
import pl.createcompetition.teamservice.security.UserPrincipal;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;

    private final NotificationMessagesToUsersService notificationMessagesToUsersService;
    private final GetQueryImplService<Team,?> queryTeamService;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public PagedResponseDto<?> searchTeam(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryTeamService.execute(Team.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addTeam (Team team, UserPrincipal userPrincipal) {

        if (!teamRepository.existsTeamByTeamNameIgnoreCase(team.getTeamName())) {
            team.setTeamOwner(userPrincipal.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(teamRepository.save(team));
        } else{
            throw new ResourceAlreadyExistException("Team", "Name", team.getTeamName());
        }
    }

    public ResponseEntity<?> updateTeam (String teamName, Team team, UserPrincipal userPrincipal) {

        if (!team.getTeamName().equals(teamName)) {
            throw new BadRequestException("Tean Name doesn't match with Team object");
        }

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(team.getTeamName(), userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);

        return ResponseEntity.ok(teamRepository.save(team));

    }

    public ResponseEntity<?> deleteTeam (String teamName, UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);

        teamRepository.deleteById(foundTeam.getId());

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> addRecruitToTeam(String teamName, String recruitName, UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);

 //       Optional<UserDetail> findRecruit = Optional.ofNullable(userDetailRepository.findByUserName(recruitName).orElseThrow(() ->
  //              new ResourceNotFoundException("UserName not exists", "Name", recruitName)));

    //    foundTeam.addRecruitToTeam(findRecruit.get());
        teamRepository.save(foundTeam);

        notificationMessagesToUsersService.notificationMessageToUser(recruitName, "Team","invite", foundTeam.getTeamName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> deleteMemberFromTeam(String teamName, String userNameToDelete, UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);

  //      UserDetail findRecruit = userDetailRepository.findByUserName(userNameToDelete).orElseThrow(() ->
   //             new ResourceNotFoundException("UserName not exists", "Name", userNameToDelete));

    //    checkIfUserIsMemberOfTeam(foundTeam, findRecruit);

     //   foundTeam.deleteRecruitFromTeam(findRecruit);

        teamRepository.save(foundTeam);
        notificationMessagesToUsersService.notificationMessageToUser(userNameToDelete, "Have been","deleted", foundTeam.getTeamName());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> teamJoinTournament(String teamName, String tournamentName,UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);

     //   Tournament findTournament = getTournament(tournamentName);

    //    if (findTournament.getMaxAmountOfTeams() == findTournament.getTeams().size()) {
     //       throw new BadRequestException("There is already the maximum number of teams");
       // }

    //    foundTeam.addTeamToTournament(findTournament);


        // Send notification to Team Members
     //   for (UserDetail userDetail: foundTeam.getUserDetails()) {
   //         notificationMessagesToUsersService.notificationMessageToUser(userDetail.getUserName(), "Team","joined tournament: ", tournamentName);
    //    }


     //   return ResponseEntity.ok(teamRepository.save(foundTeam));
            return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> teamLeaveTournament(String teamName, String tournamentName,UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);

     //   Tournament findTournament = getTournament(tournamentName);

    //    foundTeam.deleteTeamFromTournament(findTournament);

        // Send notification to Team Members
    //    for (UserDetail userDetail: foundTeam.getUserDetails()) {
   //         notificationMessagesToUsersService.notificationMessageToUser(userDetail.getUserName(), "Team","left tournament: ", tournamentName);
   //     }

       // return ResponseEntity.ok(teamRepository.save(foundTeam));
        return ResponseEntity.ok().build();

    }

    public ResponseEntity<?> teamJoinCompetition(String teamName, String competitionName,UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);
/*
        Competition findCompetition = getCompetition(competitionName);

        if (findCompetition.getMaxAmountOfTeams() == findCompetition.getTeams().size()) {
            throw new BadRequestException("There is already the maximum number of teams");
        }

        foundTeam.addTeamToCompetition(findCompetition);

        // Send notification to Team Members
        for (UserDetail userDetail: foundTeam.getUserDetails()) {
            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getUserName(), "Team","joined competition: ", competitionName);
        }



 */
        //return ResponseEntity.ok(teamRepository.save(foundTeam));
        return ResponseEntity.ok().build();

    }


    public ResponseEntity<?> teamLeaveCompetition(String teamName, String competitionName,UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getUsername());
        checkIfTeamBelongToUser(foundTeam, userPrincipal);
/*
        Competition findCompetition = getCompetition(competitionName);

        foundTeam.deleteTeamFromCompetition(findCompetition);

        // Send notification to Team Members
        for (UserDetail userDetail: foundTeam.getUserDetails()) {
            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getUserName(), "Team","left tournament: ", competitionName);
        }

        return ResponseEntity.ok(teamRepository.save(foundTeam));

 */
        return ResponseEntity.ok().build();

    }

    private void checkIfTeamBelongToUser(Team team, UserPrincipal userPrincipal) {
            if (!team.getTeamOwner().equals(userPrincipal.getUsername())) {
                throw new ResourceNotFoundException("Team named: " + team.getTeamName(), "Owner", userPrincipal.getUsername());
            }
        }

        /*
    private void checkIfUserIsMemberOfTeam(Team team, UserDetail userDetail) {
        if(!team.getUserDetails().contains(userDetail)) {
            throw new ResourceNotFoundException("User named: " + userDetail.getUserName(), "Team", team.getTeamName());
        }
    }

    private Tournament getTournament(String tournamentName) {
        return tournamentRepository.findByTournamentName(tournamentName).orElseThrow(() ->
                new ResourceNotFoundException("Tournament not exists", "Name", tournamentName));
    }

    private Competition getCompetition(String competitionName) {
        return competitionRepository.findByCompetitionName(competitionName).orElseThrow(() ->
                new ResourceNotFoundException("Competition not exists", "Name", competitionName));
    }

         */
}