// Rectangle.java
package figuras;

import java.util.Scanner;

public class Rectangle {
    public static void calculateArea() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la base del rectángulo: ");
        double base = scanner.nextDouble();
        System.out.print("Ingrese la altura del rectángulo: ");
        double height = scanner.nextDouble();
        double area = base * height;
        System.out.println("El área del rectángulo es: " + area);
    }
}