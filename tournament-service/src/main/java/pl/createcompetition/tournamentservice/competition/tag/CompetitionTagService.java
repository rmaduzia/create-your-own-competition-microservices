package pl.createcompetition.tournamentservice.competition.tag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Service
public class CompetitionTagService {

    private final CompetitionRepository competitionRepository;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public List<Competition> getCompetitionsTag(String tagName) {
        return competitionRepository.findByTagsTag(tagName);
    }

    public ResponseEntity<?> addCompetitionTag(Set<String> competitionTag, String competitionName, String userName) {

        Competition foundCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundCompetition.getEventOwner(), userName);

        foundCompetition.addManyTagToCompetition(competitionTag);

        competitionRepository.save(foundCompetition);

        List<String> tags = foundCompetition.getTags().stream()
            .map(Tag::getTag)
            .toList();;
        EventTagsDto eventTagsDto = new EventTagsDto(competitionName,tags);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventTagsDto);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition Tag already exists: " +competitionTag.iterator().next());
        }
    }

    public ResponseEntity<?> updateCompetitionTag(Tag competitionTag, String competitionName, String userName) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getEventOwner(), userName);

        findCompetition.addTagToCompetition(competitionTag);

        return ResponseEntity.ok(competitionRepository.save(findCompetition));

    }

    public ResponseEntity<?> deleteCompetitionTag(Tag competitionTag, String competitionName, String userName) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getEventOwner(), userName);

        if (findCompetition.getTags().contains(competitionTag)) {
            competitionRepository.deleteById(findCompetition.getId());
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CompetitionTag Tag not found:" + competitionTag.getId());
        }
    }
}
