package ru.gooamoko.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class AtomicPersonalAccountTest {

    @Test
    public void testSubtractWithLowFunds() {
        PersonalAccount testAccount = new AtomicPersonalAccount(new BigDecimal("10.00"));
        assertThrowsExactly(RuntimeException.class, () -> testAccount.subtract(new BigDecimal("20.00")));
    }

    @Test
    public void testAddConcurrent() throws InterruptedException {
        PersonalAccount testAccount = new AtomicPersonalAccount();

        int count = 10;
        CountDownLatch latch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                testAccount.add(new BigDecimal("100.00"));
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(new BigDecimal("1000.00"), testAccount.getFund());
    }

    @Test
    public void testAddSerial() {
        PersonalAccount testAccount = new AtomicPersonalAccount();

        int count = 10;
        for (int i = 0; i < count; i++) {
            testAccount.add(new BigDecimal("100.00"));
        }

        assertEquals(new BigDecimal("1000.00"), testAccount.getFund());
    }

    @Test
    public void testSubtractConcurrent() throws InterruptedException {
        PersonalAccount testAccount = new AtomicPersonalAccount(new BigDecimal("1000.00"));

        int count = 10;
        CountDownLatch latch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                testAccount.subtract(new BigDecimal("100.00"));
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(new BigDecimal("0.00"), testAccount.getFund());
    }

    @Test
    public void testSubtractSerial() {
        PersonalAccount testAccount = new AtomicPersonalAccount(new BigDecimal("1000.00"));

        int count = 10;
        for (int i = 0; i < count; i++) {
            testAccount.subtract(new BigDecimal("100.00"));
        }

        assertEquals(new BigDecimal("0.00"), testAccount.getFund());
    }

    @Test
    public void testTransferConcurrent() throws InterruptedException {
        PersonalAccount firstAccount = new AtomicPersonalAccount(new BigDecimal("1000.00"));
        PersonalAccount secondAccount = new AtomicPersonalAccount(new BigDecimal("1000.00"));

        int count = 10;
        CountDownLatch latch = new CountDownLatch(count * 2);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                firstAccount.transferFrom(secondAccount, new BigDecimal("100.00"));
                latch.countDown();
            });
            executorService.submit(() -> {
                secondAccount.transferFrom(firstAccount, new BigDecimal("100.00"));
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(new BigDecimal("1000.00"), firstAccount.getFund());
        assertEquals(new BigDecimal("1000.00"), secondAccount.getFund());
    }
}