package com.expenseTracker.controller;

import com.expenseTracker.model.Transaction;
import com.expenseTracker.model.User;
import com.expenseTracker.service.AuthService;
import com.expenseTracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<?> getAllTransactions(@RequestParam Long userId) {
        Optional<User> userOptional = authService.getUserById(userId);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        List<Transaction> transactions = transactionService.getAllTransactionsByUser(userOptional.get());
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id, @RequestParam Long userId) {
        Optional<User> userOptional = authService.getUserById(userId);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        return transactionService.getTransactionById(id)
                .map(transaction -> {
                    // Check if transaction belongs to user
                    if (!transaction.getUser().getId().equals(userId)) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Unauthorized access to this transaction");
                        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
                    }
                    return ResponseEntity.ok(transaction);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction, @RequestParam Long userId) {
        Optional<User> userOptional = authService.getUserById(userId);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        Transaction savedTransaction = transactionService.addTransaction(transaction, userOptional.get());
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction, @RequestParam Long userId) {
        Optional<User> userOptional = authService.getUserById(userId);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        try {
            Transaction updatedTransaction = transactionService.updateTransaction(id, transaction, userOptional.get());
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, @RequestParam Long userId) {
        Optional<User> userOptional = authService.getUserById(userId);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        try {
            transactionService.deleteTransaction(id, userOptional.get());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
    }
}