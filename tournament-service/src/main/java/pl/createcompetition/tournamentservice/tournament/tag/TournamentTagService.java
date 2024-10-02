package pl.createcompetition.tournamentservice.tournament.tag;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.tag.EventTagsDto;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;

@RequiredArgsConstructor
@Service
public class TournamentTagService {

    private final TournamentRepository tournamentRepository;
    private final VerifyMethodsForServices verifyMethodsForServices;

    public List<Tournament> getTournamentsByTag(String tagName) {
        return tournamentRepository.findByTagsTag(tagName);
    }

    public ResponseEntity<EventTagsDto> addTournamentTag(Set<String> tournamentTag, String tournamentName, String userName) {

        Tournament foundTournament =  verifyMethodsForServices.shouldFindTournament(tournamentName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundTournament.getEventOwner(), userName);

        foundTournament.addManyTagsToTournament(tournamentTag);

        tournamentRepository.save(foundTournament);

        List<String> tags = foundTournament.getTags().stream()
            .map(Tag::getTag)
            .toList();
        EventTagsDto eventTagsDto = new EventTagsDto(tournamentName,tags);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventTagsDto);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Competition Tag already exists: " +tournamentTag.iterator().next());
        }
    }

    public ResponseEntity<EventTagsDto> updateTournamentTag(String tournamentTag, String tournamentName, String userName) {

        Tournament foundTournament =  verifyMethodsForServices.shouldFindTournament(tournamentName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundTournament.getEventOwner(), userName);

        foundTournament.addTagToTournament(tournamentTag);

        tournamentRepository.save(foundTournament);

        EventTagsDto eventTagsDto = new EventTagsDto(tournamentName, List.of(tournamentTag));

        return ResponseEntity.status(HttpStatus.CREATED).body(eventTagsDto);

    }

    public ResponseEntity<Void> deleteTournamentTag(String tournamentTag, String tournamentName, String userName) {

        Tournament foundTournament =  verifyMethodsForServices.shouldFindTournament(tournamentName);
        verifyMethodsForServices.checkIfCompetitionBelongToUser(foundTournament.getEventOwner(), userName);

        boolean isRemoved = foundTournament.removeTagByName(tournamentTag);

        if (isRemoved) {
            tournamentRepository.save(foundTournament);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CompetitionTag Tag not found:" + tournamentTag);
        }
    }
}
