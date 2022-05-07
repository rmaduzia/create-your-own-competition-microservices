package pl.createcompetition.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.competition.CompetitionService;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.competition.CompetitionRepository;
import pl.createcompetition.user.detail.UserDetail;
import pl.createcompetition.user.detail.UserDetailRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.user.User;
import pl.createcompetition.util.VerifyMethodsForServices;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompetitionServiceTest {

    @Mock
    CompetitionRepository competitionRepository;
    @Mock
    UserDetailRepository userDetailRepository;
    @InjectMocks
    CompetitionService competitionService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;

    User user;
    UserDetail userDetail;
    UserPrincipal userPrincipal;
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

        competition = Competition.builder()
                .id(1L)
                .competitionOwner("test@mail.com")
                .competitionName("zawody1")
                .competitionStart(Timestamp.valueOf("2020-05-01 12:30:00"))
                .competitionEnd(Timestamp.valueOf("2020-05-02 12:30:00"))
                .city("Gdynia")
                .maxAmountOfTeams(10)
                .build();
    }

    @Test
    public void shouldAddCompetition() {

        when(userDetailRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(userDetail));
        when(competitionRepository.existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName())).thenReturn(false);
        when(userDetailRepository.save(userDetail)).thenReturn(userDetail);

        ResponseEntity<?> response = competitionService.addCompetition(competition, userPrincipal);

        verify(userDetailRepository, times(1)).save(userDetail);
        verify(userDetailRepository, times(1)).findById(ArgumentMatchers.anyLong());
        verify(competitionRepository, times(1)).existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), userDetail);
    }

    @Test
    public void shouldUpdateCompetition() {
        
        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);
        when(competitionRepository.save(competition)).thenReturn(competition);
        competition.setMaxAmountOfTeams(15);

        ResponseEntity<?> response = competitionService.updateCompetition(competition.getCompetitionName(),competition, userPrincipal);

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        verify(competitionRepository, times(1)).save(competition);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), competition);
    }

    @Test
    public void shouldDeleteCompetition() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);

        ResponseEntity<?> response = competitionService.deleteCompetition(competition.getCompetitionName(), userPrincipal);

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        verify(competitionRepository, times(1)).deleteById(competition.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldThrowExceptionCompetitionNotExists() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenThrow(new ResourceNotFoundException("Competition not exists", "Name", competition.getCompetitionName()));

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> competitionService.updateCompetition(competition.getCompetitionName(),competition, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        assertEquals("Competition not exists not found with Name : '"+ competition.getCompetitionName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionCompetitionAlreadyExists() {

        when(competitionRepository.existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName())).thenReturn(true);

        Exception exception = assertThrows(
                ResourceAlreadyExistException.class,
                () -> competitionService.addCompetition(competition, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName());
        assertEquals("Competition already exists with Name : '"+ competition.getCompetitionName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionCompetitionNotBelongToUser() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName())).thenReturn(competition);
        competition.setCompetitionOwner("OtfherOwner");

        doThrow(new BadRequestException("You are not owner of this Competition"))
                .when(verifyMethodsForServices).checkIfCompetitionBelongToUser(competition.getCompetitionName(), userPrincipal.getUsername());

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> competitionService.updateCompetition(competition.getCompetitionName(),competition, userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(verifyMethodsForServices, times(1)).shouldFindCompetition(competition.getCompetitionName());
        assertEquals("You are not owner of this Competition", exception.getMessage());
    }
}