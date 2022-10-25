package io.smanicome.bank.service;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;

import java.util.UUID;

public interface BankService {
    Operation deposit(UUID accountId, Amount amount);
}
