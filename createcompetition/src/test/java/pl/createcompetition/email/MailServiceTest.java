package pl.createcompetition.email;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import pl.createcompetition.util.email.Mail;
import pl.createcompetition.util.email.MailService;
import pl.createcompetition.util.email.TemplateValues;

import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void should_SendMail() {

        TemplateValues values = TemplateValues.builder()
                .changeDataLink("test link")
                .changedData("test Data")
                .dataValue("test value")
                .name("test name").build();

        Mail mail = new Mail("test@tes.com",values);

        mailService.send(mail);

        verify(javaMailSender, times(1)).send(any(MimeMessagePreparator.class));
    }
}