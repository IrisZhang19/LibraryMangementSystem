package com.librarymanagement.project.payloads;

import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscationDTO {

    private Long transactionId;
    private LocalDate borrowedDate;
    private LocalDate returnedDate;
    private boolean isReturned;
    private BookDTO book;
    private UserDTO user;
}
