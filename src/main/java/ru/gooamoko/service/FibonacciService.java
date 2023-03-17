package ru.gooamoko.service;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FibonacciService {
    private final AtomicReference<State> atomicState = new AtomicReference<>(new State(BigInteger.ONE, BigInteger.ONE));

    public BigInteger getNext() {
        BigInteger value;
        while (true) {
            State state = atomicState.get();
            value = state.prev.add(state.last);
            if (atomicState.compareAndSet(state, new State(state.last, value))) {
                break;
            }
        }
        return value;
    }

    private static class State {
        final BigInteger last;
        final BigInteger prev;

        State(BigInteger prev, BigInteger last) {
            this.last = last;
            this.prev = prev;
        }
    }
}
