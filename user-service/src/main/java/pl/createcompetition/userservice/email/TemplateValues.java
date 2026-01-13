package pl.createcompetition.userservice.email;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TemplateValues {
    private final String changedData;
    private final String dataValue;
    private final String name;
    private final String changeDataLink;

    public TemplateValues( String changedData,  String dataValue,  String name,  String changeDataLink) {
        this.changedData = changedData;
        this.dataValue = dataValue;
        this.name = name;
        this.changeDataLink = changeDataLink;
    }
}
