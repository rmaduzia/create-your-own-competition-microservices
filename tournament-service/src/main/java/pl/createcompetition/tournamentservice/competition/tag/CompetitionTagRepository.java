package pl.createcompetition.tournamentservice.competition.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.createcompetition.model.Tag;

public interface CompetitionTagRepository extends JpaRepository <Tag, Long> {
}
