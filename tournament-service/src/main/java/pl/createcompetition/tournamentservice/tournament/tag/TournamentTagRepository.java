package pl.createcompetition.tournamentservice.tournament.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.createcompetition.tournamentservice.model.Tag;

@Repository
public interface TournamentTagRepository extends JpaRepository <Tag, Long> {

}