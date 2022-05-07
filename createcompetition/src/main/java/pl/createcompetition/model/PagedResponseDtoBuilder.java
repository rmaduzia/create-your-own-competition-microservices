package pl.createcompetition.model;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class PagedResponseDtoBuilder {

    private List<?> listDto;

    private PageModel entityPage;

    public static PagedResponseDtoBuilder create() {
        return new PagedResponseDtoBuilder();
    }

    public PagedResponseDtoBuilder listDto(List<?> listDto) {
        this.listDto = listDto;
        return this;
    }

    public PagedResponseDtoBuilder entityPage(PageModel entityPage) {
        this.entityPage = entityPage;
        return this;
    }

    public PagedResponseDto<?> build() {
        return new PagedResponseDto<>(
                listDto,
                entityPage.getPageNumber(),
                entityPage.getPageSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast());
    }
}