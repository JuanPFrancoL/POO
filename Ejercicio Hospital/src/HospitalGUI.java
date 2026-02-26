import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalGUI {

    // ── Bindings exactos del .form ─────────────────────────────────────────────
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
    private JComboBox comboBox1;        // EPS
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

    // Botón y consola general (fuera del tabbedPane)
    private JButton procesarIngresoButton;
    // NOTA: areaResultadoIngreso NO está en el .form todavía.
    // Por ahora usamos mostrarResultado() que escribe en areaResultadoPago
    // o lanza un JOptionPane si areaResultadoPago también es null.
    private JTextArea areaResultadoIngreso; // queda null hasta que se agregue al form

    // ── Costos por síntoma ─────────────────────────────────────────────────────
    private static final double COSTO_INFARTO = 500.0;
    private static final double COSTO_DOLOR = 200.0;
    private static final double COSTO_FIEBRE = 100.0;
    private static final double COSTO_OTRO = 150.0;

    // ── Datos ──────────────────────────────────────────────────────────────────
    private List<Medico> listaMedicos = new ArrayList<>();
    private List<Paciente> listaPacientes = new ArrayList<>();
    private Atencion atencion = new Atencion();

    // ── Constructor ────────────────────────────────────────────────────────────
    public HospitalGUI() {
        // El form inicializa los componentes automáticamente.
        // inicializarEventos() se llama desde main DESPUÉS de setContentPane.
    }

    // ── Main ───────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Hospital Lili's Valley");
            HospitalGUI gui = new HospitalGUI();
            frame.setContentPane(gui.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            gui.inicializarEventos(); // DESPUÉS de setContentPane para que los componentes existan
            frame.setVisible(true);
        });
    }

    // ── Método seguro para mostrar resultados ──────────────────────────────────
    // Intenta escribir en areaResultadoIngreso; si es null usa areaResultadoPago;
    // si también es null usa un JOptionPane como último recurso.
    private void mostrarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.setText(texto);
        } else if (areaResultadoPago != null) {
            areaResultadoPago.setText(texto);
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.append(texto + "\n");
            areaResultadoIngreso.setCaretPosition(areaResultadoIngreso.getDocument().getLength());
        } else if (areaResultadoPago != null) {
            areaResultadoPago.append(texto + "\n");
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Eventos ────────────────────────────────────────────────────────────────
    public void inicializarEventos() {

        // Botón principal (fuera del tabbedPane) → actúa según la pestaña activa
        if (procesarIngresoButton != null)
            procesarIngresoButton.addActionListener(e -> procesarSegunPestana());

        // ── Tab Medicos ──────────────────────────────────────────────────────
        if (verPacientesButton != null) {
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
        }

        if (asignarPrioridadButton != null) {
            asignarPrioridadButton.addActionListener(e -> {
                if (listaMedicos.isEmpty()) {
                    mostrarResultado("No hay médicos registrados. Registre uno primero.");
                    return;
                }
                if (comboPacientes == null || comboPacientes.getSelectedItem() == null) {
                    mostrarResultado("No hay pacientes. Registre uno primero.");
                    return;
                }
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
        }

        // ── Tab Cirugias ─────────────────────────────────────────────────────
        if (operarButton != null) {
            operarButton.addActionListener(e -> {
                if (comboCirujanos == null || comboCirujanos.getSelectedItem() == null) {
                    if (areaResultado != null)
                        areaResultado.setText("No hay cirujanos. Registre un MedicoCirujano primero.");
                    return;
                }
                if (comboPacientesCirugia == null || comboPacientesCirugia.getSelectedItem() == null) {
                    if (areaResultado != null)
                        areaResultado.setText("Seleccione un paciente para operar.");
                    return;
                }
                MedicoCirujano cirujano = buscarCirujano(comboCirujanos.getSelectedItem().toString());
                Paciente paciente = buscarPaciente(comboPacientesCirugia.getSelectedItem().toString());
                if (cirujano == null || paciente == null) {
                    if (areaResultado != null)
                        areaResultado.setText("Cirujano o paciente no encontrado.");
                    return;
                }
                boolean disponible = checkDisponible != null && checkDisponible.isSelected();
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
                if (areaResultado != null) areaResultado.setText(sb.toString());
            });
        }

        // ── Tab Pagos ────────────────────────────────────────────────────────
        if (comboPacientesPago != null)
            comboPacientesPago.addActionListener(e -> calcularCosto());

        if (pagarButton != null) {
            pagarButton.addActionListener(e -> {
                if (comboPacientesPago == null || comboPacientesPago.getSelectedItem() == null) {
                    if (areaResultadoPago != null) areaResultadoPago.setText("Seleccione un paciente.");
                    return;
                }
                Paciente p = buscarPaciente(comboPacientesPago.getSelectedItem().toString());
                if (p == null) {
                    if (areaResultadoPago != null) areaResultadoPago.setText("Paciente no encontrado.");
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
                sb.append(exito ? "✔ Pago exitoso." : "✘ Pago rechazado: fondos insuficientes.");
                if (areaResultadoPago != null) areaResultadoPago.setText(sb.toString());
            });
        }
    }

    // ── Calcular costo según síntomas ──────────────────────────────────────────
    private void calcularCosto() {
        if (comboPacientesPago == null || comboPacientesPago.getSelectedItem() == null) return;
        Paciente p = buscarPaciente(comboPacientesPago.getSelectedItem().toString());
        if (p == null) return;
        if (txtCosto != null) txtCosto.setText("$" + calcularCostoPaciente(p));
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

    // ── Procesar según pestaña activa ──────────────────────────────────────────
    private void procesarSegunPestana() {
        if (tabbedPane1 == null) return;
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

    // ── Registrar Médico ───────────────────────────────────────────────────────
    private void registrarMedico() {
        String nombre = txtNombreMedico != null ? txtNombreMedico.getText().trim() : "";
        String especialidad = txtEspecialidad != null ? txtEspecialidad.getText().trim() : "";
        String registro = txtRegistro != null ? txtRegistro.getText().trim() : "";

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
            if (comboCirujanos != null) comboCirujanos.addItem(c.getNombre());
            mostrarResultado("✔ Cirujano registrado: " + c.getNombre()
                    + " | Especialidad: " + especialidad
                    + " | Quirófano: " + nq);
        } else {
            Medico m = new Medico(nombre, "000", 0, "-", especialidad, registro);
            listaMedicos.add(m);
            mostrarResultado("✔ Médico registrado: " + m.getNombre()
                    + " | Especialidad: " + especialidad);
        }

        if (txtNombreMedico != null) txtNombreMedico.setText("");
        if (txtEspecialidad != null) txtEspecialidad.setText("");
        if (txtRegistro != null) txtRegistro.setText("");
    }

    // ── Registrar Paciente ─────────────────────────────────────────────────────
    private void registrarPaciente() {
        String nombre = txtNombrePaciente != null ? txtNombrePaciente.getText().trim() : "";
        String saldoStr = txtSaldo != null ? txtSaldo.getText().trim() : "";

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

        String eps = (comboBox1 != null && comboBox1.getSelectedItem() != null)
                ? comboBox1.getSelectedItem().toString() : "Otro";
        String historial = "H" + (listaPacientes.size() + 1);
        Paciente p = new Paciente(nombre, "000", 0, "-", historial, eps, saldo);

        String[] opciones = {"Infarto", "Fiebre", "Dolor", "Otro"};
        String sintoma = (String) JOptionPane.showInputDialog(null,
                "Síntoma principal:", "Síntoma",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sintoma != null) p.agregarSintoma(sintoma);

        listaPacientes.add(p);

        // Actualizar todos los combos de pacientes
        if (comboPacientes != null) comboPacientes.addItem(p.getNombre());
        if (comboPacientesCirugia != null) comboPacientesCirugia.addItem(p.getNombre());
        if (comboPacientesPago != null) comboPacientesPago.addItem(p.getNombre());

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

        if (txtNombrePaciente != null) txtNombrePaciente.setText("");
        if (txtSaldo != null) txtSaldo.setText("");
    }

    // ── Procesar Ingreso ───────────────────────────────────────────────────────
    private void procesarIngreso() {
        if (comboPacientes == null || comboPacientes.getSelectedItem() == null) {
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

    // ── Búsquedas ──────────────────────────────────────────────────────────────
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