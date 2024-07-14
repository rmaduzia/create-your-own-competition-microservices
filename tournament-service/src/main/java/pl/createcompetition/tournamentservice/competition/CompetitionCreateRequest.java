package pl.createcompetition.tournamentservice.competition;

import static pl.createcompetition.tournamentservice.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_COMPETITION;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class CompetitionCreateRequest {

    @Column(unique = true)
    @NotBlank(message = "Competition can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Competition name can't contain number")
    private String competitionName;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "City name can't contain number")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Street name can't contain number")
    private String street;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int streetNumber;

    @Range(min = 2, max = MAX_AMOUNT_OF_TEAMS_IN_COMPETITION, message = "Number of team have to be between 2 and 30")
    private int maxAmountOfTeams;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time start of competition")
    @Future
    private LocalDateTime competitionStart;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time end of competition")
    @Past
    private LocalDateTime competitionEnd;

    private Boolean isOpenRecruitment;

}