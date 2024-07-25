package pl.createcompetition.tournamentservice.competition.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.createcompetition.tournamentservice.model.Tag;

@Repository
public interface CompetitionTagRepository extends JpaRepository <Tag, Long> {

}