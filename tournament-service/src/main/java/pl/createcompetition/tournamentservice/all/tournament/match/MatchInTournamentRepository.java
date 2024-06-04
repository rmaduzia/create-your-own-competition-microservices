package pl.createcompetition.tournamentservice.all.tournament.match;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchInTournamentRepository extends JpaRepository<MatchInTournament, Long> {
    boolean existsMatchInTournamentById(Long id);

}
