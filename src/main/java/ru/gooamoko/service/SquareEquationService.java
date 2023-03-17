package ru.gooamoko.service;

/**
 * Сервис, который находит корни квадратного уравнения вида Ax^2 + Bx + C = 0.
 */
public class SquareEquationService {

    public double[] getRoots(int a, int b, int c) {
        int d = b * b - 4 * a * c;
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
}
