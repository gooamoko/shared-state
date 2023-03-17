package ru.gooamoko.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Сервис для генерации уникальных номеров
 */
public class NumberGenerationService {
    private final AtomicLong number;

    public NumberGenerationService(long initialValue) {
        this.number = new AtomicLong(initialValue);
    }

    /**
     * Возвращает новый уникальный номер.
     * @return номер
     */
    public long getNextNumber() {
        return number.incrementAndGet();
    }
}
