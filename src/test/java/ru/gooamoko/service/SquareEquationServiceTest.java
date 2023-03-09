package ru.gooamoko.service;

import org.junit.jupiter.api.Test;
import ru.gooamoko.Worker;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SquareEquationServiceTest {

    @Test
    public void testSerial() throws Exception {
        int maxValue = 20;
        SquareEquationService service = new SquareEquationService();
        List<String> resultList = new LinkedList<>();
        for (int i = 2; i < maxValue; i++) {
            Worker worker = new Worker(i, i * 3, i + 1, service);
            String result = worker.call();
            System.out.println(result);
            resultList.add(result);
        }

        assertNotNull(resultList);
        assertEquals(maxValue - 2, resultList.size());
    }

    @Test
    public void testParallel() {
        int threads = 8;
        int maxValue = 20;
        SquareEquationService service = new SquareEquationService();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<String> resultList = new LinkedList<>();
        CountDownLatch latch = new CountDownLatch(threads);
        try {
            ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(executorService);
            // Отправляем на выполнение
            for (int i = 2; i < maxValue; i++) {
                Worker worker = new Worker(i, i * 3, i + 1, service, latch);
                completionService.submit(worker);
                if (latch.getCount() > 0) {
                    latch.countDown();
                }
            }

            // Получаем результаты
            for (int i = 2; i < maxValue; i++) {
                Future<String> futureResult = completionService.take();
                String result = futureResult.get();
                System.out.println(result);
                resultList.add(result);
            }
        } catch (Throwable e) {
            System.out.println("Interrupted.");
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        assertNotNull(resultList);
        assertEquals(maxValue - 2, resultList.size());
    }
}