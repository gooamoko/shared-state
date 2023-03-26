package ru.gooamoko.service;

import org.junit.jupiter.api.Test;
import ru.gooamoko.model.CardItem;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCardServiceTest {
    private static final int THREADS_COUNT = 10;
    private final ShoppingCardService service = new ShoppingCardService();

    @Test
    public void testAddItemsConcurrent() throws Exception {
        UUID userId = UUID.randomUUID();
        Set<CardItem> items = createCardItems();

        // Добавляем покупки в несколько потоков
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);
        try {
            CountDownLatch latch = new CountDownLatch(items.size());
            for (CardItem item : items) {
                threadPool.submit(() -> {
                    service.addItem(userId, item);
                    latch.countDown();
                });
            }
            latch.await();
        } finally {
            threadPool.shutdown();
        }

        List<CardItem> cardItems = service.getItems(userId); // Получаем содержимое корзины для пользователя

        // Проверяем, что в корзине есть всё, что мы добавляли
        assertEquals(items.size(), cardItems.size());
        for (CardItem item : cardItems) {
            assertTrue(items.contains(item));
        }
    }

    @Test
    public void testAddAndRemoveConcurrent() throws Exception {
        UUID userId = UUID.randomUUID();
        Set<CardItem> items = createCardItems();

        // Добавляем покупки в несколько потоков
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        try {
            CountDownLatch latch = new CountDownLatch(items.size() * 2);
            for (CardItem item : items) {
                CountDownLatch removeLatch = new CountDownLatch(1);
                //Добавляем покупку
                threadPool.submit(() -> {
                    service.addItem(userId, item);
                    removeLatch.countDown();
                    latch.countDown();
                });

                removeLatch.await();
                // Удаляем покупку
                threadPool.submit(() -> {
                    service.removeItem(userId, item);
                    latch.countDown();
                });
            }
            latch.await();
        } finally {
            threadPool.shutdown();
        }

        List<CardItem> cardItems = service.getItems(userId); // Получаем содержимое корзины для пользователя

        // Проверяем, что в корзине есть всё, что мы добавляли
        assertTrue(cardItems.size() < 1);
    }

    @Test
    public void testAddUsersAndItemsConcurrent() throws Exception {
        // Создаем мапу с данными для теста
        Map<UUID, CardItem> items = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            items.put(UUID.randomUUID(), new CardItem("item-" + i, BigDecimal.ONE, 1));
        }

        // Добавляем покупки в несколько потоков
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);
        try {
            CountDownLatch latch = new CountDownLatch(items.size());
            for (Map.Entry<UUID, CardItem> entry : items.entrySet()) {
                threadPool.submit(() -> {
                    service.addItem(entry.getKey(), entry.getValue());
                    latch.countDown();
                });
            }
            latch.await();
        } finally {
            threadPool.shutdown();
        }

        // Проверяем, что для каждого из пользователей содержимое корзины соответствует нашим ожиданиям
        for (Map.Entry<UUID, CardItem> entry : items.entrySet()) {
            List<CardItem> cardItems = service.getItems(entry.getKey());
            assertNotNull(cardItems);
            assertEquals(1, cardItems.size());
            assertTrue(cardItems.contains(entry.getValue()));
        }
    }

    // Создаем список покупок для теста
    private Set<CardItem> createCardItems() {
        Set<CardItem> items = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            items.add(new CardItem("item-" + i, BigDecimal.ONE, 1));
        }
        return items;
    }
}