package pl.createcompetition.tournamentservice.competition;

import static pl.createcompetition.tournamentservice.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_COMPETITION;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import pl.createcompetition.tournamentservice.competition.Competition.CompetitionDto;
import pl.createcompetition.tournamentservice.competition.match.MatchInCompetition;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.model.TeamEntity;
import pl.createcompetition.tournamentservice.query.QueryDtoInterface;

@EqualsAndHashCode(of = {"eventName"})
@Table(name = "competitions")
@Entity
@Getter
@ToString
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
    @Pattern(regexp="^[^0-9]*$", message = "Competition name can't contain number")
    private String eventName;

    @NotBlank(message = "Competition owner can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Competition owner name can't contain number")
    private String eventOwner;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "City name can't contain number")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Street name can't contain number")
    private String streetName;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int streetNumber;

    @Range(min = 2, max = MAX_AMOUNT_OF_TEAMS_IN_COMPETITION, message = "Number of team have to be between 2 and 30")
    private int maxAmountOfTeams;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time start of competition")
    @Future
    private LocalDateTime eventStartDate;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time end of competition")
    @Future
    private LocalDateTime eventEndDate;

    private boolean isOpenRecruitment;
    private boolean isEventStarted;
    private boolean isEventFinished;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "competition_tag",
            joinColumns = @JoinColumn(name = "competition_id", foreignKey = @ForeignKey(name = "FK_COMPETITION_TAG_COMPETITION_ID")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name="FK_COMPETITION_TAG_TAGS_ID")))
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

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

    public void addTagToCompetition(String tag) {
        this.tags.add(new Tag(tag));
    }

    public boolean removeTagByName(String tagName) {
        return tags.removeIf(tag -> tag.getTag().equals(tagName));
    }

    public boolean removeTeam(String teamName) {
        return teams.removeIf(v -> v.getTeamName().equals(teamName));
    }

    public boolean addTeam(String teamName) {
        TeamEntity teamEntity = new TeamEntity(teamName);
        return teams.add(teamEntity);
    }

    public void addManyTagToCompetition(Set<String> tagsList) {

        Set<Tag> tags = new HashSet<>();

        for(String value: tagsList) {
            tags.add(new Tag(value));
        }

        this.tags.addAll(tags);
    }

    @Override
    public CompetitionDto map() {
        return new CompetitionDto(eventName, city, streetName, streetNumber, eventStartDate,
            eventEndDate, isOpenRecruitment, teams,
            tags, matchInCompetition);
    }

    public static Competition createCompetition(EventCreateUpdateRequest source, String competitionOwner) {

        return Competition.builder()
            .eventName(source.getEventName())
            .eventOwner(competitionOwner)
            .city(source.getCity())
            .streetName(source.getStreetName())
            .streetNumber(source.getStreetNumber())
            .maxAmountOfTeams(source.getMaxAmountOfTeams())
            .eventStartDate(source.getEventStartDate())
            .eventEndDate(source.getEventEndDate())
            .isOpenRecruitment(source.isOpenRecruitment())
            .isEventStarted(source.isEventStarted())
            .isEventFinished(false)
            .build();
    }


    @Data
    @AllArgsConstructor
    public static class CompetitionDto {
        private String eventName;
        private String city;
        private String streetName;
        private int streetNumber;
        private LocalDateTime eventStartDate;
        private LocalDateTime eventEndDate;
        private Boolean isOpenRecruitment;
        private Set<TeamEntity> teams;
        private Set<Tag> tag;
        private List<MatchInCompetition> matchInCompetition;
    }
}