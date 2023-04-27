package ru.gooamoko;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест для проверки потокобезопасности списков.
 */
public class ConcurrentListTest {
    private static final int THREADS = 10;
    private ExecutorService pool;

    @BeforeEach
    public void prepare() {
        pool = Executors.newFixedThreadPool(THREADS);
    }

    @AfterEach
    public void cleanup() {
        pool.shutdown();
    }

//    @Test
//    public void testArrayList() {
//        doTest(new ArrayList<>());
//    }
//
//    @Test
//    public void testLinkedList() {
//        doTest(new LinkedList<>());
//    }
//
//    @Test
//    public void testCopyOnWriteArrayList() {
//        doTest(new CopyOnWriteArrayList<>());
//    }
//
//    @Test
//    public void testSynchronizedArrayList() {
//        doTest(Collections.synchronizedList(new ArrayList<>()));
//    }
//
//    @Test
//    public void testSynchronizedLinkedList() {
//        doTest(Collections.synchronizedList(new LinkedList<>()));
//    }

    @Test
    public void testAll() {
        Map<String, List<Integer>> implementations = new TreeMap<>();
        implementations.put("ArrayList", new ArrayList<>());
        implementations.put("LinkedList", new LinkedList<>());
        implementations.put("CopyOnWriteArrayList", new CopyOnWriteArrayList<>());
        implementations.put("SynchronizedArrayList", Collections.synchronizedList(new ArrayList<>()));
        implementations.put("SynchronizedLinkedList", Collections.synchronizedList(new LinkedList<>()));

        for (int i = 1; i <= 10; i++) {
            List<String> errors = new LinkedList<>();
            for (Map.Entry<String, List<Integer>> entry : implementations.entrySet()) {
                String name = entry.getKey();
                try {
                    List<Integer> list = entry.getValue();
                    list.clear();
                    doTest(list);
                } catch (Throwable t) {
                    String message = String.format("%s error on pass %d: %s (%s)", name, i, t.getClass().getName(), t.getMessage());
                    errors.add(message);
                    System.out.println(message);
                }
            }
            assertTrue(errors.isEmpty());
        }
    }


    private void doTest(List<Integer> list) {
        CyclicBarrier barrier = new CyclicBarrier(THREADS);
        int tasks = THREADS * 12;
        final AtomicInteger value = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(tasks);

        try {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    try {
                        barrier.await();
                        list.add(value.incrementAndGet());
                    } catch (BrokenBarrierException | InterruptedException e) {
                        System.out.println("Exception " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertFalse(list.isEmpty());
        assertEquals(tasks, list.size(), "Размер списка отличается от ожидаемого.");
        for (int i = 1; i <= tasks; i++) {
            assertTrue(list.contains(i), "Список не содержит значение " + i);
        }
    }
}
