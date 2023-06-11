package me.project.search.specificator;

import me.project.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Specifications reprezentuje specyfikacje używane do tworzenia predykatów w zapytaniach Criteria.
 * Implementuje interfejs Specification oraz Cloneable.
 *
 * @param <T> typ encji, dla której tworzone są specyfikacje
 */
public class Specifications<T> implements Specification<T>, Cloneable {

    private final List<SearchCriteria> searchCriteriaListAnd;
    private final List<SearchCriteria> searchCriteriaListOr;


    /**
     * Konstruktor domyślny, inicjalizuje listy searchCriteriaListAnd i searchCriteriaListOr.
     */
    public Specifications() {
        searchCriteriaListOr = new ArrayList<>();
        searchCriteriaListAnd = new ArrayList<>();
    }

    /**
     * Konstruktor kopiujący, tworzy nową instancję Specifications na podstawie istniejącej instancji.
     *
     * @param specifications obiekt Specifications, który ma zostać sklonowany
     */
    public Specifications(Specifications<T> specifications) {

        this.searchCriteriaListAnd = new ArrayList<>(specifications.searchCriteriaListAnd);
        this.searchCriteriaListOr = new ArrayList<>(specifications.searchCriteriaListOr);
    }

    /**
     * Metoda dodaje kryterium wyszukiwania do listy searchCriteriaListAnd.
     *
     * @param searchCriteria kryterium wyszukiwania, które ma zostać dodane
     * @return aktualną instancję Specifications
     */
    public Specifications<T> and(SearchCriteria searchCriteria) {
        searchCriteriaListAnd.add(searchCriteria);
        return this;
    }

    /**
     * Metoda dodaje kryterium wyszukiwania do listy searchCriteriaListOr.
     *
     * @param searchCriteria kryterium wyszukiwania, które ma zostać dodane
     * @return aktualną instancję Specifications
     */
    public Specifications<T> or(SearchCriteria searchCriteria){
        searchCriteriaListOr.add(searchCriteria);
        return this;
    }

    /**
     * Metoda sprawdza, czy listy searchCriteriaListAnd i searchCriteriaListOr są puste.
     *
     * @return true, jeśli obie listy są puste; w przeciwnym razie false
     */
    public boolean isEmpty(){
        return searchCriteriaListAnd.isEmpty() && searchCriteriaListOr.isEmpty();
    }

    /**
     * Metoda tworzy predykat na podstawie specyfikacji, używając klas CriteriaBuilder, CriteriaQuery i Root.
     * Wykonuje operacje logiczne AND i OR na predykatach z list searchCriteriaListAnd i searchCriteriaListOr.
     *
     * @param root    obiekt Root reprezentujący encję, dla której tworzony jest predykat
     * @param query   obiekt CriteriaQuery reprezentujący zapytanie kryterialne
     * @param builder obiekt CriteriaBuilder do tworzenia predykatów
     * @return predykat reprezentujący specyfikację
     * @throws IllegalStateException jeśli obie listy searchCriteriaListAnd i searchCriteriaListOr są puste
     */
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

    /**
     * Metoda tworzy nową instancję obiektu na podstawie akutalnej.
     *
     * @return Specifications, nowy sklonowany obiekt
     */
    @Override
    public Specifications<T> clone() {
        return new Specifications<>(this);

    }
}
