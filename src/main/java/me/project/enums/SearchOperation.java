package me.project.enums;

import me.project.entitiy.Bike;
import me.project.entitiy.User;
import me.project.search.SearchCriteria;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;

/**
 * Enum SearchOperation reprezentuje różne operacje wyszukiwania używane w specyfikacjach.
 * Każda operacja ma zdefiniowaną metodę "getPredicate", która generuje odpowiedni predykat dla danego kryterium wyszukiwania.
 */
public enum SearchOperation {

    /**
     * Operacja GREATER_THAN generuje predykat "greaterThan" dla danego kryterium.
     */
    GREATER_THAN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThan(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    /**
     * Operacja LESS_THAN generuje predykat "lessThan" dla danego kryterium.
     */
    LESS_THAN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThan(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    /**
     * Operacja GREATER_THAN_EQUAL generuje predykat "greaterThanOrEqualTo" dla danego kryterium.
     */
    GREATER_THAN_EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    /**
     * Operacja LESS_THAN_EQUAL generuje predykat "lessThanOrEqualTo" dla danego kryterium.
     */
    LESS_THAN_EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()),
                    criteria.getValue().toString()
            );
        }
    },

    /**
     * Operacja NOT_EQUAL generuje predykat "notEqual" dla danego kryterium.
     */
    NOT_EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.notEqual(root.get(
                    criteria.getKey()), criteria.getValue()
            );
        }
    },

    /**
     * Operacja EQUAL generuje predykat "equal" dla danego kryterium.
     */
    EQUAL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.equal(root.get(
                    criteria.getKey()), criteria.getValue()
            );
        }
    },

    /**
     * Operacja EQUAL_JOIN generuje predykat "equal" dla złączenia (join) dwóch encji.
     */
    EQUAL_JOIN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            String[] memberAndField = criteria.getKey().split("\\.", 2);

            Join<T, ?> join = root.join(memberAndField[0]);

            return builder.equal(
                    join.get(
                            memberAndField[1]
                    ),
                    criteria.getValue()
            );
        }
    },

    /**
     * Operacja EQUAL_JOIN_USER generuje predykat "equal" dla złączenia (join) danej encji z encją User.
     */
    EQUAL_JOIN_USER {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            Join<T, User> userJoin = root.join("user");
            return builder.equal(
                    userJoin.get(
                            criteria.getKey()
                    ),
                    criteria.getValue()
            );
        }
    },

    /**
     * Operacja MATCH_JOIN generuje predykat "match" dla złączenia (join) dwóch encji.
     */
    MATCH_JOIN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            String[] memberAndField = criteria.getKey().split("\\.", 2);

            Join<T, ?> join = root.join(memberAndField[0]);

            return builder.like(
                    builder.lower(join.get(memberAndField[1]))
                    , "%" + criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    /**
     * Operacja MATCH_JOIN_LIST generuje predykat "match" dla złączenia (join) dwóch list.
     */
    MATCH_JOIN_LIST {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            String[] memberAndField = criteria.getKey().split("\\.", 2);

            ListJoin<T, ?> join = root.joinList(memberAndField[0]);

            return builder.like(
                    builder.lower(join.get(memberAndField[1]))
                    , "%" + criteria.getValue().toString().toLowerCase() + "%"
            );

        }
    },

    /**
     * Operacja MATCH_JOIN_LIST_OBJECT generuje predykat "match" dla złączenia (join) dwóch list encji.
     */
    MATCH_JOIN_LIST_OBJECT {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            String[] memberAndField = criteria.getKey().split("\\.", 3);

            ListJoin<T, ?> join = root.joinList(memberAndField[0]);

            Join<?, ?> secondJoin = join.join(memberAndField[1]);

            return builder.like(
                    builder.lower(secondJoin.get(memberAndField[2]))
                    , "%" + criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    /**
     * Operacja MATCH_JOIN_BIKE generuje predykat "match" dla złączenia (join) danej encji z encją Bike.
     */
    MATCH_JOIN_BIKE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            Join<T, Bike> bikeJoin = root.join("bike");
            return builder.like(
                    builder.lower(bikeJoin.get(criteria.getKey()))
                    , "%" + criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    /**
     * Operacja MATCH generuje predykat "match" dla danego kryterium.
     */
    MATCH {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.like(
                    builder.lower(root.get(criteria.getKey()))
                    , "%" + criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    /**
     * Operacja MATCH_START generuje predykat "match" dla danego kryterium na początku przedziału.
     */
    MATCH_START {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.like(
                    builder.lower(root.get(criteria.getKey())),
                    "%" + criteria.getValue().toString().toLowerCase()
            );
        }
    },

    /**
     * Operacja MATCH_END generuje predykat "match" dla danego kryterium na końcu przedziału.
     */
    MATCH_END {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.like(
                    builder.lower(root.get(criteria.getKey())),
                    criteria.getValue().toString().toLowerCase() + "%"
            );
        }
    },

    /**
     * Operacja IN generuje predykat "in" dla danego kryterium.
     */
    IN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.in(
                    root.get(criteria.getKey())
            ).value(criteria.getValue());
        }
    },

    /**
     * Operacja NOT_IN generuje predykat "notIn" dla danego kryterium.
     */
    NOT_IN {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.not(
                    root.get(criteria.getKey())
            ).in(criteria.getValue());
        }
    },

    /**
     * Operacja EQUAL_NULL generuje predykat "equal" dla danego kryterium równego wartości NULL.
     */
    EQUAL_NULL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.isNull(root.get(criteria.getKey()));
        }
    },

    /**
     * Operacja NOT_EQUAL_NULL generuje predykat "equal" dla danego kryterium różnego od wartości NULL.
     */
    NOT_EQUAL_NULL {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.isNotNull(root.get(criteria.getKey()));
        }
    },

    /**
     * Operacja GREATER_THAN_EQUAL_DATE generuje predykat "greaterThanEqual" dla danego kryterium, który jest datą.
     */
    GREATER_THAN_EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    },

    /**
     * Operacja GREATER_THAN_DATE generuje predykat "greaterThan" dla danego kryterium, który jest datą.
     */
    GREATER_THAN_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.greaterThan(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString()));
        }
    },

    /**
     * Operacja LESS_THAN_EQUAL_DATE generuje predykat "lessThanEqual" dla danego kryterium, który jest datą.
     */
    LESS_THAN_EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    },

    /**
     * Operacja LESS_THAN_DATE generuje predykat "lessThan" dla danego kryterium, który jest datą.
     */
    LESS_THAN_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.lessThan(
                    root.get(criteria.getKey()),
                    LocalDateTime.parse(criteria.getValue().toString()));
        }
    },

    /**
     * Operacja EQUAL_DATE generuje predykat "equal" dla danego kryterium, który jest datą.
     */
    EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.equal(root.get(
                    criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    },

    /**
     * Operacja NOT_EQUAL_DATE generuje predykat "notEqual" dla danego kryterium, który jest datą.
     */
    NOT_EQUAL_DATE {
        @Override
        public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder) {
            return builder.notEqual(root.get(
                    criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString())
            );
        }
    };

    /**
     * Abstrakcyjna metoda zwraca predykat na podstawie specyfikacji, używając klas CriteriaBuilder, SearchCriteria i Root.
     * Wykorzysuje do tego zdefniowane mechanizmy w tym enumie.
     *
     * @param root    obiekt Root reprezentujący encję, dla której tworzony jest predykat
     * @param criteria   obiekt SearchCriteria reprezentujący kryterium wyszukiwania
     * @param builder obiekt CriteriaBuilder do tworzenia predykatów
     * @return predykat typu generycznego
     */
    abstract public <T> Predicate getPredicate(Root<T> root, SearchCriteria criteria, CriteriaBuilder builder);
}
