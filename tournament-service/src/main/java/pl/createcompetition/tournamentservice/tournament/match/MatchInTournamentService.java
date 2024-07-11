package pl.createcompetition.tournamentservice.tournament.match;

import java.util.Collections;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.GetQueryImplService;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;

@Service
@AllArgsConstructor
public class MatchInTournamentService {

    private final GetQueryImplService<MatchInTournament, ?> getQueryImplService;
    private final MatchInTournamentRepository matchInTournamentRepository;
    private final TournamentRepository tournamentRepository;

    public PagedResponseDto<?> searchMatchInTournament(String search, PaginationInfoRequest paginationInfoRequest) {
        return getQueryImplService.execute(MatchInTournament.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addMatchInTournament(MatchInTournament matchInTournament, String tournamentName, String userName) {

        checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(matchInTournament);
        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentNameEqualsToPath(tournamentName, foundTournament);

        checkIfTournamentBelongToUser(foundTournament, userName);
        checkIfTeamParticipatingInTournament(matchInTournament, foundTournament);
        matchInTournament.setTournament(foundTournament);

        return ResponseEntity.status(HttpStatus.CREATED).body(matchInTournamentRepository.save(matchInTournament));
    }

    public ResponseEntity<?> updateMatchInTournament(MatchInTournament matchInTournament, Long matchId, UserPrincipal userPrincipal) {

        checkIfMatchExists(matchId);
        checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(matchInTournament);

        Tournament foundTournament = shouldFindTournament(matchInTournament.getTournament().getTournamentName(), userPrincipal.getName());
        checkIfTeamParticipatingInTournament(matchInTournament, foundTournament);

        return ResponseEntity.ok(matchInTournamentRepository.save(matchInTournament));
    }

    public ResponseEntity<?> deleteMatchInTournament(Long matchId, String userName) {

        MatchInTournament foundMatch = findMatch(matchId);
        checkIfTournamentBelongToUser(foundMatch.getTournament(), userName);

        matchInTournamentRepository.deleteById(matchId);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> addVoteForWinnerTeam(Long matchId, String winnerTeam, UserPrincipal userPrincipal) {

        MatchInTournament foundMatch = findMatch(matchId);

//        Team foundTeam = teamRepository.findByTeamName(winnerTeam).orElseThrow(() ->
//                new ResourceNotFoundException("Team not exists", "Name", winnerTeam));

        if (foundMatch.getTournament().getTeams().contains(winnerTeam)) {
            foundMatch.addVotesForWinnerTeam(userPrincipal.getName(), winnerTeam);
            String countedWinnerTeam = getTheWinnerTeamBasedOnTheNumberOfVotes(foundMatch);
            foundMatch.setWinnerTeam(countedWinnerTeam);
            matchInTournamentRepository.save(foundMatch);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You didn't take part in this match");
        }

        return ResponseEntity.ok("You have voted");
    }

    public ResponseEntity<?> closeMatch(Long matchId, String userName) {

        MatchInTournament foundMatch = findMatch(matchId);
        Tournament foundTournament = shouldFindTournament(foundMatch.getTournament().getTournamentName(), userName);

        checkIfTournamentBelongToUser(foundTournament, userName);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Tournament in path are not equals to body");
        }
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, String userName) {
        if (!tournament.getTournamentOwner().equals(userName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament don't belong to you ");
        }
    }

    private void checkIfMatchExists(Long matchId) {
        if (!matchInTournamentRepository.existsById(matchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not exists, id: " + matchId);
        }
    }

    private MatchInTournament findMatch (Long matchId) {
        return matchInTournamentRepository.findById(matchId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not exists, id: " +matchId));
    }

    private void checkIfTeamParticipatingInTournament(MatchInTournament matchInTournament, Tournament tournament) {
        if (!tournament.getMatchInTournament().contains(matchInTournament)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  matchInTournament.getFirstTeamName() + " are not part of competition named: " + tournament.getTournamentName());
        }
    }

    private void checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(MatchInTournament matchInTournament) {
        if (!matchInTournament.getIsMatchWasPlayed() && matchInTournament.getIsWinnerConfirmed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't set up winner if match wasn't played");
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByTournamentNameAndTournamentOwner(tournamentName, tournamentOwner).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not exists, Name: " + tournamentName));
    }
}