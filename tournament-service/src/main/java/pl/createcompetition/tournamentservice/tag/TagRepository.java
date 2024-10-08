package pl.createcompetition.tournamentservice.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.createcompetition.tournamentservice.model.Tag;

@Repository
public interface TagRepository extends JpaRepository <Tag, Long> {

}