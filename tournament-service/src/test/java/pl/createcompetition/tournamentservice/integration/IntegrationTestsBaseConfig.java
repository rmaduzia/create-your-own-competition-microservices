package pl.createcompetition.tournamentservice.integration;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public abstract class IntegrationTestsBaseConfig {

    protected static String userToken;
    protected static final String mainUserName = "test";

    @LocalServerPort
    int serverPort;

    static int MYSQL_HOST_PORT = 34343;
    static int MYSQL_CONTAINER_PORT = 3306;

    static PortBinding portBinding = new PortBinding(
        Binding.bindPort(MYSQL_HOST_PORT), new ExposedPort(MYSQL_CONTAINER_PORT));

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withUsername("root")
        .withPassword("root")
        .withDatabaseName("competition-tournament-service")
        .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(new HostConfig().withPortBindings(portBinding))
            .withExposedPorts(ExposedPort.tcp(MYSQL_CONTAINER_PORT)))
    .withReuse(true);

    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:22.0.4")
        .withRealmImportFile("appdevelopercompetition-realm-export.json")
        .withReuse(true);

    @BeforeAll
    static void startContainers() {
        mysql.start();
        keycloakContainer.start();
    }

    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> keycloakContainer.getAuthServerUrl() + "/realms/appdevelopercompetition/protocol/openid-connect/certs");

        registry.add("keycloak.domain", keycloakContainer::getAuthServerUrl);

        registry.add("keycloak.urls.auth", keycloakContainer::getAuthServerUrl);

        registry.add("keycloak.adminClientSecret",
            () -> "**********");

        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.username", mysql::getUsername);
    }

    @BeforeAll
    static void setUp() throws URISyntaxException {
        userToken = getUserToken();
    }

    protected static String getUserToken() throws URISyntaxException {

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
