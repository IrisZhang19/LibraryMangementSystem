package com.librarymanagement.project.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
*
*
* */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name="title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name="copies_total")
    private int copiesTotal;

    @Column(name = "copies_available")
    private int copiesAvailable;

    @Column(name="description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // for unit testing
    public Book(String title, String author){
        this.title = title;
        this.author = author;
    }

    public Book(Long bookId, String title, String author, Category category){
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
    }

}
