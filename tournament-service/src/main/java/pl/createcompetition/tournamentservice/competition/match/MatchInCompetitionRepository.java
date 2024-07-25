package pl.createcompetition.tournamentservice.competition.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchInCompetitionRepository extends JpaRepository<MatchInCompetition, Long> {


}
