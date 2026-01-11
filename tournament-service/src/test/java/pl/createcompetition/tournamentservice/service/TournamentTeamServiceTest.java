package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.tournamentservice.kafka.domain.MessageSendFacade;
import pl.createcompetition.tournamentservice.kafka.domain.NotifyTeamMembersRequest;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;
import pl.createcompetition.tournamentservice.tournament.participation.TournamentTeamService;

@ExtendWith(MockitoExtension.class)
public class TournamentTeamServiceTest {

    @Mock
    TournamentRepository tournamentRepository;
    @InjectMocks
    TournamentTeamService tournamentTeamService;
    @Mock
    VerifyMethodsForServices verifyMethodsForServices;
    @Mock
    MessageSendFacade messageSendFacade;

    @Mock
    UserPrincipal userPrincipal;

    @Captor
    ArgumentCaptor<NotifyTeamMembersRequest> notifyTeamMembersRequestArgumentCaptor;

    private static final String userName = "someUserName";

    Tournament tournament;
    TeamDto firstTeamDto;
    TeamDto secondTeamDto;

    @BeforeEach
    public void setUp() {

        when(userPrincipal.getName()).thenReturn(userName);

        tournament = Tournament.builder()
            .maxAmountOfTeams(10)
            .eventOwner(userName)
            .eventName("Tourtnament1")
            .isEventFinished(false)
            .build();
        
        firstTeamDto = TeamDto.builder()
            .teamName("firstTeamName")
            .teamOwner(userPrincipal.getName())
            .build();

        secondTeamDto = TeamDto.builder()
            .teamName("secondTeamName")
            .teamOwner("someOtherTeamOwner")
            .build();
    }

    @Test
    public void shouldJoinTournament() {

        when(verifyMethodsForServices.shouldFindTeam(firstTeamDto.getTeamName(), firstTeamDto.getTeamOwner())).thenReturn(
            firstTeamDto);
        when(tournamentRepository.findByEventName(ArgumentMatchers.anyString())).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ResponseEntity<?> response = tournamentTeamService.teamJoinTournament(tournament.getEventName(), firstTeamDto.getTeamName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(messageSendFacade, times(1)).sendEvent(any());
        verify(messageSendFacade).sendEvent(notifyTeamMembersRequestArgumentCaptor.capture());
        verify(verifyMethodsForServices, times(1)).shouldFindTeam(firstTeamDto.getTeamName(), firstTeamDto.getTeamOwner());

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        NotifyTeamMembersRequest notifyTeamMembersRequest = notifyTeamMembersRequestArgumentCaptor.getValue();

        assertEquals(firstTeamDto.getTeamName(), notifyTeamMembersRequest.getTeamName());
        assertEquals("Your team: " + firstTeamDto.getTeamName() + " joined tournament: " + tournament.getEventName(), notifyTeamMembersRequest.getBody());
        assertEquals(firstTeamDto.getTeamName(), notifyTeamMembersRequest.getKey());
        assertNotNull(notifyTeamMembersRequest.getId());
    }

    @Test
    public void shouldLeaveTournament() {

        tournament.addTeamToTournament(firstTeamDto.getTeamName());
        tournament.addTeamToTournament(secondTeamDto.getTeamName());

        when(verifyMethodsForServices.shouldFindTeam(firstTeamDto.getTeamName(), firstTeamDto.getTeamOwner())).thenReturn(
            firstTeamDto);
        when(tournamentRepository.findByEventName(ArgumentMatchers.anyString())).thenReturn(
            Optional.of(tournament));
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ResponseEntity<?> response = tournamentTeamService.teamLeaveTournament(tournament.getEventName(), firstTeamDto.getTeamName(), userPrincipal.getName());

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(firstTeamDto.getTeamName(), firstTeamDto.getTeamOwner());
        verify(tournamentRepository, times(1)).save(tournament);
        verify(messageSendFacade, times(1)).sendEvent(any());
        verify(messageSendFacade).sendEvent(notifyTeamMembersRequestArgumentCaptor.capture());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String expectedMessage = "Team: " + firstTeamDto.getTeamName() + " left tournament: " + tournament.getEventName();
        assertEquals(expectedMessage, response.getBody());

        NotifyTeamMembersRequest notifyTeamMembersRequest = notifyTeamMembersRequestArgumentCaptor.getValue();

        assertEquals(firstTeamDto.getTeamName(), notifyTeamMembersRequest.getTeamName());
        assertEquals("Your team: " + firstTeamDto.getTeamName() + " left tournament: " + tournament.getEventName(), notifyTeamMembersRequest.getBody());
        assertEquals(firstTeamDto.getTeamName(), notifyTeamMembersRequest.getKey());
        assertNotNull(notifyTeamMembersRequest.getId());
    }
}
