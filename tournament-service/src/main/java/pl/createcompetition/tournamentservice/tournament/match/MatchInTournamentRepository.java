package pl.createcompetition.tournamentservice.tournament.match;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchInTournamentRepository extends JpaRepository<MatchInTournament, Long> {
    boolean existsMatchInTournamentById(Long id);

}
