package io.smanicome.bank.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Statement(UUID accountId, List<Operation> operations, BigDecimal balance, LocalDateTime date) {
    public Statement {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(operations);
        Objects.requireNonNull(balance);
        Objects.requireNonNull(date);
    }
}
