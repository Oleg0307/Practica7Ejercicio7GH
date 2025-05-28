// Circle.java
package figuras;

import java.util.Scanner;

public class Circle {
    public static void calculateArea() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el radio del círculo: ");
        double radius = scanner.nextDouble();
        double area = Math.PI * radius * radius;
        System.out.println("El área del círculo es: " + area);
    }
}