package pl.createcompetition.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.competition.match.MatchInCompetition;
import pl.createcompetition.competition.match.MatchInCompetitionService;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.competition.match.MatchInCompetitionRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.user.User;
import pl.createcompetition.user.detail.UserDetail;
import pl.createcompetition.util.VerifyMethodsForServices;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchInCompetitionServiceTest {

    @Mock
    MatchInCompetitionRepository matchInCompetitionRepository;
    @InjectMocks
    MatchInCompetitionService matchInCompetitionService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

    User user;
    UserDetail userDetail;
    UserPrincipal userPrincipal;
    Competition competition;
    MatchInCompetition matchInCompetition;
    MatchInCompetition otherMatchInCompetition;

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

        competition = Competition.builder()
                .id(1L)
                .competitionOwner("test@mail.com")
                .competitionName("zawody1")
                .competitionStart(Timestamp.valueOf("2020-05-01 12:30:00"))
                .competitionEnd(Timestamp.valueOf("2020-05-02 12:30:00"))
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

        Exception exception = assertThrows(BadRequestException.class,
                () -> matchInCompetitionService.addMatchInCompetition(matchInCompetition, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals("You can't set up winner if match wasn't played", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTeamNotParticipatingInCompetition() {

        lenient().when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);

        Exception exception = assertThrows(BadRequestException.class,
                () -> matchInCompetitionService.addMatchInCompetition(otherMatchInCompetition, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals("Match are not part of competition named: "+competition.getCompetitionName(), exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionThatMatchIdNotExists() {

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> matchInCompetitionService.deleteMatchInCompetition(matchInCompetition.getId(), userPrincipal));

        assertEquals("Match not exists not found with Id : '" + matchInCompetition.getId() + "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionThatCompetititonNotBelongToUser() {

        when(matchInCompetitionRepository.findById(competition.getId())).thenReturn(java.util.Optional.of(matchInCompetition));
        competition.setCompetitionOwner("OtherOwner");

        Exception exception = assertThrows(BadRequestException.class,
                () -> matchInCompetitionService.deleteMatchInCompetition(matchInCompetition.getId(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        assertEquals("Competition don't belong to you", exception.getMessage());
    }
}