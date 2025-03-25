package com.librarymanagement.project.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


/**
 * Represents a Book entity in the library management system.
 * This entity stores information about books.
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    /**
     * The unique identifier for the book.
     * This ID is automatically generated by the database when a new book is created.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;


    /**
     * The title of the book.
     * This field cannot be blank.
     */
    @NotBlank
    @Column(name="title")
    private String title;

    /**
     * The author of the book.
     * This field cannot be blank.
     */
    @NotBlank
    @Column(name="author")
    private String author;

    /**
     * The total number of copies of this book available in the library.
     * This value represents all copies of the book, regardless of availability.
     */
    @Column(name="copies_total")
    private int copiesTotal;

    /**
     * The number of copies of the book that are currently available for borrowing.
     * This value is updated when books are borrowed or returned.
     */
    @Column(name = "copies_available")
    private int copiesAvailable;

    /**
     * A description of the book.
     */
    @Column(name="description")
    private String description;

    /**
     * The category to which this book belongs.
     * A book can belong to only one category, but each category can contain multiple books.
     */
    @ManyToOne
    @NonNull
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Constructor used for creating a Book object with just the title and author.
     * This constructor is typically used in unit testing.
     *
     * @param bookId The id of the book
     * @param title The title of the book.
     * @param author The author of the book.
     */
    public Book(Long bookId, String title, String author){
        this.title = title;
        this.author = author;
        this.bookId = bookId;
    }

    /**
     * Constructor used for creating a Book object with the book's ID, title, author, and category.
     * This constructor is used when a book is being fetched from the database or created with an existing ID.
     *
     * @param bookId The unique identifier for the book.
     * @param title The title of the book.
     * @param author The author of the book.
     * @param category The category to which the book belongs.
     */
    public Book(Long bookId, String title, String author, Category category){
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
    }


    public boolean isAvailable(){
        return this.copiesAvailable > 0;
    }

    public void borrowOneCopy(){
        if(this.copiesAvailable > 0){
            this.copiesAvailable = this.copiesAvailable - 1;
        }
    }

    public void returnOneCopy() {
        if(this.copiesAvailable < this.copiesTotal) {
            this.copiesAvailable = this.copiesAvailable + 1;
        }
    }
}
