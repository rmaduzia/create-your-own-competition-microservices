package pl.createcompetition.tournamentservice.tournament;

import static pl.createcompetition.tournamentservice.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_TOURNAMENT;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import pl.createcompetition.tournamentservice.tournament.Tournament.TournamentDto;
import pl.createcompetition.tournamentservice.tournament.match.MatchInTournament;
import pl.createcompetition.tournamentservice.model.Tag;
import pl.createcompetition.tournamentservice.model.TeamEntity;
import pl.createcompetition.tournamentservice.query.QueryDtoInterface;

//@EqualsAndHashCode(of = {"id", "tournamentName"})
@EqualsAndHashCode(of = {"id"})
@Table(name = "tournaments")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament implements QueryDtoInterface<TournamentDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tournament owner can't be empty")
    private String eventOwner;

    @NotBlank(message = "Tournament name can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Tournament name can't contain number")
    private String eventName;

    @Range(min = 2, max =MAX_AMOUNT_OF_TEAMS_IN_TOURNAMENT, message = "Number of teams have to be beetwen 2 and 30")
    private int maxAmountOfTeams;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Wrong city name")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Street name can't contain number")
    private String streetName;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int streetNumber;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time start of tournament")
    @Future
    private LocalDateTime eventStartDate;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time end of competition")
    @Future
    private LocalDateTime eventEndDate;

    private Boolean isStarted;
    private Boolean isFinished;

    @ElementCollection
    @CollectionTable(name = "drawed_teams_in_tournament", joinColumns = @JoinColumn(name = "tournament_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "FK_DRAWED_TEAMS_IN_TOURNAMENT_TOURNAMENT_ID"))
    @MapKeyColumn(name = "id")
    @Column(name = "duel")
    @Builder.Default
    private Map<String, String> drawnTeams = new TreeMap<>();

    @ElementCollection
    @CollectionTable(name = "match_times_in_tournament", joinColumns = @JoinColumn(name = "tournament_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_MATCH_TIMES_IN_TOURNAMENT_TOURNAMENT_ID")))
    @MapKeyColumn(name = "id")
    @Column(name = "match_time")
    @Builder.Default
    private Map<String, LocalDateTime> matchTimes = new TreeMap<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "tournament",
            cascade = CascadeType.ALL)
    private List<MatchInTournament> matchInTournament = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    @JoinTable(name = "tournament_tags",
            joinColumns = @JoinColumn(name = "tournament_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TAGS_TOURNAMENT_ID")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TAGS_TAG_ID")))
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @Builder.Default
    @JoinTable(name = "tournament_teams",
            joinColumns = @JoinColumn(name = "tournament_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TEAMS_TOURNAMENT_ID")),
            inverseJoinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TEAMS_TEAM_ID")))
    private Set<TeamEntity> teams = new HashSet<>();

    public void addTeamToTournament(String teamName) {
        TeamEntity teamEntity = new TeamEntity(teamName);
        this.teams.add(teamEntity);
    }

    public boolean deleteTeamFromTournament(String teamName) {
        return this.teams.removeIf(v -> v.getTeamName().equals(teamName));
    }

    @Override
    public TournamentDto map() {
        return new TournamentDto(eventOwner, eventName, maxAmountOfTeams, city, streetName,
            streetNumber,
            tags, matchInTournament);
    }

    public static Tournament createTournamentFromDto(TournamentCreateUpdateRequest source, String tournamentOwner) {
        return Tournament.builder()
            .eventName(source.getEventName())
            .eventOwner(tournamentOwner)
            .maxAmountOfTeams(source.getMaxAmountOfTeams())
            .city(source.getCity())
            .streetName(source.getStreetName())
            .streetNumber(source.getStreetNumber())
            .eventStartDate(source.getEventStartDate())
            .eventEndDate(source.getEventEndDate())
            .isStarted(source.getIsStarted())
            .isFinished(false)
            .build();
    }

    @Data
    @AllArgsConstructor
    public static class TournamentDto {
        private String tournamentOwner;
        private String tournamentName;
        private int maxAmountOfTeams;
        private String city;
        private String street;
        private int street_number;
        private Set<Tag> tag;
        private List<MatchInTournament> matchInTournament;
    }
}
