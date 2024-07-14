package pl.createcompetition.tournamentservice.competition;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.model.PagedResponseDto;
import pl.createcompetition.tournamentservice.query.GetQueryImplService;
import pl.createcompetition.tournamentservice.query.PaginationInfoRequest;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@Service
@AllArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final GetQueryImplService<Competition,?> queryUserDetailService;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public PagedResponseDto<?> searchCompetition(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(Competition.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addCompetition(CompetitionCreateRequest competitionCreateRequest, String userName) {

        if(!competitionRepository.existsCompetitionByCompetitionNameIgnoreCase(
            competitionCreateRequest.getCompetitionName())) {
            Competition competition = Competition.createCompetition(competitionCreateRequest, userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(competitionRepository.save(competition));
        } else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition already exists. Named: " + competitionCreateRequest.getCompetitionName());
        }
    }

    public ResponseEntity<?> updateCompetition(String competitionName, Competition competition, String userName) {

        if (!competition.getCompetitionName().equals(competitionName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition Name doesn't match with Competition object");
        }
        Competition findCompetition = verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName());
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionOwner(), userName);

        return ResponseEntity.ok(competitionRepository.save(competition));
    }

    public ResponseEntity<?> deleteCompetition(String competitionName, String userName){

        Competition findCompetition = verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionName(), userName);

        competitionRepository.deleteById(findCompetition.getId());
        return ResponseEntity.noContent().build();
    }
}