package io.smanicome.bank.service;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;
import io.smanicome.bank.account.OperationType;
import io.smanicome.bank.account.Statement;
import io.smanicome.bank.data.OperationsDao;
import io.smanicome.bank.exceptions.NotEnoughBalanceException;
import io.smanicome.bank.utils.StatementWriter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

public class OperationsService implements BankService {
    private final StatementWriter writer;
    private final OperationsDao operationsDao;
    private final Clock clock;

    public OperationsService(StatementWriter writer, OperationsDao operationsDao, Clock clock) {
        this.writer = writer;
        this.operationsDao = operationsDao;
        this.clock = clock;
    }

    public Operation deposit(UUID accountId, Amount amount) {
        final var balance = getCurrentBalanceOfAccount(accountId);
        final var updatedBalance = balance.add(amount.getValue());
        final var operation = new Operation(OperationType.DEPOSIT, accountId, amount, LocalDateTime.now(clock), updatedBalance);
        return operationsDao.save(operation);
    }

    public Operation withdraw(UUID accountId, Amount amount) throws NotEnoughBalanceException {
        final var balance = getCurrentBalanceOfAccount(accountId);

        if(amount.getValue().doubleValue() > balance.doubleValue()) throw new NotEnoughBalanceException();

        final var updatedBalance = balance.subtract(amount.getValue());

        final var operation = new Operation(OperationType.WITHDRAWAL, accountId, amount, LocalDateTime.now(clock), updatedBalance);
        return operationsDao.save(operation);
    }

    public void printAccountStatement(UUID accountId) {
        final var operations = operationsDao.findByAccountId(accountId);

        final var balance = operations.stream().max(Comparator.comparing(Operation::date))
                .map(Operation::balance)
                .orElse(BigDecimal.ZERO);

        final var statement = new Statement(accountId, operations, balance, LocalDateTime.now(clock));
        writer.write(statement);
    }

    private BigDecimal getCurrentBalanceOfAccount(UUID accountId) {
        return operationsDao.findLastByAccountId(accountId)
            .map(Operation::balance)
            .orElse(BigDecimal.ZERO);
    }
}
