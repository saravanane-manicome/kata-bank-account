package io.smanicome.bank.account;

import io.smanicome.bank.exceptions.NegativeAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class AmountShould {

    @DisplayName("should get number passed as parameter")
    @Test
    void getValuePassedAsParameterWithScale() throws NegativeAmountException {
        assertEquals(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_EVEN), Amount.of(BigDecimal.valueOf(10)).getValue());
    }

    @DisplayName("should throw when given a negative number")
    @Test
    void throwWhenGivenNegativeNumber() {
        assertThrows(NegativeAmountException.class, () -> Amount.of(BigDecimal.valueOf(-1)));
    }
}