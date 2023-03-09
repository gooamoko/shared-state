package ru.gooamoko;

import ru.gooamoko.service.SquareEquationService;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Worker implements Callable<String> {
    private final int a;
    private final int b;
    private final int c;
    private final SquareEquationService service;

    private CountDownLatch latch;


    public Worker(int a, int b, int c, SquareEquationService service, CountDownLatch latch) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.service = service;
        this.latch = latch;
    }

    public Worker(int a, int b, int c, SquareEquationService service) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.service = service;
    }

    @Override
    public String call() throws InterruptedException {
        if (latch != null) {
            latch.await();
        }
        service.create(a, b, c);
        return String.format("A [%d = %d], B [%d = %d], C [%d = %d]. Корни: %s",
                a, service.getA(),
                b, service.getB(),
                c, service.getC(),
                printRoots(service.getRoots()));
    }

    private String printRoots(double[] roots) {
        if (roots == null || roots.length == 0) {
            return "нет.";
        } else if (roots.length == 1) {
            return String.format("один корень = %f", roots[0]);
        } else {
            return String.format("первый = %f, второй = %f", roots[0], roots[1]);
        }
    }
}
