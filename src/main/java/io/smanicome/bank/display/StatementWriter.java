package io.smanicome.bank.display;

import io.smanicome.bank.account.Statement;

public interface StatementWriter {
    void write(Statement statement);
}
