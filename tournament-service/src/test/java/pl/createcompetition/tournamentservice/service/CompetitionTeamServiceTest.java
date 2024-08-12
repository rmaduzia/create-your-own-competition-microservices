package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.competition.CompetitionTeamService;
import pl.createcompetition.tournamentservice.kafka.domain.MessageSendFacade;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;

@ExtendWith(MockitoExtension.class)
public class CompetitionTeamServiceTest {

    @Mock
    CompetitionRepository competitionRepository;
    @InjectMocks
    CompetitionTeamService competitionTeamService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;
    @Mock
    MessageSendFacade messageSendFacade;

    @Mock
    UserPrincipal userPrincipal;

    private static final String userName = "someUserName";

    Competition competition;
    TeamDto teamDto;
    
    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        competition = Competition.builder()
            .maxAmountOfTeams(10)
            .eventOwner(userName)
            .eventName("Tourtnament1")
            .isEventFinished(false)
            .build();
        
        teamDto = TeamDto.builder()
            .teamName("someTeamName")
            .teamOwner(userPrincipal.getName())

            .build();
    }

    @Test
    public void shouldJoinCompetition() {

        when(verifyMethodsForServices.shouldFindTeam(teamDto.getTeamName(), teamDto.getTeamOwner())).thenReturn(teamDto);
        when(competitionRepository.findByEventName(ArgumentMatchers.anyString())).thenReturn(Optional.of(competition));
        when(competitionRepository.save(competition)).thenReturn(competition);

        ResponseEntity<?> response =  competitionTeamService.teamJoinCompetition(teamDto.getTeamName(), competition.getEventName(), userPrincipal.getName());

        verify(competitionRepository, times(1)).save(competition);
        verify(messageSendFacade, times(1)).sendEvent(any());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals("Added team: " + teamDto.getTeamName() +  " to competition: " + competition.getEventName(), response.getBody());
    }

    @Test
    public void shouldLeaveCompetition() {

        competition.addTeam(teamDto.getTeamName());
        competition.addTeam("secondTeamName");

        when(verifyMethodsForServices.shouldFindTeam(teamDto.getTeamName(), teamDto.getTeamOwner())).thenReturn(teamDto);
        when(competitionRepository.findByEventName(ArgumentMatchers.anyString())).thenReturn(Optional.of(competition));
        when(competitionRepository.save(competition)).thenReturn(competition);

        ResponseEntity<?> response = competitionTeamService.teamLeaveCompetition(teamDto.getTeamName(), competition.getEventName(), userPrincipal.getName());

        verify(competitionRepository, times(1)).save(competition);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals("Removed team: " + teamDto.getTeamName() +  " from competition: " + competition.getEventName(), response.getBody());
    }
}