package pl.createcompetition.tournamentservice.query;

import jakarta.validation.constraints.Min;
import java.beans.ConstructorProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationInfoRequest {

    @Min(0)
    Integer pageNumber;
    @Min(1)
    Integer pageSize;

    @ConstructorProperties({"page-number","page-size"})
    public PaginationInfoRequest(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber != null ? pageNumber : 0;
        this.pageSize = pageSize != null ? pageSize : 100;
    }
}
