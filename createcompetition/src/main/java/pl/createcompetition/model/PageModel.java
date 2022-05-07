package pl.createcompetition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PageModel {

    private int pageNumber;
    private int pageSize;
    private int TotalElements;
    private int TotalPages;
    private boolean Last;
}
