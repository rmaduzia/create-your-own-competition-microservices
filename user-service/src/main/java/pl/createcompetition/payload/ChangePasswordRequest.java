package pl.createcompetition.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
public class ChangePasswordRequest implements ChangeRequest {
    private String newPassword;
    private Long UserId;
    private String Password;

    public ChangePasswordRequest(@Min(6) @Max(20) String newPassword,@NotNull Long UserId,@NotBlank String oldPassword) {
        this.newPassword = newPassword;
        this.UserId = UserId;
        this.Password = oldPassword;
    }
}