package pl.createcompetition.util.query;

import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class GetQueryImplService<B extends QueryDtoInterface<R>,R> extends GetQueryAbstractService<B, R> {

    @Override
    public  Predicate getPredicate(Predicate predicate, CriteriaBuilder builder, Root r, List<SearchCriteria> params) {
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        return searchConsumer.getPredicate();
    }
}



