package pl.createcompetition.util.email;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailTemplateCreator {

    private final TemplateEngine templateEngine;

    public MailTemplateCreator(@Qualifier("templateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildEmailTemplate(TemplateValues values){
        Context context = new ContextBuilder()
                .addVariable("topic", EmailMassageTemplate.topic(values.getChangedData()))
                .addVariable("welcome", EmailMassageTemplate.welcome(values.getName()))
                .addVariable("message", EmailMassageTemplate.message(values.getChangedData(),values.getDataValue()))
                .addVariable("button", EmailMassageTemplate.buttonName(values.getChangedData()))
                .addVariable("change_url", EmailMassageTemplate.changeLink(values.getChangeDataLink()))
                .addVariable("goodbye", EmailMassageTemplate.goodbye())
                .addVariable("app_name", EmailMassageTemplate.appName()).build();

        return templateEngine.process("email/Email-alert-template.html",context);
    }
}
