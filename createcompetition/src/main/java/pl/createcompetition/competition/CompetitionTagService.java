package pl.createcompetition.competition;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.createcompetition.exception.ResourceAlreadyExistException;
import pl.createcompetition.exception.ResourceNotFoundException;
import pl.createcompetition.model.Tag;
import pl.createcompetition.security.UserPrincipal;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class CompetitionTagService {

    private final CompetitionRepository competitionRepository;

    public ResponseEntity<?> getCompetitionTag(List<String> competitionTag) {
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> addCompetitionTag(Set<Tag> competitionTag, String competitionName, UserPrincipal userPrincipal) {

        Competition findCompetition =  checkIfCompetitionExists(competitionName);
        checkIfCompetitionBelongToUser(findCompetition, userPrincipal);

        findCompetition.addManyTagToCompetition(competitionTag);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(competitionRepository.save(findCompetition));
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceAlreadyExistException("Tag", "CompetitionTag", competitionTag.iterator().next().getTag());
        }
    }

    public ResponseEntity<?> updateCompetitionTag(Tag competitionTag, String competitionName, UserPrincipal userPrincipal) {

        Competition findCompetition =  checkIfCompetitionExists(competitionName);
        checkIfCompetitionBelongToUser(findCompetition, userPrincipal);

        findCompetition.addTagToCompetition(competitionTag);

        return ResponseEntity.ok(competitionRepository.save(findCompetition));

    }

    public ResponseEntity<?> deleteCompetitionTag(Tag competitionTag, String competitionName, UserPrincipal userPrincipal) {

        Competition findCompetition =  checkIfCompetitionExists(competitionName);
        checkIfCompetitionBelongToUser(findCompetition, userPrincipal);

        if (findCompetition.getTag().contains(competitionTag)) {
            competitionRepository.deleteById(findCompetition.getId());
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException("CompetitionTag", "Tag", competitionTag.getId());
        }
    }

    private Competition checkIfCompetitionExists(String competitionName) {
        return competitionRepository.findByCompetitionName(competitionName).orElseThrow(() ->
                new ResourceNotFoundException("Competition not exists", "Name", competitionName));
    }

    private void checkIfCompetitionBelongToUser(Competition competition, UserPrincipal userPrincipal) {
        if(!competition.getCompetitionOwner().equals(userPrincipal.getUsername())) {
            throw new ResourceNotFoundException("Competition named: " + competition.getCompetitionName(), "Owner", userPrincipal.getUsername());
        }
    }
}
