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
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;
import pl.createcompetition.tournamentservice.competition.EventMapper;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class CompetitionControllerTests {

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    EventMapper eventMapper;

    private static String userToken;

    @LocalServerPort
    private int serverPort;

    private static final String mainUserName = "test";

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