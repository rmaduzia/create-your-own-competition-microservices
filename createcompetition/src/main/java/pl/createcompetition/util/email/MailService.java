package pl.createcompetition.util.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    private JavaMailSender javaMailSender;
    private MailTemplateCreator mailCreatorService;

    public MailService(JavaMailSender javaMailSender, MailTemplateCreator mailCreatorService) {
        this.javaMailSender = javaMailSender;
        this.mailCreatorService = mailCreatorService;
    }


    public void send(final Mail mail) {
        log.info("Starting email preparation");
        try {
            MimeMessagePreparator mailMessage = createMimeMessage(mail);
            javaMailSender.send(mailMessage);
            log.info("Email has been sent");
        } catch (MailException e) {
            log.error("Failed to process email sending: ", e);
        }
    }

    private MimeMessagePreparator createMimeMessage(final Mail mail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(mail.getMailTo());
            messageHelper.setSubject(EmailMassageTemplate.topic(mail.getTemplateValues().getChangedData()));
            messageHelper.setText(mailCreatorService.buildEmailTemplate(mail.getTemplateValues()), true);
        };
    }
}
