package pl.createcompetition.tournamentservice.tournament.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchInTournamentRepository extends JpaRepository<MatchInTournament, Long> {
    boolean existsMatchInTournamentById(Long id);

}
