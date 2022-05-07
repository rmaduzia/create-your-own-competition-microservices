package pl.createcompetition.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class MailTemplateCreatorTest {

    @Autowired
    MailTemplateCreator creator;

    @Test
    void should_BuildEmailTemplate() {

        TemplateValues values = TemplateValues.builder()
                .changeDataLink("test link")
                .changedData("test Data")
                .dataValue("test value")
                .name("test name").build();

        String emailHtml = creator.buildEmailTemplate(values);

        assertThat(emailHtml).isNotEmpty();
    }
}

