package ru.gooamoko.account;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Класс упрощенного лицевого счета.
 */
public class AtomicPersonalAccount implements PersonalAccount {
    private final AtomicReference<BigDecimal> fundReference; // Ссылка на сумму на счете

    public AtomicPersonalAccount(BigDecimal fund) {
        this.fundReference = new AtomicReference<>(fund);
    }

    public AtomicPersonalAccount() {
        this.fundReference = new AtomicReference<>(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal add(BigDecimal amount) {
        while (true) {
            BigDecimal oldValue = fundReference.get();
            BigDecimal newValue = oldValue.add(amount);
            if (fundReference.compareAndSet(oldValue, newValue)) {
                return newValue;
            }
        }
    }

    @Override
    public BigDecimal subtract(BigDecimal amount) {
        while (true) {
            BigDecimal oldValue = fundReference.get();
            if (oldValue.compareTo(amount) < 0) {
                throw new RuntimeException("Fund to low to subtract " + amount.toString() + ".");
            }
            BigDecimal newValue = oldValue.subtract(amount);
            if (fundReference.compareAndSet(oldValue, newValue)) {
                return newValue;
            }
        }
    }

    @Override
    public BigDecimal transferFrom(PersonalAccount from, BigDecimal amount) {
        from.subtract(amount);
        return add(amount);
    }

    @Override
    public BigDecimal getFund() {
        return fundReference.get();
    }
}
