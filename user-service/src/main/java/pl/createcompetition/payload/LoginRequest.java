package pl.createcompetition.payload;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;


}
