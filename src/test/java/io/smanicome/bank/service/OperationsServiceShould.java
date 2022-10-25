package io.smanicome.bank.service;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;
import io.smanicome.bank.account.OperationType;
import io.smanicome.bank.account.Statement;
import io.smanicome.bank.data.OperationsDao;
import io.smanicome.bank.exceptions.NegativeAmountException;
import io.smanicome.bank.exceptions.NotEnoughBalanceException;
import io.smanicome.bank.utils.StatementWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationsServiceShould {
    @Mock
    private OperationsDao operationsDao;

    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

    @Mock
    private StatementWriter writer;

    private OperationsService operationsService;

    @BeforeEach
    void setUp() {
        operationsService = new OperationsService(writer, operationsDao, fixedClock);
    }

    @DisplayName("should deposit into account")
    @Test
    void depositIntoAccount() throws NegativeAmountException {
        final var accountId = UUID.randomUUID();
        final var date = LocalDateTime.now(fixedClock);
        final var operation = new Operation(
                OperationType.DEPOSIT,
                accountId,
                Amount.of(BigDecimal.valueOf(100)),
                date,
                BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_EVEN)
        );

        when(operationsDao.save(any())).thenReturn(operation);

        final var resultingOperation = operationsService.deposit(accountId, Amount.of(BigDecimal.valueOf(100)));

        assertEquals(operation, resultingOperation);

        final var orderVerifier = inOrder(operationsDao);
        orderVerifier.verify(operationsDao).findLastByAccountId(accountId);
        orderVerifier.verify(operationsDao).save(operation);
        orderVerifier.verifyNoMoreInteractions();
    }

    @DisplayName("should withdraw from account")
    @Test
    void withdrawFromAccount() throws NotEnoughBalanceException, NegativeAmountException {
        final var accountId = UUID.randomUUID();
        final var date = LocalDateTime.now(fixedClock);
        final var operation = new Operation(
                OperationType.WITHDRAWAL,
                accountId,
                Amount.of(BigDecimal.valueOf(100)),
                date,
                BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_EVEN)
        );

        final var latestOperation = new Operation(
                OperationType.DEPOSIT,
                accountId,
                Amount.of(BigDecimal.valueOf(200)),
                date,
                BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_EVEN)
        );

        when(operationsDao.findLastByAccountId(any())).thenReturn(Optional.of(latestOperation));
        when(operationsDao.save(any())).thenReturn(operation);

        final var resultingOperation = operationsService.withdraw(accountId, Amount.of(BigDecimal.valueOf(100)));

        assertEquals(operation, resultingOperation);

        final var orderVerifier = inOrder(operationsDao);
        orderVerifier.verify(operationsDao).findLastByAccountId(accountId);
        orderVerifier.verify(operationsDao).save(operation);
        orderVerifier.verifyNoMoreInteractions();
    }

    @DisplayName("should throw when trying to withdraw from account with amount greater than balance")
    @Test
    void throwWhenWithdrawAmountGreaterThanAccountBalance() {
        final var accountId = UUID.randomUUID();

        assertThrows(NotEnoughBalanceException.class, () -> operationsService.withdraw(accountId, Amount.of(BigDecimal.valueOf(100))));

        verify(operationsDao).findLastByAccountId(accountId);
        verifyNoMoreInteractions(operationsDao);
    }

    @DisplayName("should print account statement")
    @Test
    void printAccountStatement() throws NegativeAmountException {
        final var accountId = UUID.randomUUID();
        final var date = LocalDateTime.now(fixedClock);

        final var registeredOperations = List.of(
                new Operation(
                        OperationType.WITHDRAWAL,
                        accountId,
                        Amount.of(BigDecimal.valueOf(1000)),
                        date.plusMinutes(20),
                        BigDecimal.valueOf(6000).setScale(2, RoundingMode.HALF_EVEN)
                ),
                new Operation(
                        OperationType.DEPOSIT,
                        accountId,
                        Amount.of(BigDecimal.valueOf(5000)),
                        date.plusMinutes(10),
                        BigDecimal.valueOf(7000).setScale(2, RoundingMode.HALF_EVEN)
                ),
                new Operation(
                        OperationType.DEPOSIT,
                        accountId,
                        Amount.of(BigDecimal.valueOf(2000)),
                        date,
                        BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_EVEN)
                )
        );

        final var statement = new Statement(accountId, registeredOperations, registeredOperations.get(0).balance(), date);

        when(operationsDao.findByAccountId(any())).thenReturn(registeredOperations);

        operationsService.printAccountStatement(accountId);

        final var orderVerifier = inOrder(operationsDao, writer);
        orderVerifier.verify(operationsDao).findByAccountId(accountId);
        orderVerifier.verify(writer).write(statement);
        orderVerifier.verifyNoMoreInteractions();
    }
}