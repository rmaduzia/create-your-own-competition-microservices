package pl.createcompetition.tournamentservice.all.tournament.match;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.createcompetition.tournamentservice.all.tournament.Tournament;
import pl.createcompetition.tournamentservice.all.tournament.match.MatchInTournament.MatchInTournamentDto;
import pl.createcompetition.tournamentservice.query.QueryDtoInterface;

@EqualsAndHashCode(of = {"id"})
@Table(name = "matches_in_tournaments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MatchInTournament implements QueryDtoInterface<MatchInTournamentDto> {

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
