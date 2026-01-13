package pl.createcompetition.userservice.integration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import io.restassured.response.Response;
import java.net.URISyntaxException;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pl.createcompetition.userservice.microserviceschanges.ExchangePasswordForTokenRequestRecord;
import pl.createcompetition.userservice.microserviceschanges.ValidJwtToken;
import pl.createcompetition.userservice.user.UserCreateRecord;

public class UserControllerIntegrationTests extends IntegrationTestsBaseConfig{

    private static String userToken;

    @BeforeAll
    static void setup() throws URISyntaxException {
        userToken = getUserToken();
    }

    @BeforeEach
    void setUpTests() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;
    }

    @Test
    void shouldGetCurrentUser() {

        Response response = given()
            .header("Authorization", "Bearer " + userToken)
            .get("/user/me");

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().asString());

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);

        JsonObject principalObject = jsonObject.getAsJsonObject("tokenAttributes");

        String userId = jsonObject.get("userId").toString();
        String email = jsonObject.get("email").getAsString();
        String preferredUsername = principalObject.get("preferred_username").getAsString();

        assertEquals(200, response.getStatusCode());
        //TODO FIX THIS TEST CASE - MIGHT HAVE TO ADD STATIC ID TO USER IN EXPORTED REALM
//        assertEquals("5bba374c-868f-466c-b73c-19cb10d80e61", userId, "User id does not match");
        assertEquals("test@test.com", email, "User email does not match");
        assertEquals("test", preferredUsername, "Preferred username does not match");

    }

    @Test
    void whenGetCurrentUserUnauthorizedExceptionShouldBeThrown() {
        given()
            .get("/user/me")
            .then().statusCode(401);
    }

    @Test
    void shouldCreateUser() {

        String userName = "myTestUserName";
        String password = "myTestPassword";
        String email = "myTestEmail@test.pl";

        UserCreateRecord userCreateRecord = new UserCreateRecord(userName, email, password);

        Response response = given()
            .contentType("application/json")
            .body(userCreateRecord)
            .post("keycloak/user/create");

        assertEquals(200, response.getStatusCode(), "Invalid response status code");

        Gson gson = new Gson();

        Map<String, String> jsonMap = gson.fromJson(response.getBody().asString(), new TypeToken<Map<String, String>>(){}.getType());

        assertEquals(jsonMap.size(), 2 ,"Response body should contain only two fields");

        assertEquals(userName, jsonMap.get("userName"), "Unexpected username in response");
        assertEquals(email, jsonMap.get("email"), "Unexpected email in response");

    }

    @Test
    void whenCreatingUserShouldThrowExceptionThatUserNameAlreadyExists() {


        String duplicatedUserName = "test";
        String password = "myTestPassword";
        String email = "testNew@test.com";

        UserCreateRecord userCreateRecord = new UserCreateRecord(duplicatedUserName, email, password);

        Response response = given()
            .contentType("application/json")
            .body(userCreateRecord)
            .post("/keycloak/user/create");

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().asString());



        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.getBody().asString(), JsonObject.class);
        String messageResponseField = jsonResponse.get("message").getAsString();

        assertEquals(409, response.getStatusCode(), "Invalid response status code");
        assertEquals("{\"errorMessage\":\"User exists with same username\"}", messageResponseField);

    }

    @Test
    void whenCreatingUserShouldThrowExceptionThatEmailAlreadyExists() {

        String userName = "newTest";
        String password = "myTestPassword";
        String duplicatedEmail = "test@test.com";

        UserCreateRecord userCreateRecord = new UserCreateRecord(userName, duplicatedEmail, password);

        Response response = given()
            .contentType("application/json")
            .body(userCreateRecord)
            .post("/keycloak/user/create");

        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.getBody().asString(), JsonObject.class);
        String messageResponseField = jsonResponse.get("message").getAsString();

        assertEquals(409, response.getStatusCode(), "Invalid response status code");
        assertEquals("{\"errorMessage\":\"User exists with same email\"}", messageResponseField);

    }

    @Test
    void shouldExchangePasswordForToken() throws IllegalAccessException {

        String username = "test";
        String password = "test";

        ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord = new ExchangePasswordForTokenRequestRecord(username, password);

        Response response = given()
            .contentType("application/json")
            .body(exchangePasswordForTokenRequestRecord)
            .post("keycloak/user/login");

        assertEquals(200, response.getStatusCode(), "Invalid response status code");

        ValidJwtToken validJwtToken = response.getBody().as(ValidJwtToken.class);

        Class<?> tokenClass = ValidJwtToken.class;
        Field[] fields = tokenClass.getDeclaredFields();

        for(Field field: fields) {
            field.setAccessible(true);
            assertNotNull(field.get(validJwtToken), "Field: " + field + "is null");
        }
    }

    @Test
    void shouldThrowExceptionInvalidCredentialsWhenExchangePasswordForToken() {

        String userName = "test";
        String invalidPassword = "invalidPassword";

        ExchangePasswordForTokenRequestRecord exchangePasswordForTokenRequestRecord = new ExchangePasswordForTokenRequestRecord(userName, invalidPassword);

        Response response = given()
            .contentType("application/json")
            .body(exchangePasswordForTokenRequestRecord)
            .post("/keycloak/user/login");

        System.out.println("response: " + response.getStatusCode());
        System.out.println("response: " + response.getBody().asString());

//        ResponseStatusException responseStatusException = response.getBody().as(ResponseStatusException.class);

        System.out.println("__________________________________");

        assertEquals(401, response.getStatusCode());
        assertEquals("Unauthorized", response.jsonPath().getString("error"));
//        assertEquals();


    }


    @Test
    void shouldChangeEmail() {


        Response response = given()
            .header("Authorization", "Bearer " + userToken)
            .post("keycloak/user/changeMail");

        System.out.println("response: " + response.getStatusCode());
    }


    //TODO IMPLEMENTS TESTS
    @Test
    void shouldUpdatePassword() {

        Response response = given()
            .header("Authorization", "Bearer " + userToken)
            .post("keycloak/user/changePassword");

    }

    //TODO IMPLEMENTS TESTS
    @Test
    void shouldChangeUserName() {

        Response response = given()
            .header("Authorization", "Bearer " + userToken)
            .post("keycloak/user/change-username");
    }

    //TODO IMPLEMENTS TESTS
    @Test
    void shouldSendVerificationEmail() {

        String userId = "";

        Response response = given()
            .header("Authorization", "Bearer " + userToken)
            .post("/keycloak/" +  userId + "/send-verify-email");

    }

}
