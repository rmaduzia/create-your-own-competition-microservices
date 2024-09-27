package pl.createcompetition.tournamentservice.competition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>{

    Optional<Competition> findByEventName(String eventName);
    List<Competition> findByTagsTag(String tagName);
    boolean existsCompetitionByEventNameIgnoreCase(String eventName);

    @Query("SELECT c FROM Competition c LEFT JOIN FETCH c.tags WHERE c.eventName = :eventName")
    Optional<Competition> findByEventNameWithTags(@Param("eventName") String eventName);

}
