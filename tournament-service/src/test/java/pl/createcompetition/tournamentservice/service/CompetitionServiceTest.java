package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
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
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.competition.CompetitionService;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@ExtendWith(MockitoExtension.class)
public class CompetitionServiceTest {

    @Mock
    CompetitionRepository competitionRepository;

    @Spy
    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @InjectMocks
    CompetitionService competitionService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

    @Mock
    UserPrincipal userPrincipal;
    Competition competition;
    EventCreateUpdateRequest eventCreateUpdateRequest;

    private static final String userName = "someUserName";

    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        competition = Competition.builder()
                .eventOwner("test@mail.com")
                .eventName("zawody1")
                .eventStartDate(Timestamp.valueOf("2020-05-01 12:30:00").toLocalDateTime())
                .eventEndDate(Timestamp.valueOf("2020-05-02 12:30:00").toLocalDateTime())
                .city("Gdynia")
                .maxAmountOfTeams(10)
                .build();


        eventCreateUpdateRequest = EventCreateUpdateRequest.builder()
            .eventName("zawody1")
            .eventStartDate(Timestamp.valueOf("2020-05-01 12:30:00").toLocalDateTime())
            .eventEndDate(Timestamp.valueOf("2020-05-02 12:30:00").toLocalDateTime())
            .city("Gdynia")
            .maxAmountOfTeams(10)
            .build();
    }

    @Test
    public void shouldAddCompetition() {

        when(competitionRepository.existsCompetitionByEventNameIgnoreCase(competition.getEventName())).thenReturn(false);

        Competition competitionWithAudit = Competition.builder()
            .id(1L)
            .eventOwner("zawody1")
            .eventStartDate(competition.getEventStartDate())
            .eventEndDate(competition.getEventEndDate())
            .city("Gdynia")
            .maxAmountOfTeams(10)
            .createdDate(Instant.now())
            .updatedDate(Instant.now())
            .createdBy(userName)
            .updatedBy(userName)
            .version(1)
            .build();

        when(competitionRepository.save(competition)).thenReturn(competitionWithAudit);


        ResponseEntity<?> response = competitionService.addCompetition(
            eventCreateUpdateRequest, userPrincipal.getName());

        verify(competitionRepository, times(1)).existsCompetitionByEventNameIgnoreCase(competition.getEventName());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);


        Competition returned = (Competition) response.getBody();
        assertEquals(returned, response.getBody());

        assertNotNull(returned,"Returned competition should not be null");
        assertNotNull(returned.getCreatedDate(), "createdDate should be set by auditing on save");
        assertNotNull(returned.getUpdatedDate(), "updatedDate should be set by auditing on save");
        assertNotNull(returned.getCreatedBy(), "createdBy should be set by auditing");
        assertNotNull(returned.getUpdatedBy(), "updatedBy should be set by auditing");
        assertNotNull(returned.getVersion(), "version (optimistic lock) should be set");
        assertTrue(returned.getVersion() >= 0);
    }

    @Test
    public void shouldUpdateCompetition() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenReturn(competition);

        competition.setVersion(2);

        eventCreateUpdateRequest.setVersion(2);

        Competition saved = Competition.builder()
            .id(competition.getId())
            .eventName(competition.getEventName())
            .eventOwner(competition.getEventOwner())
            .city(competition.getCity())
            .streetName(competition.getStreetName())
            .streetNumber(competition.getStreetNumber())
            .maxAmountOfTeams(15)
            .eventStartDate(competition.getEventStartDate())
            .eventEndDate(competition.getEventEndDate())
            .isOpenRecruitment(competition.isOpenRecruitment())
            .version(3)
            .build();

        when(competitionRepository.save(competition)).thenReturn(saved);

        ResponseEntity<?> response = competitionService.updateCompetition(competition.getEventName(),
            eventCreateUpdateRequest, userPrincipal.getName());

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getEventName());
        verify(competitionRepository, times(1)).save(competition);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EventCreateUpdateRequest returnedDto = (EventCreateUpdateRequest) response.getBody();
        assertNotNull(returnedDto);
        assertEquals(3, returnedDto.getVersion());
    }

    @Test
    public void shouldThrowOptimisticLockingOnUpdate_whenRepositoryThrows() {
        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenReturn(competition);

        eventCreateUpdateRequest.setVersion(1);
        competition.setVersion(1);

        when(competitionRepository.save(competition)).thenThrow(new org.springframework.dao.OptimisticLockingFailureException("Stale state"));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> competitionService.updateCompetition(competition.getEventName(), eventCreateUpdateRequest, userPrincipal.getName())
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Competition was updated by another transaction. Please try again.", exception.getReason());
    }

    @Test
    public void shouldDeleteCompetition() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenReturn(competition);

        ResponseEntity<?> response = competitionService.deleteCompetition(competition.getEventName(), userPrincipal.getName());

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getEventName());
        verify(competitionRepository, times(1)).deleteById(competition.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionCompetitionNotExists() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition not exists, Name: " + competition.getEventName()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> competitionService.updateCompetition(competition.getEventName(),
                    eventCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getEventName());
        assertEquals("Competition not exists, Name: "+ competition.getEventName(), exception.getReason());
    }

    @Test
    public void shouldThrowExceptionCompetitionAlreadyExists() {

        when(competitionRepository.existsCompetitionByEventNameIgnoreCase(competition.getEventName())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> competitionService.addCompetition(eventCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).existsCompetitionByEventNameIgnoreCase(competition.getEventName());
        assertEquals("Competition already exists. Named: "+ competition.getEventName(), exception.getReason());
    }

    @Test
    public void shouldThrowExceptionCompetitionNotBelongToUser() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenReturn(competition);
        competition.setEventOwner("OtherOwner");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not owner of this Competition"))
            .when(verifyMethodsForServices).checkIfCompetitionBelongToUser(competition.getEventOwner(), userName);


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> competitionService.updateCompetition(competition.getEventName(),
                    eventCreateUpdateRequest, userPrincipal.getName()),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getEventName());

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You are not owner of this Competition", exception.getReason());
    }
}