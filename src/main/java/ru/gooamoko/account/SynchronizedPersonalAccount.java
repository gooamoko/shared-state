package ru.gooamoko.account;

import java.math.BigDecimal;

/**
 * Класс упрощенного лицевого счета.
 */
public class SynchronizedPersonalAccount implements PersonalAccount {
    private BigDecimal fund; // Сумма на счете

    public SynchronizedPersonalAccount(BigDecimal fund) {
        this.fund = fund;
    }

    public SynchronizedPersonalAccount() {
        this.fund = BigDecimal.ZERO;
    }

    @Override
    public synchronized BigDecimal add(BigDecimal amount) {
        fund = fund.add(amount);
        return fund;
    }

    @Override
    public synchronized BigDecimal subtract(BigDecimal amount) {
        if (fund.compareTo(amount) < 0) {
            throw new RuntimeException("Fund to low to subtract " + amount.toString() + ".");
        }
        fund = fund.subtract(amount);
        return fund;
    }

    @Override
    public BigDecimal transferFrom(PersonalAccount from, BigDecimal amount) {
        from.subtract(amount);
        return add(amount);
    }

    @Override
    public synchronized BigDecimal getFund() {
        return fund;
    }
}
