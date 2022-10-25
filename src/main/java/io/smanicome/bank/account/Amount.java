package io.smanicome.bank.account;

import io.smanicome.bank.exceptions.NegativeAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Amount {
    private final BigDecimal value;

    private Amount(BigDecimal value) {
        this.value = value;
    }

    public static Amount of(BigDecimal value) throws NegativeAmountException {
        if(value.doubleValue() < 0) throw new NegativeAmountException();
        return new Amount(value.setScale(2, RoundingMode.HALF_EVEN));
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return value.equals(amount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
