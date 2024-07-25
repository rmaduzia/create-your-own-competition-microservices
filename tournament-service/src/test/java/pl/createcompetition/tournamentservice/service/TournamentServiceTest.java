package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentCreateUpdateRequest;
import pl.createcompetition.tournamentservice.tournament.TournamentMapper;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.tournament.TournamentService;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TournamentServiceTest {

    @Mock
    TournamentRepository tournamentRepository;
    @InjectMocks
    TournamentService tournamentService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;
    @Spy
    TournamentMapper tournamentMapper = Mappers.getMapper(TournamentMapper.class);

    @Mock
    UserPrincipal userPrincipal;

    TournamentCreateUpdateRequest tournamentCreateUpdateRequest;
    Tournament tournament;
    TeamDto teamDto;

    private static final String userName = "someUserName";
    
    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        tournament = Tournament.builder()
            .maxAmountOfTeams(10)
            .tournamentOwner(userName)
            .tournamentName("Tourtnament1")
            .isFinished(false)
            .build();

        tournamentCreateUpdateRequest = TournamentCreateUpdateRequest.builder()
            .maxAmountOfTeams(10)
            .tournamentName("Tourtnament1")
            .build();

        teamDto = TeamDto.builder()
            .teamName("someTeamName")
            .teamOwner("someTeamOwner")

            .build();
    }

    @Test
    public void shouldAddTournament() {

        when(tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())).thenReturn(false);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ResponseEntity<?> response = tournamentService.addTournament(tournamentCreateUpdateRequest, userPrincipal.getName());

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
        ResponseEntity<?> response = tournamentService.updateTournament(tournament.getTournamentName(), tournamentCreateUpdateRequest, userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }

    @Test
    public void shouldDeleteTournament() {
        
        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        ResponseEntity<?> response = tournamentService.deleteTournament(tournament.getTournamentName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).deleteByTournamentName(tournament.getTournamentName());
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionTournamentNotExists() {

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
                () -> tournamentService.updateTournament(tournament.getTournamentName(), tournamentCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Tournament not exists. Name: " +  tournament.getTournamentName(), exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND.value()+ " " +HttpStatus.NOT_FOUND.name() + " \"Tournament not exists. Name: " +  tournament.getTournamentName() +"\"", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTournamentAlreadyExists() {

        when(tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
                () -> tournamentService.addTournament(tournamentCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName());

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Tournament named: " + tournament.getTournamentName() + " already exists", exception.getReason());
        assertEquals(HttpStatus.CONFLICT.value()+ " " +HttpStatus.CONFLICT.name() + " \"Tournament named: "  + tournament.getTournamentName() + " already exists\"" , exception.getMessage());

    }

    @Test
    public void shouldThrowExceptionTournamentNotBelongToUser() {
        
        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        tournament.setTournamentOwner("OtherOwner");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tournamentService.updateTournament(tournament.getTournamentName(), tournamentCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Not found Tournament named: " + tournament.getTournamentName() + " with Owner: " + userPrincipal.getName(), exception.getReason());
        assertEquals( HttpStatus.NOT_FOUND.value()+ " " +HttpStatus.NOT_FOUND.name() + " \"Not found Tournament named: " + tournament.getTournamentName() + " with Owner: " + userPrincipal.getName() +"\"", exception.getMessage());

    }

    @Test
    public void shouldSetTheDatesOfTheTeamsMatches() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        LocalDateTime date = LocalDateTime.now();
        Map<String, LocalDateTime> dateMatch = new HashMap<>();

        dateMatch.put("1", date);

        ResponseEntity<?> response = tournamentService.setTheDatesOfTheTeamsMatches(tournament.getTournamentName(), dateMatch, userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);

    }

    @Test
    public void shouldDeleteTheDateOfTheTeamsMatches() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        LocalDateTime date = LocalDateTime.now();
        Map<String, LocalDateTime> dateMatch = new HashMap<>();
        dateMatch.put("1", date);

        tournamentService.setTheDatesOfTheTeamsMatches(tournament.getTournamentName(), dateMatch, userPrincipal.getName());
        ResponseEntity<?> response = tournamentService.deleteDateOfTheTeamsMatches(tournament.getTournamentName(), "1", userPrincipal.getName());

        verify(tournamentRepository, times(2)).save(tournament);
        verify(tournamentRepository, times(2)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldDeleteTeamFromTournament() {

        tournament.addTeamToTournament(teamDto.getTeamName());

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        lenient().when(verifyMethodsForServices.shouldFindTeam(teamDto.getTeamName())).thenReturn(
            teamDto);

        ResponseEntity<?> response = tournamentService.removeTeamFromTournament(tournament.getTournamentName(), teamDto.getTeamName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldStartTournament() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Map<String, String> drawedTeams = new TreeMap<>();
        drawedTeams.put("FirstKey", "FirstValue");
        tournament.setDrawnTeams(drawedTeams);

        ResponseEntity<?> response = tournamentService.startTournament(tournament.getTournamentName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }
}