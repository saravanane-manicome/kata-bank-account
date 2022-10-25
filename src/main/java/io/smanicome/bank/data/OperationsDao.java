package io.smanicome.bank.data;

import io.smanicome.bank.account.Operation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationsDao {
    Operation save(Operation operation);
    Optional<Operation> findLastByAccountId(UUID accountId);
    List<Operation> findByAccountId(UUID accountId);
}
