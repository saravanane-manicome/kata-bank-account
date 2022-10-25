package io.smanicome.bank.service;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;
import io.smanicome.bank.account.OperationType;
import io.smanicome.bank.data.OperationsDao;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class OperationsService implements BankService {
    private final OperationsDao operationsDao;
    private final Clock clock;

    public OperationsService(OperationsDao operationsDao, Clock clock) {
        this.operationsDao = operationsDao;
        this.clock = clock;
    }

    public Operation deposit(UUID accountId, Amount amount) {
        final var balance = getCurrentBalanceOfAccount(accountId);
        final var updatedBalance = balance.add(amount.getValue());
        final var operation = new Operation(OperationType.DEPOSIT, accountId, amount, LocalDateTime.now(clock), updatedBalance);
        return operationsDao.save(operation);
    }

    private BigDecimal getCurrentBalanceOfAccount(UUID accountId) {
        return operationsDao.findLastByAccountId(accountId)
            .map(Operation::balance)
            .orElse(BigDecimal.ZERO);
    }
}
