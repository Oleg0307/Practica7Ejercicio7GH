package figuranew;

public class Rectangle extends Figura {
    private double lado;

    @Override
    public void calcularArea() {
        double area = lado * lado;
        mostrarResultado(area);
    }

    @Override
    public void pedirDatos() {
        System.out.print("Ingrese el lado del cuadrado: ");
        lado = scanner.nextDouble();
    }


}
