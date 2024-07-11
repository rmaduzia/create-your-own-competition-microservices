package pl.createcompetition.tournamentservice.competition;

import static pl.createcompetition.tournamentservice.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_COMPETITION;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import pl.createcompetition.tournamentservice.competition.Competition.CompetitionDto;
import pl.createcompetition.tournamentservice.competition.match.MatchInCompetition;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.model.TeamEntity;
import pl.createcompetition.tournamentservice.query.QueryDtoInterface;

@EqualsAndHashCode(of = {"id", "competitionName"})
@Table(name = "competitions")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Competition implements QueryDtoInterface<CompetitionDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Competition can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Competition name can't contain number")
    private String competitionName;

    @NotBlank(message = "Competition owner can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Competition owner name can't contain number")
    private String competitionOwner;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "City name can't contain number")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Street name can't contain number")
    private String street;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int street_number;

    @Range(min = 2, max = MAX_AMOUNT_OF_TEAMS_IN_COMPETITION, message = "Number of team have to be between 2 and 30")
    private int maxAmountOfTeams;

    @Column(columnDefinition = "DATE")
    @NotBlank(message = "Pick time start of competition")
    @Future
    private LocalDateTime competitionStart;

    @Column(columnDefinition = "DATE")
    @NotBlank(message = "Pick time end of competition")
    @Past
    private LocalDateTime competitionEnd;

    private Boolean isOpenRecruitment;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(name = "competition_tag",
            joinColumns = @JoinColumn(name = "competition_id", foreignKey = @ForeignKey(name = "FK_COMPETITION_TAG_COMPETITION_ID")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name="FK_COMPETITION_TAG_TAGS_ID")))
    @Builder.Default
    private Set<Tag> tag = new HashSet<>();

    @JsonManagedReference
    @ManyToMany
    @JoinTable(name = "competition_team",
            joinColumns = @JoinColumn(name = "competition_id", foreignKey = @ForeignKey(name = "FK_COMPETITION_TEAM_COMPETITION_ID")),
            inverseJoinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "FK_COMPETITION_TEAM_TEAM_ID")))
    @Builder.Default
    private Set<TeamEntity> teams = new HashSet<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "competition",
            cascade = CascadeType.ALL)
    private List<MatchInCompetition> matchInCompetition = new ArrayList<>();

    public void addTagToCompetition(Tag tag) {
        this.tag.add(tag);
    }

    public boolean removeTeam(String teamName) {
        return teams.removeIf(v -> v.getTeamName().equals(teamName));
    }

    public boolean addTeam(String teamName) {
        TeamEntity teamEntity = new TeamEntity(teamName);
        return teams.add(teamEntity);
    }

    public void addManyTagToCompetition(Set<Tag> tag) {
        this.tag.addAll(tag);
    }

    @Override
    public CompetitionDto map() {
        return new CompetitionDto(competitionName, city, street, street_number, competitionStart, competitionEnd, isOpenRecruitment, teams,tag, matchInCompetition);
    }

    @Data
    @AllArgsConstructor
    public static class CompetitionDto {
        private String competitionName;
        private String city;
        private String street;
        private int street_number;
        private LocalDateTime competitionStart;
        private LocalDateTime competitionEnd;
        private Boolean isOpenRecruitment;
        private Set<TeamEntity> teams;
        private Set<Tag> tag;
        private List<MatchInCompetition> matchInCompetition;
    }
}