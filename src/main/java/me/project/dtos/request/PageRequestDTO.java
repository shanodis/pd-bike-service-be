package me.project.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Locale;

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

    private Integer pageLimit ;

    private String sortDir;

    private String sortBy;

    public <T> PageRequest getRequest(Class<T> cls) {
        if(Arrays.stream(cls.getDeclaredFields()).noneMatch(field -> field.getName().equals(sortBy)))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Sorting Field:" + sortBy + " doesn't exist on this entity");

        if(!sortDir.equals("asc") && !sortDir.equals("desc"))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Sort Direction must be ASC or DESC");

        if(page < 1)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Page Number cannot be less than 1");

        if(pageLimit < -1 || pageLimit == 0)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Page Size cannot be less than -1 or equal 0");

        if(pageLimit == -1)
            return PageRequest.of(page - 1, Integer.MAX_VALUE, Sort.Direction.valueOf(sortDir.toUpperCase()) , sortBy);

        return PageRequest.of(page - 1, pageLimit, Sort.Direction.valueOf(sortDir.toUpperCase()) , sortBy);
    }

}
