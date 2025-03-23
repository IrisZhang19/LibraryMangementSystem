package com.librarymanagement.project.repositories;

import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

    Page<Book> findByCategory(Category category, Pageable pageDetails);

    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageDetails);

    Page<Book> findByTitleLikeIgnoreCase(String s, Pageable pageDetails);
}
