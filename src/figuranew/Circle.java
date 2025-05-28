package figuranew;

public class Circle extends Figura {
    private double radio;

    @Override
    public void calcularArea() {
        double area = Math.PI * radio * radio;
        mostrarResultado(area);
    }

    @Override
    public void pedirDatos() {
        System.out.print("Ingrese el radio del c�rculo: ");
        radio = scanner.nextDouble();
    }

}
