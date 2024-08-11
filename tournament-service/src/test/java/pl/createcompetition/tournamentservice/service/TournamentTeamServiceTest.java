package pl.createcompetition.tournamentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import pl.createcompetition.tournamentservice.kafka.domain.MessageSendFacade;
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

        ResponseEntity<?> response = tournamentTeamService.teamJoinTournament(firstTeamDto.getTeamName(), tournament.getEventName(), userPrincipal.getName());

        verify(tournamentRepository, times(1)).save(tournament);
        verify(verifyMethodsForServices, times(1)).shouldFindTeam(firstTeamDto.getTeamName(), firstTeamDto.getTeamOwner());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
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

        ResponseEntity<?> response = tournamentTeamService.teamLeaveTournament(firstTeamDto.getTeamName(), tournament.getEventName(), userPrincipal.getName());

        verify(verifyMethodsForServices, times(1)).shouldFindTeam(firstTeamDto.getTeamName(), firstTeamDto.getTeamOwner());
        verify(tournamentRepository, times(1)).save(tournament);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNull(response.getBody());
    }
}
