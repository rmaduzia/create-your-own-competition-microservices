package pl.createcompetition.tournament.match;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.*;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.team.Team;
import pl.createcompetition.team.TeamRepository;
import pl.createcompetition.tournament.Tournament;
import pl.createcompetition.tournament.TournamentRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.util.query.GetQueryImplService;

import java.util.Collections;

@Service
@AllArgsConstructor
public class MatchInTournamentService {

    private final GetQueryImplService<MatchInTournament, ?> getQueryImplService;
    private final MatchInTournamentRepository matchInTournamentRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    public PagedResponseDto<?> searchMatchInTournament(String search, PaginationInfoRequest paginationInfoRequest) {
        return getQueryImplService.execute(MatchInTournament.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addMatchInTournament(MatchInTournament matchInTournament, String tournamentName, UserPrincipal userPrincipal) {

        checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(matchInTournament);
        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentNameEqualsToPath(tournamentName, foundTournament);

        checkIfTournamentBelongToUser(foundTournament, userPrincipal);
        checkIfTeamParticipatingInTournament(matchInTournament, foundTournament);
        matchInTournament.setTournament(foundTournament);

        return ResponseEntity.status(HttpStatus.CREATED).body(matchInTournamentRepository.save(matchInTournament));
    }

    public ResponseEntity<?> updateMatchInTournament(MatchInTournament matchInTournament, Long matchId, UserPrincipal userPrincipal) {

        checkIfMatchExists(matchId);
        checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(matchInTournament);

        Tournament foundTournament = shouldFindTournament(matchInTournament.getTournament().getTournamentName(), userPrincipal.getUsername());
        checkIfTeamParticipatingInTournament(matchInTournament, foundTournament);

        return ResponseEntity.ok(matchInTournamentRepository.save(matchInTournament));
    }

    public ResponseEntity<?> deleteMatchInTournament(Long matchId, UserPrincipal userPrincipal) {

        MatchInTournament foundMatch = findMatch(matchId);
        checkIfTournamentBelongToUser(foundMatch.getTournament(), userPrincipal);

        matchInTournamentRepository.deleteById(matchId);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> addVoteForWinnerTeam(Long matchId, String winnerTeam, UserPrincipal userPrincipal) {

        MatchInTournament foundMatch = findMatch(matchId);

        Team foundTeam = teamRepository.findByTeamName(winnerTeam).orElseThrow(() ->
                new ResourceNotFoundException("Team not exists", "Name", winnerTeam));

        if (foundMatch.getTournament().getTeams().contains(foundTeam)) {
            foundMatch.addVotesForWinnerTeam(userPrincipal.getUsername(), winnerTeam);
            String countedWinnerTeam = getTheWinnerTeamBasedOnTheNumberOfVotes(foundMatch);
            foundMatch.setWinnerTeam(countedWinnerTeam);
            matchInTournamentRepository.save(foundMatch);
        } else {
            throw new BadRequestException("You didn't take part in this match");
        }

        return ResponseEntity.ok("You have voted");
    }

    public ResponseEntity<?> closeMatch(Long matchId, UserPrincipal userPrincipal) {

        MatchInTournament foundMatch = findMatch(matchId);
        Tournament foundTournament = shouldFindTournament(foundMatch.getTournament().getTournamentName(), userPrincipal.getUsername());

        checkIfTournamentBelongToUser(foundTournament, userPrincipal);
        foundMatch.setIsClosed(true);

        return ResponseEntity.ok("Zamknięto mecz");
    }

    private String getTheWinnerTeamBasedOnTheNumberOfVotes(MatchInTournament matchInTournament) {

        int amountOfVotesForFirstTeam = Collections.frequency(matchInTournament.getVotesForWinnerTeam().values(), matchInTournament.getFirstTeamName());
        int amountOfVotesForSecondTeam = Collections.frequency(matchInTournament.getVotesForWinnerTeam().values(), matchInTournament.getSecondTeamName());

        //RETURN NAME OF WINNER TEAM
        return amountOfVotesForFirstTeam > amountOfVotesForSecondTeam ? matchInTournament.getFirstTeamName() : matchInTournament.getSecondTeamName();
    }



    private void checkIfTournamentNameEqualsToPath(String tournamentName, Tournament tournament) {
        if (!tournamentName.equals(tournament.getTournamentName())) {
            throw new BadRequestException("Tournament in path are not equals to body");
        }
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, UserPrincipal userPrincipal) {
        if (!tournament.getTournamentOwner().equals(userPrincipal.getUsername())) {
            throw new BadRequestException("Tournament don't belong to you ");
        }
    }

    private void checkIfMatchExists(Long matchId) {
        if (!matchInTournamentRepository.existsById(matchId)) {
            throw new ResourceNotFoundException("Match not exists", "Id", matchId);
        }
    }

    private MatchInTournament findMatch (Long matchId) {
        return matchInTournamentRepository.findById(matchId).orElseThrow(() ->
                new ResourceNotFoundException("Match not exists", "Id", matchId));
    }

    private void checkIfTeamParticipatingInTournament(MatchInTournament matchInTournament, Tournament tournament) {
        if (!tournament.getMatchInTournament().contains(matchInTournament)) {
            throw new BadRequestException(matchInTournament.getFirstTeamName() + " are not part of competition named: " + tournament.getTournamentName());
        }
    }

    private void checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(MatchInTournament matchInTournament) {
        if (!matchInTournament.getIsMatchWasPlayed() && matchInTournament.getIsWinnerConfirmed()) {
            throw new BadRequestException("You can't set up winner if match wasn't played");
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByTournamentNameAndTournamentOwner(tournamentName, tournamentOwner).orElseThrow(() ->
                new ResourceNotFoundException("Tournament not exists", "Name", tournamentName));
    }
}