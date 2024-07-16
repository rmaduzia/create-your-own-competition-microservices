package pl.createcompetition.tournamentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.createcompetition.tournamentservice.competition.Competition;
import pl.createcompetition.tournamentservice.tournament.Tournament;

@EqualsAndHashCode(of = {"id", "teamName"})
@Table(name = "teams")
@Getter
@Setter
@Entity
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name")
    private String teamName;

    @ManyToMany(mappedBy = "teams")
    private Set<Competition> competitions = new HashSet<>();

    @ManyToMany(mappedBy = "teams")
    private Set<Tournament> tournaments = new HashSet<>();

    public TeamEntity(String teamName) {
        this.teamName = teamName;
    }

}
