package pl.createcompetition.tournamentservice.all.tournament.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FindTeamService {

    private final RestClient restClient;

    private static final String teamServiceUrlEndpoint = "http://localhost:8080/team/get-information";

    public Team findTeam(String teamName, String teamOwner) {
       return getTeam(teamName, teamOwner);
    }

    public Team findTeam(String teamName) {
        return getTeam(teamName, null);
    }

    private Team getTeam (String teamName, String teamOwner) {

        String teamOwnerParam = teamOwner == null ?
            "" : "&teamOwner=" + teamOwner;

        return restClient.get()
            .uri(teamServiceUrlEndpoint + "?teamName=" + teamOwner + teamOwnerParam)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Team not exists, Name: " + teamName);
            })
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "We did not find team named: " + teamName + "please try again later and contact support if problem will still exists");
            })
            .body(Team.class);
    }
}