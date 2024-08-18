package pl.createcompetition.teamservice.all;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class VerifyMethodsForServices {

    private final TeamRepository teamRepository;

    public Team shouldFindTeam(String teamName, String teamOwner) {
        return teamRepository.findByTeamNameAndTeamOwner(teamName, teamOwner).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Team named: " + teamName  + " not exists"));
    }

    public Team shouldFindTeam(String teamName) {
        return teamRepository.findByTeamName(teamName).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Team named: " + teamName  + " not exists"));
    }

}
