package pl.createcompetition.tournamentservice.competition.tag;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.tag.EventTagsDto;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@RequiredArgsConstructor
@Service
public class CompetitionTagService {

    private final CompetitionRepository competitionRepository;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public List<Competition> getCompetitionsTag(String tagName) {
        return competitionRepository.findByTagsTag(tagName);
    }

    public ResponseEntity<EventTagsDto> addCompetitionTag(Set<String> competitionTag, String competitionName, String userName) {

        Competition foundCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundCompetition.getEventOwner(), userName);

        foundCompetition.addManyTagToCompetition(competitionTag);

        competitionRepository.save(foundCompetition);

        List<String> tags = foundCompetition.getTags().stream()
            .map(Tag::getTag)
            .toList();
        EventTagsDto eventTagsDto = new EventTagsDto(competitionName,tags);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventTagsDto);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition Tag already exists: " +competitionTag.iterator().next());
        }
    }

    public ResponseEntity<EventTagsDto> updateCompetitionTag(String competitionTag, String competitionName, String userName) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getEventOwner(), userName);

        findCompetition.addTagToCompetition(competitionTag);

        competitionRepository.save(findCompetition);

        EventTagsDto eventTagsDto = new EventTagsDto(competitionName, List.of(competitionTag));

        return ResponseEntity.status(HttpStatus.CREATED).body(eventTagsDto);

    }

    public ResponseEntity<Void> deleteCompetitionTag(String competitionTag, String competitionName, String userName) {

        Competition findCompetition =  verifyMethodsForServices.shouldFindCompetition(competitionName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(findCompetition.getEventOwner(), userName);

        boolean isRemoved = findCompetition.removeTagByName(competitionTag);

        competitionRepository.save(findCompetition);

        if (isRemoved) {
//            competitionRepository.deleteById(findCompetition.getId());
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CompetitionTag Tag not found:" + competitionTag);
        }
    }
}
