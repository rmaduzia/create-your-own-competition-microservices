package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.util.Sets;
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
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.competition.tag.CompetitionTagService;
import pl.createcompetition.tournamentservice.competition.tag.EventTagsDto;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@ExtendWith(MockitoExtension.class)
public class CompetitionTagServiceTest {

    @Mock
    CompetitionRepository competitionRepository;

    CompetitionTagService competitionTagService;

    @InjectMocks
    VerifyMethodsForServices verifyMethodsForServices;


    @Mock
    private UserPrincipal userPrincipal;

    @Spy
    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    private static final String userName = "someUserName";

    Competition competition;
    Tag competitionTag;

    @BeforeEach
    public void setUp() {

        competitionTagService = new CompetitionTagService(competitionRepository, verifyMethodsForServices);

        when(userPrincipal.getName()).thenReturn(userName);

        competition = Competition.builder()
                .id(1L)
                .eventOwner(userName)
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

        when(competitionRepository.findByEventName(competition.getEventName())).thenReturn(
            Optional.ofNullable(competition));

        Set<String> tags = new HashSet<>(Set.of("someTag", "someNextTag"));
        ResponseEntity<?> response = competitionTagService.addCompetitionTag(tags, competition.getEventName(), userPrincipal.getName());

        verify(competitionRepository, times(1)).save(competition);

        EventTagsDto returnedEventTagsDto = (EventTagsDto) response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assert returnedEventTagsDto != null;
        assertEquals(competition.getEventName(), returnedEventTagsDto.getEventName());
        assertTrue(returnedEventTagsDto.getTags().containsAll(tags));
    }

    @Test
    public void shouldUpdateTag() {

        when(competitionRepository.findByEventName(competition.getEventName())).thenReturn(
            Optional.ofNullable(competition));

        when(competitionRepository.save(competition)).thenReturn(competition);

        String competitionTag = "updatedTag";
        ResponseEntity<?> response = competitionTagService.updateCompetitionTag(competitionTag, competition.getEventName(), userPrincipal.getName());

        verify(competitionRepository, times(1)).save(competition);

        EventTagsDto returnedEventTagsDto = (EventTagsDto) response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assert returnedEventTagsDto != null;
        assertEquals(competition.getEventName(), returnedEventTagsDto.getEventName());
        assertTrue(returnedEventTagsDto.getTags().contains(competitionTag));

    }

    @Test
    public void shouldThrowExceptionCompetitionNotExistsWhenAddTag() {

        Set<String> tags = Set.of("someTag");

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () ->  competitionTagService.addCompetitionTag(tags, competition.getEventName(), userPrincipal.getName()),
            "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).findByEventName(competition.getEventName());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Competition not exists, Name: "+ competition.getEventName(), exception.getReason());
    }


    @Test
    public void shouldThrowExceptionCompetitionNotExistsWhenUpdateTag() {

        String competitionTag = "updatedTag";

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () ->  competitionTagService.updateCompetitionTag(competitionTag, competition.getEventName(), userPrincipal.getName()),
            "Expected doThing() to throw, but it didn't");

        verify(competitionRepository, times(1)).findByEventName(competition.getEventName());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Competition not exists, Name: "+ competition.getEventName(), exception.getReason());
    }

}
