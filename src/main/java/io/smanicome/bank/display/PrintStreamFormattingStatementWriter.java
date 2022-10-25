package io.smanicome.bank.display;

import io.smanicome.bank.account.Statement;

import java.io.PrintStream;
import java.util.List;

public class PrintStreamFormattingStatementWriter implements StatementWriter {

    private final StatementFormatter formatter;
    private final PrintStream out;

    public PrintStreamFormattingStatementWriter(StatementFormatter formatter, PrintStream out) {
        this.formatter = formatter;
        this.out = out;
    }

    @Override
    public void write(Statement statement) {
        List<String> lines = formatter.format(statement);
        for (String line : lines) {
            out.println(line);
        }
    }
}
