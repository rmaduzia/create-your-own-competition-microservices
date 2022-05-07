package pl.createcompetition.payload.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private HttpStatus status;
    private String message;
    private List<String> errors;


    private ErrorResponse(Builder builder) {
        this.status = builder.status;
        this.message = builder.message;
        this.errors = builder.errorsList;
    }

    public static class Builder implements Responses<ErrorResponse> {
        private HttpStatus status;
        private String message;
        private List<String> errorsList;
        private String error;

        public Builder(HttpStatus status, String message) {
            this.status = status;
            this.message = message;

        }

        public Builder errorsList(List<String> errors) {
            errorsList = errors;
            return this;
        }

        @Override
        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}