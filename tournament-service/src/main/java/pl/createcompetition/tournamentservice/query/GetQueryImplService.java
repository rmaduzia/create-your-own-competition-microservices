package pl.createcompetition.tournamentservice.query;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Service;

@Service
public class GetQueryImplService<B extends QueryDtoInterface<R>,R> extends GetQueryAbstractService<B, R> {

    @Override
    public  Predicate getPredicate(Predicate predicate, CriteriaBuilder builder, Root r, List<SearchCriteria> params) {
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        return searchConsumer.getPredicate();
    }
}



