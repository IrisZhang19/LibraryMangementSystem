package com.librarymanagement.project.repositories;

import com.librarymanagement.project.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(Long userId, Long bookId);

}
