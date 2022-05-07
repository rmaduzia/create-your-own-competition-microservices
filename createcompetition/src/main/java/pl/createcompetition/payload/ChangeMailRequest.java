package pl.createcompetition.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.createcompetition.payload.interfaces.ChangeRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ChangeMailRequest implements ChangeRequest {
    @Email
    private String email;
    @NotNull
    private Long userId;
    @NotBlank
    private String password;

    public ChangeMailRequest(@Max(40) String email, Long userId, String password) {
        this.email = email;
        this.userId = userId;
        this.password = password;
    }
}