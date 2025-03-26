package com.librarymanagement.project.configs;

/**
 * Contains some global constants, mainly for pagination and sorting.
 */
public class AppConstants {

    /**
     * Default page number for pagination (0-based index).
     */
    public static final String PAGE_NUMBER = "0";

    /**
     * Default page size for pagination.
     */
    public static final String PAGE_SIZE = "3";

    /**
     * Default sorting criteria for categories (sorted by categoryId).
     */
    public static final String SORT_CATEGORIES_BY = "categoryId";

    /**
     * Default sorting criteria for books (sorted by bookId).
     */
    public static final String SORT_BOOKS_BY = "bookId";

    /**
     * Default sorting direction (ascending order).
     */
    public static final String SORT_DIR = "asc";

}
