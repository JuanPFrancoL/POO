import java.util.List;

public class MedicoCirujano extends Medico implements ICirujano {
    private int numeroQuirofano;
    private boolean quirofanoDisponible;

    public MedicoCirujano(String nombre, String dni, int edad, String genero, String especialidad, String nRegistro, List<Paciente> lPacientes, int numeroQuirofano, boolean quirofanoDisponible) {
        super(nombre, dni, edad, genero, especialidad, nRegistro); // ← quita lPacientes de aquí
        this.numeroQuirofano = numeroQuirofano;
        this.quirofanoDisponible = quirofanoDisponible;
    }

    public MedicoCirujano(String nombre, String dni, int edad, String genero, String especialidad, String nRegistro, int numeroQuirofano, boolean quirofanoDisponible) {
        super(nombre, dni, edad, genero, especialidad, nRegistro);
        this.numeroQuirofano = numeroQuirofano;
        this.quirofanoDisponible = quirofanoDisponible;
    }

    public int getNumeroQuirofano() {
        return numeroQuirofano;
    }

    public void setNumeroQuirofano(int numeroQuirofano) {
        this.numeroQuirofano = numeroQuirofano;
    }

    public boolean isQuirofanoDisponible() {
        return quirofanoDisponible;
    }

    public void setQuirofanoDisponible(boolean quirofanoDisponible) {
        this.quirofanoDisponible = quirofanoDisponible;
    }

    @Override
    public void operar() {
        if (quirofanoDisponible) {
            System.out.println("Cirujano " + nombre +
                    " realizó la operación en quirófano " + numeroQuirofano);
            quirofanoDisponible = false;
        } else {
            System.out.println("Cirujano " + nombre +
                    " NO pudo operar. Quirófano ocupado.");
        }
    }

    @Override
    public void presentarse() {
        System.out.println("Cirujano: " + nombre +
                " - Quirófano " + numeroQuirofano);
    }
}
