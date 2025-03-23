package com.librarymanagement.project.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  Represents a paginated response for books.
 *  This class is used to return category data with pagination details.
 *  It contains a list of {@link BookDTO} objects, each representing a book's data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    /**
     * A list of {@link BookDTO} objects representing the books on the current page.
     */
    private List<BookDTO> content;

    /**
     * The current page number.
     */
    private Integer pageNumber;

    /**
     * The number of books per page.
     */
    private Integer pageSize;

    /**
     * The total number of books.
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
