package pl.createcompetition.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamName(String teamName);
    Optional<Team> findByTeamNameAndTeamOwner(String teamName, String teamOwner);
    boolean existsTeamByTeamNameIgnoreCase(String teamName);
}
