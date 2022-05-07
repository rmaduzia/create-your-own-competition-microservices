package pl.createcompetition.email;

import lombok.Getter;

@Getter
public class Mail {

    private final String mailTo;
    private final TemplateValues templateValues;

    public Mail(String mailTo, TemplateValues templateValues) {
        this.mailTo = mailTo;
        this.templateValues = templateValues;
    }
}
