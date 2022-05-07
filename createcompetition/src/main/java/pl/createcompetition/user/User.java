package pl.createcompetition.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.createcompetition.model.AuthProvider;
import pl.createcompetition.user.detail.UserDetail;


import javax.persistence.*;
import javax.validation.constraints.*;

@EqualsAndHashCode(of = {"id", "email"})
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    private String imageUrl;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @JsonBackReference
    @OneToOne(mappedBy="user",cascade = CascadeType.ALL)
    private UserDetail userDetail;

}


