package io.smanicome.bank.utils;

import io.smanicome.bank.account.Amount;
import io.smanicome.bank.account.Operation;
import io.smanicome.bank.account.OperationType;
import io.smanicome.bank.account.Statement;
import io.smanicome.bank.exceptions.NegativeAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabularStatementFormatterShould {
    @DisplayName("should format statement with no operation")
    @Test
    void formatStatementWithNoOperation() {
        final var formatter = new TabularStatementFormatter();
        final var date = LocalDateTime.now();
        final var accountId = UUID.randomUUID();
        final var operations = List.<Operation>of();
        final var statement = new Statement(
            accountId,
            operations,
            BigDecimal.valueOf(6000).setScale(2, RoundingMode.HALF_EVEN),
            date
        );

        final var formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        final var expectedLines = List.of(
            "| ----------------------------------------------------------- |",
            "| STATEMENT OF ACCOUNT N°" + accountId + " |",
            "|                     " + formattedDate + "                     |",
            "|                       BALANCE 6000.00                       |",
            "| ----------------------------------------------------------- |"
        );

        final var lines = formatter.format(statement);

        assertEquals(expectedLines, lines);
    }

    @DisplayName("should format statement with one operation")
    @Test
    void formatStatementWithOneOperation() throws NegativeAmountException {
        final var formatter = new TabularStatementFormatter();

        final var accountId = UUID.randomUUID();
        final var operationDate = LocalDateTime.now();
        final var statementDate = operationDate.plusMinutes(10);

        final var formattedOperationDate = operationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        final var formattedStatementDate = statementDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        final var operations = List.of(
            new Operation(
                OperationType.DEPOSIT,
                accountId,
                Amount.of(BigDecimal.valueOf(2000)),
                operationDate,
                BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_EVEN)
            )
        );

        final var statement = new Statement(
            accountId,
            operations,
            BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_EVEN),
            statementDate
        );

        final var expectedLines = List.of(
                "| ----------------------------------------------------------- |",
                "| STATEMENT OF ACCOUNT N°" + accountId + " |",
                "|                     " + formattedStatementDate + "                     |",
                "|                       BALANCE 2000.00                       |",
                "| ----------------------------------------------------------- |",
                "| TYPE       |         DATE        |                   AMOUNT |",
                "| ----------------------------------------------------------- |",
                "| DEPOSIT    | " + formattedOperationDate + " |                  2000.00 |",
                "| ----------------------------------------------------------- |"
        );

        final var lines = formatter.format(statement);

        assertEquals(expectedLines, lines);
    }

    @DisplayName("should format statement with multiple operations")
    @Test
    void formatStatementWithMultipleOperations() throws NegativeAmountException {
        final var formatter = new TabularStatementFormatter();

        final var accountId = UUID.randomUUID();
        final var operationDate1 = LocalDateTime.now();
        final var operationDate2 = operationDate1.plusMinutes(10);
        final var operationDate3 = operationDate2.plusMinutes(10);
        final var statementDate = operationDate3.plusMinutes(10);

        final var formattedOperationDate1 = operationDate1.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        final var formattedOperationDate2 = operationDate2.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        final var formattedOperationDate3 = operationDate3.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        final var formattedStatementDate = statementDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        final var operations = List.of(
            new Operation(
                    OperationType.DEPOSIT,
                    accountId,
                    Amount.of(BigDecimal.valueOf(2000)),
                    operationDate1,
                    BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_EVEN)
            ),
            new Operation(
                    OperationType.DEPOSIT,
                    accountId,
                    Amount.of(BigDecimal.valueOf(5000)),
                    operationDate2,
                    BigDecimal.valueOf(7000).setScale(2, RoundingMode.HALF_EVEN)
            ),
            new Operation(
                    OperationType.WITHDRAWAL,
                    accountId,
                    Amount.of(BigDecimal.valueOf(1000)),
                    operationDate3,
                    BigDecimal.valueOf(6000).setScale(2, RoundingMode.HALF_EVEN)
            )
        );

        final var statement = new Statement(
            accountId,
            operations,
            BigDecimal.valueOf(6000).setScale(2, RoundingMode.HALF_EVEN),
            statementDate
        );

        final var expectedLines = List.of(
            "| ----------------------------------------------------------- |",
            "| STATEMENT OF ACCOUNT N°" + accountId + " |",
            "|                     " + formattedStatementDate + "                     |",
            "|                       BALANCE 6000.00                       |",
            "| ----------------------------------------------------------- |",
            "| TYPE       |         DATE        |                   AMOUNT |",
            "| ----------------------------------------------------------- |",
            "| WITHDRAWAL | " + formattedOperationDate3 + " |                  1000.00 |",
            "| DEPOSIT    | " + formattedOperationDate2 + " |                  5000.00 |",
            "| DEPOSIT    | " + formattedOperationDate1 + " |                  2000.00 |",
            "| ----------------------------------------------------------- |"
        );

        final var lines = formatter.format(statement);

        assertEquals(expectedLines, lines);
    }
}