package ru.gooamoko.service;

import org.junit.jupiter.api.Test;
import ru.gooamoko.model.CardItem;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCardServiceTest {
    private static final int THREADS_COUNT = 10;
    private final ShoppingCardService service = new ShoppingCardService();

    @Test
    public void testAddItemsConcurrent() throws Exception {
        UUID userId = UUID.randomUUID();
        Set<CardItem> items = createCardItems();

        // Добавляем пкупки в несколько потоков
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);
        try {
            for (CardItem item : items) {
                threadPool.submit(() -> service.addItem(userId, item));
            }
        } finally {
            threadPool.shutdown();
        }

        // Ждем завершения добавления
        Thread.sleep(5000);
        List<CardItem> cardItems = service.getItems(userId);
        assertEquals(items.size(), cardItems.size());
        for (CardItem item : cardItems) {
            assertTrue(items.contains(item));
        }
    }

    private Set<CardItem> createCardItems() {
        Set<CardItem> items = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            items.add(new CardItem("item-" + i, BigDecimal.ONE, 1));
        }
        return items;
    }
}