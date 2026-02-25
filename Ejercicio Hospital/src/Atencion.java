/**
 * Clase encargada de gestionar el ingreso del paciente
 */
public class Atencion {
    public void procesarIngreso(Paciente p) {
        String pabellon;
        switch (p.getEps()) {
            case "Sura":
                pabellon = "Pabellon A";
                break;
            case "Sanitas":
                pabellon = "Pabellon B";
                break;
            case "FOMAG":
                pabellon = "Pabellon C";
                break;
            default:
                pabellon = "Pabellón D";
        }
        System.out.println("Paciente asignado a " + pabellon);
    }
}
