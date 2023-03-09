package ru.gooamoko.service;

/**
 * Сервис, который находит корни квадратного уравнения вида Ax^2 + Bx + C = 0.
 */
public class SquareEquationService {
    private int a; // Коэфициент A
    private int b; // Коэфициент B
    private int c; // Коэфициент C
    private int d; // Дискриминант

    public void create(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = b * b - 4 * a * c;
    }

    public boolean hasRoots() {
        return d < 0;
    }

    public double[] getRoots() {
        if (d < 0) {
            return new double[0];
        } else if (d == 0) {
            return new double[] {(double)-b / (2 * a)};
        } else {
            return new double[] {
                    (-b + Math.sqrt(d)) / (2 * a),
                    (-b - Math.sqrt(d)) / (2 * a)
            };
        }
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }
}
