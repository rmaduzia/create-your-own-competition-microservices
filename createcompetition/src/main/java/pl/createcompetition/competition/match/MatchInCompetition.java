package pl.createcompetition.competition.match;

import lombok.*;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.util.query.QueryDtoInterface;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of="id")
@Entity
@Table(name = "matches_in_competitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchInCompetition implements QueryDtoInterface<MatchInCompetition.MatchesInCompetitionDto> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

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
    @CollectionTable(name="votes_for_winning_team_in_competition_matches", joinColumns = @JoinColumn(name = "match_in_competition_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "FK_VOTES_FOR_WINNING_TEAM_IN_COMPETITION_MATCHES_COMPETITION_ID"))
    @MapKeyColumn(name = "user_name")
    @Column(name = "team_name")
    private Map<String, String> votesForWinnerTeam = new HashMap<>();

    private Boolean isWinnerConfirmed;
    private Boolean isMatchWasPlayed;
    private Boolean isClosed;

    public void addMatchToCompetition(Competition competition) {
        this.competition = competition;
        competition.getMatchInCompetition().add(this);
    }

    public void addVotesForWinnerTeam(String teamName, String userName) {
        this.votesForWinnerTeam.put(teamName, userName);
    }

    @Override
    public MatchInCompetition.MatchesInCompetitionDto map() {
        return new MatchInCompetition.MatchesInCompetitionDto(competition, firstTeamName, secondTeamName, matchDate, winnerTeam, votesForWinnerTeam, isWinnerConfirmed, isMatchWasPlayed);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchesInCompetitionDto {
        private Competition competition;
        private String firstTeamName;
        private String secondTeamName;
        private java.sql.Timestamp matchDate;
        private String winnerTeam;
        private Map<String, String> voteForWinnerTeam;
        private Boolean isWinnerConfirmed;
        private Boolean isMatchWasPlayed;
    }
}
