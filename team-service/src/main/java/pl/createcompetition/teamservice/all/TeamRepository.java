package pl.createcompetition.teamservice.all;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamName(String teamName);
    Optional<Team> findByTeamNameAndTeamOwner(String teamName, String teamOwner);
    List<String> findTeamMembersByTeamName(String teamName);
    boolean existsTeamByTeamNameIgnoreCase(String teamName);
}
