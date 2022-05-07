package pl.createcompetition.tournament.match;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.createcompetition.tournament.match.MatchInTournament;

public interface MatchInTournamentRepository extends JpaRepository<MatchInTournament, Long> {
    boolean existsMatchInTournamentById(Long id);

}
