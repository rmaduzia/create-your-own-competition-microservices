package pl.createcompetition.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.createcompetition.model.*;
import pl.createcompetition.tournament.match.MatchInTournament;
import pl.createcompetition.tournament.match.MatchInTournamentRepository;
import pl.createcompetition.tournament.Tournament;
import pl.createcompetition.tournament.TournamentRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.tournament.match.MatchInTournamentService;
import pl.createcompetition.user.User;
import pl.createcompetition.user.detail.UserDetail;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MatchInTournamentServiceTest {

    @Mock
    MatchInTournamentRepository matchInTournamentRepository;
    @Mock
    TournamentRepository tournamentRepository;
    @InjectMocks
    MatchInTournamentService matchInTournamentService;

    User user;
    UserDetail userDetail;
    UserPrincipal userPrincipal;
    Tournament tournament;
    MatchInTournament matchInTournament;

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

        tournament = Tournament.builder()
                .id(1L)
                .maxAmountOfTeams(10)
                .tournamentOwner("test@mail.com")
                .tournamentName("Tourtnament1").build();



        matchInTournament = MatchInTournament.builder()
                .id(1L)
                .firstTeamName("firstTeam")
                .secondTeamName("secondTeam")
                .isMatchWasPlayed(false)
                .isWinnerConfirmed(false)
                .build();

        matchInTournament.addMatchToTournament(tournament);

    }

    @Test
    public void shouldAddMatchInTournament() {

        when(tournamentRepository.findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), tournament.getTournamentOwner())).thenReturn(java.util.Optional.of(tournament));
        when(matchInTournamentRepository.save(matchInTournament)).thenReturn(matchInTournament);

        ResponseEntity<?> response = matchInTournamentService.addMatchInTournament(matchInTournament, tournament.getTournamentName(), userPrincipal);

        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), tournament.getTournamentOwner());
        verify(matchInTournamentRepository, times(1)).save(matchInTournament);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), matchInTournament);
    }

    @Test
    public void shouldUpdateMatchInTournament() {

        when(matchInTournamentRepository.existsById(matchInTournament.getId())).thenReturn(true);
        when(tournamentRepository.findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), tournament.getTournamentOwner())).thenReturn(java.util.Optional.of(tournament));
        when(matchInTournamentRepository.save(matchInTournament)).thenReturn(matchInTournament);

        matchInTournament.setIsMatchWasPlayed(true);

        ResponseEntity<?> response = matchInTournamentService.updateMatchInTournament(matchInTournament, matchInTournament.getId(), userPrincipal);


        verify(matchInTournamentRepository, times(1)).existsById(matchInTournament.getId());
        verify(tournamentRepository, times(1)).findByTournamentNameAndTournamentOwner(tournament.getTournamentName(), tournament.getTournamentOwner());
        verify(matchInTournamentRepository, times(1)).save(matchInTournament);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), matchInTournament);
    }

    @Test
    public void shouldDeleteMatchInTournament() {

        when(matchInTournamentRepository.findById(matchInTournament.getId())).thenReturn(Optional.of(matchInTournament));

        ResponseEntity<?> response = matchInTournamentService.deleteMatchInTournament(matchInTournament.getId(), userPrincipal);

        verify(matchInTournamentRepository, times(1)).findById(matchInTournament.getId());
        verify(matchInTournamentRepository, times(1)).deleteById(matchInTournament.getId());

        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}