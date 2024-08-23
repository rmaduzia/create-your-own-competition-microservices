package pl.createcompetition.tournamentservice.integration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;

public class CompetitionControllerTests extends IntegrationTestsBaseConfig{

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    EventMapper eventMapper;

    @BeforeEach
    void setUpTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        competitionRepository.deleteAll();
    }

    @Test
    void shouldAddCompetition() {

        EventCreateUpdateRequest eventCreateUpdateRequest = getCompetitionCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .post("competition");

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());

        Competition createdCompetition = response.getBody().as(Competition.class);

        assertEquals(eventCreateUpdateRequest.getEventName(), createdCompetition.getEventName());
        assertEquals(mainUserName, createdCompetition.getEventOwner());
        assertEquals(eventCreateUpdateRequest.getCity(), createdCompetition.getCity());
        assertEquals(eventCreateUpdateRequest.getStreetName(), createdCompetition.getStreetName());
        assertEquals(eventCreateUpdateRequest.getStreetNumber(), createdCompetition.getStreetNumber());
        assertEquals(eventCreateUpdateRequest.getMaxAmountOfTeams(), createdCompetition.getMaxAmountOfTeams());
        assertEquals(eventCreateUpdateRequest.getEventStartDate(), createdCompetition.getEventStartDate());
        assertEquals(eventCreateUpdateRequest.getEventEndDate(), createdCompetition.getEventEndDate());
        assertEquals(eventCreateUpdateRequest.isOpenRecruitment(), createdCompetition.isOpenRecruitment());
        assertTrue(createdCompetition.getTags().isEmpty());
        assertTrue(createdCompetition.getTeams().isEmpty());
        assertTrue(createdCompetition.getMatchInCompetition().isEmpty());
    }

    @Test
    void shouldThrowErrorThatCompetitionAlreadyExistsWhenCreating() {

        saveCompetition();

        EventCreateUpdateRequest eventCreateUpdateRequest = getCompetitionCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .post("competition");

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("Competition already exists. Named: " + eventCreateUpdateRequest.getEventName(),
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldUpdateCompetition() {

        int maxAmountOfTeams = 10;
        int updatedStreetNumber = 5;
        boolean closeRecruitment = false;
        String updatedCityName = "updated city";
        String updatedStreet = "updated street";

        Competition competition = getCompetition();

        EventCreateUpdateRequest updateRequest = eventMapper.mapCompetitionToSimpleCompetitionDto(competition);

        updateRequest.setCity(updatedCityName);
        updateRequest.setStreetName(updatedStreet);
        updateRequest.setStreetNumber(updatedStreetNumber);
        updateRequest.setMaxAmountOfTeams(maxAmountOfTeams);
        updateRequest.setOpenRecruitment(closeRecruitment);

        competitionRepository.save(competition);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(updateRequest)
            .when()
            .put("competition/" + updateRequest.getEventName());

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        EventCreateUpdateRequest updatedCompetition = response.getBody().as(
            EventCreateUpdateRequest.class);

        assertEquals(updateRequest.getEventName(), updatedCompetition.getEventName());
        assertEquals(updateRequest.getEventStartDate(), updatedCompetition.getEventStartDate());
        assertEquals(updateRequest.getEventEndDate(), updatedCompetition.getEventEndDate());
        assertEquals(updateRequest.getCity(), updatedCompetition.getCity());
        assertEquals(updateRequest.getStreetName(), updatedCompetition.getStreetName());
        assertEquals(updateRequest.getStreetNumber(), updatedCompetition.getStreetNumber());
        assertEquals(updateRequest.getMaxAmountOfTeams(), updatedCompetition.getMaxAmountOfTeams());
        assertEquals(updateRequest.isOpenRecruitment(), updatedCompetition.isOpenRecruitment());
    }

    @Test
    void shouldThrowErrorThatCompetitionFromParamDoesNotMatchWithRequestBody() {

        EventCreateUpdateRequest eventCreateUpdateRequest = getCompetitionCreateUpdateRequest();
        String invalidCompetitionName = "different name";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("competition/" + invalidCompetitionName);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Competition Name doesn't match with Competition object", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionRequestBodyDoesNotMatchWithParam() {

        EventCreateUpdateRequest eventCreateUpdateRequest = getCompetitionCreateUpdateRequest();
        String invalidCompetitionName = "different name";

        eventCreateUpdateRequest.setEventName(invalidCompetitionName);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("competition/zawody");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Competition Name doesn't match with Competition object", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionDoesNotBelongToUserWhenUpdate() {

        saveCompetitionWithDifferentOwner();

        EventCreateUpdateRequest eventCreateUpdateRequest = getCompetitionCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("competition/" + eventCreateUpdateRequest.getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("You are not owner of this Competition", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionWasNotFoundWhenUpdate() {

        saveCompetition();

        EventCreateUpdateRequest eventCreateUpdateRequest = getCompetitionCreateUpdateRequest();
        String competitionNameWhichNotExists = "someWrongCompetitionName";

        eventCreateUpdateRequest.setEventName(competitionNameWhichNotExists);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(eventCreateUpdateRequest)
            .when()
            .put("competition/" + eventCreateUpdateRequest.getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Competition not exists, Name: " + competitionNameWhichNotExists, response.jsonPath().getString("message"));
    }

    @Test
    void shouldDeleteCompetition() {

        Competition competition = getCompetition();
        competition.setEventOwner(mainUserName);
        competitionRepository.save(competition);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("competitionName" , competition.getEventName())
            .delete("competition/{competitionName}");

        assertEquals(204, response.getStatusCode());
        assertEquals(Optional.empty(), competitionRepository.findByEventName(competition.getEventName()));
    }

    @Test
    void shouldThrowErrorThatCompetitionDoesNotBelongToUserWhenDeleteCompetition() {

        Competition competition = getCompetition();
        competition.setEventOwner("someOtherOwner");

        competitionRepository.save(competition);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("competitionName" , competition.getEventName())
            .delete("competition/{competitionName}");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("You are not owner of this Competition", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionWasNotFoundWhenDeleteCompetition() {

        String competitionNameWhichNotExists = "someWrongCompetitionName";
        saveCompetitionWithDifferentOwner();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("competitionName" , competitionNameWhichNotExists)
            .delete("competition/{competitionName}");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Competition not exists, Name: " + competitionNameWhichNotExists, response.jsonPath().getString("message"));
    }

    public void saveCompetition() {
        Competition competition = getCompetition();
        competitionRepository.save(competition);
    }

    public void saveCompetitionWithDifferentOwner() {
        Competition competition = getCompetition();
        competition.setEventOwner("someOtherOwner");

        competitionRepository.save(competition);
    }

    private static Competition getCompetition() {
        LocalDateTime startDate = Timestamp.valueOf("2090-05-01 12:30:00").toLocalDateTime();
        LocalDateTime endDate = Timestamp.valueOf("2091-05-02 12:30:00").toLocalDateTime();
        String competitionName = "zawody";
        String city = "Gdynia";
        String street = "Some Street";
        int streetNumber = 14;
        int maxAmountOfTeams = 10;
        boolean openRecruitment = true;

        return Competition.builder()
            .eventName(competitionName)
            .eventOwner(mainUserName)
            .eventStartDate(startDate)
            .eventEndDate(endDate)
            .city(city)
            .streetName(street)
            .streetNumber(streetNumber)
            .maxAmountOfTeams(maxAmountOfTeams)
            .isOpenRecruitment(openRecruitment)
            .build();
    }

    private static EventCreateUpdateRequest getCompetitionCreateUpdateRequest() {

        LocalDateTime startDate = Timestamp.valueOf("2090-05-01 12:30:00").toLocalDateTime();
        LocalDateTime endDate = Timestamp.valueOf("2091-05-02 12:30:00").toLocalDateTime();
        String competitionName = "zawody";
        String city = "Gdynia";
        String street = "Some Street";
        int streetNumber = 14;
        int maxAmountOfTeams = 10;
        boolean isRecruitmentOpen = true;

        return EventCreateUpdateRequest.builder()
            .eventName(competitionName)
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