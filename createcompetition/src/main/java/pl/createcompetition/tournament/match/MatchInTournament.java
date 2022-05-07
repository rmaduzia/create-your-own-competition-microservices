package pl.createcompetition.tournament.match;

import lombok.*;
import pl.createcompetition.util.query.QueryDtoInterface;
import pl.createcompetition.tournament.Tournament;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of = {"id"})
@Table(name = "matches_in_tournaments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MatchInTournament implements QueryDtoInterface<MatchInTournament.MatchInTournamentDto> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tournament tournament;

    @NotBlank(message = "Team name can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Team name can't contain number")
    private String firstTeamName;

    @NotBlank(message = "Team name can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Team name can't contain number")
    private String secondTeamName;

    @Column(columnDefinition="DATE")
    private java.sql.Timestamp matchDate;

    @NotBlank(message = "Team name can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Team name can't contain number")
    private String winnerTeam;

    @ElementCollection
    @CollectionTable(name = "votes_for_winning_team_in_tournament_matches", joinColumns = @JoinColumn(name ="match_in_tournament_id", referencedColumnName = "id"),
                     foreignKey = @ForeignKey(name = "FK_VOTES_FOR_WINNING_TEAM_IN_TOURNAMENT_MATCHES_COMPETITION_ID"))
    @MapKeyColumn(name = "user_name")
    @Column(name = "team_name")
    private Map<String, String> votesForWinnerTeam = new HashMap<>();

    private Boolean isWinnerConfirmed;
    private Boolean isMatchWasPlayed;
    private Boolean isClosed;


    public void addMatchToTournament(Tournament tournament) {
        this.tournament = tournament;
        tournament.getMatchInTournament().add(this);
    }

    public void addVotesForWinnerTeam(String userName, String teamName) {
        this.votesForWinnerTeam.put(userName, teamName);
    }


    @Override
    public MatchInTournamentDto map() {
        return new MatchInTournamentDto(tournament, firstTeamName, secondTeamName, matchDate, winnerTeam, votesForWinnerTeam, isWinnerConfirmed, isMatchWasPlayed);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchInTournamentDto {
        private Tournament tournament;
        private String firstTeamName;
        private String secondTeamName;
        private java.sql.Timestamp matchDate;
        private String winnerTeam;
        private Map<String, String> voteForWinnerTeam = new HashMap<>();
        private Boolean isWinnerConfirmed;
        private Boolean isMatchWasPlayed;
    }
}
