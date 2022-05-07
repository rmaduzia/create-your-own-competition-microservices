package pl.createcompetition.competition;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.exception.BadRequestException;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.model.PagedResponseDto;
import pl.createcompetition.user.detail.UserDetail;
import pl.createcompetition.payload.PaginationInfoRequest;
import pl.createcompetition.user.detail.UserDetailRepository;
import pl.createcompetition.security.UserPrincipal;
import pl.createcompetition.util.VerifyMethodsForServices;
import pl.createcompetition.util.query.GetQueryImplService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserDetailRepository userDetailRepository;
    private final GetQueryImplService<Competition,?> queryUserDetailService;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public PagedResponseDto<?> searchCompetition(String search, PaginationInfoRequest paginationInfoRequest) {

        return queryUserDetailService.execute(Competition.class, search, paginationInfoRequest.getPageNumber(), paginationInfoRequest.getPageSize());
    }

    public ResponseEntity<?> addCompetition(Competition competition, UserPrincipal userPrincipal) {

        if(!competitionRepository.existsCompetitionByCompetitionNameIgnoreCase(competition.getCompetitionName())) {
            Optional<UserDetail> userDetail = userDetailRepository.findById(userPrincipal.getId());
            competition.setCompetitionOwner(userPrincipal.getUsername());
            userDetail.get().addUserToCompetition(competition);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDetailRepository.save(userDetail.get()));
        } else{
            throw new ResourceAlreadyExistException("Competition", "Name", competition.getCompetitionName());
        }
    }

    public ResponseEntity<?> updateCompetition(String competitionName, Competition competition, UserPrincipal userPrincipal) {

        if (!competition.getCompetitionName().equals(competitionName)) {
            throw new BadRequestException("Competition Name doesn't match with Competition object");
        }
        Competition findCompetition = verifyMethodsForServices.shouldFindCompetition(competition.getCompetitionName());
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionName(), userPrincipal.getUsername());

        return ResponseEntity.ok(competitionRepository.save(competition));
    }

    public ResponseEntity<?> deleteCompetition(String competitionName, UserPrincipal userPrincipal){

        Competition findCompetition = verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionName(), userPrincipal.getUsername());

        competitionRepository.deleteById(findCompetition.getId());
        return ResponseEntity.noContent().build();
    }
}