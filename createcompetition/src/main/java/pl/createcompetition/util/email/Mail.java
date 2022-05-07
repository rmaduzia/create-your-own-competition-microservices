package pl.createcompetition.util.email;

import lombok.Getter;

@Getter
public class Mail {
    private String mailTo;
    private TemplateValues templateValues;

    public Mail(String mailTo, TemplateValues templateValues) {
        this.mailTo = mailTo;
        this.templateValues = templateValues;
    }
}
