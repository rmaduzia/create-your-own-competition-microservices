package pl.createcompetition.teamservice.all;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

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
