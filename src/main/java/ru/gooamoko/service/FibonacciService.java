package ru.gooamoko.service;

import java.math.BigInteger;

public class FibonacciService {
    private BigInteger prev = new BigInteger("0");
    private BigInteger last = new BigInteger("1");

    public BigInteger getNext() {
        BigInteger next = last.add(prev);
            prev = last;
            last = next;

        return new BigInteger(next.toByteArray());
    }
}
