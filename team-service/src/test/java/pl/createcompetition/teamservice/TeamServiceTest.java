package pl.createcompetition.teamservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.teamservice.all.CreateTeamRequest;
import pl.createcompetition.teamservice.all.Team;
import pl.createcompetition.teamservice.all.TeamRepository;
import pl.createcompetition.teamservice.all.TeamService;
import pl.createcompetition.teamservice.all.VerifyMethodsForServices;
import pl.createcompetition.teamservice.exception.ResourceAlreadyExistException;
import pl.createcompetition.teamservice.exception.ResourceNotFoundException;
//import pl.createcompetition.teamservice.notification.NotificationMessagesToUsersService;
import pl.createcompetition.teamservice.notification.NotificationRepository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {


    @Mock
    TeamRepository teamRepository;
//    @Mock
//    UserDetailRepository userDetailRepository;

    @Mock
    NotificationRepository notificationRepository;
    @InjectMocks
    TeamService teamService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

//    @Mock
//    NotificationMessagesToUsersService notificationMessagesToUsersService;

    Team team;
    CreateTeamRequest createTeamRequest;

    String recruitName;
    String userName;



    @BeforeEach
    public void setUp() {

        userName = "test";

        recruitName = "testRecruit";

        team = Team.builder()
                .id(1L)
                .teamOwner(userName)
                .teamName("team1")
                .isOpenRecruitment(true)
                .city("Gdynia").build();

        createTeamRequest = CreateTeamRequest.builder()
            .teamName("team1")
            .city("Gdynia")
            .build();
    }

    @Test
    public void shouldAddTeam() {

        when(teamRepository.existsTeamByTeamNameIgnoreCase(team.getTeamName())).thenReturn(false);
        when(teamRepository.save(team)).thenReturn(team);

        ResponseEntity<Team> response = teamService.addTeam(createTeamRequest, userName);

        verify(teamRepository, times(1)).existsTeamByTeamNameIgnoreCase(team.getTeamName());
        verify(teamRepository, times(1)).save(team);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), team);
    }

    @Test
    public void shouldUpdateTeam() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(teamRepository.save(team)).thenReturn(team);
        team.setMaxAmountMembers(15);

        ResponseEntity<?> response = teamService.updateTeam(team.getTeamName(),team, userName);

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        verify(teamRepository, times(1)).save(team);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), team);
    }

    @Test
    public void shouldDeleteTeam() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);

        ResponseEntity<?> response = teamService.deleteTeam(team.getTeamName(), userName);

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        verify(teamRepository, times(1)).deleteById(team.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionTeamNotExists() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenThrow(new ResourceNotFoundException("Team not exists", "Name", team.getTeamName()));

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teamService.updateTeam(team.getTeamName(),team, userName),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        assertEquals("Team not exists not found with Name : '"+ team.getTeamName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTeamAlreadyExists() {

        when(teamRepository.existsTeamByTeamNameIgnoreCase(team.getTeamName())).thenReturn(true);

        Exception exception = assertThrows(
                ResourceAlreadyExistException.class,
                () -> teamService.addTeam(createTeamRequest, userName),
                "Expected doThing() to throw, but it didn't");

        verify(teamRepository, times(1)).existsTeamByTeamNameIgnoreCase(team.getTeamName());
        assertEquals("Team already exists with Name : '"+ team.getTeamName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTeamNotBelongToUser() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);

        team.setTeamOwner("OtherOwner");

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teamService.updateTeam(team.getTeamName(),team, userName),
                "Expected doThing() to throw, but it didn't");

        assertEquals("Team named: "+ team.getTeamName()+ " not found with Owner : " + "'"+ userName+"'", exception.getMessage());
    }

    @Test
    public void shouldAddRecruitToTeam() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
//        when(userDetailRepository.findByUserName(userDetailTeamMember.getUserName())).thenReturn(Optional.of(userDetailTeamMember));

        ResponseEntity<?> response = teamService.addRecruitToTeam(team.getTeamName(), recruitName, userName);

        verify(teamRepository, times(1)).save(team);
        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

















//    @Test
//    public void shouldJoinTournament() {
//
//        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
//        when(tournamentRepository.findByTournamentName(ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
//        when(teamRepository.save(team)).thenReturn(team);
//
//        ResponseEntity<?> response = teamService.teamJoinTournament(team.getTeamName(), tournament.getTournamentName(), userPrincipal);
//
//        verify(teamRepository, times(1)).save(team);
//        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(), team);
//    }


//
//    @Test
//    public void shouldLeaveTournament() {
//
//        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
//        when(tournamentRepository.findByTournamentName(ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
//        when(teamRepository.save(team)).thenReturn(team);
//
//        ResponseEntity<?> response = teamService.teamJoinTournament(team.getTeamName(), tournament.getTournamentName(), userPrincipal);
//
//        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
//        verify(teamRepository, times(1)).save(team);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(), team);
//    }
//
//    @Test
//    public void shouldJoinCompetition() {
//
//        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
//        when(competitionRepository.findByCompetitionName(ArgumentMatchers.anyString())).thenReturn(Optional.of(competition));
//        when(teamRepository.save(team)).thenReturn(team);
//
//        ResponseEntity<?> response =  teamService.teamJoinCompetition(team.getTeamName(), tournament.getTournamentName(), userPrincipal);
//
//        verify(teamRepository, times(1)).save(team);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(), team);
//    }
//
//    @Test
//    public void shouldLeaveCompetition() {
//
//        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
//        when(competitionRepository.findByCompetitionName(ArgumentMatchers.anyString())).thenReturn(Optional.of(competition));
//        when(teamRepository.save(team)).thenReturn(team);
//
//        ResponseEntity<?> response = teamService.teamJoinCompetition(team.getTeamName(), tournament.getTournamentName(), userPrincipal);
//
//        verify(teamRepository, times(1)).save(team);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(response.getBody(), team);
//    }





    //Working on this test Case
/*
    @Test
    public void shouldSendNotification() {


        when(teamRepository.findByTeamNameAndTeamOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(team));
        when(userDetailRepository.findByUserName(userDetailTeamMember.getUserName())).thenReturn(Optional.of(userDetailTeamMember));

        doCallRealMethod().when(teamService).notificationMessageToUser(userDetailTeamMember.getUserName(),"Team","invite",team.getTeamName());

       // teamService.addRecruitToTeam(team.getTeamName(), userDetailTeamMember.getUserName(), userPrincipal);


        teamService.notificationMessageToUser(userDetailTeamMember.getUserName(),"Team","invite",team.getTeamName());

        verify(notificationRepository, times(1)).save(ArgumentMatchers.any(UserNotification.class));
    }
 */


}
