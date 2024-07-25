package pl.createcompetition.tournamentservice.competition;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>{

    Optional<Competition> findByCompetitionName(String competitionName);
    boolean existsCompetitionByCompetitionNameIgnoreCase(String competitionName);
    Optional<Competition> findByCompetitionNameAndCompetitionOwner(String competitionName, String competitionOwner);
    
}
