package pl.createcompetition.tournamentservice.tournament;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByEventName(String eventName);
    Optional<Tournament> findByEventNameAndEventOwner(String eventName, String tournamentOwner);
    void deleteByEventName(String tournamentName);

    boolean existsTournamentByEventNameIgnoreCase(String eventName);

}