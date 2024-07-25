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

    public ResponseEntity<?> addCompetition(EventCreateUpdateRequest eventCreateUpdateRequest, String userName) {

        if(!competitionRepository.existsCompetitionByEventNameIgnoreCase(eventCreateUpdateRequest.getEventName())) {
            Competition competition = Competition.createCompetition(eventCreateUpdateRequest, userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(competitionRepository.save(competition));
        } else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition already exists. Named: " + eventCreateUpdateRequest.getEventName());
        }
    }

    public ResponseEntity<?> updateCompetition(String eventName, EventCreateUpdateRequest eventCreateUpdateRequest, String userName) {

        if (!eventCreateUpdateRequest.getEventName().equals(eventName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition Name doesn't match with Competition object");
        }
        Competition foundCompetition = verifyMethodsForServices.shouldFindCompetition(
            eventCreateUpdateRequest.getEventName());
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundCompetition.getEventOwner(), userName);

        competitionMapper.updateCompetitionFromDto(eventCreateUpdateRequest, foundCompetition);

        EventCreateUpdateRequest savedCompetition = competitionMapper.mapCompetitionToSimpleCompetitionDto(competitionRepository.save(foundCompetition));

        return ResponseEntity.ok(savedCompetition);
    }

    public ResponseEntity<?> deleteCompetition(String eventName, String userName){

        Competition findCompetition = verifyMethodsForServices.shouldFindCompetition(eventName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getEventOwner(), userName);

        competitionRepository.deleteById(findCompetition.getId());
        return ResponseEntity.noContent().build();
    }

}