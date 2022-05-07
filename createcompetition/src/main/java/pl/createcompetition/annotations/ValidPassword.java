package pl.createcompetition.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConstraintValidator.class)
public @interface ValidPassword {

    String message() default "Password is to weak";

    Class<?>[] groups() default{} ;

    Class<? extends Payload>[] payload() default {};

}
