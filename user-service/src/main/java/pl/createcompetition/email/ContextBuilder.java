package pl.createcompetition.email;

import lombok.NoArgsConstructor;
import org.thymeleaf.context.Context;

@NoArgsConstructor
public class ContextBuilder {

    private final Context context = new Context();

    public ContextBuilder addVariable(String name, Object value) {
        context.setVariable(name,value);
        return this;
    }

    public Context build() {
        return context;
    }
}
