package pl.createcompetition.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import pl.createcompetition.competition.Competition;
import pl.createcompetition.team.Team;
import pl.createcompetition.tournament.Tournament;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = {"id", "tag"})
@Table(name = "tags")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    @NotBlank(message = "Tag name can't be empty")
    private String tag;

    @ManyToMany(mappedBy = "tag")
    @JsonBackReference
    @Builder.Default
    private Set<Competition> competitions = new HashSet<>();

    @ManyToMany(mappedBy = "tag")
    @JsonBackReference
    @Builder.Default
    private Set<Tournament> tournaments = new HashSet<>();

    @ManyToMany(mappedBy = "tag")
    @JsonBackReference
    @Builder.Default
    private Set<Team> teams = new HashSet<>();

    @Data
    @AllArgsConstructor
    public static class TagsDto {
        private String tag;
        private Set<Competition> competitions;
        private Set<Tournament> tournaments;
        private Set<Team> teams;
    }
}



