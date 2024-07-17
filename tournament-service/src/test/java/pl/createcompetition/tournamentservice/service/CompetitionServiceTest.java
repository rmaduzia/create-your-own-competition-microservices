package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionMapper;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.competition.CompetitionService;
import pl.createcompetition.tournamentservice.competition.CompetitionCreateUpdateRequest;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@ExtendWith(MockitoExtension.class)
public class CompetitionServiceTest {

    @Mock
    CompetitionRepository competitionRepository;
//    @Mock
    @Spy
    CompetitionMapper competitionMapper = Mappers.getMapper(CompetitionMapper.class);

    @InjectMocks
    CompetitionService competitionService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

    @Mock
    UserPrincipal userPrincipal;
    Competition competition;
    CompetitionCreateUpdateRequest competitionCreateUpdateRequest;

    private static final String userName = "someUserName";

    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        competition = Competition.builder()
                .competitionOwner("test@mail.com")
                .competitionName("zawody1")
                .competitionStart(Timestamp.valueOf("2020-05-01 12:30:00").toLocalDateTime())
                .competitionEnd(Timestamp.valueOf("2020-05-02 12:30:00").toLocalDateTime())
                .city("Gdynia")
                .maxAmountOfTeams(10)
                .build();


        competitionCreateUpdateRequest = CompetitionCreateUpdateRequest.builder()
            .competitionName("zawody1")
            .competitionStart(Timestamp.valueOf("2020-05-01 12:30:00").toLocalDateTime())
            .competitionEnd(Timestamp.valueOf("2020-05-02 12:30:00").toLocalDateTime())
            .city("Gdynia")
            .maxAmountOfTeams(10)
            .build();
    }

    @Test
    public void shouldAddCompetition() {

        when(competitionRepository.existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName())).thenReturn(false);
        when(competitionRepository.save(competition)).thenReturn(competition);

        ResponseEntity<?> response = competitionService.addCompetition(
            competitionCreateUpdateRequest, userPrincipal.getName());

        verify(competitionRepository, times(1)).existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(competition, response.getBody());
    }

    @Test
    public void shouldUpdateCompetition() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);
        when(competitionRepository.save(competition)).thenReturn(competition);
        competition.setMaxAmountOfTeams(15);

        ResponseEntity<?> response = competitionService.updateCompetition(competition.getCompetitionName(),competitionCreateUpdateRequest, userPrincipal.getName());

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        verify(competitionRepository, times(1)).save(competition);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), competitionCreateUpdateRequest);
    }

    @Test
    public void shouldDeleteCompetition() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);

        ResponseEntity<?> response = competitionService.deleteCompetition(competition.getCompetitionName(), userPrincipal.getName());

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        verify(competitionRepository, times(1)).deleteById(competition.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionCompetitionNotExists() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition not exists, Name: " + competition.getCompetitionName()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> competitionService.updateCompetition(competition.getCompetitionName(),competitionCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        assertEquals("Competition not exists, Name: "+ competition.getCompetitionName(), exception.getReason());
    }

    @Test
    public void shouldThrowExceptionCompetitionAlreadyExists() {

        when(competitionRepository.existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> competitionService.addCompetition(competitionCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName());
        assertEquals("Competition already exists. Named: "+ competition.getCompetitionName(), exception.getReason());
    }

    @Test
    public void shouldThrowExceptionCompetitionNotBelongToUser() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);
        competition.setCompetitionOwner("OtherOwner");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not owner of this Competition"))
            .when(verifyMethodsForServices).checkIfCompetitionBelongToUser(competition.getCompetitionOwner(), userName);


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> competitionService.updateCompetition(competition.getCompetitionName(), competitionCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You are not owner of this Competition", exception.getReason());
    }
}