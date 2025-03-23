package com.librarymanagement.project.payloads;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a paginated response for categories.
 * This class is used to return category data with pagination details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    /**
     * List of category data transfer objects (DTOs) for the current page.
     */
    private List<CategoryDTO> content;

    /**
     * The current page number.
     */
    private Integer pageNumber;

    /**
     * The number of categories per page.
     */
    private Integer pageSize;

    /**
     * The total number of categories.
     */
    private Long totalElements;

    /**
     * The total number of pages.
     */
    private Integer totalPages;

    /**
     * Indicates whether this is the last page in the pagination.
     */
    private boolean lastPage;
}
