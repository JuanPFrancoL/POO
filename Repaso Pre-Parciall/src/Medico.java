import java.util.ArrayList;
import java.util.List;

public class Medico extends Persona {
    private String especialidad;
    private String nRegistro;
    protected List<Paciente> lPacientes;

    public Medico(String nombre, String dni, int edad, String genero, String especialidad, String nRegistro) {
        super(nombre, dni, edad, genero);
        this.especialidad = especialidad;
        this.nRegistro = nRegistro;
        this.lPacientes = new ArrayList<>();
    }

    public void agregarPaciente(Paciente p) {
        lPacientes.add(p);
    }

    public void revisarPacientes() {
        System.out.println("Pacientes del Dr. " + nombre + ":");
        for (Paciente p : lPacientes) {
            System.out.println("- " + p.getNombre());
        }
    }

    public int asignarPrioridad(String sintoma) {
        int prioridad = 0;
        switch (sintoma) {
            case "Infarto":
                prioridad = 1;
                break;
            case "Dolor":
                prioridad = 2;
                break;
            case "Fiebre":
                prioridad = 3;
                break;
            default:
                prioridad = 4;
        }

        System.out.println("El Dr. " + nombre +
                " evaluó el síntoma " + sintoma +
                " y asignó Prioridad " + prioridad);

        return prioridad;
    }

    public void evaluarPaciente(Paciente p) {

        for (String sintoma : p.getSintomas()) {
            asignarPrioridad(sintoma);
        }
    }

    @Override
    public void presentarse() {
        System.out.println("Médico: " + nombre + " - " + especialidad);
    }
}
