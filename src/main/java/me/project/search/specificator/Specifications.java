package me.project.search.specificator;

import me.project.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Specifications<T> implements Specification<T>, Cloneable {

    private final List<SearchCriteria> searchCriteriaListAnd;
    private final List<SearchCriteria> searchCriteriaListOr;


    public Specifications() {
        searchCriteriaListOr = new ArrayList<>();
        searchCriteriaListAnd = new ArrayList<>();
    }

    public Specifications(Specifications<T> specifications) {

        this.searchCriteriaListAnd = new ArrayList<>(specifications.searchCriteriaListAnd);
        this.searchCriteriaListOr = new ArrayList<>(specifications.searchCriteriaListOr);
    }

    public Specifications<T> and(SearchCriteria searchCriteria) {
        searchCriteriaListAnd.add(searchCriteria);
        return this;
    }

    public Specifications<T> or(SearchCriteria searchCriteria){
        searchCriteriaListOr.add(searchCriteria);
        return this;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {

        List<Predicate> AndPredicates = new ArrayList<>();
        List<Predicate> OrPredicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteriaListAnd)
           AndPredicates.add(criteria.getOperation().getPredicate(root,criteria,builder));

        for (SearchCriteria criteria : searchCriteriaListOr)
           OrPredicates.add(criteria.getOperation().getPredicate(root,criteria,builder));

        Predicate and = builder.and(AndPredicates.toArray(new Predicate[0]));
        Predicate or = builder.or(OrPredicates.toArray(new Predicate[0]));

        if(OrPredicates.isEmpty() && AndPredicates.isEmpty())
            throw new IllegalStateException("Cant use specification with empty predicates - use default find all then");

        //If both are not empty
        if(!OrPredicates.isEmpty() && !AndPredicates.isEmpty())
            return builder.and(and, or);

        //If and is empty return or
        if(AndPredicates.isEmpty())
            return or;

        //else return and
        return and;
    }

    @Override
    public Specifications<T> clone() {
        return new Specifications<>(this);

    }


}
