import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalGUI {
    private JPanel mainPanel;
    private JPanel SDAW;
    private JTabbedPane tabbedPane1;

    // Tab "Medicos"
    private JTextField txtNombreMedico;
    private JTextField txtEspecialidad;
    private JTextField txtRegistro;
    private JButton verPacientesButton;
    private JButton asignarPrioridadButton;

    // Tab "Pacientes"
    private JTextField txtNombrePaciente;
    private JComboBox comboBox1;
    private JTextField txtSaldo;

    // Tab "Cirujias"
    private JComboBox comboCirujanos;
    private JComboBox comboPacientesCirugia;
    private JCheckBox checkDisponible;
    private JButton operarButton;
    private JTextArea areaResultado;

    // Tab "Atencion"
    private JComboBox comboPacientes;

    // Tab "Pagos"
    private JComboBox comboPacientesPago;
    private JTextField txtCosto;
    private JButton pagarButton;
    private JTextArea areaResultadoPago;
    private JButton procesarIngresoButton;
    private JTextArea areaResultadoIngreso;

    //Costos por síntoma
    private static final double COSTO_INFARTO = 500.0;
    private static final double COSTO_DOLOR = 200.0;
    private static final double COSTO_FIEBRE = 100.0;
    private static final double COSTO_OTRO = 150.0;

    //Datos
    private List<Medico> listaMedicos;
    private List<Paciente> listaPacientes;
    private Atencion atencion;

    //Constructor
    public HospitalGUI() {
        listaMedicos = new ArrayList<>();
        listaPacientes = new ArrayList<>();
        atencion = new Atencion();
    }

    //Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Hospital Lili's Valley");
            HospitalGUI gui = new HospitalGUI();
            frame.setContentPane(gui.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            gui.inicializarEventos();   // ← mover DESPUÉS de setContentPane
            frame.setVisible(true);
        });
    }

    //Eventos
    public void inicializarEventos() {

        //Botón principal según pestaña activa
        procesarIngresoButton.addActionListener(e -> procesarSegunPestana());

        //Tab Medicos
        verPacientesButton.addActionListener(e -> {
            if (listaPacientes.isEmpty()) {
                mostrarResultado("No hay pacientes registrados.");
                return;
            }
            StringBuilder sb = new StringBuilder("=== Pacientes Registrados ===\n");
            for (Paciente p : listaPacientes) {
                sb.append("• ").append(p.getNombre())
                        .append(" | EPS: ").append(p.getEps())
                        .append(" | Saldo: $").append(p.getSaldo())
                        .append(" | Síntomas: ").append(p.getSintomas())
                        .append("\n");
            }
            mostrarResultado(sb.toString());
        });

        // Asignar prioridad: el médico evalúa los síntomas del paciente seleccionado
        asignarPrioridadButton.addActionListener(e -> {
            if (listaMedicos.isEmpty()) {
                mostrarResultado("No hay médicos registrados. Registre uno primero.");
                return;
            }
            if (comboPacientes.getSelectedItem() == null) {
                mostrarResultado("No hay pacientes. Registre uno primero.");
                return;
            }

            // Tomar el primer médico disponible como evaluador
            Medico medicoEvaluador = listaMedicos.get(0);
            Paciente p = buscarPaciente(comboPacientes.getSelectedItem().toString());

            if (p == null || p.getSintomas().isEmpty()) {
                mostrarResultado("El paciente no tiene síntomas registrados.");
                return;
            }

            StringBuilder sb = new StringBuilder(
                    "=== Evaluación del Dr. " + medicoEvaluador.getNombre() + " ===\n");
            sb.append("Paciente: ").append(p.getNombre()).append("\n\n");

            for (String sintoma : p.getSintomas()) {
                int prioridad;
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
                sb.append("Síntoma: '").append(sintoma)
                        .append("'  →  Prioridad ").append(prioridad)
                        .append(prioridad == 1 ? " (URGENTE)" :
                                prioridad == 2 ? " (MODERADO)" :
                                        prioridad == 3 ? " (LEVE)" : " (NORMAL)")
                        .append("\n");
            }
            mostrarResultado(sb.toString());
        });

        //Tab Cirugias
        operarButton.addActionListener(e -> {
            if (comboCirujanos.getSelectedItem() == null) {
                areaResultado.setText("No hay cirujanos registrados.\nRegistre un MedicoCirujano primero.");
                return;
            }
            if (comboPacientesCirugia.getSelectedItem() == null) {
                areaResultado.setText("Seleccione un paciente para operar.");
                return;
            }

            MedicoCirujano cirujano = buscarCirujano(comboCirujanos.getSelectedItem().toString());
            Paciente paciente = buscarPaciente(comboPacientesCirugia.getSelectedItem().toString());

            if (cirujano == null || paciente == null) {
                areaResultado.setText("Cirujano o paciente no encontrado.");
                return;
            }

            boolean disponible = checkDisponible.isSelected();
            cirujano.setQuirofanoDisponible(disponible);

            StringBuilder sb = new StringBuilder("=== Resultado Cirugía ===\n");
            sb.append("Cirujano    : ").append(cirujano.getNombre()).append("\n");
            sb.append("Especialidad: ").append(cirujano.getEspecialidad()).append("\n");
            sb.append("Quirófano   : ").append(cirujano.getNumeroQuirofano()).append("\n");
            sb.append("Paciente    : ").append(paciente.getNombre()).append("\n");
            sb.append("Estado      : ").append(disponible ? "Disponible" : "No disponible").append("\n\n");
            sb.append(disponible
                    ? "✔ Operación realizada exitosamente a " + paciente.getNombre() + "."
                    : "✘ No se puede operar: quirófano no disponible.");

            cirujano.operar();
            areaResultado.setText(sb.toString());
        });

        //Tab Pagos

        // Al seleccionar paciente en Pagos, calcular costo automáticamente
        comboPacientesPago.addActionListener(e -> calcularCosto());

        // Botón Pagar
        pagarButton.addActionListener(e -> {
            if (comboPacientesPago.getSelectedItem() == null) {
                areaResultadoPago.setText("Seleccione un paciente.");
                return;
            }
            Paciente p = buscarPaciente(comboPacientesPago.getSelectedItem().toString());
            if (p == null) {
                areaResultadoPago.setText("Paciente no encontrado.");
                return;
            }

            double costo = calcularCostoPaciente(p);
            boolean exito = p.verificarPresupuesto(costo);

            StringBuilder sb = new StringBuilder("=== Resultado Pago ===\n");
            sb.append("Paciente : ").append(p.getNombre()).append("\n");
            sb.append("EPS      : ").append(p.getEps()).append("\n");
            sb.append("Síntomas : ").append(p.getSintomas()).append("\n");
            sb.append("Costo    : $").append(costo).append("\n");
            sb.append("Saldo    : $").append(p.getSaldo()).append("\n\n");
            sb.append(exito
                    ? "✔ Pago exitoso."
                    : "✘ Pago rechazado: fondos insuficientes.");
            areaResultadoPago.setText(sb.toString());
        });
    }

    //Calcular costo según síntomas del paciente seleccionado
    private void calcularCosto() {
        if (comboPacientesPago.getSelectedItem() == null) return;
        Paciente p = buscarPaciente(comboPacientesPago.getSelectedItem().toString());
        if (p == null) return;
        txtCosto.setText("$" + calcularCostoPaciente(p));
    }

    private double calcularCostoPaciente(Paciente p) {
        double total = 0;
        for (String sintoma : p.getSintomas()) {
            switch (sintoma) {
                case "Infarto":
                    total += COSTO_INFARTO;
                    break;
                case "Dolor":
                    total += COSTO_DOLOR;
                    break;
                case "Fiebre":
                    total += COSTO_FIEBRE;
                    break;
                default:
                    total += COSTO_OTRO;
                    break;
            }
        }
        return total;
    }

    //Procesar según pestaña
    private void procesarSegunPestana() {
        switch (tabbedPane1.getSelectedIndex()) {
            case 0:
                registrarMedico();
                break;
            case 1:
                registrarPaciente();
                break;
            case 3:
                procesarIngreso();
                break;
            default:
                mostrarResultado("Use los botones de la pestaña activa.");
        }
    }

    //Registrar Médico
    private void registrarMedico() {
        String nombre = txtNombreMedico.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String registro = txtRegistro.getText().trim();

        if (nombre.isEmpty() || especialidad.isEmpty() || registro.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Complete Nombre, Especialidad y Registro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(null,
                "¿Registrar como MedicoCirujano?",
                "Tipo de médico", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            String nqStr = JOptionPane.showInputDialog(null, "Número de quirófano:");
            if (nqStr == null || nqStr.trim().isEmpty()) return;
            int nq;
            try {
                nq = Integer.parseInt(nqStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Número de quirófano inválido.");
                return;
            }
            MedicoCirujano c = new MedicoCirujano(
                    nombre, "000", 0, "-", especialidad, registro, nq, false);
            listaMedicos.add(c);
            comboCirujanos.addItem(c.getNombre());
            mostrarResultado("✔ Cirujano registrado: " + c.getNombre()
                    + " | Especialidad: " + especialidad
                    + " | Quirófano: " + nq);
        } else {
            Medico m = new Medico(nombre, "000", 0, "-", especialidad, registro);
            listaMedicos.add(m);
            mostrarResultado("✔ Médico registrado: " + m.getNombre()
                    + " | Especialidad: " + especialidad);
        }

        txtNombreMedico.setText("");
        txtEspecialidad.setText("");
        txtRegistro.setText("");
    }

    //Registrar Paciente
    private void registrarPaciente() {
        String nombre = txtNombrePaciente.getText().trim();
        String saldoStr = txtSaldo.getText().trim();

        if (nombre.isEmpty() || saldoStr.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Complete Nombre y Saldo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double saldo;
        try {
            saldo = Double.parseDouble(saldoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "El saldo debe ser un número válido.");
            return;
        }

        String eps = comboBox1.getSelectedItem().toString();
        String historial = "H" + (listaPacientes.size() + 1);
        Paciente p = new Paciente(nombre, "000", 0, "-", historial, eps, saldo);

        String[] opciones = {"Infarto", "Fiebre", "Dolor", "Otro"};
        String sintoma = (String) JOptionPane.showInputDialog(null,
                "Síntoma principal:", "Síntoma",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sintoma != null) p.agregarSintoma(sintoma);

        listaPacientes.add(p);

        // Actualizar todos los combos de pacientes
        comboPacientes.addItem(p.getNombre());
        comboPacientesCirugia.addItem(p.getNombre());
        comboPacientesPago.addItem(p.getNombre());

        mostrarResultado("✔ Paciente registrado: " + p.getNombre()
                + " | EPS: " + eps
                + " | Saldo: $" + saldo
                + " | Historial: " + historial
                + (sintoma != null ? " | Síntoma: " + sintoma : "")
                + "\n\nCostos por síntoma:"
                + "\n  Infarto → $" + COSTO_INFARTO
                + "\n  Dolor   → $" + COSTO_DOLOR
                + "\n  Fiebre  → $" + COSTO_FIEBRE
                + "\n  Otro    → $" + COSTO_OTRO);

        txtNombrePaciente.setText("");
        txtSaldo.setText("");
    }

    //Procesar Ingreso
    private void procesarIngreso() {
        if (comboPacientes.getSelectedItem() == null) {
            mostrarResultado("No hay pacientes registrados. Registre uno primero.");
            return;
        }
        Paciente p = buscarPaciente(comboPacientes.getSelectedItem().toString());
        if (p == null) {
            mostrarResultado("Paciente no encontrado.");
            return;
        }
        String resultado = atencion.procesarIngreso(p);
        mostrarResultado("=== Ingreso Procesado ===\n"
                + resultado
                + "\nEPS     : " + p.getEps()
                + "\nSaldo   : $" + p.getSaldo()
                + "\nSíntomas: " + p.getSintomas());
    }

    //Métodos seguros para mostrar resultados en la consola
    // Si areaResultadoIngreso existe (está en el form) escribe ahí.
    // Si por alguna razón es null, usa JOptionPane como respaldo.
    private void mostrarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.setText(texto);
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.append(texto + "\n");
            areaResultadoIngreso.setCaretPosition(areaResultadoIngreso.getDocument().getLength());
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Busquedas
    private Paciente buscarPaciente(String nombre) {
        for (Paciente p : listaPacientes)
            if (p.getNombre().equalsIgnoreCase(nombre)) return p;
        return null;
    }

    private MedicoCirujano buscarCirujano(String nombre) {
        for (Medico m : listaMedicos)
            if (m instanceof MedicoCirujano && m.getNombre().equalsIgnoreCase(nombre))
                return (MedicoCirujano) m;
        return null;
    }
}