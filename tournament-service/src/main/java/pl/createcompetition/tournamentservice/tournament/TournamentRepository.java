package pl.createcompetition.tournamentservice.tournament;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.createcompetition.tournamentservice.competition.Competition;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByEventName(String eventName);
    Optional<Tournament> findByEventNameAndEventOwner(String eventName, String tournamentOwner);
    void deleteByEventName(String tournamentName);

    boolean existsTournamentByEventNameIgnoreCase(String eventName);
    List<Tournament> findByTagsTag(String tagName);

    @Query("SELECT c FROM Tournament c LEFT JOIN FETCH c.tags WHERE c.eventName = :eventName")
    Optional<Tournament> findByEventNameWithTags(@Param("eventName") String eventName);

    @Query("SELECT c FROM Tournament c LEFT JOIN FETCH c.teams WHERE c.eventName = :eventName")
    Optional<Tournament> findByEventNameWithTeams(@Param("eventName") String eventName);
}