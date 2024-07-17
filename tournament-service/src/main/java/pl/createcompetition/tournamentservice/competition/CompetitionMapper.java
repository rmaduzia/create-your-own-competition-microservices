package pl.createcompetition.tournamentservice.competition;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface CompetitionMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateCompetitionFromDto(CompetitionCreateUpdateRequest source, @MappingTarget Competition target);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    CompetitionCreateUpdateRequest mapCompetitionToSimpleCompetitionDto(Competition source);


}
