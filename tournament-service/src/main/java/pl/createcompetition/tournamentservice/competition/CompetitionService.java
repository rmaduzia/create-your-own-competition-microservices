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
    private final CompetitionMapper competitionMapper;

    public PagedResponseDto<?> searchCompetition(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(Competition.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addCompetition(CompetitionCreateUpdateRequest competitionCreateUpdateRequest, String userName) {

        if(!competitionRepository.existsCompetitionByEventNameIgnoreCase(competitionCreateUpdateRequest.getEventName())) {
            Competition competition = Competition.createCompetition(competitionCreateUpdateRequest, userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(competitionRepository.save(competition));
        } else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition already exists. Named: " + competitionCreateUpdateRequest.getEventName());
        }
    }

    public ResponseEntity<?> updateCompetition(String competitionName, CompetitionCreateUpdateRequest competitionCreateUpdateRequest, String userName) {

        if (!competitionCreateUpdateRequest.getEventName().equals(competitionName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition Name doesn't match with Competition object");
        }
        Competition foundCompetition = verifyMethodsForServices.shouldFindCompetition(competitionCreateUpdateRequest.getEventName());
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundCompetition.getEventOwner(), userName);

        competitionMapper.updateCompetitionFromDto(competitionCreateUpdateRequest, foundCompetition);

        CompetitionCreateUpdateRequest savedCompetition = competitionMapper.mapCompetitionToSimpleCompetitionDto(competitionRepository.save(foundCompetition));

        return ResponseEntity.ok(savedCompetition);
    }

    public ResponseEntity<?> deleteCompetition(String competitionName, String userName){

        Competition findCompetition = verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getEventOwner(), userName);

        competitionRepository.deleteById(findCompetition.getId());
        return ResponseEntity.noContent().build();
    }

}