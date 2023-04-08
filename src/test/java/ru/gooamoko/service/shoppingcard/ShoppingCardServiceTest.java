package ru.gooamoko.service.shoppingcard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gooamoko.model.CardItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Тест для сравнения скорости работы разных реализаций
 */
public class ShoppingCardServiceTest {
    private static final int USERS_COUNT = 1000;
    private static final int BROWSE_COUNT = 100;
    private static final int ITEMS_COUNT = 100;

    private final Map<String, ShoppingCardService> services = new HashMap<>();

    @BeforeEach
    public void setup() {
        services.put("ConcurrentMapShoppingCardService", new ConcurrentMapShoppingCardService());
        services.put("SynchronizedShoppingCardService", new SynchronizedShoppingCardService());
        services.put("LockShoppingCardService", new LockShoppingCardService());
    }


    @Test
    public void testPerformanceRegularUse() {
        for (int i = 1; i <= 3; i++) {
            Map<String, List<Long>> resultsMap = new TreeMap<>();
            for (Map.Entry<String, ShoppingCardService> entry : services.entrySet()) {
                List<Long> resultTimeList = new LinkedList<>();
                for (int threads = 4; threads < 41; threads+=2) {
                    System.out.println("Testing " + entry.getKey() + " on " + threads + " threads.");
                    long start = System.currentTimeMillis();

                    ShoppingCardService service = entry.getValue();
                    CountDownLatch testLatch = new CountDownLatch(USERS_COUNT);
                    ExecutorService threadPool = Executors.newFixedThreadPool(threads);
                    try {
                        for (int user = 0; user < USERS_COUNT; user++) {
                            UUID userId = UUID.randomUUID();
                            threadPool.submit(() -> {
                                Set<CardItem> items = createCardItems();
                                // Пользователь добавляет покупку, просматривает другие товары и удаляет покупку
                                for (CardItem item : items) {
                                    service.addItem(userId, item);
                                    for (int count = 0; count < BROWSE_COUNT; count++) {
                                        List<CardItem> cardItemsList = service.getItems(userId);
                                        if (cardItemsList == null || cardItemsList.size() != 1) {
                                            System.out.println("User " + userId + " missed his item!");
                                        }
                                    }
                                    service.removeItem(userId, item);
                                }

                                testLatch.countDown();
                            });
                        }

                        testLatch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        threadPool.shutdown();
                    }

                    long milllis = System.currentTimeMillis() - start;
                    resultTimeList.add(milllis);
                    System.out.println("Test for " + entry.getKey() + " on " + threads + " threads completed. It takes " + milllis + " ms.");
                }
                resultsMap.put(entry.getKey(), resultTimeList);
            }
            String tmpPath = System.getProperty("java.io.tmpdir");
            String fileName = tmpPath + String.format("/test_results-%d.csv", i);
            try {
                File tempFile = new File(fileName);
                System.out.println("Writing results into file " + fileName);
                try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                    for (Map.Entry<String, List<Long>> entry : resultsMap.entrySet()) {
                        String row = entry.getKey() + ";" + entry.getValue().stream().map(Object::toString).collect(Collectors.joining(";")) + System.lineSeparator();
                        fileOutputStream.write(row.getBytes(StandardCharsets.UTF_8));
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // Создаем список покупок для теста
    private Set<CardItem> createCardItems() {
        Set<CardItem> items = new HashSet<>();
        for (int i = 0; i < ITEMS_COUNT; i++) {
            items.add(new CardItem("item-" + i, BigDecimal.ONE, 1));
        }
        return items;
    }
}