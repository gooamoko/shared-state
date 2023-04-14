package ru.gooamoko.account;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс упрощенного лицевого счета.
 */
public class LockPersonalAccount implements PersonalAccount {
    private final Lock accountLock = new ReentrantLock();
    private BigDecimal fund; // Сумма на счете

    public LockPersonalAccount(BigDecimal fund) {
        this.fund = fund;
    }

    public LockPersonalAccount() {
        this.fund = BigDecimal.ZERO;
    }

    @Override
    public BigDecimal add(BigDecimal amount) {
        try {
            accountLock.lock();
            fund = fund.add(amount);
        } finally {
            accountLock.unlock();
        }

        return fund;
    }

    @Override
    public BigDecimal subtract(BigDecimal amount) {
        try {
            accountLock.lock();
            if (fund.compareTo(amount) < 0) {
                throw new RuntimeException("Fund to low to subtract " + amount.toString() + ".");
            }
            fund = fund.subtract(amount);
        } finally {
            accountLock.unlock();
        }
        return fund;
    }

    @Override
    public BigDecimal transferFrom(PersonalAccount from, BigDecimal amount) {
        from.subtract(amount);
        return add(amount);
    }

    @Override
    public BigDecimal getFund() {
        return fund;
    }
}
