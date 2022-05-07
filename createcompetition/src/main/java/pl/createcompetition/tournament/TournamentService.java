package pl.createcompetition.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.util.VerifyMethodsForServices;
import pl.createcompetition.team.Team;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.util.query.GetQueryImplService;
import pl.createcompetition.util.MatchTeamsInTournament;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final GetQueryImplService<Tournament,?> queryUserDetailService;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public PagedResponseDto<?> searchTournament(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(Tournament.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addTournament(Tournament tournament, UserPrincipal userPrincipal) {

        if (!tournamentRepository.existsTournamentByTournamentNameIgnoreCase(tournament.getTournamentName())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(tournamentRepository.save(tournament));
        } else {
            throw new ResourceAlreadyExistException("Tournament", "Name", tournament.getTournamentName());

        }
    }

    public ResponseEntity<?> updateTournament(String tournamentName, Tournament tournament, UserPrincipal userPrincipal) {

        if (!tournament.getTournamentName().equals(tournamentName)) {
            throw new BadRequestException("Tean Name doesn't match with Team object");
        }

        Tournament foundTournament = shouldFindTournament(tournament.getTournamentName(), userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        return ResponseEntity.ok(tournamentRepository.save(tournament));
    }

    public ResponseEntity<?> deleteTournament(String tournamentName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        tournamentRepository.deleteByTournamentName(tournamentName);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> removeTeamFromTournament(String tournamentName, String teamName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        Team foundTeam = verifyMethodsForServices.shouldFindTeam(teamName);

        foundTournament.deleteTeamFromTournament(foundTeam);
        tournamentRepository.save(foundTournament);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> startTournament(String tournamentName, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        if (foundTournament.getDrawedTeams().isEmpty()){
            throw new BadRequestException("You have to draw teams before start competition");
        }

        foundTournament.setIsStarted(true);
        return ResponseEntity.ok(tournamentRepository.save(foundTournament));

    }

    public ResponseEntity<?> drawTeamOptions(Boolean isWithEachOther, String tournamentName,UserPrincipal userPrincipal){

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        if (!foundTournament.getIsStarted()) {
            throw new BadRequestException("You can't draw team if competition already started");
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

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        foundTournament.setMatchTimes(dateMatch);

        return ResponseEntity.ok(tournamentRepository.save(foundTournament));
    }

    public ResponseEntity<?> deleteDateOfTheTeamsMatches(String tournamentName, String idDateMatch, UserPrincipal userPrincipal) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
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

        Tournament foundTournament = shouldFindTournament(tournamentName, userPrincipal.getUsername());
        checkIfTournamentBelongToUser(foundTournament, userPrincipal);

        List<String> listOfTeams = new ArrayList<>();

        for (Team f : foundTournament.getTeams()) {
            listOfTeams.add(f.getTeamName());
        }
        return listOfTeams;
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, UserPrincipal userPrincipal) {
        if (!tournament.getTournamentOwner().equals(userPrincipal.getUsername())) {
            throw new ResourceNotFoundException("Tournament named: " + tournament.getTournamentName(), "Owner", userPrincipal.getUsername());
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByTournamentNameAndTournamentOwner(tournamentName, tournamentOwner).orElseThrow(() ->
                new ResourceNotFoundException("Tournament not exists", "Name", tournamentName));
    }
}
