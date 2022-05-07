package pl.createcompetition.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.competition.CompetitionRepository;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.notification.NotificationMessagesToUsersService;
import pl.createcompetition.notification.NotificationRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.team.Team;
import pl.createcompetition.team.TeamRepository;
import pl.createcompetition.team.TeamService;
import pl.createcompetition.tournament.Tournament;
import pl.createcompetition.tournament.TournamentRepository;
import pl.createcompetition.user.User;
import pl.createcompetition.user.detail.UserDetail;
import pl.createcompetition.user.detail.UserDetailRepository;
import pl.createcompetition.util.VerifyMethodsForServices;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;
    @Mock
    UserDetailRepository userDetailRepository;
    @Mock
    TournamentRepository tournamentRepository;
    @Mock
    CompetitionRepository competitionRepository;
    @Mock
    NotificationRepository notificationRepository;
    @InjectMocks
    TeamService teamService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;
    @Mock
    NotificationMessagesToUsersService notificationMessagesToUsersService;

    User user;
    User userTeamMember;
    UserDetail userDetail;
    UserDetail userDetailTeamMember;
    UserPrincipal userPrincipal;
    Team team;
    Tournament tournament;
    Competition competition;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .password("Password%123")
                .id(1L).provider(AuthProvider.local)
                .email("test@mail.com").emailVerified(true).build();

        userPrincipal = UserPrincipal.create(user);

        userDetail = UserDetail.builder()
                .id(1L)
                .user(user)
                .age(15)
                .city("Gdynia")
                .gender(Gender.FEMALE).build();

        team = Team.builder()
                .id(1L)
                .teamOwner("test@mail.com")
                .teamName("team1")
                .isOpenRecruitment(true)
                .city("Gdynia").build();

        // SECOND USER - TEAM MEMBER
        userTeamMember = User.builder()
                .password("Password%123")
                .id(2L).provider(AuthProvider.local)
                .email("test@mail.com").emailVerified(true).build();

        userPrincipal = UserPrincipal.create(userTeamMember);

        userDetailTeamMember = UserDetail.builder()
                .id(2L)
                .user(userTeamMember)
                .age(15)
                .userName("userNameTeamMember")
                .city("Gdynia")
                .gender(Gender.FEMALE).build();

        tournament = Tournament.builder()
                .id(1L)
                .tournamentName("tournamentName")
                .maxAmountOfTeams(10).build();

        competition = Competition.builder()
                .id(1L)
                .competitionName("tournamentName")
                .maxAmountOfTeams(10).build();
    }

    @Test
    public void shouldAddTeam() {

        when(userDetailRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(userDetail));
        when(teamRepository.existsTeamByTeamNameIgnoreCase(team.getTeamName())).thenReturn(false);
        when(userDetailRepository.save(userDetail)).thenReturn(userDetail);

        ResponseEntity<?> response = teamService.addTeam(team, userPrincipal);

        verify(teamRepository, times(1)).existsTeamByTeamNameIgnoreCase(team.getTeamName());
        verify(userDetailRepository, times(1)).save(userDetail);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), userDetail);
    }

    @Test
    public void shouldUpdateTeam() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(teamRepository.save(team)).thenReturn(team);
        team.setMaxAmountMembers(15);

        ResponseEntity<?> response = teamService.updateTeam(team.getTeamName(),team, userPrincipal);

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        verify(teamRepository, times(1)).save(team);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), team);
    }

    @Test
    public void shouldDeleteTeam() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);

        ResponseEntity<?> response = teamService.deleteTeam(team.getTeamName(), userPrincipal);

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        verify(teamRepository, times(1)).deleteById(team.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionTeamNotExists() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenThrow(new ResourceNotFoundException("Team not exists", "Name", team.getTeamName()));

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teamService.updateTeam(team.getTeamName(),team, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        assertEquals("Team not exists not found with Name : '"+ team.getTeamName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTeamAlreadyExists() {

        when(teamRepository.existsTeamByTeamNameIgnoreCase(team.getTeamName())).thenReturn(true);

        Exception exception = assertThrows(
                ResourceAlreadyExistException.class,
                () -> teamService.addTeam(team, userPrincipal),
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
                () -> teamService.updateTeam(team.getTeamName(),team, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals("Team named: "+ team.getTeamName()+ " not found with Owner : " + "'"+userPrincipal.getUsername()+"'", exception.getMessage());
    }


    @Test
    public void shouldAddRecruitToTeam() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(userDetailRepository.findByUserName(userDetailTeamMember.getUserName())).thenReturn(Optional.of(userDetailTeamMember));

        ResponseEntity<?> response = teamService.addRecruitToTeam(team.getTeamName(), userDetailTeamMember.getUserName(), userPrincipal);

        verify(teamRepository, times(1)).save(team);
        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void shouldJoinTournament() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(tournamentRepository.findByTournamentName(ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(teamRepository.save(team)).thenReturn(team);

        ResponseEntity<?> response = teamService.teamJoinTournament(team.getTeamName(), tournament.getTournamentName(), userPrincipal);

        verify(teamRepository, times(1)).save(team);
        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), team);
    }

    @Test
    public void shouldLeaveTournament() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(tournamentRepository.findByTournamentName(ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(teamRepository.save(team)).thenReturn(team);

        ResponseEntity<?> response = teamService.teamJoinTournament(team.getTeamName(), tournament.getTournamentName(), userPrincipal);

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(team.getTeamName(), team.getTeamOwner());
        verify(teamRepository, times(1)).save(team);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), team);
    }

    @Test
    public void shouldJoinCompetition() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(competitionRepository.findByCompetitionName(ArgumentMatchers.anyString())).thenReturn(Optional.of(competition));
        when(teamRepository.save(team)).thenReturn(team);

        ResponseEntity<?> response =  teamService.teamJoinCompetition(team.getTeamName(), tournament.getTournamentName(), userPrincipal);

        verify(teamRepository, times(1)).save(team);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), team);
    }

    @Test
    public void shouldLeaveCompetition() {

        when(verifyMethodsForServices.shouldFindTeam(team.getTeamName(), team.getTeamOwner())).thenReturn(team);
        when(competitionRepository.findByCompetitionName(ArgumentMatchers.anyString())).thenReturn(Optional.of(competition));
        when(teamRepository.save(team)).thenReturn(team);

        ResponseEntity<?> response = teamService.teamJoinCompetition(team.getTeamName(), tournament.getTournamentName(), userPrincipal);

        verify(teamRepository, times(1)).save(team);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), team);
    }


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
