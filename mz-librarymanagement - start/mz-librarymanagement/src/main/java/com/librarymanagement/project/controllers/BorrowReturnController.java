package com.librarymanagement.project.controllers;


import com.librarymanagement.project.payloads.TransactionDTO;
import com.librarymanagement.project.services.BorrowReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BorrowReturnController {

    @Autowired
    private BorrowReturnService borrowReturnService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<TransactionDTO> borrowBook(@PathVariable Long bookId){
        TransactionDTO transcationDTO = borrowReturnService.borrowBook(bookId);
        return new ResponseEntity<>(transcationDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/return/{bookId}")
    public ResponseEntity<TransactionDTO> returnBook(@PathVariable Long bookId){
        TransactionDTO transcationDTO = borrowReturnService.returnBook(bookId);
        return new ResponseEntity<>(transcationDTO, HttpStatus.OK);
    }
}
