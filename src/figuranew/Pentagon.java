package figuranew;

public class Pentagon extends Figura {
    private double lado;

    private double apotema;

    @Override
    public void calcularArea() {
        double area = (5 * lado * apotema) / 2;
        mostrarResultado(area);
    }

    @Override
    public void pedirDatos() {  // cambiado de protected a public
        System.out.print("Ingrese el lado del pentágono: ");
        lado = scanner.nextDouble();
        System.out.print("Ingrese el apotema del pentágono: ");
        apotema = scanner.nextDouble();
    }


}
