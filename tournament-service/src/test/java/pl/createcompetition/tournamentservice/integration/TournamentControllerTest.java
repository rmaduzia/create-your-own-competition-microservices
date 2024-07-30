package pl.createcompetition.tournamentservice.integration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.model.TeamEntity;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.tournament.TournamentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class TournamentControllerTest {

    @Autowired
    TournamentRepository tournamentRepository;

    private static String userToken;

    private static final String mainUserName = "test";

    @LocalServerPort
    private int serverPort;

    static int MYSQL_HOST_PORT = 34343;
    static int MYSQL_CONTAINER_PORT = 3306;

    static PortBinding portBinding = new PortBinding(Binding.bindPort(MYSQL_HOST_PORT), new ExposedPort(MYSQL_CONTAINER_PORT));

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withUsername("root")
        .withPassword("root")
        .withDatabaseName("competition-tournament-service")
        .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(new HostConfig().withPortBindings(portBinding))
            .withExposedPorts(ExposedPort.tcp(MYSQL_CONTAINER_PORT)));

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:22.0.4")
        .withRealmImportFile("appdevelopercompetition-realm-export.json");

    @DynamicPropertySource
    static void setupKeyCloak(DynamicPropertyRegistry registry) {

        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> keycloakContainer.getAuthServerUrl() + "/realms/appdevelopercompetition/protocol/openid-connect/certs");

        registry.add("keycloak.domain",
            () -> keycloakContainer.getAuthServerUrl());

        registry.add("keycloak.urls.auth",
            () -> keycloakContainer.getAuthServerUrl());

        registry.add("keycloak.adminClientSecret",
            () -> "**********");
    }

    @BeforeAll
    static void setUp() throws URISyntaxException {
        userToken = getUserToken();
    }

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

    private static String getUserToken() throws URISyntaxException {

        URI authorizationUri = new URIBuilder(keycloakContainer.getAuthServerUrl() + "/realms/appdevelopercompetition/protocol/openid-connect/token").build();

        WebClient webClient = WebClient.builder().build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "password");
        formData.add("username", mainUserName);
        formData.add("password", "test");
        formData.add("client_id", "competition-app-client");
        formData.add("client_secret", "**********");
        formData.add("redirect-uri", "http://localhost:9093/callback");
        formData.add("scope", "openid profile");

        String result = webClient.post()
            .uri(authorizationUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String.class)
            .block();

        JacksonJsonParser jsonParser = new JacksonJsonParser();

        return jsonParser.parseMap(result)
            .get("access_token")
            .toString();
    }
}