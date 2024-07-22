package pl.createcompetition.tournamentservice.tournament;

import static pl.createcompetition.tournamentservice.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_TOURNAMENT;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TournamentCreateUpdateRequest {

    @NotBlank(message = "Tournament owner can't be empty")
    private String tournamentOwner;

    @NotBlank(message = "Tournament name can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Tournament name can't contain number")
    private String tournamentName;

    @Range(min = 2, max =MAX_AMOUNT_OF_TEAMS_IN_TOURNAMENT, message = "Number of teams have to be beetwen 2 and 30")
    private int maxAmountOfTeams;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[a-zA-Z]*$", message = "Wrong city name")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Street name can't contain number")
    private String street;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int streetNumber;

    @Column(columnDefinition = "DATE")
    @NotBlank(message = "Pick time start of tournament")
    @Future
    private LocalDateTime tournamentStart;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time end of competition")
    @Future
    private LocalDateTime tournamentEnd;

    private Boolean isStarted;
    private Boolean isFinished;
}