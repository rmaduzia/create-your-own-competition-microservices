package pl.createcompetition.tournamentservice.integration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.tag.EventTagsDto;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;

public class TournamentTagControllerTests extends IntegrationTestsBaseConfig{

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    EventMapper eventMapper;

    @BeforeAll
    static void setup() { System.setProperty("api.version", "1.44"); }



    @BeforeEach
    void setUpTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        tournamentRepository.deleteAll();
    }

    @Test
    void shouldAddTagToTournament() {

        saveTournament();

        List<String> listOfTags = new ArrayList<>(Arrays.asList("firstTag", "secondTag", "thirdTag"));

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(listOfTags)
            .when()
            .post("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());

        EventTagsDto responseBody = response.getBody().as(EventTagsDto.class);
        listOfTags.add("sampleTag");

        assertEquals("zawody", responseBody.getEventName());
        assertEquals(5, responseBody.getTags().size());

        assertTrue(responseBody.getTags().containsAll(listOfTags));
    }

    @Test
    void shouldThrowErrorThatTournamentNotFoundWhenAddTag() {

        List<String> listOfTags = new ArrayList<>(Arrays.asList("firstTag", "secondTag", "thirdTag"));

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(listOfTags)
            .when()
            .post("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        assertEquals("Tournament not exists, Name: zawody" ,
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatYouAreNotOwnerOfTournamentWhenAddTag() {

        saveTournamentWithDifferentOwner();

        List<String> listOfTags = new ArrayList<>(Arrays.asList("firstTag", "secondTag", "thirdTag"));

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(listOfTags)
            .when()
            .post("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

        System.out.println("response: " + response.getBody().asString());

        assertEquals("You are not owner of this Tournament" ,
            response.jsonPath().getString("message"));
    }


    @Test
    void shouldUpdateTagToTournament() {
        saveTournament();

        String newTag = "newTagAdded";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(newTag)
            .when()
            .put("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());

        EventTagsDto responseBody = response.getBody().as(EventTagsDto.class);

        assertEquals("zawody", responseBody.getEventName());
        assertEquals(1, responseBody.getTags().size());

        assertTrue(responseBody.getTags().contains(newTag));
    }

    @Test
    void shouldThrowErrorThatTournamentNotFoundWhenUpdateTag() {

        String newTag = "newTagAdded";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(newTag)
            .when()
            .put("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        assertEquals("Tournament not exists, Name: zawody" ,
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatYouAreNotOwnerOfTournamentWhenUpdateTag() {

        saveTournamentWithDifferentOwner();

        String newTag = "newTagAdded";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(newTag)
            .when()
            .put("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

        assertEquals("You are not owner of this Tournament" ,
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldDeleteTagToTurnament() {
        saveTournament();

        String tagToDelete = "otherTag";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(tagToDelete)
            .when()
            .delete("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());

        Tournament tournament = tournamentRepository.findByEventNameWithTags("zawody").orElseThrow();

        assertEquals(1, tournament.getTags().size());

        assertTrue(tournament.getTags().stream().anyMatch(v -> v.getTag().equals("sampleTag")));

    }

    @Test
    void shouldThrowErrorThatTournamentNotFoundWhenDeleteTagToTournament() {

        String tagToDelete = "otherTag";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(tagToDelete)
            .when()
            .delete("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        assertEquals("Tournament not exists, Name: zawody",
            response.getBody().jsonPath().getString(("message")));
    }

    @Test
    void shouldThrowErrorThatYouAreNotOwnerOfCompetitionWhenDeleteTagToCompetition() {

        saveTournamentWithDifferentOwner();

        String tagToDelete = "otherTag";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(tagToDelete)
            .when()
            .delete("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

        assertEquals("You are not owner of this Tournament",
            response.getBody().jsonPath().getString(("message")));
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
        String competitionName = "zawody";
        String city = "Gdynia";
        String street = "Some Street";
        int streetNumber = 14;
        int maxAmountOfTeams = 10;
        boolean openRecruitment = true;

        return Tournament.builder()
            .eventName(competitionName)
            .eventOwner(mainUserName)
            .eventStartDate(startDate)
            .eventEndDate(endDate)
            .city(city)
            .streetName(street)
            .streetNumber(streetNumber)
            .maxAmountOfTeams(maxAmountOfTeams)
            .isOpenRecruitment(openRecruitment)
            .tags(Set.of(new Tag("sampleTag"), new Tag ("otherTag")))
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