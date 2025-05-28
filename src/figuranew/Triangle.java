package figuranew;

public class Triangle extends Figura {
    private double base;
    private double altura;

    @Override
    public void calcularArea() {
        double area = (base * altura) / 2;
        mostrarResultado(area);
    }

    @Override
    public void pedirDatos() {
        System.out.print("Ingrese la base del tri�ngulo: ");
        base = scanner.nextDouble();
        System.out.print("Ingrese la altura del tri�ngulo: ");
        altura = scanner.nextDouble();
    }

}
