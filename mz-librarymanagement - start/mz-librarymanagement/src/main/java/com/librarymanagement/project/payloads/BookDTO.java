package com.librarymanagement.project.payloads;

import com.librarymanagement.project.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for transferring book-related data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    /**
     * The unique identifier for the book.
     */
    private Long bookId;

    /**
     * The title of the book.
     */
    private String title;

    /**
     * The author of the book.
     */
    private String author;

    /**
     * The total number of copies of this book available in the library.
     */
    private int copiesTotal;

    /**
     * The number of copies of the book that are currently available for borrowing.
     */
    private int copiesAvailable;

    /**
     * A brief description of the book.
     */
    private String description;

    /**
     * The category to which this book belongs.
     */
    private Category category;

//    // for unit testing
//    public BookDTO(String title, String author){
//        this.title = title;
//        this.author = author;
//    }
//
//    public BookDTO(Long id, String title, String author, Category category){
//        this.bookId = id;
//        this.title = title;
//        this.author = author;
//        this.category = category;
//    }


}
