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
    private JComboBox comboBox1;
    private JTextField txtSaldo;

    // Tab "Cirujias"
    private JComboBox comboCirujanos;
    private JCheckBox checkDisponible;
    private JButton operarButton;
    private JTextArea areaResultado;

    // Tab "Atencion"
    private JComboBox comboPacientes;

    // Fuera del tabbedPane
    private JButton procesarIngresoButton;
    private JTextArea areaResultadoIngreso;


    // ── Datos ──────────────────────────────────────────────────────────────────
    private List<Medico> listaMedicos;
    private List<Paciente> listaPacientes;
    private Atencion atencion;

    // ── Constructor ────────────────────────────────────────────────────────────
    public HospitalGUI() {
        listaMedicos = new ArrayList<>();
        listaPacientes = new ArrayList<>();
        atencion = new Atencion();
    }

    // ── Main ───────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Hospital Lili's Valley");
            HospitalGUI gui = new HospitalGUI();  // aquí IntelliJ inyecta $$$setupUI$$$()
            frame.setContentPane(gui.mainPanel);   // mainPanel ya existe
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            gui.inicializarEventos();              // ← mover AQUÍ, después de setContentPane
            frame.setVisible(true);
        });
    }

    // ── Eventos ────────────────────────────────────────────────────────────────
    public void inicializarEventos() {

        procesarIngresoButton.addActionListener(e -> procesarSegunPestana());

        verPacientesButton.addActionListener(e -> {
            if (listaPacientes.isEmpty()) {
                areaResultadoIngreso.setText("No hay pacientes registrados.");
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
            areaResultadoIngreso.setText(sb.toString());
        });

        asignarPrioridadButton.addActionListener(e -> {
            if (comboPacientes.getSelectedItem() == null) {
                areaResultadoIngreso.setText("No hay pacientes. Registre uno primero.");
                return;
            }
            Paciente p = buscarPaciente(comboPacientes.getSelectedItem().toString());
            if (p == null || p.getSintomas().isEmpty()) {
                areaResultadoIngreso.setText("El paciente no tiene síntomas registrados.");
                return;
            }
            StringBuilder sb = new StringBuilder("=== Prioridades para " + p.getNombre() + " ===\n");
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
                        .append("'  →  Prioridad ").append(prioridad).append("\n");
            }
            areaResultadoIngreso.setText(sb.toString());
        });

        operarButton.addActionListener(e -> {
            if (comboCirujanos.getSelectedItem() == null) {
                areaResultado.setText("No hay cirujanos.\nRegistre un MedicoCirujano primero.");
                return;
            }
            MedicoCirujano cirujano = buscarCirujano(comboCirujanos.getSelectedItem().toString());
            if (cirujano == null) {
                areaResultado.setText("Cirujano no encontrado.");
                return;
            }
            boolean disponible = checkDisponible.isSelected();
            cirujano.setQuirofanoDisponible(disponible);

            StringBuilder sb = new StringBuilder("=== Resultado Cirugía ===\n");
            sb.append("Cirujano    : ").append(cirujano.getNombre()).append("\n");
            sb.append("Especialidad: ").append(cirujano.getEspecialidad()).append("\n");
            sb.append("Quirófano   : ").append(cirujano.getNumeroQuirofano()).append("\n");
            sb.append("Estado      : ").append(disponible ? "Disponible" : "No disponible").append("\n\n");
            sb.append(disponible
                    ? "✔ Operación realizada exitosamente."
                    : "✘ No se puede operar: quirófano no disponible.");
            cirujano.operar();
            areaResultado.setText(sb.toString());
        });
    }

    // ── Procesar según pestaña ─────────────────────────────────────────────────
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
                areaResultadoIngreso.setText("Use el botón 'Operar' en la pestaña Cirugías.");
        }
    }

    // ── Registrar Médico ───────────────────────────────────────────────────────
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
            areaResultadoIngreso.setText("✔ Cirujano registrado: " + c.getNombre()
                    + " | Especialidad: " + especialidad
                    + " | Quirófano: " + nq);
        } else {
            Medico m = new Medico(nombre, "000", 0, "-", especialidad, registro);
            listaMedicos.add(m);
            areaResultadoIngreso.setText("✔ Médico registrado: " + m.getNombre()
                    + " | Especialidad: " + especialidad);
        }

        txtNombreMedico.setText("");
        txtEspecialidad.setText("");
        txtRegistro.setText("");
    }

    // ── Registrar Paciente ─────────────────────────────────────────────────────
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
        comboPacientes.addItem(p.getNombre());

        areaResultadoIngreso.setText("✔ Paciente registrado: " + p.getNombre()
                + " | EPS: " + eps
                + " | Saldo: $" + saldo
                + " | Historial: " + historial
                + (sintoma != null ? " | Síntoma: " + sintoma : ""));

        txtNombrePaciente.setText("");
        txtSaldo.setText("");
    }

    // ── Procesar Ingreso ───────────────────────────────────────────────────────
    private void procesarIngreso() {
        if (comboPacientes.getSelectedItem() == null) {
            areaResultadoIngreso.setText("No hay pacientes registrados. Registre uno primero.");
            return;
        }
        Paciente p = buscarPaciente(comboPacientes.getSelectedItem().toString());
        if (p == null) {
            areaResultadoIngreso.setText("Paciente no encontrado.");
            return;
        }
        String resultado = atencion.procesarIngreso(p);
        areaResultadoIngreso.setText("=== Ingreso Procesado ===\n"
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