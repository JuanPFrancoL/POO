import java.util.ArrayList;
import java.util.List;

public class Paciente extends Persona {
    private String nHistorial;
    private String eps;
    private double saldo;
    private List<String> sintomas;

    public Paciente(String nombre, String dni, int edad, String genero, String nHistorial, String eps, double saldo) {
        super(nombre, dni, edad, genero);
        this.nHistorial = nHistorial;
        this.eps = eps;
        this.saldo = saldo;
        this.sintomas = new ArrayList<>();  // ← esta línea es la que falta
    }

    public String getnHistorial() {
        return nHistorial;
    }

    public void setnHistorial(String nHistorial) {
        this.nHistorial = nHistorial;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public List<String> getSintomas() {
        return sintomas;
    }

    public void setSintomas(List<String> sintomas) {
        this.sintomas = sintomas;
    }

    public boolean verificarPresupuesto(double costo) {
        if (saldo >= costo) {
            saldo -= costo;
            System.out.println("El paciente " + nombre + " intenta pagar $" + costo
                    + ". Saldo actual $" + saldo + " -> Resultado: Exito.");
            return true;
        } else {
            System.out.println("El paciente " + nombre + " intenta pagar $" + costo
                    + ". Saldo actual $" + saldo + " -> Resultado: Rechazado (Fondos insuficientes).");
            return false;
        }
    }

    public void agregarSintoma(String sintoma) {
        sintomas.add(sintoma);
    }

    public void mostrarSintomas() {
        System.out.println("Síntomas de " + nombre + ":");
        for (String s : sintomas) {
            System.out.println("- " + s);
        }
    }


    @Override
    public void presentarse() {

    }
}
