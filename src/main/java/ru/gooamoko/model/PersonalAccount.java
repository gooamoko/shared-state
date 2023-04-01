package ru.gooamoko.model;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс упрощенного лицевого счета.
 */
public class PersonalAccount {
    private final Lock accountLock = new ReentrantLock();
    private BigDecimal fund; // Сумма на счете

    public PersonalAccount(BigDecimal fund) {
        this.fund = fund;
    }

    public PersonalAccount() {
        this.fund = BigDecimal.ZERO;
    }

    /**
     * Зачисление средств на счет.
     */
    public BigDecimal add(BigDecimal amount) {
        try {
            accountLock.lock();
            fund = fund.add(amount);
        } finally {
            accountLock.unlock();
        }

        return fund;
    }

    /**
     * Списание средств со счета.
     */
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

    /**
     * Переводим с одного счета на другой.
     */
    public BigDecimal transferFrom(PersonalAccount from, BigDecimal amount) {
        try {
            from.subtract(amount);
            accountLock.lock();
            fund = fund.add(amount);
        } finally {
            accountLock.unlock();
        }
        return fund;
    }

    /**
     * Возвращает количество средств на счете.
     *
     * @return количество средств.
     */
    public BigDecimal getFund() {
        return fund;
    }
}
