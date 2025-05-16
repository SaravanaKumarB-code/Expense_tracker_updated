package com.expenseTracker.repository;

import com.expenseTracker.model.Transaction;
import com.expenseTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUser(User user);
    
    List<Transaction> findByUserOrderByDateDesc(User user);
    
}