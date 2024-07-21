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
import pl.createcompetition.tournamentservice.competition.CompetitionCreateUpdateRequest;
import pl.createcompetition.tournamentservice.competition.CompetitionMapper;
import pl.createcompetition.tournamentservice.competition.CompetitionRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class CompetitionControllerTests {

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    CompetitionMapper competitionMapper;

    private static String userToken;

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
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 9095;

        userToken = getUserToken();
    }

    @BeforeEach
    void cleanDatabase() {
        competitionRepository.deleteAll();
    }

    @Test
    void shouldAddCompetition() {

        CompetitionCreateUpdateRequest competitionCreateUpdateRequest = getcompetitionCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(competitionCreateUpdateRequest)
            .when()
            .post("competition");

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());

        Competition createdCompetition = response.getBody().as(Competition.class);

        assertEquals(competitionCreateUpdateRequest.getCompetitionName(), createdCompetition.getCompetitionName());
        assertEquals(mainUserName, createdCompetition.getCompetitionOwner());
        assertEquals(competitionCreateUpdateRequest.getCity(), createdCompetition.getCity());
        assertEquals(competitionCreateUpdateRequest.getStreet(), createdCompetition.getStreet());
        assertEquals(competitionCreateUpdateRequest.getStreetNumber(), createdCompetition.getStreetNumber());
        assertEquals(competitionCreateUpdateRequest.getMaxAmountOfTeams(), createdCompetition.getMaxAmountOfTeams());
        assertEquals(competitionCreateUpdateRequest.getCompetitionStart(), createdCompetition.getCompetitionStart());
        assertEquals(competitionCreateUpdateRequest.getCompetitionEnd(), createdCompetition.getCompetitionEnd());
        assertEquals(competitionCreateUpdateRequest.getIsOpenRecruitment(), createdCompetition.getIsOpenRecruitment());
        assertTrue(createdCompetition.getTags().isEmpty());
        assertTrue(createdCompetition.getTeams().isEmpty());
        assertTrue(createdCompetition.getMatchInCompetition().isEmpty());
    }

    @Test
    void shouldThrowErrorThatCompetitionAlreadyExistsWhenCreating() {

        saveCompetition();

        CompetitionCreateUpdateRequest competitionCreateUpdateRequest = getcompetitionCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(competitionCreateUpdateRequest)
            .when()
            .post("competition");

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("Competition already exists. Named: " + competitionCreateUpdateRequest.getCompetitionName(),
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

        CompetitionCreateUpdateRequest updateRequest = competitionMapper.mapCompetitionToSimpleCompetitionDto(competition);

        updateRequest.setCity(updatedCityName);
        updateRequest.setStreet(updatedStreet);
        updateRequest.setStreetNumber(updatedStreetNumber);
        updateRequest.setMaxAmountOfTeams(maxAmountOfTeams);
        updateRequest.setIsOpenRecruitment(closeRecruitment);

        competitionRepository.save(competition);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(updateRequest)
            .when()
            .put("competition/" + updateRequest.getCompetitionName());

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        CompetitionCreateUpdateRequest updatedCompetition = response.getBody().as(CompetitionCreateUpdateRequest.class);

        assertEquals(updateRequest.getCompetitionName(), updatedCompetition.getCompetitionName());
        assertEquals(updateRequest.getCompetitionStart(), updatedCompetition.getCompetitionStart());
        assertEquals(updateRequest.getCompetitionEnd(), updatedCompetition.getCompetitionEnd());
        assertEquals(updateRequest.getCity(), updatedCompetition.getCity());
        assertEquals(updateRequest.getStreet(), updatedCompetition.getStreet());
        assertEquals(updateRequest.getStreetNumber(), updatedCompetition.getStreetNumber());
        assertEquals(updateRequest.getMaxAmountOfTeams(), updatedCompetition.getMaxAmountOfTeams());
        assertEquals(updateRequest.getIsOpenRecruitment(), updatedCompetition.getIsOpenRecruitment());
    }

    @Test
    void shouldThrowErrorThatCompetitionFromParamDoesNotMatchWithRequestBody() {

        CompetitionCreateUpdateRequest competitionCreateUpdateRequest = getcompetitionCreateUpdateRequest();
        String invalidCompetitionName = "different name";

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(competitionCreateUpdateRequest)
            .when()
            .put("competition/" + invalidCompetitionName);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Competition Name doesn't match with Competition object", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionRequestBodyDoesNotMatchWithParam() {

        CompetitionCreateUpdateRequest competitionCreateUpdateRequest = getcompetitionCreateUpdateRequest();
        String invalidCompetitionName = "different name";

        competitionCreateUpdateRequest.setCompetitionName(invalidCompetitionName);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(competitionCreateUpdateRequest)
            .when()
            .put("competition/zawody");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Competition Name doesn't match with Competition object", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionDoesNotBelongToUserWhenUpdate() {

        saveCompetitionWithDifferentOwner();

        CompetitionCreateUpdateRequest competitionCreateUpdateRequest = getcompetitionCreateUpdateRequest();

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(competitionCreateUpdateRequest)
            .when()
            .put("competition/" + competitionCreateUpdateRequest.getCompetitionName());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("You are not owner of this Competition", response.jsonPath().getString("message"));
    }

    @Test
    void shouldThrowErrorThatCompetitionWasNotFoundWhenUpdate() {

        saveCompetition();

        CompetitionCreateUpdateRequest competitionCreateUpdateRequest = getcompetitionCreateUpdateRequest();
        String competitionNameWhichNotExists = "someWrongCompetitionName";

        competitionCreateUpdateRequest.setCompetitionName(competitionNameWhichNotExists);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(competitionCreateUpdateRequest)
            .when()
            .put("competition/" + competitionCreateUpdateRequest.getCompetitionName());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Competition not exists, Name: " + competitionNameWhichNotExists, response.jsonPath().getString("message"));
    }

    @Test
    void shouldDeleteCompetition() {

        Competition competition = getCompetition();
        competition.setCompetitionOwner(mainUserName);
        competitionRepository.save(competition);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("competitionName" , competition.getCompetitionName())
            .delete("competition/{competitionName}");

        assertEquals(204, response.getStatusCode());
        assertEquals(Optional.empty(), competitionRepository.findByCompetitionName(competition.getCompetitionName()));
    }

    @Test
    void shouldThrowErrorThatCompetitionDoesNotBelongToUserWhenDeleteCompetition() {

        Competition competition = getCompetition();
        competition.setCompetitionOwner("someOtherOwner");

        competitionRepository.save(competition);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("competitionName" , competition.getCompetitionName())
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
        competition.setCompetitionOwner("someOtherOwner");

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
            .competitionName(competitionName)
            .competitionOwner(mainUserName)
            .competitionStart(startDate)
            .competitionEnd(endDate)
            .city(city)
            .street(street)
            .streetNumber(streetNumber)
            .maxAmountOfTeams(maxAmountOfTeams)
            .isOpenRecruitment(openRecruitment)
            .build();
    }

    private static CompetitionCreateUpdateRequest getcompetitionCreateUpdateRequest() {

        LocalDateTime startDate = Timestamp.valueOf("2090-05-01 12:30:00").toLocalDateTime();
        LocalDateTime endDate = Timestamp.valueOf("2091-05-02 12:30:00").toLocalDateTime();
        String competitionName = "zawody";
        String city = "Gdynia";
        String street = "Some Street";
        int streetNumber = 14;
        int maxAmountOfTeams = 10;
        boolean isRecruitmentOpen = true;

        return CompetitionCreateUpdateRequest.builder()
            .competitionName(competitionName)
            .competitionStart(startDate)
            .competitionEnd(endDate)
            .city(city)
            .street(street)
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