package io.smanicome.bank.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Operation(OperationType operationType, UUID accountId, Amount amount, LocalDateTime date, BigDecimal balance) {

    public Operation {
        Objects.requireNonNull(operationType);
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(date);
        Objects.requireNonNull(balance);
    }
}
