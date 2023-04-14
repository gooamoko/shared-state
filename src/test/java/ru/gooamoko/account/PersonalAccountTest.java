package ru.gooamoko.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Тест для замера скорости реализаций PersonalAccount
 */
class PersonalAccountTest {
    private final static int TRANSACTIONS = 5000;
    private final static int ACCOUNTS = 500;

    private final Map<String, PersonalAccount> implementations = new TreeMap<>();
    private final PersonalAccount[] accounts = new PersonalAccount[ACCOUNTS];

    @BeforeEach
    public void setup() {
        implementations.put("ru.gooamoko.account.AtomicPersonalAccount", new AtomicPersonalAccount());
        implementations.put("ru.gooamoko.account.LockPersonalAccount", new LockPersonalAccount());
    }

    @Test
    public void testPerformance() throws Exception {
        for (int i = 1; i < 3; i++) {
            System.out.printf("Pass %d.\n", i);
            for (Map.Entry<String, PersonalAccount> entry : implementations.entrySet()) {
                System.out.printf("Testing %s...\n", entry.getKey());
                for (int a = 0; a < ACCOUNTS; a++) {
                    Class<?> accountClass = Class.forName(entry.getKey());
                    Constructor<?>[] constructors = accountClass.getDeclaredConstructors();
                    for (Constructor<?> constructor : constructors) {
                        if (constructor.getParameterCount() > 0) {
                            accounts[a] = (PersonalAccount) constructor.newInstance(new BigDecimal("100.00"));
                        }
                    }
                }

                for (int threads = 4; threads <= 40; threads += 2) {
                    long startTime = System.currentTimeMillis();
                    CountDownLatch latch = new CountDownLatch(TRANSACTIONS);
                    ExecutorService threadPool = Executors.newFixedThreadPool(threads);
                    try {
                        Random random = new Random();
                        for (int transaction = 0; transaction < TRANSACTIONS; transaction++) {
                            threadPool.submit(() -> {
                                for (int t = 0; t < TRANSACTIONS; t++) {
                                    BigDecimal amount = new BigDecimal("10.00");
                                    int firstIndex = random.nextInt(ACCOUNTS);
                                    int secondIndex = random.nextInt(ACCOUNTS);
                                    PersonalAccount firstAccount = accounts[firstIndex];
                                    PersonalAccount secondAccount = accounts[secondIndex];

                                    firstAccount.transferFrom(secondAccount, amount);
                                    secondAccount.transferFrom(firstAccount, amount);
                                }
                                latch.countDown();
                            });
                        }
                        latch.await();
                        int count = 0;
                        for (int a = 0; a < ACCOUNTS; a++) {
                            if (new BigDecimal("100.00").compareTo(accounts[a].getFund()) != 0) {
                                count += 1;
                            }
                        }
                        long time = System.currentTimeMillis() - startTime;
                        System.out.printf("Testing %s with %d threads takes %d ms. Suspected accounts: %d\n", entry.getKey(), threads, time, count);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        threadPool.shutdown();
                    }
                }
            }
        }
    }
}