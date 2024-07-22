package pl.createcompetition.tournamentservice.tournament;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateTournamentFromDto(TournamentCreateUpdateRequest source, @MappingTarget Tournament target);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    TournamentCreateUpdateRequest mapTournamentToSimpleTournamentDto(Tournament tournament);

}