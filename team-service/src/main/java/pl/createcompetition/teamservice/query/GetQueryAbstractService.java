package pl.createcompetition.teamservice.query;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pl.createcompetition.teamservice.all.PageModel;
import pl.createcompetition.teamservice.all.PagedResponseDto;
import pl.createcompetition.teamservice.all.PagedResponseDtoBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public abstract class GetQueryAbstractService<B extends QueryDtoInterface<R>, R> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected abstract Predicate getPredicate(Predicate predicate, CriteriaBuilder builder, Root r, List<SearchCriteria> params);

    //Page Number start from 0
    public PagedResponseDto<?> execute(Class<B> encja, String search, int pageNumber, int pageSize) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<B> query = builder.createQuery(encja);
        final Root r = query.from(encja);

        List<SearchCriteria> params = new ArrayList<>();
        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
            }
        }

        Predicate predicate = builder.conjunction();
        predicate = getPredicate(predicate, builder, r, params);
        query.where(predicate);

        var mapperFirst = new Mapper<B, R>();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        countQuery.select(builder.count(countQuery.from(encja)));

        int total_elements = Math.toIntExact(entityManager.createQuery(countQuery).getSingleResult());
        int totalPages = (int) Math.ceil(total_elements / (double) pageSize);
        boolean isLast = false;

        if (pageNumber == totalPages) {
            isLast = true;
        }


        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageNumber);
        typedQuery.setMaxResults(pageSize);

        List<B> typeQueryList = typedQuery.getResultList();
        List<R> dtoResultList = mapperFirst.map(typeQueryList);

        PageModel pageModel = new PageModel(pageNumber,pageSize,total_elements,totalPages,isLast);

        return PagedResponseDtoBuilder.create()
                .listDto(dtoResultList)
                .entityPage(pageModel)
                .build();
    }

    static class Mapper<A extends QueryDtoInterface<B>, B> {
        public List<B> map(List<A> from) {
            return from.stream()
                    .map(QueryDtoInterface::map)
                    .collect(toList());
        }
    }
}