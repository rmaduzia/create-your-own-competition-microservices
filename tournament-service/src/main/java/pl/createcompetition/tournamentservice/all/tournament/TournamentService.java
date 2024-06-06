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
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
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

    public ResponseEntity<?> addTournament(Tournament tournament, UserPrincipal userPrincipal) {

        if (!tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(tournamentRepository.save(tournament));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tournament named: " + tournament.getTournamentName() + " already exists");

        }
    }

    public ResponseEntity<?> updateTournament(String tournamentName, Tournament tournament, UserPrincipal userPrincipal) {

        if (!tournament.getTournamentName().equals(tournamentName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tean Name doesn't match with Team object");
        }

        Tournament foundTournament = shouldFindTournament(tournament.getTournamentName(), userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        return ResponseEntity.ok(tournamentRepository.save(tournament));
    }

    public ResponseEntity<?> deleteTournament(String tournamentName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        tournamentRepository.deleteByTournamentName(tournamentName);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> removeTeamFromTournament(String tournamentName, String teamName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        boolean isRemoved = foundTournament.deleteTeamFromTournament(teamName);

        if (!isRemoved) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Team not exists. Name: " +teamName);
        }

        foundTournament.deleteTeamFromTournament(teamName);
        tournamentRepository.save(foundTournament);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> startTournament(String tournamentName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        if (foundTournament.getDrawedTeams().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"You have to draw teams before start competition");
        }

        foundTournament.setIsStarted(true);
        return ResponseEntity.ok(tournamentRepository.save(foundTournament));

    }

    public ResponseEntity<?> drawTeamOptions(Boolean isWithEachOther, String tournamentName,UserPrincipal userPrincipal){

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        if (!foundTournament.getIsStarted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't draw team if competition already started");
        }

        Map<String,String> matchedTeams;

        if (isWithEachOther) {
            matchedTeams = matchTeamsWithEachOtherInTournament(tournamentName, userPrincipal);
            foundTournament.setDrawedTeams(matchedTeams);
            tournamentRepository.save(foundTournament);
            return ResponseEntity.ok().body(matchedTeams);
        }
        else
            matchedTeams = matchTeamsInTournament(tournamentName, userPrincipal);
            foundTournament.setDrawedTeams(matchedTeams);
            tournamentRepository.save(foundTournament);
            return ResponseEntity.ok().body(matchedTeams);
    }

    public ResponseEntity<?> setTheDatesOfTheTeamsMatches(String tournamentName, Map<String, Date> dateMatch, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        foundTournament.setMatchTimes(dateMatch);

        return ResponseEntity.ok(tournamentRepository.save(foundTournament));
    }

    public ResponseEntity<?> deleteDateOfTheTeamsMatches(String tournamentName, String idDateMatch, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        foundTournament.getMatchTimes().remove(idDateMatch);
        tournamentRepository.save(foundTournament);

        return ResponseEntity.noContent().build();
    }


    private Map<String,String> matchTeamsInTournament(String tournamentName, UserPrincipal userPrincipal) {

        List<String> listOfTeams = shouldFindTeamInUserTournament(tournamentName, userPrincipal);

        return MatchTeamsInTournament.matchTeamsInTournament(listOfTeams);
    }

    private Map<String,String>  matchTeamsWithEachOtherInTournament(String tournamentName, UserPrincipal userPrincipal) {

        List<String> listOfTeams = shouldFindTeamInUserTournament(tournamentName, userPrincipal);

        return MatchTeamsInTournament.matchTeamsWithEachOtherInTournament(listOfTeams);
    }

    private List<String> shouldFindTeamInUserTournament(String tournamentName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getName());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        return new ArrayList<>(foundTournament.getTeams());
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, UserPrincipal userPrincipal) {
        if (!tournament.getTournamentOwner().equals(userPrincipal.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament named: " + tournament.getTournamentName()+ " Owner: " +userPrincipal.getName());
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByTournamentNameAndTournamentOwner(tournamentName, tournamentOwner).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not exists. Name: " + tournamentName));
    }



}
