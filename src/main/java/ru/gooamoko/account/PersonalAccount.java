package ru.gooamoko.account;

import java.math.BigDecimal;

public interface PersonalAccount {

    /**
     * Зачисление средств на счет.
     */
    BigDecimal add(BigDecimal amount);

    /**
     * Списание средств со счета.
     */
    BigDecimal subtract(BigDecimal amount);

    /**
     * Переводим с одного счета на другой.
     */
    BigDecimal transferFrom(PersonalAccount from, BigDecimal amount);

    /**
     * Возвращает количество средств на счете.
     *
     * @return количество средств.
     */
    BigDecimal getFund();
}
