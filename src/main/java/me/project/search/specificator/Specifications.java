package me.project.search.specificator;

import me.project.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Specifications<T> implements Specification<T>, Cloneable {
    private final List<SearchCriteria> searchCriteriaList;

    public Specifications() {
        this.searchCriteriaList = new ArrayList<>();
    }

    public Specifications(ArrayList<SearchCriteria> list) {
        this.searchCriteriaList = new ArrayList<>(list);
    }

    public void onlyAdd(SearchCriteria searchCriteria) {
        this.searchCriteriaList.add(searchCriteria);
    }

    public Specifications<T> add(SearchCriteria searchCriteria) {
        searchCriteriaList.add(searchCriteria);
        return this;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteriaList)
           predicates.add(criteria.getOperation().getPredicate(root,criteria,builder));

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Specifications<T> clone() {
        return new Specifications<>(new ArrayList<>(searchCriteriaList));

    }


}
