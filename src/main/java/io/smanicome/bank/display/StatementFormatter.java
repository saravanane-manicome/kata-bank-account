package io.smanicome.bank.display;

import io.smanicome.bank.account.Statement;

import java.util.List;

public interface StatementFormatter {
    List<String> format(Statement statement);
}
