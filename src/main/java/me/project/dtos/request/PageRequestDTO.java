package me.project.dtos.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@NoArgsConstructor
public class PageRequestDTO {

    public PageRequestDTO(Integer page, Integer pageLimit, String sortDir, String sortBy) {

        this.page = 1;
        this.pageLimit = 200;
        this.sortDir = "asc";

        if (page != null) {
            this.page = page;
        }

        if (pageLimit != null) {
            this.pageLimit = pageLimit;
        }

        if (sortDir != null) {
            this.sortDir = sortDir.toLowerCase();
        }

        if (sortBy != null) {
            this.sortBy = sortBy;
        }
    }

    private Integer page;

    private Integer pageLimit;

    private String sortDir;

    private String sortBy;

    public <T> PageRequest getRequest(Class<T> cls) {

        Field[] declaredFields = cls.getDeclaredFields();

        if (Arrays.stream(declaredFields).noneMatch(field -> field.getName().equals(sortBy))) {

            String[] clazzAndMember = sortBy.split("\\.", 2);


            Class<?> clazz = Arrays.stream(declaredFields)
                    .filter(field -> field.getName().equals(clazzAndMember[0]))
                    .findFirst()
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("No member with class %s exists on this entity", clazzAndMember[0]))
                    )
                    .getType();

            AtomicBoolean fieldMatches = new AtomicBoolean(false);

            if (Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> field.getName().equals(clazzAndMember[1])))
                fieldMatches.set(true);

            if (!fieldMatches.get())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("No field %s exists on this member class", clazzAndMember[1]));

        }

        if (!sortDir.equals("asc") && !sortDir.equals("desc"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort Direction must be ASC or DESC");

        if (page < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Number cannot be less than 1");

        if (pageLimit < -1 || pageLimit == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Size cannot be less than -1 or equal 0");

        if (pageLimit == -1)
            return PageRequest.of(page - 1, Integer.MAX_VALUE, Sort.Direction.valueOf(sortDir.toUpperCase()), sortBy);

        return PageRequest.of(page - 1, pageLimit, Sort.Direction.valueOf(sortDir.toUpperCase()), sortBy);
    }

}
