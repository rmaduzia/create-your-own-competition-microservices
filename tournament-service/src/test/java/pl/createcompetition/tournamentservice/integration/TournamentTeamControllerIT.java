package pl.createcompetition.tournamentservice.integration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.restassured.response.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.createcompetition.tournamentservice.kafka.domain.MessageSendFacade;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;
import pl.createcompetition.tournamentservice.tournament.VerifyMethodsForServices;
import pl.createcompetition.tournamentservice.tournament.participation.TeamDto;

public class TournamentTeamControllerIT extends IntegrationTestsBaseConfig {

    @Autowired
    TournamentRepository tournamentRepository;

    @MockBean
    MessageSendFacade messageSendFacade;

    @MockBean
    VerifyMethodsForServices verifyMethodsForServices;

    @BeforeEach
    void setupTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        tournamentRepository.deleteAll();
    }

    @Test
    void teamShouldJoinToTournament() {

        saveTournamentWithTeams();

        String teamName = "someNewTeamName";
        String tournamentName = "zawody";

        TeamDto teamDto = TeamDto.builder()
            .teamName(teamName)
            .teamOwner(mainUserName)
            .build();

        when(verifyMethodsForServices.shouldFindTeam(teamName, mainUserName)).thenReturn(teamDto);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(teamName)
            .pathParam("tournamentName", tournamentName)
            .when()
            .post("tournament/{tournamentName}/team/joinTournament");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Team: " + teamName + " joined tournament: " + tournamentName, response.getBody().asString());

        Tournament tournament = tournamentRepository.findByEventNameWithTeams(tournamentName).orElseThrow();

        assertEquals(4, tournament.getTeams().size());
        assertTrue(tournament.getTeams().stream().anyMatch(v -> v.getTeamName().equals(teamName)));

        verify(messageSendFacade, times(1)).sendEvent(any());
    }

    @Test
    void shouldThrowErrorThatTournamentNotExistsWhenTeamTryJoinToTournament() {

        saveTournamentWithTeams();

        String teamName = "someNewTeamName";
        String tournamentName = "zawody";

        TeamDto teamDto = TeamDto.builder()
            .teamName(teamName)
            .teamOwner(mainUserName)
            .build();

        when(verifyMethodsForServices.shouldFindTeam(teamName, mainUserName)).thenReturn(teamDto);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(teamName)
            .pathParam("tournamentName", tournamentName)
            .when()
            .post("tournament/{tournamentName}/team/joinTournament");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Tournament not exists. Name: " + tournamentName, response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatTeamNotExistsWhenTeamTryJoinToTournament() {

        saveTournamentWithTeams();

        String teamName = "someNewTeamName";
        String tournamentName = "zawody";

        when(verifyMethodsForServices.shouldFindTeam(teamName, mainUserName)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found, Team Name: " + teamName + " Team owner: " + mainUserName));


        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(teamName)
            .pathParam("tournamentName", tournamentName)
            .when()
            .post("tournament/{tournamentName}/team/joinTournament");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Team not found, Team Name: " + teamName + " Team owner: " + mainUserName, response.jsonPath().getString("message"));
    }



    public void saveTournamentWithTeams() {
        Tournament tournament = getTournament();

        tournament.addTeamToTournament("firstTeam");
        tournament.addTeamToTournament("secondTeam");
        tournament.addTeamToTournament("thirdTeam");

        tournamentRepository.save(tournament);
    }


    public void saveTournament() {
        Tournament tournament = getTournament();
        tournamentRepository.save(tournament);
    }

    public void saveTournamentWithDifferentOwner() {
        Tournament tournament = getTournament();
        tournament.setEventOwner("someOtherOwner");

        tournamentRepository.save(tournament);
    }

    private static Tournament getTournament() {
        LocalDateTime startDate = Timestamp.valueOf("2090-05-01 12:30:00").toLocalDateTime();
        LocalDateTime endDate = Timestamp.valueOf("2091-05-02 12:30:00").toLocalDateTime();
        String tournamentName = "zawody";
        String city = "Gdynia";
        String street = "Some Street";
        int streetNumber = 14;
        int maxAmountOfTeams = 10;
        boolean isStarted = true;

        return Tournament.builder()
            .eventName(tournamentName)
            .eventOwner(mainUserName)
            .eventStartDate(startDate)
            .eventEndDate(endDate)
            .city(city)
            .streetName(street)
            .streetNumber(streetNumber)
            .maxAmountOfTeams(maxAmountOfTeams)
            .isEventStarted(isStarted)
            .build();
    }


}
