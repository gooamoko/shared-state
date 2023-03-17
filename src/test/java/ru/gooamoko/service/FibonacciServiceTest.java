package ru.gooamoko.service;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FibonacciServiceTest {
    private static final int COUNT = 100;

    @Test
    public void testSerial() {
        FibonacciService service = new FibonacciService();
        Set<BigInteger> serialNumbers = new HashSet<>();
        for (int i = 0; i < COUNT; i++) {
            serialNumbers.add(service.getNext());
        }
        assertEquals(COUNT, serialNumbers.size());
    }


    @Test
    public void testConcurrent() throws Exception {
        FibonacciService service = new FibonacciService();
        Set<BigInteger> serialNumbers = new HashSet<>();
        for (int i = 0; i < COUNT; i++) {
            serialNumbers.add(service.getNext());
        }
        assertEquals(COUNT, serialNumbers.size());

        service = new FibonacciService();
        int threads = 10;
        Set<BigInteger> parallelNumbers = new HashSet<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        try {
            ExecutorCompletionService<BigInteger> executorService = new ExecutorCompletionService<>(threadPool);

            // Сабмитим задачи
            for (int i = 0; i < COUNT; i++) {
                executorService.submit(service::getNext);
            }

            // Получаем результаты
            for (int i = 0; i < COUNT; i++) {
                Future<BigInteger> future = executorService.take();
                parallelNumbers.add(future.get());
            }

            assertEquals(COUNT, parallelNumbers.size());
            for (BigInteger number : serialNumbers) {
                assertTrue(parallelNumbers.contains(number), "No nuumber: " + number.toString());
            }
        } finally {
            threadPool.shutdown();
        }
    }
}