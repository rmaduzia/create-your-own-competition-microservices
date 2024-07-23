package pl.createcompetition.tournamentservice.competition.tag;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.microserviceschanges.UserPrincipal;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@AllArgsConstructor
@Service
public class CompetitionTagService {

    private final CompetitionRepository competitionRepository;
    private final VerifyMethodsForServices verifyMethodsForServices;


    public ResponseEntity<?> getCompetitionTag(List<String> competitionTag) {
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> addCompetitionTag(Set<Tag> competitionTag, String competitionName, UserPrincipal userPrincipal) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionOwner(), userPrincipal.getName());

        findCompetition.addManyTagToCompetition(competitionTag);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(competitionRepository.save(findCompetition));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition Tag already exists: " +competitionTag.iterator().next().getTag());
        }
    }

    public ResponseEntity<?> updateCompetitionTag(Tag competitionTag, String competitionName, UserPrincipal userPrincipal) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionOwner(), userPrincipal.getName());

        findCompetition.addTagToCompetition(competitionTag);

        return ResponseEntity.ok(competitionRepository.save(findCompetition));

    }

    public ResponseEntity<?> deleteCompetitionTag(Tag competitionTag, String competitionName, UserPrincipal userPrincipal) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getCompetitionOwner(), userPrincipal.getName());

        if (findCompetition.getTags().contains(competitionTag)) {
            competitionRepository.deleteById(findCompetition.getId());
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CompetitionTag Tag not found:" + competitionTag.getId());
        }
    }
}
