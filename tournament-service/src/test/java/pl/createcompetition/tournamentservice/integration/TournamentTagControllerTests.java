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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pl.createcompetition.tournamentservice.competition.Competition;
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
    EventMapper eventMapper;

    @BeforeEach
    void setUpTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        tournamentRepository.deleteAll();
    }

    @Test
    void shouldAddTagToCompetition() {

        saveTournament();

        List<String> listOfTags = new ArrayList<>(Arrays.asList("firstTag", "secondTag", "thirdTag"));

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(listOfTags)
            .when()
            .post("competition/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());

        EventTagsDto responseBody = response.getBody().as(EventTagsDto.class);
        listOfTags.add("sampleTag");

        assertEquals("zawody", responseBody.getEventName());
        assertEquals(5, responseBody.getTags().size());

        assertTrue(responseBody.getTags().containsAll(listOfTags));
    }

    @Test
    void shouldThrowErrorThatCompetitionNotFoundWhenAddTag() {

        List<String> listOfTags = new ArrayList<>(Arrays.asList("firstTag", "secondTag", "thirdTag"));

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(listOfTags)
            .when()
            .post("competition/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        assertEquals("Competition not exists, Name: zawody" ,
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatYouAreNotOwnerOfCompetitionWhenAddTag() {

        saveCompetitionWithDifferentOwner();

        List<String> listOfTags = new ArrayList<>(Arrays.asList("firstTag", "secondTag", "thirdTag"));

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(listOfTags)
            .when()
            .post("competition/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

        System.out.println("response: " + response.getBody().asString());

        assertEquals("You are not owner of this Competition" ,
            response.jsonPath().getString("message"));
    }


    @Test
    void shouldUpdateTagToCompetition() {
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
    void shouldThrowErrorThatCompetitionNotFoundWhenUpdateTag() {

        String newTag = "newTagAdded";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(newTag)
            .when()
            .put("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        assertEquals("Competition not exists, Name: zawody" ,
            response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatYouAreNotOwnerOfCompetitionWhenUpdateTag() {

        saveCompetitionWithDifferentOwner();

        String newTag = "newTagAdded";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(newTag)
            .when()
            .put("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

        assertEquals("You are not owner of this Competition" ,
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

        Tournament competition = tournamentRepository.findByEventNameWithTags("zawody").orElseThrow();

        assertEquals(1, competition.getTags().size());

        assertTrue(competition.getTags().stream().anyMatch(v -> v.getTag().equals("sampleTag")));

    }

    @Test
    void shouldThrowErrorThatCompetitionNotFoundWhenDeleteTagToTournament() {

        String tagToDelete = "otherTag";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(tagToDelete)
            .when()
            .delete("tournament/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());

        assertEquals("Competition not exists, Name: zawody",
            response.getBody().jsonPath().getString(("message")));
    }

    @Test
    void shouldThrowErrorThatYouAreNotOwnerOfCompetitionWhenDeleteTagToCompetition() {

        saveCompetitionWithDifferentOwner();

        String tagToDelete = "otherTag";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(tagToDelete)
            .when()
            .delete("competition/tags/" + getTournament().getEventName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());

        assertEquals("You are not owner of this Competition",
            response.getBody().jsonPath().getString(("message")));
    }

    public void saveTournament() {
        Tournament competition = getTournament();
        tournamentRepository.save(competition);
    }

    public void saveCompetitionWithDifferentOwner() {
        Tournament competition = getTournament();
        competition.setEventOwner("someOtherOwner");

        tournamentRepository.save(competition);
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