package pl.createcompetition.tournamentservice.competition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>{

    Optional<Competition> findByEventName(String eventName);
    List<Competition> findByTagsTag(String tagName);
    boolean existsCompetitionByEventNameIgnoreCase(String eventName);

}
