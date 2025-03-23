package com.librarymanagement.project.payloads;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Data Transfer Object (DTO) for Category entity.
* Used to transfer category data between different layers of the application.
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    /**
     * The unique identifier of the category.
     */
    private Long categoryId;

    /**
     * The name of the category.
     */
    private String categoryName;

}
