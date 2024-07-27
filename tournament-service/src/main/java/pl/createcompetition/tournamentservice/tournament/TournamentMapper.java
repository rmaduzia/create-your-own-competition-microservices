package pl.createcompetition.tournamentservice.tournament;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import pl.createcompetition.tournamentservice.competition.EventCreateUpdateRequest;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateTournamentFromDto(EventCreateUpdateRequest source, @MappingTarget Tournament target);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    EventCreateUpdateRequest mapTournamentToSimpleTournamentDto(Tournament tournament);

}