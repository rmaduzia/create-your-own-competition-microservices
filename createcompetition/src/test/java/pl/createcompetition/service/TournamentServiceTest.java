package pl.createcompetition.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.tournament.Tournament;
import pl.createcompetition.tournament.TournamentRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.team.Team;
import pl.createcompetition.tournament.TournamentService;
import pl.createcompetition.user.User;
import pl.createcompetition.util.VerifyMethodsForServices;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    TournamentRepository tournamentRepository;
    @InjectMocks
    TournamentService tournamentService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

    User user;
    UserPrincipal userPrincipal;
    Tournament tournament;
    Team team;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .password("Password%123")
                .id(1L).provider(AuthProvider.local)
                .email("test@mail.com").emailVerified(true).build();

        userPrincipal = UserPrincipal.create(user);

        tournament = Tournament.builder()
                .id(1L)
                .maxAmountOfTeams(10)
                .tournamentOwner("test@mail.com")
                .tournamentName("Tourtnament1").build();


        team = Team.builder()
                .id(1L)
                .teamOwner("test@mail.com")
                .teamName("team1")
                .isOpenRecruitment(true)
                .city("Gdynia").build();

    }

    @Test
    public void shouldAddTournament() {

        when(tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())).thenReturn(false);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ResponseEntity<?> response = tournamentService.addTournament(tournament, userPrincipal);

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), tournament);
    }

    @Test
    public void shouldUpdateTournament() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        tournament.setMaxAmountOfTeams(15);
        ResponseEntity<?> response = tournamentService.updateTournament(tournament.getTournamentName(), tournament, userPrincipal);

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }

    @Test
    public void shouldDeleteTournament() {
        
        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        ResponseEntity<?> response = tournamentService.deleteTournament(tournament.getTournamentName(), userPrincipal);

        verify(tournamentRepository, times(1)).deleteByTournamentName(tournament.getTournamentName());
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionTournamentNotExists() {

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> tournamentService.updateTournament(tournament.getTournamentName(), tournament, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals("Tournament not exists not found with Name : '"+ tournament.getTournamentName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTournamentAlreadyExists() {

        when(tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())).thenReturn(true);

        Exception exception = assertThrows(
                ResourceAlreadyExistException.class,
                () -> tournamentService.addTournament(tournament, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName());
        assertEquals("Tournament already exists with Name : '"+ tournament.getTournamentName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTournamentNotBelongToUser() {
        
        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        tournament.setTournamentOwner("OtherOwner");

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> tournamentService.updateTournament(tournament.getTournamentName(), tournament, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals("Tournament named: "+ tournament.getTournamentName()+ " not found with Owner : " + "'"+userPrincipal.getUsername()+"'", exception.getMessage());
    }

    @Test
    public void shouldSetTheDatesOfTheTeamsMatches() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Date date = new Date();
        Map<String, Date> dateMatch = new HashMap<>();

        dateMatch.put("1", date);

        ResponseEntity<?> response = tournamentService.setTheDatesOfTheTeamsMatches(tournament.getTournamentName(), dateMatch, userPrincipal);

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);

    }

    @Test
    public void shouldDeleteTheDateOfTheTeamsMatches() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        Date date = new Date();
        Map<String, Date> dateMatch = new HashMap<>();
        dateMatch.put("1", date);

        tournamentService.setTheDatesOfTheTeamsMatches(tournament.getTournamentName(), dateMatch, userPrincipal);
        ResponseEntity<?> response = tournamentService.deleteDateOfTheTeamsMatches(tournament.getTournamentName(), "1", userPrincipal);

        verify(tournamentRepository, times(2)).save(tournament);
        verify(tournamentRepository, times(2)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldDeleteTeamFromTournament() {
        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        lenient().when(verifyMethodsForServices.shouldFindTeam(team.getTeamName())).thenReturn(team);

        ResponseEntity<?> response = tournamentService.removeTeamFromTournament(tournament.getTournamentName(), team.getTeamName(), userPrincipal);

        verify(tournamentRepository, times(1)).save(tournament);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldStartTournament() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Map<String, String> drawedTeams = new TreeMap<>();
        drawedTeams.put("FirstKey", "FirstValue");
        tournament.setDrawedTeams(drawedTeams);

        ResponseEntity<?> response = tournamentService.startTournament(tournament.getTournamentName(), userPrincipal);

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getUsername());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }
}