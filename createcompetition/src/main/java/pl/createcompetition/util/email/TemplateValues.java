package pl.createcompetition.util.email;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TemplateValues {
    private String changedData;
    private String dataValue;
    private String name;
    private String changeDataLink;

    public TemplateValues( String changedData,  String dataValue,  String name,  String changeDataLink) {
        this.changedData = changedData;
        this.dataValue = dataValue;
        this.name = name;
        this.changeDataLink = changeDataLink;
    }
}
