package pl.createcompetition.tournamentservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import pl.createcompetition.tournamentservice.tournament.Tournament;
import pl.createcompetition.tournamentservice.competition.Competition;


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

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference("competition-tag")
    @Builder.Default
    private Set<Competition> competitions = new HashSet<>();

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference("tournament-tag")
    @Builder.Default
    private Set<Tournament> tournaments = new HashSet<>();

    @Data
    @AllArgsConstructor
    public static class TagsDto {
        private String tag;
        private Set<Competition> competitions;
        private Set<Tournament> tournaments;
    }

    public Tag(String tag) {
        this.tag = tag;
    }
}



