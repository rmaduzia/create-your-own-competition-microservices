package pl.createcompetition.teamservice.all;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamName(String teamName);
    Optional<Team> findByTeamNameAndTeamOwner(String teamName, String teamOwner);

    @Query("SELECT tm FROM Team t JOIN t.teamMembers tm WHERE t.teamName = :teamName")
    List<String> findTeamMembersByTeamName(String teamName);
    boolean existsTeamByTeamNameIgnoreCase(String teamName);
}
