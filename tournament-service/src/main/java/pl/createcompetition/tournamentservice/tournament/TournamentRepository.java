package pl.createcompetition.tournamentservice.tournament;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByTournamentName(String tournamentName);
    Optional<Tournament> findByTournamentNameAndTournamentOwner(String tournamentName, String tournamentOwner);
    void deleteByTournamentName(String tournamentName);

    boolean existsTournamentByTournamentNameIgnoreCase(String tournamentName);


}
