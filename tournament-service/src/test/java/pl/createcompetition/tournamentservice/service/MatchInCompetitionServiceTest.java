package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.match.MatchInCompetition;
import pl.createcompetition.tournamentservice.competition.match.MatchInCompetitionRepository;
import pl.createcompetition.tournamentservice.competition.match.MatchInCompetitionService;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MatchInCompetitionServiceTest {

    @Mock
    MatchInCompetitionRepository matchInCompetitionRepository;
    @InjectMocks
    MatchInCompetitionService matchInCompetitionService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

    @Mock
    UserPrincipal userPrincipal;

    Competition competition;
    MatchInCompetition matchInCompetition;
    MatchInCompetition otherMatchInCompetition;

    private static final String userName = "someUserName";

    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        competition = Competition.builder()
                .id(1L)
                .competitionOwner(userName)
                .competitionName("zawody1")
                .competitionStart(LocalDateTime.now())
                .competitionEnd(LocalDateTime.now())
                .city("Gdynia")
                .maxAmountOfTeams(10)
                .build();

        matchInCompetition = MatchInCompetition.builder()
                .id(1L)
                .firstTeamName("firstTeam")
                .secondTeamName("secondTeam")
                .isMatchWasPlayed(false)
                .isWinnerConfirmed(false)
                .build();

        matchInCompetition.addMatchToCompetition(competition);

        otherMatchInCompetition = MatchInCompetition.builder()
                .id(2L)
                .firstTeamName("otherFirstTeam")
                .secondTeamName("otherSecondTeam")
                .isMatchWasPlayed(false)
                .isWinnerConfirmed(false)
                .build();
    }

    @Test
    public void shouldAddMatchInCompetition() {

        lenient().when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);
        when(matchInCompetitionRepository.save(matchInCompetition)).thenReturn(matchInCompetition);

        ResponseEntity<?> response = matchInCompetitionService.addMatchInCompetition(matchInCompetition, competition.getCompetitionName(), userPrincipal);

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        verify(matchInCompetitionRepository, times(1)).save(matchInCompetition);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), matchInCompetition);
    }

    @Test
    public void shouldUpdateMatchInCompetition() {

        when(matchInCompetitionRepository.findById(competition.getId())).thenReturn(java.util.Optional.of(matchInCompetition));
        lenient().when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);
        when(matchInCompetitionRepository.save(matchInCompetition)).thenReturn(matchInCompetition);

        matchInCompetition.setIsMatchWasPlayed(true);

        ResponseEntity<?> response = matchInCompetitionService.updateMatchInCompetition(matchInCompetition, matchInCompetition.getId(), userPrincipal);

        verify(matchInCompetitionRepository, times(1)).findById(matchInCompetition.getId());
        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        verify(matchInCompetitionRepository, times(1)).save(matchInCompetition);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), matchInCompetition);
    }

    @Test
    public void shouldDeleteMatchInCompetition() {

        when(matchInCompetitionRepository.findById(competition.getId())).thenReturn(java.util.Optional.of(matchInCompetition));

        ResponseEntity<?> response = matchInCompetitionService.deleteMatchInCompetition(matchInCompetition.getId(), userPrincipal);

        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }


    @Test
    public void shouldThrowExceptionThatWinnerTeamHasNotApprovedBeforeMatchStarted() {

        matchInCompetition.setIsWinnerConfirmed(true);
        matchInCompetition.setIsMatchWasPlayed(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> matchInCompetitionService.addMatchInCompetition(matchInCompetition, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You can't set up winner if match wasn't played", exception.getReason());

    }

    @Test
    public void shouldThrowExceptionTeamNotParticipatingInCompetition() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> matchInCompetitionService.addMatchInCompetition(otherMatchInCompetition, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Match are not part of competition named: "+competition.getCompetitionName(), exception.getReason());

    }

    @Test
    public void shouldThrowExceptionThatMatchIdNotExists() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> matchInCompetitionService.deleteMatchInCompetition(matchInCompetition.getId(), userPrincipal));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Match not exists, Id: " + matchInCompetition.getId(), exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND.value()+ " " +HttpStatus.NOT_FOUND.name() + " \"Match not exists, Id: " + matchInCompetition.getId() + "\"" , exception.getMessage());

    }

    @Test
    public void shouldThrowExceptionThatCompetititonNotBelongToUser() {

        when(matchInCompetitionRepository.findById(competition.getId())).thenReturn(java.util.Optional.of(matchInCompetition));
        competition.setCompetitionOwner("OtherOwner");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> matchInCompetitionService.deleteMatchInCompetition(matchInCompetition.getId(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Competition don't belong to you", exception.getReason());
    }
}