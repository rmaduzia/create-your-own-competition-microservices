package pl.createcompetition.teamservice.all;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.createcompetition.teamservice.exception.ResourceNotFoundException;

@Service
@AllArgsConstructor
public class VerifyMethodsForServices {

    private final TeamRepository teamRepository;

    public Team shouldFindTeam(String teamName, String teamOwner) {
        return teamRepository.findByTeamNameAndTeamOwner(teamName, teamOwner).orElseThrow(() ->
                new ResourceNotFoundException("Team not exists", "Name", teamName));
    }

    public Team shouldFindTeam(String teamName) {
        return teamRepository.findByTeamName(teamName).orElseThrow(() ->
                new ResourceNotFoundException("Team not exists", "Name", teamName));
    }

}
