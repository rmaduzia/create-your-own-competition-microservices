package pl.createcompetition.tournamentservice.competition.match;

import java.util.Collections;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.GetQueryImplService;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;


@AllArgsConstructor
@Service
public class MatchInCompetitionService {

    private final GetQueryImplService<MatchInCompetition,?> getQueryImplService;
    private final VerifyMethodsForServices verifyMethodsForServices;
    private final MatchInCompetitionRepository matchInCompetitionRepository;
//    private final TeamRepository teamRepository;

    public PagedResponseDto<?> searchMatchInCompetition(String search, PaginationInfoRequest paginationInfoRequest) {

        return getQueryImplService.execute(MatchInCompetition.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addMatchInCompetition(MatchInCompetition matchInCompetition, String competitionName, UserPrincipal userPrincipal) {

        checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(matchInCompetition);
        Competition foundCompetition = verifyMethodsForServices.shouldFindCompetition(competitionName);

        checkIfCompetitionByNameBelongToUser(competitionName, foundCompetition);
        checkIfTeamParticipatingInCompetition(matchInCompetition, foundCompetition);

        matchInCompetition.addMatchToCompetition(foundCompetition);

        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundCompetition.getEventName(), userPrincipal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(matchInCompetitionRepository.save(matchInCompetition));
    }

    public ResponseEntity<?> updateMatchInCompetition(MatchInCompetition matchInCompetition, Long matchId, UserPrincipal userPrincipal) {

        checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(matchInCompetition);
        MatchInCompetition foundMatch = findMatch(matchId);
        Competition foundCompetition = verifyMethodsForServices.shouldFindCompetition(matchInCompetition.getCompetition().getEventName());

        checkIfCompetitionBelongToUser(foundMatch, userPrincipal);
        checkIfTeamParticipatingInCompetition(matchInCompetition, foundCompetition);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundCompetition.getEventName(), userPrincipal.getName());

        return ResponseEntity.ok(matchInCompetitionRepository.save(matchInCompetition));
    }

    public ResponseEntity<?> deleteMatchInCompetition(Long matchId, UserPrincipal userPrincipal) {

        MatchInCompetition foundMatch = findMatch(matchId);
        checkIfCompetitionBelongToUser(foundMatch, userPrincipal);

        matchInCompetitionRepository.deleteById(matchId);

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<?> addVoteForWinnerTeam(Long matchId, String winnerTeam, UserPrincipal userPrincipal) {

        MatchInCompetition foundMatch = findMatch(matchId);

//        Team foundTeam = teamRepository.findByTeamName(winnerTeam).orElseThrow(() ->
//                new ResourceNotFoundException("Team not exists", "Name", winnerTeam));
        
        if (foundMatch.getCompetition().getTeams().contains(winnerTeam)) {
            foundMatch.addVotesForWinnerTeam(userPrincipal.getName(), winnerTeam);
            String countedWinnerTeam = getTheWinnerTeamBasedOnTheNumberOfVotes(foundMatch);
            foundMatch.setWinnerTeam(countedWinnerTeam);
            matchInCompetitionRepository.save(foundMatch);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You didn't take part in this match");
        }

        return ResponseEntity.ok("You have voted");
    }

    public ResponseEntity<?> closeMatch(Long matchId, UserPrincipal userPrincipal) {

        MatchInCompetition foundMatch = findMatch(matchId);
        verifyMethodsForServices.shouldFindCompetition(foundMatch.getCompetition().getEventName());

        checkIfCompetitionBelongToUser(foundMatch, userPrincipal);
        foundMatch.setIsClosed(true);

        return ResponseEntity.ok("Zamknięto mecz");
    }

    private String getTheWinnerTeamBasedOnTheNumberOfVotes(MatchInCompetition matchInCompetition) {

        int amountOfVotesForFirstTeam = Collections.frequency(matchInCompetition.getVotesForWinnerTeam().values(), matchInCompetition.getFirstTeamName());
        int amountOfVotesForSecondTeam = Collections.frequency(matchInCompetition.getVotesForWinnerTeam().values(), matchInCompetition.getSecondTeamName());

        //RETURN NAME OF WINNER TEAM
        return amountOfVotesForFirstTeam > amountOfVotesForSecondTeam ? matchInCompetition.getFirstTeamName() : matchInCompetition.getSecondTeamName();
    }


    private void checkIfCompetitionByNameBelongToUser(String competitionName, Competition competition) {
        if (!competition.getEventName().equals(competitionName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Competition don't belong to you");
        }
    }

    private void checkIfCompetitionBelongToUser(MatchInCompetition matchInCompetition, UserPrincipal userPrincipal) {
        if (!matchInCompetition.getCompetition().getEventOwner().equals(userPrincipal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition don't belong to you");
        }
    }

    private MatchInCompetition findMatch(Long matchId) {
        return matchInCompetitionRepository.findById(matchId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,  "Match not exists, Id: " + matchId));
    }

    private void checkIfTeamParticipatingInCompetition(MatchInCompetition matchInCompetition, Competition competition) {
        if (!competition.getMatchInCompetition().contains(matchInCompetition)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match are not part of competition named: " + competition.getEventName());
        }
    }

    private void checkIfWinnerTeamHasNotBeenApprovedBeforeMatchStarted(MatchInCompetition matchInCompetition) {
        if (!matchInCompetition.getIsMatchWasPlayed() && matchInCompetition.getIsWinnerConfirmed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't set up winner if match wasn't played");
        }
    }
}
