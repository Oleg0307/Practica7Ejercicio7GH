package figuranew;

public class Square extends Figura {
    private double base;
    private double altura;

    @Override
    public void calcularArea() {
        double area = base * altura;
        mostrarResultado(area);
    }

    @Override
    public void pedirDatos() {
        System.out.print("Ingrese el lado del cuadrado: ");
        base = altura = scanner.nextDouble();
    }

}
