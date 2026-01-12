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
import org.springframework.http.HttpStatus;
import pl.createcompetition.teamservice.all.CreateTeamRequest;
import pl.createcompetition.teamservice.all.Team;
import pl.createcompetition.teamservice.all.TeamRepository;

public class TeamControllerTestsIT extends IntegrationTestsBaseConfig{

    @Autowired
    TeamRepository teamRepository;

    static String teamName;

    private Team preparedTeam;

    private static final String mainUserName = "test";

    private static String userToken;

    @BeforeAll
    static void setUp() throws URISyntaxException {
        teamName = "myTeamName";
        userToken = getUserToken();
    }

    @BeforeEach
    void setUpTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        teamRepository.deleteAll();
//        teamRepository.flush();

        preparedTeam = Team.builder()
        .teamName(teamName)
        .maxAmountMembers(30)
        .teamOwner(mainUserName)
        .teamMembers(null)
        .city("Gdynia")
        .isOpenRecruitment(true)
        .build();
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

        Team returnedTeam = response.getBody().as(Team.class);
        Team teamFromRepository = teamRepository.findByTeamName("teamName").orElse(null);

        assertEquals(201, response.getStatusCode());
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
        teamRepository.saveAndFlush(preparedTeam);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("teamName", teamName)
            .queryParam("recruitName", firstRecruit)
            .delete("team/{teamName}/removeRecruit");

        Team teamFromRepository = teamRepository.findByTeamName(teamName).orElse(null);

        assertNotNull(teamFromRepository);
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

    @Test
    void shouldGetListOfTeamMembers() {

        Set<String> teamMembers = new HashSet<>(Arrays.asList("firstRecruit", "secondRecruit", "thirdRecruit"));

        preparedTeam.setTeamMembers(teamMembers);

        teamRepository.save(preparedTeam);

        Response response = given().header("Authorization", "Bearer " + userToken)
            .contentType("application/json")
            .pathParam("teamName", teamName)
            .get("team/team-members/{teamName}");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        List<String> returnedTeam = response.jsonPath().getList("$", String.class);

        assertEquals(teamMembers, Set.copyOf(returnedTeam));
    }
}