package pl.createcompetition.service;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.competition.CompetitionTagService;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.model.Tag;
import pl.createcompetition.competition.CompetitionRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.user.User;
import pl.createcompetition.user.detail.UserDetail;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CompetitionTagServiceTest {

    @Mock
    CompetitionRepository competitionRepository;
    @InjectMocks
    CompetitionTagService competitionTagService;

    User user;
    UserDetail userDetail;
    UserPrincipal userPrincipal;
    Competition competition;
    Tag competitionTag;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .password("Password%123")
                .id(1L)
                .provider(AuthProvider.local)
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
                .tag(Sets.newHashSet())
                .build();

        competitionTag = Tag.builder().tag("Tag").id(1L).competitions(Sets.newHashSet()).build();
    }

    @Test
    public void shouldAddTags() {

        when(competitionRepository.findByCompetitionName(eq(competition.getCompetitionName()))).thenReturn(Optional.of(competition));
        when(competitionRepository.save(competition)).thenReturn(competition);

        Set<Tag> tags = Set.of(competitionTag);
        ResponseEntity<?> response = competitionTagService.addCompetitionTag(tags, competition.getCompetitionName(), userPrincipal);

        verify(competitionRepository, times(1)).save(competition);
        verify(competitionRepository, times(1)).findByCompetitionName(competition.getCompetitionName());

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), competition);
    }

    @Test
    public void shouldUpdateTag() {

        when(competitionRepository.findByCompetitionName(competition.getCompetitionName())).thenReturn(Optional.of(competition));
        when(competitionRepository.save(competition)).thenReturn(competition);

        competitionTag.setTag("updatedTag");
        ResponseEntity<?> response = competitionTagService.updateCompetitionTag(competitionTag, competition.getCompetitionName(), userPrincipal);

        verify(competitionRepository, times(1)).save(competition);
        verify(competitionRepository, times(1)).findByCompetitionName(competition.getCompetitionName());

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), competition);
    }

    @Test
    public void shouldThrowExceptionCompetitionNotExistsWhenAddTag() {

        Set<Tag> tags = Set.of(competitionTag);

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () ->  competitionTagService.addCompetitionTag(tags, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).findByCompetitionName(competition.getCompetitionName());
        assertEquals("Competition not exists not found with Name : '"+ competition.getCompetitionName()+ "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionTagAlreadyExists() {

        when(competitionRepository.findByCompetitionName(eq(competition.getCompetitionName()))).thenReturn(Optional.of(competition));
        Set<Tag> tags = Set.of(competitionTag);

        when(competitionTagService.addCompetitionTag(tags, competition.getCompetitionName(), userPrincipal)).thenThrow(DataIntegrityViolationException.class);

        Exception exception = assertThrows(
                ResourceAlreadyExistException.class,
                () -> competitionTagService.addCompetitionTag(tags, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(2)).findByCompetitionName(competition.getCompetitionName());
        assertEquals("Tag already exists with CompetitionTag : '" + competitionTag.getTag() + "'", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenCompetitionNotBelongToUser() {

        when(competitionRepository.findByCompetitionName(competition.getCompetitionName())).thenReturn(Optional.of(competition));
        Set<Tag> tags = Set.of(competitionTag);

        competition.setCompetitionOwner("OtherOwner");

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> competitionTagService.addCompetitionTag(tags, competition.getCompetitionName(), userPrincipal),
                "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).findByCompetitionName(competition.getCompetitionName());
        assertEquals("Competition named: "+ competition.getCompetitionName()+ " not found with Owner : " + "'"+userPrincipal.getUsername()+"'", exception.getMessage());
    }

    @Test
    public void shouldDeleteTag() {

        when(competitionRepository.findByCompetitionName(competition.getCompetitionName())).thenReturn(Optional.of(competition));

        Set<Tag> tags = Set.of(competitionTag);
        competitionTagService.addCompetitionTag(tags, competition.getCompetitionName(), userPrincipal);

        ResponseEntity<?> response = competitionTagService.deleteCompetitionTag(competitionTag, competition.getCompetitionName(), userPrincipal);

        verify(competitionRepository, times(2)).findByCompetitionName(competition.getCompetitionName());
        verify(competitionRepository, times(1)).deleteById(competitionTag.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}
