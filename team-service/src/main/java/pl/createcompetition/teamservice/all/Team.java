package pl.createcompetition.teamservice.all;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import pl.createcompetition.teamservice.query.QueryDtoInterface;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pl.createcompetition.teamservice.config.AppConstants.MAX_AMOUNT_OF_USERS_IN_TEAM;


@EqualsAndHashCode(of="id")
@Table(name = "teams")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team implements QueryDtoInterface<Team.TeamDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Team name can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Team name can't contain number")
    @Size(min = 3, max = 30, message = "team name length can't be less then 3 and exceed 30")
    private String teamName;

    @Max(value = MAX_AMOUNT_OF_USERS_IN_TEAM, message = "You can't have more members then: " + MAX_AMOUNT_OF_USERS_IN_TEAM)
    @Min(value = 1, message = "You can't have less member then 1")
    private int maxAmountMembers;

    @NotBlank(message = "Team owner can't be empty")
    private String teamOwner;

    @ElementCollection
    @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id" , referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "FK_TEAM_MEMBERS_TEAM_ID"))
    @MapKeyColumn(name = "id")
    @Column(name = "user_name")
    @Builder.Default
    Map<Long, String> team_members = new HashMap<>();

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "City can't contain number")
    private String city;

    private Boolean isOpenRecruitment;

    @Override
    public TeamDto map() {
        return new TeamDto(teamName,maxAmountMembers,teamOwner, city, isOpenRecruitment);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamDto {
        private String teamName;
        private int maxAmountMembers;
        private String teamOwner;
        private String city;
        private Boolean isOpenRecruitment;

    }
}