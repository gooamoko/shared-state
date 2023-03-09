package ru.gooamoko;

import ru.gooamoko.service.SquareEquationService;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random rnd = new Random(System.currentTimeMillis());
        SquareEquationService service = new SquareEquationService();
        for (int i = 2; i < 100; i++) {
            int b = i * 3, c = rnd.nextInt(10) + 2;
            System.out.printf("Уравнение %dx^2 + %dx + %d = 0%n", i, b, c);
            service.create(i, b, c);
            System.out.println("Корни уравнения: " + printRoots(service.getRoots()));
        }
    }

    private static String printRoots(double[] roots) {
        if (roots == null || roots.length == 0) {
            return "Корней нет.";
        } else if (roots.length == 1) {
            return String.format("Единственный корень = %f", roots[0]);
        } else {
            return String.format("Корень 1 = %f, Корень 2 = %f", roots[0], roots[1]);
        }
    }
}