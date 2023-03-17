package ru.gooamoko.service;

import java.math.BigInteger;

public class FibonacciService {
    private BigInteger prev = BigInteger.ONE;
    private BigInteger last = BigInteger.ONE;

    public BigInteger getNext() {
        BigInteger next = last.add(prev);
            prev = last;
            last = next;

        return new BigInteger(next.toByteArray());
    }
}
