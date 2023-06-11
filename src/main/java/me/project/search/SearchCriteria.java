package me.project.search;

import me.project.enums.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Klasa SearchCriteria reprezentuje kryterium wyszukiwania, używane w specyfikacjach.
 * Klasa jest generowana automatycznie za pomocą Lombok, w związku z czym posiada adnotacje @NoArgsConstructor,
 * @AllArgsConstructor, @Getter oraz @Setter.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchCriteria {
    private String key;
    private Object value;
    private SearchOperation operation;
}
