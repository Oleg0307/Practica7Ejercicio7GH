package figuranew;

import java.util.Scanner;

public abstract class Figura {
    protected Scanner scanner = new Scanner(System.in);

    public abstract void calcularArea();

    public abstract void pedirDatos();

    protected void mostrarResultado(double area) {
        System.out.println("El área es: " + area);
    }

}
