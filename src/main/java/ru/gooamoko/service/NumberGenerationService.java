package ru.gooamoko.service;

/**
 * Сервис для генерации уникальных номеров
 */
public class NumberGenerationService {
    private long number;

    public NumberGenerationService(long initialValue) {
        this.number = initialValue;
    }

    /**
     * Возвращает новый уникальный номер.
     * @return номер
     */
    public long getNextNumber() {
        return ++number;
    }
}
