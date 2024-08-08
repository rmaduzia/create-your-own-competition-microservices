package pl.createcompetition.teamservice;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
import pl.createcompetition.teamservice.all.CreateTeamRequest;
import pl.createcompetition.teamservice.all.Team;
import pl.createcompetition.teamservice.all.TeamRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class EndToEndTesting {

//    @LocalServerPort
//    int applicationPort;

    @Autowired
    TeamRepository teamRepository;

//    @Autowired
//    DynamicPropertyRegistry registry;

    static String teamName;

    static Team preparedTeam;

    private static final String mainUserName = "test";

    private static String userToken;

    static int MYSQL_HOST_PORT = 34343;
    static int MYSQL_CONTAINER_PORT = 3306;
    static PortBinding portBinding = new PortBinding(Ports.Binding.bindPort(MYSQL_HOST_PORT), new ExposedPort(MYSQL_CONTAINER_PORT));

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withUsername("root")
            .withPassword("root")
            .withDatabaseName("team-service")
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

        teamName = "myTeamName";

        preparedTeam = Team.builder()
            .id(1L)
            .teamName(teamName)
            .maxAmountMembers(30)
            .teamOwner(mainUserName)
            .teamMembers(null)
            .city("Gdynia")
            .isOpenRecruitment(true)
            .build();

        userToken = getUserToken();
    }

    @BeforeEach
    void cleanDatabase() {

        teamRepository.deleteAll();
        teamRepository.flush();
    }


    @Test
    void shouldAddTeam() {

        CreateTeamRequest createTeamRequest = CreateTeamRequest.builder()
            .teamName("teamName")
            .city("Gdynia")
            .build();

        Response response = given().header("Authorization", "Bearer " + userToken)
          .contentType("application/json")
          .body(createTeamRequest)
          .when()
          .post("team");

        System.out.println("response team: " + response.getBody().asString());

        Team returnedTeam = response.getBody().as(Team.class);
        Team teamFromRepository = teamRepository.findByTeamName("teamName").orElse(null);


        assertEquals(response.getStatusCode(), 201);
        assertEquals(returnedTeam, teamFromRepository, "Returned team does not match with expected");

        assertEquals(createTeamRequest.getTeamName(), teamFromRepository.getTeamName());
        assertEquals(createTeamRequest.getCity(), teamFromRepository.getCity());
        assertEquals(mainUserName, teamFromRepository.getTeamOwner());
        assertTrue(teamFromRepository.isOpenRecruitment());
        assertTrue(teamFromRepository.getTeamMembers().isEmpty());
        assertEquals(30, teamFromRepository.getMaxAmountMembers());

    }

    @Test
    void shouldUpdateTeam() {

        teamRepository.save(preparedTeam);

        preparedTeam.setCity("newCity");
        preparedTeam.setMaxAmountMembers(80);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .body(preparedTeam)
            .pathParam("teamName", teamName)
            .put("team/{teamName}");

        Team returnedTeam = response.getBody().as(Team.class);
        Team teamFromRepository = teamRepository.findByTeamName(teamName).orElse(null);


        assertEquals(200, response.getStatusCode());
        assertEquals(returnedTeam, preparedTeam, "Returned team does not match with expected");
        assertEquals(teamFromRepository, preparedTeam, "Returned team does not match with expected");
    }

    @Test
    void shouldDeleteTeam() {

        teamRepository.save(preparedTeam);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("teamName", teamName)
            .delete("team/{teamName}");

        Team teamFromRepository = teamRepository.findByTeamName("teamName").orElse(null);

        assertEquals(204, response.getStatusCode());
        assertNull(teamFromRepository);
    }

    @Test
    void addRecruitToTeam() {

        teamRepository.save(preparedTeam);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("teamName", teamName)
            .queryParam("recruitName", "secondUser")
            .post("team/{teamName}/addRecruit");

        Team savedTeam = teamRepository.findByTeamName(teamName).orElse(null);

        assertEquals(201, response.getStatusCode());
        assertNotNull(savedTeam);
        assertEquals(1, savedTeam.getTeamMembers().size());
        assertTrue(savedTeam.getTeamMembers().contains("secondUser"));

    }

    @Test
    void removeMemberFromTeam() {

        String firstRecruit = "firstRecruit";
        String secondRecruit = "secondRecruit";

        Set<String> teamMembers = new HashSet<>(Arrays.asList(firstRecruit, secondRecruit));

        preparedTeam.setTeamMembers(teamMembers);

        teamRepository.save(preparedTeam);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("teamName", teamName)
            .queryParam("recruitName", firstRecruit)
            .delete("team/{teamName}/removeRecruit");

        Team teamFromRepository = teamRepository.findByTeamName(teamName).orElse(null);

        assert teamFromRepository != null;
        assertEquals(1 ,teamFromRepository.getTeamMembers().size());
        assertTrue(teamFromRepository.getTeamMembers().contains("secondRecruit"));
        assertFalse(teamFromRepository.getTeamMembers().contains("firstRecruit"));
        assertEquals(200, response.getStatusCode());

    }

    @Test
    void removeMemberFromTeamThrowExceptionThatUserNotBelongToTeam() {

        String firstRecruit = "firstRecruit";
        String recruitWhichNotBelongToTeam = "recruitWhichNotBelongToTeam";

        Set<String> teamMembers = new HashSet<>(List.of(firstRecruit));

        preparedTeam.setTeamMembers(teamMembers);

        teamRepository.save(preparedTeam);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("teamName", teamName)
            .queryParam("recruitName", recruitWhichNotBelongToTeam)
            .delete("team/{teamName}/removeRecruit");

        assertEquals(404, response.getStatusCode());
        assertEquals("UserName: " + recruitWhichNotBelongToTeam + " does not belong to team: myTeamName", response.jsonPath().getString("message"));

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
        formData.add("redirect_uri", "http://localhost:9093/callback");
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