package pl.createcompetition.team;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.model.Tag;
import pl.createcompetition.tournament.Tournament;
import pl.createcompetition.user.detail.UserDetail;
import pl.createcompetition.util.query.QueryDtoInterface;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

import static pl.createcompetition.config.AppConstants.MAX_AMOUNT_OF_USERS_IN_TEAM;

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

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "City can't contain number")
    private String city;

    private Boolean isOpenRecruitment;

    @JsonManagedReference
    @ManyToMany(mappedBy="teams")
    @Builder.Default
    private Set<UserDetail> userDetails = new HashSet<>();

    @JsonManagedReference
    @ManyToMany
    @JoinTable(name = "teams_tags",
            joinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "FK_TEAM_TAGS_TEAM_ID")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "FK_TEAM_TAGS_TAG_ID") ))
    @Builder.Default
    private Set<Tag> tag = new HashSet<>();

    @JsonBackReference
    @ManyToMany(mappedBy = "teams")
    @Builder.Default
    private Set<Tournament> tournaments = new HashSet<>();

    @JsonBackReference
    @ManyToMany(mappedBy = "teams")
    @Builder.Default
    private Set<Competition> competitions = new HashSet<>();

    public void addTeamToTournament(Tournament tournament) {
        this.tournaments.add(tournament);
        tournament.getTeams().add(this);
    }

    public void deleteTeamFromTournament(Tournament tournament) {
        this.tournaments.remove(tournament);
        tournament.getTeams().remove(this);
    }

    public void addTeamToCompetition(Competition competition) {
        this.competitions.add(competition);
        competition.getTeams().add(this);
    }

    public void deleteTeamFromCompetition(Competition competition) {
        this.competitions.remove(competition);
        competition.getTeams().remove(this);
    }

    public void addRecruitToTeam(UserDetail userDetail) {
        this.userDetails.add(userDetail);
        userDetail.getTeams().add(this);
    }

    public void deleteRecruitFromTeam(UserDetail userDetail) {
        this.userDetails.remove(userDetail);
        userDetail.getTeams().remove(this);
    }


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
