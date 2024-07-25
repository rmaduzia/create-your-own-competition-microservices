package pl.createcompetition.tournamentservice.competition;

import static pl.createcompetition.tournamentservice.config.AppConstants.MAX_AMOUNT_OF_TEAMS_IN_COMPETITION;

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
public class EventCreateUpdateRequest {

    @Column(unique = true)
    @NotBlank(message = "Competition can't be empty")
    private String eventName;

    @NotBlank(message = "City can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "City name can't contain number")
    private String city;

    @NotBlank(message = "Street can't be empty")
    @Pattern(regexp="^[^0-9]*$", message = "Street name can't contain number")
    private String streetName;

    @Min(value = 1, message = "Street number can't be lower then 1")
    private int streetNumber;

    @Range(min = 2, max = MAX_AMOUNT_OF_TEAMS_IN_COMPETITION, message = "Number of team have to be between 2 and 30")
    private int maxAmountOfTeams;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time start of competition")
    @Future
    private LocalDateTime eventStartDate;

    @Column(columnDefinition = "DATE")
    @NotNull(message = "Pick time end of competition")
    @Future
    private LocalDateTime eventEndDate;

    private Boolean isOpenRecruitment;

}