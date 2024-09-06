package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.tournament.TournamentService;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TournamentServiceTest {

    @Mock
    TournamentRepository tournamentRepository;
    @InjectMocks
    TournamentService tournamentService;
    @Spy
    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Mock
    UserPrincipal userPrincipal;

    EventCreateUpdateRequest tournamentCreateUpdateRequest;
    Tournament tournament;
    TeamDto teamDto;

    private static final String userName = "someUserName";
    
    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        tournament = Tournament.builder()
            .maxAmountOfTeams(10)
            .eventOwner(userName)
            .eventName("Tourtnament1")
            .isEventFinished(false)
            .build();

        tournamentCreateUpdateRequest = EventCreateUpdateRequest.builder()
            .maxAmountOfTeams(10)
            .eventName("Tourtnament1")
            .build();

        teamDto = TeamDto.builder()
            .teamName("someTeamName")
            .teamOwner("someTeamOwner")

            .build();
    }

    @Test
    public void shouldAddTournament() {

        when(tournamentRepository.existsTournamentByEventNameIgnoreCase(tournament.getEventName())).thenReturn(false);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ResponseEntity<?> response = tournamentService.addTournament(tournamentCreateUpdateRequest, userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).existsTournamentByEventNameIgnoreCase(tournament.getEventName());

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), tournament);
    }

    @Test
    public void shouldUpdateTournament() {

        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        tournament.setMaxAmountOfTeams(15);
        ResponseEntity<?> response = tournamentService.updateTournament(tournament.getEventName(), tournamentCreateUpdateRequest, userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }

    @Test
    public void shouldDeleteTournament() {
        
        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        ResponseEntity<?> response = tournamentService.deleteTournament(tournament.getEventName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).deleteById(tournament.getId());
        verify(tournamentRepository, times(1)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionTournamentNotExists() {

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
                () -> tournamentService.updateTournament(tournament.getEventName(), tournamentCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Not found tournament where Name: "+ tournamentCreateUpdateRequest.getEventName() + " and Owner: " + userPrincipal.getName(), exception.getReason());
    }

    @Test
    public void shouldThrowExceptionTournamentAlreadyExists() {

        when(tournamentRepository.existsTournamentByEventNameIgnoreCase(tournament.getEventName())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
                () -> tournamentService.addTournament(tournamentCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).existsTournamentByEventNameIgnoreCase(tournament.getEventName());

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Tournament already exists. Named: " + tournament.getEventName(), exception.getReason());
    }

    @Test
    public void shouldThrowExceptionTournamentNotBelongToUser() {

        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found Tournament named: " + tournament.getEventName() + " with Owner: " + userName));

        tournament.setEventOwner("OtherOwner");


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tournamentService.updateTournament(tournament.getEventName(), tournamentCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(tournamentRepository, times(1)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Not found Tournament named: " + tournament.getEventName() + " with Owner: " + userPrincipal.getName(), exception.getReason());
    }

    @Test
    public void shouldSetTheDatesOfTheTeamsMatches() {

        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        LocalDateTime date = LocalDateTime.now();
        Map<String, LocalDateTime> dateMatch = new HashMap<>();

        dateMatch.put("1", date);

        ResponseEntity<?> response = tournamentService.setTheDatesOfTheTeamsMatches(tournament.getEventName(), dateMatch, userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }

    @Test
    public void shouldDeleteTheDateOfTheTeamsMatches() {

        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        LocalDateTime date = LocalDateTime.now();
        Map<String, LocalDateTime> dateMatch = new HashMap<>();
        dateMatch.put("1", date);

        tournamentService.setTheDatesOfTheTeamsMatches(tournament.getEventName(), dateMatch, userPrincipal.getName());
        ResponseEntity<?> response = tournamentService.deleteDateOfTheTeamsMatches(tournament.getEventName(), "1", userPrincipal.getName());

        verify(tournamentRepository, times(2)).save(tournament);
        verify(tournamentRepository, times(2)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldDeleteTeamFromTournament() {

        tournament.addTeamToTournament(teamDto.getTeamName());

        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));

        ResponseEntity<?> response = tournamentService.removeTeamFromTournament(tournament.getEventName(), teamDto.getTeamName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldStartTournament() {

        when(tournamentRepository.findByEventNameAndEventOwner(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Map<String, String> drawedTeams = new TreeMap<>();
        drawedTeams.put("FirstKey", "FirstValue");
        tournament.setDrawnTeams(drawedTeams);

        ResponseEntity<?> response = tournamentService.startTournament(tournament.getEventName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(tournamentRepository, times(1)).findByEventNameAndEventOwner(tournament.getEventName(), userPrincipal.getName());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), tournament);
    }
}