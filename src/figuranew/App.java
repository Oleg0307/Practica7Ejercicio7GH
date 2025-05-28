package figuranew;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            System.out.println("Calculadora de �rea - Seleccione una opci�n:");
            System.out.println("1. Circulo");
            System.out.println("2. Cuadrado");
            System.out.println("3. Triangulo");
            System.out.println("4. Rectangulo");
            System.out.println("5. Pentagono");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            option = scanner.nextInt();
            
            Figura figura = null;
            
            switch (option) {
                case 1:
                    figura = new Circle();
                    break;
                case 2:
                    figura = new Square();
                    break;
                case 3:
                    figura = new Triangle();
                    break;
                case 4:
                    figura = new Rectangle();
                    break;
                case 5:
                    figura = new Pentagon();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
            
            if (figura != null) {
                figura.pedirDatos();
                figura.calcularArea();
            }
            
        } while (option != 0);
        scanner.close();
    }
}
