/**
 * Clase encargada de gestionar el ingreso del paciente
 */
public class Atencion {
    public String procesarIngreso(Paciente p) {
        String pabellon;
        switch (p.getEps()) {
            case "Sura":
                pabellon = "Pabellón A";
                break;
            case "Sanitas":
                pabellon = "Pabellón B";
                break;
            case "FOMAG":
                pabellon = "Pabellón C";
                break;
            default:
                pabellon = "Pabellón D";
        }
        String resultado = "Paciente " + p.getNombre() + " asignado a " + pabellon;
        System.out.println(resultado);
        return resultado;
    }
}
