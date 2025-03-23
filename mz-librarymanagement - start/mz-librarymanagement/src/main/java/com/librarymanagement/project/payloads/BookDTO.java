package com.librarymanagement.project.payloads;

import com.librarymanagement.project.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long bookId;
    private String title;
    private String author;
    private int copiesTotal;
    private int copiesAvailable;
    private String description;
    private Category category;

    // for unit testing
    public BookDTO(String title, String author){
        this.title = title;
        this.author = author;
    }

    public BookDTO(Long id, String title, String author, Category category){
        this.bookId = id;
        this.title = title;
        this.author = author;
        this.category = category;
    }


}
