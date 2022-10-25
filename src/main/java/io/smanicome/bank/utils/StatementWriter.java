package io.smanicome.bank.utils;

import io.smanicome.bank.account.Statement;

import java.io.OutputStream;

public interface StatementWriter {
    void write(Statement statement);
}
