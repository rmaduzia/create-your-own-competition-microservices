package pl.createcompetition.teamservice.all;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.teamservice.exception.BadRequestException;
import pl.createcompetition.teamservice.exception.ResourceAlreadyExistException;
import pl.createcompetition.teamservice.exception.ResourceNotFoundException;
import pl.createcompetition.teamservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.teamservice.query.GetQueryImplService;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TournamentServiceHaveToBeExtracted {

    private final TeamRepository teamRepository;

//    private final NotificationMessagesToUsersService notificationMessagesToUsersService;
    private final GetQueryImplService<Team,?> queryTeamService;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public PagedResponseDto<?> searchTeam(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryTeamService.execute(Team.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> teamJoinTournament(String teamName, String tournamentName,UserPrincipal userPrincipal) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userPrincipal.getName());
        checkIfTeamBelongToUser(foundTeam, userPrincipal.getName());

     //   Tournament findTournament = getTournament(tournamentName);

    //    if (findTournament.getMaxAmountOfTeams() == findTournament.getTeams().size()) {
     //       throw new BadRequestException("There is already the maximum number of teams");
       // }

    //    foundTeam.addTeamToTournament(findTournament);


        // Send notification to Team Members
     //   for (UserDetail userDetail: foundTeam.getUserDetails()) {
   //         notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","joined tournament: ", tournamentName);
    //    }


     //   return ResponseEntity.ok(teamRepository.save(foundTeam));
            return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> teamLeaveTournament(String teamName, String tournamentName,String userName) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeam, userName);

     //   Tournament findTournament = getTournament(tournamentName);

    //    foundTeam.deleteTeamFromTournament(findTournament);

        // Send notification to Team Members
    //    for (UserDetail userDetail: foundTeam.getUserDetails()) {
   //         notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","left tournament: ", tournamentName);
   //     }

       // return ResponseEntity.ok(teamRepository.save(foundTeam));
        return ResponseEntity.ok().build();

    }

    public ResponseEntity<?> teamJoinCompetition(String teamName, String competitionName, String userName) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeam, userName);
/*
        Competition findCompetition = getCompetition(competitionName);

        if (findCompetition.getMaxAmountOfTeams() == findCompetition.getTeams().size()) {
            throw new BadRequestException("There is already the maximum number of teams");
        }

        foundTeam.addTeamToCompetition(findCompetition);

        // Send notification to Team Members
        for (UserDetail userDetail: foundTeam.getUserDetails()) {
            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","joined competition: ", competitionName);
        }



 */
        //return ResponseEntity.ok(teamRepository.save(foundTeam));
        return ResponseEntity.ok().build();

    }


    public ResponseEntity<?> teamLeaveCompetition(String teamName, String competitionName, String userName) {

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName, userName);
        checkIfTeamBelongToUser(foundTeam, userName);
/*
        Competition findCompetition = getCompetition(competitionName);

        foundTeam.deleteTeamFromCompetition(findCompetition);

        // Send notification to Team Members
        for (UserDetail userDetail: foundTeam.getUserDetails()) {
            notificationMessagesToUsersService.notificationMessageToUser(userDetail.getName(), "Team","left tournament: ", competitionName);
        }

        return ResponseEntity.ok(teamRepository.save(foundTeam));

 */
        return ResponseEntity.ok().build();
    }

    private void checkIfTeamBelongToUser(Team team, String userName) {
            if (!team.getTeamOwner().equals(userName)) {
                throw new ResourceNotFoundException("Team named: " + team.getTeamName(), "Owner", userName);
            }
        }

//    private void checkIfUserIsMemberOfTeam(Team team, String username) {
//        if(team.getTeam_members().containsValue(username)) {
//            throw new ResourceNotFoundException("User named: " + username, "Team", team.getTeamName() + " not found in Team");
//        }
//    }

//    private Tournament getTournament(String tournamentName) {
//        return tournamentRepository.findByTournamentName(tournamentName).orElseThrow(() ->
//                new ResourceNotFoundException("Tournament not exists", "Name", tournamentName));
//    }

//    private Competition getCompetition(String competitionName) {
//        return competitionRepository.findByCompetitionName(competitionName).orElseThrow(() ->
//                new ResourceNotFoundException("Competition not exists", "Name", competitionName));
//    }


}