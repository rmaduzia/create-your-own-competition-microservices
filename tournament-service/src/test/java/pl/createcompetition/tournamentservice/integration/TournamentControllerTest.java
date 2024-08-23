package pl.createcompetition.tournamentservice.integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TournamentControllerTest extends IntegrationTestsBaseConfig {

    @Autowired
    TournamentRepository tournamentRepository;

    @BeforeEach
    void setupTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;
        tournamentRepository.deleteAll();
    }

    @Test
    void shouldCreateTournament() {

        EventCreateUpdateRequest tournamentCreateUpdateRequest = getTournamentCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(tournamentCreateUpdateRequest)
            .when()
            .post("tournament");

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());

        Tournament createdTournament = response.getBody().as(Tournament.class);

        assertEquals(tournamentCreateUpdateRequest.getEventName(), createdTournament.getEventName());
        assertEquals(mainUserName, createdTournament.getEventOwner());
        assertEquals(tournamentCreateUpdateRequest.getCity(), createdTournament.getCity());
        assertEquals(tournamentCreateUpdateRequest.getStreetName(), createdTournament.getStreetName());
        assertEquals(tournamentCreateUpdateRequest.getStreetNumber(), createdTournament.getStreetNumber());
        assertEquals(tournamentCreateUpdateRequest.getMaxAmountOfTeams(), createdTournament.getMaxAmountOfTeams());
        assertEquals(tournamentCreateUpdateRequest.getEventStartDate(), createdTournament.getEventStartDate());
        assertEquals(tournamentCreateUpdateRequest.getEventEndDate(), createdTournament.getEventEndDate());
        assertEquals(tournamentCreateUpdateRequest.isOpenRecruitment(), createdTournament.isOpenRecruitment());
        assertTrue(createdTournament.getTags().isEmpty());
        assertTrue(createdTournament.getTeams().isEmpty());
        assertTrue(createdTournament.getMatchInTournament().isEmpty());
    }

    @Test
    void shouldThrowErrorThatCompetitionAlreadyExistsWhenCreating() {

        saveTournament();

        EventCreateUpdateRequest eventCreateUpdateRequest = getTournamentCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .post("tournament");

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("Tournament already exists. Named: " + eventCreateUpdateRequest.getEventName(),
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionRequestBodyDoesNotMatchWithParam() {

        EventCreateUpdateRequest eventCreateUpdateRequest = getTournamentCreateUpdateRequest();
        String invalidCompetitionName = "different name";

        eventCreateUpdateRequest.setEventName(invalidCompetitionName);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("tournament/zawody");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Tournament Name doesn't match with Tournament object", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionDoesNotBelongToUserWhenUpdate() {

        saveTournamentWithDifferentOwner();

        EventCreateUpdateRequest eventCreateUpdateRequest = getTournamentCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("tournament/" + eventCreateUpdateRequest.getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Not found tournament where Name: "+ eventCreateUpdateRequest.getEventName() + " and Owner: " + mainUserName, response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionWasNotFoundWhenUpdate() {

        saveTournament();

        EventCreateUpdateRequest eventCreateUpdateRequest = getTournamentCreateUpdateRequest();
        String competitionNameWhichNotExists = "someWrongCompetitionName";

        eventCreateUpdateRequest.setEventName(competitionNameWhichNotExists);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("tournament/" + eventCreateUpdateRequest.getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Not found tournament where Name: "+ eventCreateUpdateRequest.getEventName() + " and Owner: " + mainUserName, response.jsonPath().getString("message"));
    }

    @Test
    void shouldDeleteCompetition() {

        Tournament tournament = getTournament();
        tournament.setEventOwner(mainUserName);
        tournamentRepository.save(tournament);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("tournamentName" , tournament.getEventName())
            .delete("tournament/{tournamentName}");

        assertEquals(204, response.getStatusCode());
        assertEquals(Optional.empty(), tournamentRepository.findByEventName(tournament.getEventName()));
    }

    @Test
    void shouldThrowErrorThatCompetitionDoesNotBelongToUserWhenDeleteCompetition() {

        Tournament tournament = getTournament();
        tournament.setEventOwner("someOtherOwner");

        tournamentRepository.save(tournament);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("tournamentName" , tournament.getEventName())
            .delete("tournament/{tournamentName}");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Not found tournament where Name: "+ tournament.getEventName() + " and Owner: " + mainUserName, response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionWasNotFoundWhenDeleteCompetition() {

        String competitionNameWhichNotExists = "someWrongCompetitionName";
        saveTournamentWithDifferentOwner();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("tournamentName" , competitionNameWhichNotExists)
            .delete("tournament/{tournamentName}");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Not found tournament where Name: "+ competitionNameWhichNotExists + " and Owner: " + mainUserName, response.jsonPath().getString("message"));
    }


    @Test
    void shouldRemoveTeamFromTournament() {

        Tournament tournament = getTournament();

        String firstTeam = "firstTeamName";
        String secondTeam = "secondTeamName";

        tournament.addTeamToTournament(firstTeam);
        tournament.addTeamToTournament(secondTeam);

        tournamentRepository.save(tournament);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("tournamentName", tournament.getEventName())
            .pathParam("teamName", firstTeam)
            .delete("tournament/{tournamentName}/teams/{teamName}");


        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
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

    private static EventCreateUpdateRequest getTournamentCreateUpdateRequest() {

        LocalDateTime startDate = Timestamp.valueOf("2090-05-01 12:30:00").toLocalDateTime();
        LocalDateTime endDate = Timestamp.valueOf("2091-05-02 12:30:00").toLocalDateTime();
        String tournamentName = "zawody";
        String city = "Gdynia";
        String street = "Some Street";
        int streetNumber = 14;
        int maxAmountOfTeams = 10;
        boolean isRecruitmentOpen = true;

        return EventCreateUpdateRequest.builder()
            .eventName(tournamentName)
            .eventStartDate(startDate)
            .eventEndDate(endDate)
            .city(city)
            .streetName(street)
            .streetNumber(streetNumber)
            .maxAmountOfTeams(maxAmountOfTeams)
            .isOpenRecruitment(isRecruitmentOpen)
            .build();
    }
}