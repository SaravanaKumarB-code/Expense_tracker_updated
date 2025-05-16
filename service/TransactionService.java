package com.expenseTracker.service;

import com.expenseTracker.model.Transaction;
import com.expenseTracker.model.User;
import com.expenseTracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactionsByUser(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction addTransaction(Transaction transaction, User user) {
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction, User user) {
        return transactionRepository.findById(id)
                .map(transaction -> {
                    // Ensure this transaction belongs to the user
                    if (!transaction.getUser().getId().equals(user.getId())) {
                        throw new RuntimeException("Unauthorized access to this transaction");
                    }
                    transaction.setAmount(updatedTransaction.getAmount());
                    transaction.setCategory(updatedTransaction.getCategory());
                    transaction.setDescription(updatedTransaction.getDescription());
                    transaction.setDate(updatedTransaction.getDate());
                    return transactionRepository.save(transaction);
                }).orElseThrow(() -> new RuntimeException("Transaction not found with id " + id));
    }

    public void deleteTransaction(Long id, User user) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            // Ensure this transaction belongs to the user
            if (!transaction.get().getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to this transaction");
            }
            transactionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Transaction not found with id " + id);
        }
    }
}