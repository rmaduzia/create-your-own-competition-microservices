package pl.createcompetition.tournament;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import pl.createcompetition.model.Tag;
import pl.createcompetition.util.query.QueryDtoInterface;
import pl.createcompetition.team.Team;
import pl.createcompetition.tournament.match.MatchInTournament;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;
import static pl.createcompetition.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_TOURNAMENT;

@EqualsAndHashCode(of="id")
@Table(name = "tournaments")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament implements QueryDtoInterface<Tournament.TournamentDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tournament owner can't be empty")
    private String tournamentOwner;

    @NotBlank(message = "Tournament name can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Tournament name can't contain number")
    private String tournamentName;

    @Range(min = 2, max =MAX_AMOUNT_OF_TEAMS_IN_TOURNAMENT, message = "Number of teams have to be beetwen 2 and 30")
    private int maxAmountOfTeams;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Wrong city name")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Street name can't contain number")
    private String street;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int street_number;

    @Column(columnDefinition = "DATE")
    @NotBlank(message = "Pick time start of tournament")
    @Future
    private java.sql.Timestamp tournamentStart;

    private Boolean isStarted;
    private Boolean isFinished;

    @ElementCollection
    @CollectionTable(name = "drawed_teams_in_tournament", joinColumns = @JoinColumn(name = "tournament_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "FK_DRAWED_TEAMS_IN_TOURNAMENT_TOURNAMENT_ID"))
    @MapKeyColumn(name = "id")
    @Column(name = "duel")
    @Builder.Default
    private Map<String, String> drawedTeams = new TreeMap<>();

    @ElementCollection
    @CollectionTable(name = "match_times_in_tournament", joinColumns = @JoinColumn(name = "tournament_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_MATCH_TIMES_IN_TOURNAMENT_TOURNAMENT_ID")))
    @MapKeyColumn(name = "id")
    @Column(name = "match_time")
    @Builder.Default
    private Map<String, Date> matchTimes = new TreeMap<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "tournament",
            cascade = CascadeType.ALL)
    private List<MatchInTournament> matchInTournament = new ArrayList<>();

    @ManyToMany
    @JsonManagedReference
    @Builder.Default
    @JoinTable(name = "tournament_tags",
            joinColumns = @JoinColumn(name = "tournament_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TAGS_TOURNAMENT_ID")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TAGS_TAG_ID")))
    private Set<Tag> tag = new HashSet<>();

    @ManyToMany
    @JsonManagedReference
    @Builder.Default
    @JoinTable(name = "tournament_teams",
            joinColumns = @JoinColumn(name = "tournament_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TEAMS_TOURNAMENT_ID")),
            inverseJoinColumns = @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "FK_TOURNAMENT_TEAMS_TEAM_ID")))
    private Set<Team> teams = new HashSet<>();

    public void addTeamToTournament(Team teams) {
        this.teams.add(teams);
        teams.getTournaments().add(this);
    }

    public void deleteTeamFromTournament(Team teams) {
        this.teams.remove(teams);
        teams.getTournaments().remove(this);
    }

    @Override
    public TournamentDto map() {
        return new TournamentDto(tournamentOwner, tournamentName, maxAmountOfTeams, city, street, street_number, tag, matchInTournament);
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
