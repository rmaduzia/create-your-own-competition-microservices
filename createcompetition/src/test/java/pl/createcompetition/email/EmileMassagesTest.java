package pl.createcompetition.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.createcompetition.util.email.EmailMassageTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class EmileMassagesTest {

    @Test
    void should_AddTopic() {

        String topicMessage = EmailMassageTemplate.topic("Test Data");

        assertThat(topicMessage).isEqualTo("Your Test Data has been changed");
    }

    @Test
    void should_AddWelcomeMessage() {

        String welcomeMessage = EmailMassageTemplate.welcome("Test Name");

        assertThat(welcomeMessage).isEqualTo("Welcome : Test Name.");
    }

    @Test
    void should_AddEmailBody() {

        String emileMessage = EmailMassageTemplate.message("Test Data", "Test Value");

        assertThat(emileMessage).isEqualTo("Your Test Data has been changed " +
                LocalDate.now().toString() + "to : Test Value.");
    }

    @Test
    void should_AddButtonName() {

        String buttonName = EmailMassageTemplate.buttonName("Test Data");

        assertThat(buttonName).isEqualTo("Change : Test Data.");
    }

    @Test
    void should_AddChangeLink() {

        String changeLink = EmailMassageTemplate.changeLink("Test link");

        assertThat(changeLink).isEqualTo("Test link");
    }

    @Test
    void should_AddGreetingMessage() {

        String goodbyeMessage = EmailMassageTemplate.goodbye();

        assertThat(goodbyeMessage).isEqualTo("Best regards,");
    }

    @Test
    void should_AddAppName() {

        String appName = EmailMassageTemplate.appName();

        assertThat(appName).isEqualTo("Junior Start app");
    }
}