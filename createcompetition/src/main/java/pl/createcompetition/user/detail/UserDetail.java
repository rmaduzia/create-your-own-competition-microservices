package pl.createcompetition.user.detail;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.model.Gender;
import pl.createcompetition.util.query.QueryDtoInterface;
import pl.createcompetition.team.Team;
import pl.createcompetition.user.User;

import javax.persistence.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.util.*;

@EqualsAndHashCode(of = {"id", "user"})
@Getter
@Setter
@Entity
@DynamicUpdate
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail implements QueryDtoInterface<UserDetail.UserDetailDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_detail_id")
    private Long id;

    @NotBlank(message="Bad username")
    @Column(unique = true)
    private String userName;

    private String city;

    @Min(value = 15, message="you must be at least 15 years old")
    @Max(value=110, message="older then 110 years are you even alive? ")
    @NotBlank(message="Age can't be empty")
    private int age;

    @Enumerated(EnumType.STRING)
    @NotBlank(message="Gender can't be empty")
    private Gender gender;

    @OneToOne
    @MapsId
    @JsonManagedReference
    private User user;

    @JsonBackReference
    @ManyToMany
    @JoinTable(name="user_competitions",
        joinColumns=@JoinColumn(name="user_id", foreignKey = @ForeignKey(name = "FK_USER_COMPETITIONS_USER_ID")),
        inverseJoinColumns=@JoinColumn(name="competition_id", foreignKey = @ForeignKey(name = "FK_USER_COMPETITIONS_COMPETITION_ID")))
    @Builder.Default
    private Set<Competition> competitions = new HashSet<>();

    @JsonBackReference
    @ManyToMany
    @JoinTable(name="user_teams",
        joinColumns=@JoinColumn(name="user_id", foreignKey = @ForeignKey(name = "FK_USER_TEAMS_USER_ID")),
        inverseJoinColumns=@JoinColumn(name="team_id", foreignKey = @ForeignKey(name = "FK_USER_TEAMS_TEAM_ID")))
    @Builder.Default
    private Set<Team> teams = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_detail_opinion_about_users", joinColumns = @JoinColumn(name = "user_detail_id", referencedColumnName = "user_user_id",
            foreignKey = @ForeignKey(name = "FK_USER_DETAIL_OPINION_ABOUT_USERS_USER_DETAIL_ID")))
    @MapKeyColumn(name = "user_name")
    @Column(name = "opinion")
    private Map<String,String> opinionAboutUser = new TreeMap<>();

    public void addUserToCompetition(Competition competition) {
        this.competitions.add(competition);
        competition.getUserDetails().add(this);
    }

    public void addUserToTeam(Team team){
        this.teams.add(team);
        team.getUserDetails().add(this);
    }

    @Override
    public UserDetailDto map() {
        return new UserDetailDto(city,age,gender);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserDetailDto {
        private String city;
        private int age;
        private Gender gender;

    }

}
