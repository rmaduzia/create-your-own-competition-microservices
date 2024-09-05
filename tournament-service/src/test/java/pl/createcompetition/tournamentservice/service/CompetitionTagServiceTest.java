package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.module.ResolutionException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.competition.tag.CompetitionTagService;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;


@ExtendWith(MockitoExtension.class)
public class CompetitionTagServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private VerifyMethodsForServices verifyMethodsForServices;

    @InjectMocks
    private CompetitionTagService competitionTagService;

    @Mock
    UserPrincipal userPrincipal;

    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    private static final String userName = "someUserName";

    Competition competition;
    Tag competitionTag;

    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        competition = Competition.builder()
                .id(1L)
                .eventOwner("test@mail.com")
                .eventName("zawody1")
                .eventStartDate(Timestamp.valueOf("2020-05-01 12:30:00").toLocalDateTime())
                .eventEndDate(Timestamp.valueOf("2020-05-02 12:30:00").toLocalDateTime())
                .city("Gdynia")
                .maxAmountOfTeams(10)
                .tags(Sets.newHashSet())
                .build();

        competitionTag = Tag.builder().tag("Tag").id(1L).competitions(Sets.newHashSet()).build();
    }

    @Test
    public void shouldAddTags() {

        when(competitionRepository.save(competition)).thenReturn(competition);

        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenReturn(competition);


        Set<Tag> tags = Set.of(competitionTag);
        ResponseEntity<?> response = competitionTagService.addCompetitionTag(tags, competition.getEventName(), userPrincipal.getName());

        verify(competitionRepository, times(1)).save(competition);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), competition);
    }

    @Test
    public void shouldUpdateTag() {

        when(verifyMethodsForServices.shouldFindCompetition(competition.getEventName())).thenReturn(competition);
        when(competitionRepository.save(competition)).thenReturn(competition);

        competitionTag.setTag("updatedTag");
        ResponseEntity<?> response = competitionTagService.updateCompetitionTag(competitionTag, competition.getEventName(), userPrincipal.getName());

        verify(competitionRepository, times(1)).save(competition);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), competition);
    }


    @Test
    public void shouldThrowExceptionCompetitionNotExistsWhenAddTag() {

        Set<String> tags = Set.of("someTag");

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () ->  competitionTagService.addCompetitionTag(tags, competition.getEventName(), userPrincipal.getName()),
            "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).findByEventName(competition.getEventName());
        assertEquals("Competition not exists not found with Name : '"+ competition.getEventName()+ "'", exception.getMessage());
    }


}
