package com.expenseTracker.service;

import com.expenseTracker.model.Transaction;
import com.expenseTracker.model.User;
import com.expenseTracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Double> getSummaryForUser(User user) {
        List<Transaction> transactions = transactionRepository.findByUser(user);

        double income = transactions.stream()
                .filter(t -> t.getAmount() != null && t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.getAmount() != null && t.getAmount() < 0)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = income + expense;

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", income);
        summary.put("expense", Math.abs(expense));
        summary.put("balance", balance);

        return summary;
    }
}