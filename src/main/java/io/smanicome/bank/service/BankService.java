package io.smanicome.bank.service;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;
import io.smanicome.bank.exceptions.NotEnoughBalanceException;

import java.util.UUID;

public interface BankService {
    Operation deposit(UUID accountId, Amount amount);
    Operation withdraw(UUID accountId, Amount amount) throws NotEnoughBalanceException;
    void printAccountStatement(UUID accountId);
}
