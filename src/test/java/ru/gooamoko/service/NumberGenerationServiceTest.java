package ru.gooamoko.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberGenerationServiceTest {
    private static final int COUNT = 1000;
    private final NumberGenerationService service = new NumberGenerationService(1024);

    @Test
    public void testSerial() {
        Set<Long> uniqueNumbers = new HashSet<>();
        for (int i = 0; i < COUNT; i++) {
            uniqueNumbers.add(service.getNextNumber());
        }
        assertEquals(COUNT, uniqueNumbers.size());
    }

    @Test
    public void testConcurrent() throws Exception {
        int threads = 10;
        Set<Long> uniqueNumbers = new HashSet<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        try {
            ExecutorCompletionService<Long> executorService = new ExecutorCompletionService<>(threadPool);

            // Сабмитим задачи
            for (int i = 0; i < COUNT; i++) {
                executorService.submit(new Worker(service));
            }

            // Получаем результаты
            for (int i = 0; i < COUNT; i++) {
                Future<Long> future = executorService.take();
                uniqueNumbers.add(future.get());
            }

            assertEquals(COUNT, uniqueNumbers.size());
        } finally {
            threadPool.shutdown();
        }
    }

    private static class Worker implements Callable<Long> {
        private final NumberGenerationService service;

        public Worker(NumberGenerationService service) {
            this.service = service;
        }

        @Override
        public Long call() {
            return service.getNextNumber();
        }
    }
}