package pl.createcompetition.tournamentservice.all.tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.GetQueryImplService;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;
import pl.createcompetition.tournamentservice.util.MatchTeamsInTournament;

@RequiredArgsConstructor
@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final GetQueryImplService<Tournament,?> queryUserDetailService;

    public PagedResponseDto<?> searchTournament(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(Tournament.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addTournament(Tournament tournament, String userName) {

        if (!tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())) {
            tournament.setTournamentOwner(userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(tournamentRepository.save(tournament));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tournament named: " + tournament.getTournamentName() + " already exists");

        }
    }

    public ResponseEntity<?> updateTournament(String tournamentName, Tournament tournament, String userName) {

        if (!tournament.getTournamentName().equals(tournamentName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tean Name doesn't match with Team object");
        }

        Tournament foundTournament = shouldFindTournament(tournament.getTournamentName(), userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        return ResponseEntity.ok(tournamentRepository.save(tournament));
    }

    public ResponseEntity<?> deleteTournament(String tournamentName, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        tournamentRepository.deleteByTournamentName(tournamentName);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> removeTeamFromTournament(String tournamentName, String teamName, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        boolean isRemoved = foundTournament.deleteTeamFromTournament(teamName);

        if (!isRemoved) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Team not exists. Name: " +teamName);
        }

        foundTournament.deleteTeamFromTournament(teamName);
        tournamentRepository.save(foundTournament);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> startTournament(String tournamentName, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        if (foundTournament.getDrawnTeams().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"You have to draw teams before start competition");
        }

        foundTournament.setIsStarted(true);
        return ResponseEntity.ok(tournamentRepository.save(foundTournament));

    }

    public ResponseEntity<?> drawTeamOptions(Boolean isWithEachOther, String tournamentName, String userName){

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        if (!foundTournament.getIsStarted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't draw team if competition already started");
        }

        Map<String,String> matchedTeams;

        if (isWithEachOther) {
            matchedTeams = matchTeamsWithEachOtherInTournament(tournamentName, userName);
        }
        else {
            matchedTeams = matchTeamsInTournament(tournamentName, userName);
        }

        foundTournament.setDrawnTeams(matchedTeams);
        tournamentRepository.save(foundTournament);
        return ResponseEntity.ok().body(matchedTeams);
    }

    public ResponseEntity<?> setTheDatesOfTheTeamsMatches(String tournamentName, Map<String, Date> dateMatch, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        foundTournament.setMatchTimes(dateMatch);

        return ResponseEntity.ok(tournamentRepository.save(foundTournament));
    }

    public ResponseEntity<?> deleteDateOfTheTeamsMatches(String tournamentName, String idDateMatch, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        foundTournament.getMatchTimes().remove(idDateMatch);
        tournamentRepository.save(foundTournament);

        return ResponseEntity.noContent().build();
    }


    private Map<String,String> matchTeamsInTournament(String tournamentName, String userName) {

        List<String> listOfTeams = shouldFindTeamInUserTournament(tournamentName, userName);

        return MatchTeamsInTournament.matchTeamsInTournament(listOfTeams);
    }

    private Map<String,String>  matchTeamsWithEachOtherInTournament(String tournamentName, String userName) {

        List<String> listOfTeams = shouldFindTeamInUserTournament(tournamentName, userName);

        return MatchTeamsInTournament.matchTeamsWithEachOtherInTournament(listOfTeams);
    }

    private List<String> shouldFindTeamInUserTournament(String tournamentName, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        return new ArrayList<>(foundTournament.getTeams());
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, String userName) {
        if (!tournament.getTournamentOwner().equals(userName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found Tournament named: " + tournament.getTournamentName()+ " with Owner: " + userName);
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByTournamentNameAndTournamentOwner(tournamentName, tournamentOwner).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not exists. Name: " + tournamentName));
    }



}
