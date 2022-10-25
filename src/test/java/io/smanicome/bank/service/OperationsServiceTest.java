package io.smanicome.bank.service;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;
import io.smanicome.bank.account.OperationType;
import io.smanicome.bank.data.OperationsDao;
import io.smanicome.bank.exceptions.NegativeAmountException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationsServiceTest {
    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

    @Mock
    private OperationsDao operationsDao;

    private OperationsService operationsService;

    @BeforeEach
    void setUp() {
        operationsService = new OperationsService(operationsDao, fixedClock);
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
}