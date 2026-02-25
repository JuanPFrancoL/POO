import java.util.ArrayList;
import java.util.List;

public class gestionHospital {
    public static void main(String[] args) {
        Medico m1 = new Medico("Carlos Perez", "101", 45, "M", "General", "REG01");
        Medico m2 = new Medico("Laura Gómez", "102", 38, "F", "Pediatría", "REG02");
        Medico m3 = new Medico("Andres Ruiz", "103", 50, "M", "Cardiología", "REG03");
        Medico m4 = new Medico("Sofia Torres", "104", 42, "F", "Dermatología", "REG04");

        MedicoCirujano c1 = new MedicoCirujano("Ana Lopez", "201", 48, "F",
                "Cirugía General", "REG05", 5, true);

        MedicoCirujano c2 = new MedicoCirujano("Jorge Diaz", "202", 55, "M",
                "Neurocirugía", "REG06", 102, false);

        MedicoCirujano c3 = new MedicoCirujano("Paula Rojas", "203", 44, "F",
                "Cirugía Cardiaca", "REG07", 103, true);

        MedicoCirujano c4 = new MedicoCirujano("Miguel Castro", "204", 60, "M",
                "Cirugía Plástica", "REG08", 104, false);

        Paciente p1 = new Paciente("Luis", "301", 30, "M", "H01", "Sura", 100);
        Paciente p2 = new Paciente("Maria", "302", 25, "F", "H02", "Sanitas", 30);
        Paciente p3 = new Paciente("Pedro", "303", 40, "M", "H03", "Sura", 80);
        Paciente p4 = new Paciente("Lucia", "304", 35, "F", "H04", "NuevaEPS", 60);
        Paciente p5 = new Paciente("Juan", "305", 28, "M", "H05", "Sura", 20);
        Paciente p6 = new Paciente("Elena", "306", 32, "F", "H06", "Sanitas", 120);
        Paciente p7 = new Paciente("Camilo", "307", 45, "M", "H07", "NuevaEPS", 90);
        Paciente p8 = new Paciente("Valeria", "308", 29, "F", "H08", "Sura", 70);
        Paciente p9 = new Paciente("Diego", "309", 50, "M", "H09", "Sanitas", 40);
        Paciente p10 = new Paciente("Sara", "310", 22, "F", "H10", "Sura", 110);
        Paciente p11 = new Paciente("Tomas", "311", 60, "M", "H11", "NuevaEPS", 55);
        Paciente p12 = new Paciente("Daniela", "312", 33, "F", "H12", "Sanitas", 95);

        p1.agregarSintoma("Infarto");
        p2.agregarSintoma("Fiebre");
        p3.agregarSintoma("Dolor");
        p4.agregarSintoma("Fiebre");
        p5.agregarSintoma("Infarto");
        p6.agregarSintoma("Dolor");
        p7.agregarSintoma("Fiebre");
        p8.agregarSintoma("Infarto");
        p9.agregarSintoma("Dolor");
        p10.agregarSintoma("Fiebre");
        p11.agregarSintoma("Infarto");
        p12.agregarSintoma("Fiebre");

        m1.agregarPaciente(p1);
        m1.agregarPaciente(p2);
        m2.agregarPaciente(p3);
        m2.agregarPaciente(p4);
        m3.agregarPaciente(p5);
        m3.agregarPaciente(p6);
        m4.agregarPaciente(p7);
        m4.agregarPaciente(p8);

        List<Persona> personas = new ArrayList<>();

        personas.add(m1);
        personas.add(m2);
        personas.add(m3);
        personas.add(m4);

        personas.add(c1);
        personas.add(c2);
        personas.add(c3);
        personas.add(c4);

        personas.add(p1);
        personas.add(p2);
        personas.add(p3);
        personas.add(p4);
        personas.add(p5);
        personas.add(p6);
        personas.add(p7);
        personas.add(p8);
        personas.add(p9);
        personas.add(p10);
        personas.add(p11);
        personas.add(p12);

        System.out.println("=== PRESENTACIÓN GENERAL ===");

        for (Persona persona : personas) {
            persona.presentarse();
        }

        System.out.println("\n=== REVISIÓN MÉDICA ===");

        m1.revisarPacientes();
        m1.asignarPrioridad("Infarto");

        System.out.println("\n=== INTENTO DE PAGO ===");

        p1.verificarPresupuesto(50);
        p2.verificarPresupuesto(50);

        System.out.println("\n=== CIRUGÍAS ===");

        c1.operar();
        c2.operar();

        System.out.println("\n=== INGRESO DE PACIENTE ===");

        Atencion atencion = new Atencion();
        atencion.procesarIngreso(p1);
        atencion.procesarIngreso(p4);
    }
}