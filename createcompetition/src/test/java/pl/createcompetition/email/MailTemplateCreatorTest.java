package pl.createcompetition.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.mock.mockito.SpyBean;
import pl.createcompetition.util.email.MailTemplateCreator;
import pl.createcompetition.util.email.TemplateValues;

import static org.assertj.core.api.Assertions.assertThat;


@Disabled
@SpringBootTest
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

