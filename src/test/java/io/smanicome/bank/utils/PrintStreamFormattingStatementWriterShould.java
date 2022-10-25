package io.smanicome.bank.utils;

import io.smanicome.bank.account.Statement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrintStreamFormattingStatementWriterShould {
    @Mock
    private StatementFormatter formatter;

    @Mock
    private PrintStream out;

    @InjectMocks
    private PrintStreamFormattingStatementWriter writer;

    @DisplayName("should print nothing but header")
    @Test
    void formatAndWriteNothingButHeader() {
        final var statement = new Statement(UUID.randomUUID(), List.of(), BigDecimal.ZERO, LocalDateTime.now());

        when(formatter.format(any())).thenReturn(List.of());

        writer.write(statement);

        verify(formatter).format(statement);
        verifyNoMoreInteractions(formatter);
        verifyNoInteractions(out);
    }

    @DisplayName("should print formatted hello world")
    @Test
    void formatAndWriteHelloWorld() {
        final var statement = new Statement(UUID.randomUUID(), List.of(), BigDecimal.ZERO, LocalDateTime.now());

        when(formatter.format(any())).thenReturn(List.of("hello world"));

        writer.write(statement);

        final var orderVerifier = inOrder(formatter, out);
        orderVerifier.verify(formatter).format(statement);
        orderVerifier.verify(out).println("hello world");
        orderVerifier.verifyNoMoreInteractions();
    }

    @DisplayName("should print formatted hello world in two lines")
    @Test
    void formatAndWriteHelloWorldInTwoLines() {
        final var statement = new Statement(UUID.randomUUID(), List.of(), BigDecimal.ZERO, LocalDateTime.now());

        when(formatter.format(any())).thenReturn(List.of("hello", "world"));

        writer.write(statement);

        final var orderVerifier = inOrder(formatter, out);
        orderVerifier.verify(formatter).format(statement);
        orderVerifier.verify(out).println("hello");
        orderVerifier.verify(out).println("world");
        orderVerifier.verifyNoMoreInteractions();
    }
}