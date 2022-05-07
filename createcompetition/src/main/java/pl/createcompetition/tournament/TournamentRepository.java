package pl.createcompetition.tournament;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByTournamentName(String tournamentName);
    Optional<Tournament> findByTournamentNameAndTournamentOwner(String tournamentName, String tournamentOwner);
    void deleteByTournamentName(String tournamentName);

    boolean existsTournamentByTournamentNameIgnoreCase(String tournamentName);


}
