package com.librarymanagement.project.models;


import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "borrowed_date")
    private LocalDate borrowedDate;

    @Nullable  // The return date can be null initially
    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Column(name = "is_returned")
    private boolean isReturned = false;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
