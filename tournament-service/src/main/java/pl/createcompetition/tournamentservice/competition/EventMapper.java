package pl.createcompetition.tournamentservice.competition;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateCompetitionFromDto(EventCreateUpdateRequest source, @MappingTarget Competition target);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    EventCreateUpdateRequest mapCompetitionToSimpleCompetitionDto(Competition source);


}
