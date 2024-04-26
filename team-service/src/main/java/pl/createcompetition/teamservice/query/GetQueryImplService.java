package pl.createcompetition.teamservice.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetQueryImplService<B extends QueryDtoInterface<R>,R> extends GetQueryAbstractService<B, R> {

    @Override
    public Predicate getPredicate(Predicate predicate, CriteriaBuilder builder, Root r, List<SearchCriteria> params) {
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        return searchConsumer.getPredicate();
    }
}



