package pl.createcompetition.tournamentservice.tournament;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.model.TeamEntity;
import pl.createcompetition.tournamentservice.query.GetQueryImplService;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;
import pl.createcompetition.tournamentservice.util.MatchTeamsInTournament;

@RequiredArgsConstructor
@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final GetQueryImplService<Tournament,?> queryUserDetailService;
    private final TournamentMapper tournamentMapper;

    public PagedResponseDto<?> searchTournament(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(Tournament.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addTournament(EventCreateUpdateRequest eventCreateUpdateRequest, String userName) {

        if (!tournamentRepository.existsTournamentByEventNameIgnoreCase(eventCreateUpdateRequest.getEventName())) {

            Tournament tournament = Tournament.createTournamentFromDto(eventCreateUpdateRequest, userName);

            return ResponseEntity.status(HttpStatus.CREATED).body(tournamentRepository.save(tournament));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tournament named: " + eventCreateUpdateRequest.getEventName() + " already exists");

        }
    }

    public ResponseEntity<?> updateTournament(String tournamentName, EventCreateUpdateRequest eventCreateUpdateRequest, String userName) {

        if (!eventCreateUpdateRequest.getEventName().equals(tournamentName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tean Name doesn't match with Team object");
        }

        Tournament foundTournament = shouldFindTournament(eventCreateUpdateRequest.getEventName(), userName);
        checkIfTournamentBelongToUser(foundTournament, userName);
        tournamentMapper.updateTournamentFromDto(eventCreateUpdateRequest, foundTournament);

        return ResponseEntity.ok(tournamentRepository.save(foundTournament));
    }

    public ResponseEntity<?> deleteTournament(String tournamentName, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        tournamentRepository.deleteByEventName(tournamentName);

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

        foundTournament.setIsEventStarted(true);
        return ResponseEntity.ok(tournamentRepository.save(foundTournament));

    }

    public ResponseEntity<?> drawTeamOptions(Boolean isWithEachOther, String tournamentName, String userName){

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        if (!foundTournament.getIsEventStarted()) {
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

    public ResponseEntity<?> setTheDatesOfTheTeamsMatches(String tournamentName, Map<String, LocalDateTime> dateMatch, String userName) {

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

        List<String> listOfTeams = shouldFindTeamInUserTournament(tournamentName, userName).stream().map(
            TeamEntity::getTeamName).collect(
            Collectors.toList());
        return MatchTeamsInTournament.matchTeamsInTournament(listOfTeams);
    }

    private Map<String,String>  matchTeamsWithEachOtherInTournament(String tournamentName, String userName) {

        List<String> listOfTeams = shouldFindTeamInUserTournament(tournamentName, userName).stream()
            .map(TeamEntity::getTeamName).collect(
                Collectors.toList());
        return MatchTeamsInTournament.matchTeamsWithEachOtherInTournament(listOfTeams);
    }

    private List<TeamEntity> shouldFindTeamInUserTournament(String tournamentName, String userName) {

        Tournament foundTournament = shouldFindTournament(tournamentName, userName);
        checkIfTournamentBelongToUser(foundTournament, userName);

        return new ArrayList<>(foundTournament.getTeams());
    }

    private void checkIfTournamentBelongToUser(Tournament tournament, String userName) {
        if (!tournament.getEventOwner().equals(userName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found Tournament named: " + tournament.getEventName()+ " with Owner: " + userName);
        }
    }

    private Tournament shouldFindTournament(String tournamentName, String tournamentOwner) {
        return tournamentRepository.findByEventNameAndEventOwner(tournamentName, tournamentOwner).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not exists. Name: " + tournamentName));
    }



}
