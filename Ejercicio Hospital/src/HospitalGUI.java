import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del Sistema Hospital Lili's Valley.
 * <p>
 * Contiene la interfaz gráfica Y la lógica de gestión (listas de médicos
 * y pacientes). Es el punto de entrada del programa (método main).
 * <p>
 * Pestañas:
 * 1. Médicos      – Registrar Medico o MedicoCirujano
 * 2. Pacientes    – Registrar Paciente con síntoma y EPS
 * 3. Cirugías     – Seleccionar cirujano, indicar estado del quirófano y operar
 * 4. Atención     – Seleccionar paciente y procesar su ingreso al pabellón
 * <p>
 * Botones inferiores:
 * • "Procesar Ingreso"   – actúa según la pestaña activa (registra o procesa)
 * • "Ver Pacientes"      – lista todos los pacientes en el área de resultados
 * • "Asignar Prioridad"  – evalúa síntomas del paciente seleccionado
 */
public class HospitalGUI extends JFrame {

    // ── Componentes de la GUI ────────────────────────────────────────────────────
    private JPanel SDAW;
    private JTabbedPane tabbedPane1;

    // Tab Médicos
    private JTextField txtNombreMedico;
    private JTextField txtEspecialidad;
    private JTextField txtRegistro;
    private JCheckBox checkEsCirujano;
    private JTextField txtNroQuirofano;

    // Tab Pacientes
    private JTextField txtNombrePaciente;
    private JTextField txtSaldo;
    private JComboBox<String> comboBox1;      // EPS
    private JComboBox<String> comboSintoma;

    // Tab Cirugías
    private JComboBox<MedicoCirujano> comboCirujanos;
    private JCheckBox checkDisponible;
    private JButton operarButton;
    private JTextArea areaResultado;

    // Tab Atención
    private JComboBox<Paciente> comboPacientes;

    // Panel inferior
    private JButton procesarIngresoButton;
    private JButton verPacientesButton;
    private JButton asignarPrioridadButton;
    private JTextArea areaResultadoIngreso;

    // ── Lógica de gestión (antes en GestionHospital) ─────────────────────────────
    private List<Medico> listaMedicos;
    private List<Paciente> listaPacientes;
    private Atencion atencion;

    // ════════════════════════════════════════════════════════════════════════════
    public HospitalGUI() {
        listaMedicos = new ArrayList<>();
        listaPacientes = new ArrayList<>();
        atencion = new Atencion();

        construirUI();
        inicializarEventos();

        setTitle("Sistema Hospital Lili's Valley");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(SDAW);
        setSize(860, 580);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─── Construcción de la UI ───────────────────────────────────────────────────
    private void construirUI() {
        SDAW = new JPanel(new BorderLayout(8, 8));
        SDAW.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titulo = new JLabel("HOSPITAL LILI'S VALLEY – SISTEMA DE GESTIÓN", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(30, 80, 160));
        SDAW.add(titulo, BorderLayout.NORTH);

        // TabbedPane central
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.addTab("Médicos", crearTabMedicos());
        tabbedPane1.addTab("Pacientes", crearTabPacientes());
        tabbedPane1.addTab("Cirugías", crearTabCirugias());
        tabbedPane1.addTab("Atención", crearTabAtencion());
        SDAW.add(tabbedPane1, BorderLayout.CENTER);

        // Panel inferior
        SDAW.add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    /**
     * Pestaña 1: registro de médicos (y cirujanos)
     */
    private JPanel crearTabMedicos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNombreMedico = new JTextField(18);
        txtEspecialidad = new JTextField(18);
        txtRegistro = new JTextField(18);
        checkEsCirujano = new JCheckBox("Es Cirujano");
        txtNroQuirofano = new JTextField(6);
        txtNroQuirofano.setEnabled(false);

        // Habilitar/deshabilitar campo quirófano según checkbox
        checkEsCirujano.addActionListener(e ->
                txtNroQuirofano.setEnabled(checkEsCirujano.isSelected()));

        int row = 0;
        addFormRow(panel, g, row++, "Nombre:", txtNombreMedico);
        addFormRow(panel, g, row++, "Especialidad:", txtEspecialidad);
        addFormRow(panel, g, row++, "Registro:", txtRegistro);

        g.gridx = 0;
        g.gridy = row;
        panel.add(checkEsCirujano, g);
        g.gridx = 1;
        panel.add(new JLabel(), g);
        row++;

        addFormRow(panel, g, row, "N° Quirófano:", txtNroQuirofano);
        return panel;
    }

    /**
     * Pestaña 2: registro de pacientes
     */
    private JPanel crearTabPacientes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNombrePaciente = new JTextField(18);
        txtSaldo = new JTextField(18);
        comboBox1 = new JComboBox<>(new String[]{"Sura", "Sanitas", "FOMAG", "NuevaEPS", "Otro"});
        comboSintoma = new JComboBox<>(new String[]{"Infarto", "Fiebre", "Dolor", "Otro"});

        int row = 0;
        addFormRow(panel, g, row++, "Nombre:", txtNombrePaciente);
        addFormRow(panel, g, row++, "EPS:", comboBox1);
        addFormRow(panel, g, row++, "Saldo:", txtSaldo);
        addFormRow(panel, g, row, "Síntoma:", comboSintoma);
        return panel;
    }

    /**
     * Pestaña 3: cirugías
     */
    private JPanel crearTabCirugias() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        comboCirujanos = new JComboBox<>();
        checkDisponible = new JCheckBox("Quirófano disponible");
        operarButton = new JButton("Operar");
        operarButton.setBackground(new Color(200, 50, 50));
        operarButton.setForeground(Color.WHITE);
        operarButton.setFont(operarButton.getFont().deriveFont(Font.BOLD));

        addFormRow(top, g, 0, "Cirujano:", comboCirujanos);
        g.gridx = 0;
        g.gridy = 1;
        top.add(checkDisponible, g);
        g.gridx = 1;
        top.add(operarButton, g);

        areaResultado = new JTextArea(5, 30);
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultado), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Pestaña 4: atención / ingreso
     */
    private JPanel crearTabAtencion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        comboPacientes = new JComboBox<>();

        addFormRow(panel, g, 0, "Paciente:", comboPacientes);

        // Info extra
        g.gridx = 0;
        g.gridy = 1;
        g.gridwidth = 2;
        JLabel info = new JLabel("<html><i>Seleccione un paciente y pulse 'Procesar Ingreso'</i></html>");
        info.setForeground(Color.GRAY);
        panel.add(info, g);
        return panel;
    }

    /**
     * Panel inferior con botones y área de resultados
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new TitledBorder("Resultados"));

        // Botones
        procesarIngresoButton = new JButton("Procesar Ingreso");
        verPacientesButton = new JButton("Ver Pacientes");
        asignarPrioridadButton = new JButton("Asignar Prioridad");

        procesarIngresoButton.setBackground(new Color(50, 130, 50));
        procesarIngresoButton.setForeground(Color.WHITE);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        botones.add(procesarIngresoButton);
        botones.add(verPacientesButton);
        botones.add(asignarPrioridadButton);

        areaResultadoIngreso = new JTextArea(6, 60);
        areaResultadoIngreso.setEditable(false);
        areaResultadoIngreso.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(botones, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultadoIngreso), BorderLayout.CENTER);
        return panel;
    }

    // ─── Utilidad para agregar fila label + campo ────────────────────────────────
    private void addFormRow(JPanel p, GridBagConstraints g, int row,
                            String label, JComponent field) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 1;
        g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(field, g);
    }

    // ─── Eventos ─────────────────────────────────────────────────────────────────
    private void inicializarEventos() {

        // "Procesar Ingreso" actúa según la pestaña activa
        procesarIngresoButton.addActionListener(e -> procesarSegunPestana());

        // Botón Operar (dentro de la pestaña Cirugías)
        operarButton.addActionListener(e -> realizarOperacion());

        // Ver lista completa de pacientes
        verPacientesButton.addActionListener(e ->
                areaResultadoIngreso.setText(listarPacientes()));

        // Asignar prioridad al paciente seleccionado en comboPacientes
        asignarPrioridadButton.addActionListener(e -> asignarPrioridad());
    }

    /**
     * Dependiendo de la pestaña activa:
     * 0 – Médicos   → registrar médico (o cirujano)
     * 1 – Pacientes → registrar paciente
     * 2 – Cirugías  → ya tiene su propio botón; aquí no hace nada extra
     * 3 – Atención  → procesar ingreso del paciente seleccionado
     */
    private void procesarSegunPestana() {
        int tab = tabbedPane1.getSelectedIndex();
        switch (tab) {
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
                areaResultadoIngreso.setText("Use los controles de la pestaña activa.");
        }
    }

    /**
     * Registra un Medico o MedicoCirujano y actualiza los combos.
     */
    private void registrarMedico() {
        String nombre = txtNombreMedico.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String registro = txtRegistro.getText().trim();

        if (nombre.isEmpty() || especialidad.isEmpty() || registro.isEmpty()) {
            mostrarError("Complete los campos Nombre, Especialidad y Registro.");
            return;
        }

        if (checkEsCirujano.isSelected()) {
            String nqStr = txtNroQuirofano.getText().trim();
            if (nqStr.isEmpty()) {
                mostrarError("Ingrese el número de quirófano.");
                return;
            }
            int nq;
            try {
                nq = Integer.parseInt(nqStr);
            } catch (NumberFormatException ex) {
                mostrarError("El número de quirófano debe ser entero.");
                return;
            }
            MedicoCirujano c = new MedicoCirujano(nombre, "000", 0, "-",
                    especialidad, registro, nq, false);
            listaMedicos.add(c);
            areaResultadoIngreso.setText("✔ Cirujano registrado: " + c);
        } else {
            Medico m = new Medico(nombre, "000", 0, "-", especialidad, registro);
            listaMedicos.add(m);
            areaResultadoIngreso.setText("✔ Médico registrado: " + m);
        }

        limpiarCamposMedico();
        actualizarCombos();
    }

    /**
     * Registra un Paciente y actualiza los combos.
     */
    private void registrarPaciente() {
        String nombre = txtNombrePaciente.getText().trim();
        String saldoStr = txtSaldo.getText().trim();

        if (nombre.isEmpty() || saldoStr.isEmpty()) {
            mostrarError("Complete los campos Nombre y Saldo.");
            return;
        }

        double saldo;
        try {
            saldo = Double.parseDouble(saldoStr);
        } catch (NumberFormatException ex) {
            mostrarError("El saldo debe ser un número válido.");
            return;
        }

        String eps = (String) comboBox1.getSelectedItem();
        String sintoma = (String) comboSintoma.getSelectedItem();

        // Generar historial automático
        String historial = "H" + (listaPacientes.size() + 1);
        Paciente p = new Paciente(nombre, "000", 0, "-", historial, eps, saldo);
        p.agregarSintoma(sintoma);

        listaPacientes.add(p);
        areaResultadoIngreso.setText("✔ Paciente registrado: " + p +
                " | Síntoma: " + sintoma + " | Historial: " + historial);

        limpiarCamposPaciente();
        actualizarCombos();
    }

    /**
     * Procesa el ingreso del paciente seleccionado en comboPacientes
     * y muestra el pabellón asignado.
     */
    private void procesarIngreso() {
        Paciente p = (Paciente) comboPacientes.getSelectedItem();
        if (p == null) {
            areaResultadoIngreso.setText("No hay pacientes registrados. Registre uno primero.");
            return;
        }
        String resultado = atencion.procesarIngreso(p);
        areaResultadoIngreso.setText("=== Ingreso procesado ===\n" + resultado +
                "\nEPS: " + p.getEps() +
                "\nSaldo: $" + p.getSaldo() +
                "\nSíntomas: " + p.getSintomas());
    }

    /**
     * Realiza la operación con el cirujano seleccionado.
     * Actualiza el estado de disponibilidad del quirófano según el checkbox.
     */
    private void realizarOperacion() {
        MedicoCirujano cirujano = (MedicoCirujano) comboCirujanos.getSelectedItem();
        if (cirujano == null) {
            areaResultado.setText("No hay cirujanos registrados.\nRegistre un MedicoCirujano primero.");
            return;
        }

        cirujano.setQuirofanoDisponible(checkDisponible.isSelected());

        StringBuilder sb = new StringBuilder("=== Resultado de la Cirugía ===\n");
        sb.append("Cirujano: ").append(cirujano.getNombre()).append("\n");
        sb.append("Especialidad: ").append(cirujano.getEspecialidad()).append("\n");
        sb.append("Quirófano: ").append(cirujano.getNumeroQuirofano()).append("\n");
        sb.append("Disponible: ").append(checkDisponible.isSelected() ? "Sí" : "No").append("\n\n");

        if (checkDisponible.isSelected()) {
            sb.append("✔ Operación realizada exitosamente.");
        } else {
            sb.append("✘ No se puede operar: quirófano no disponible.");
        }

        // También llama al método operar() que imprime en consola
        cirujano.operar();
        areaResultado.setText(sb.toString());
    }

    /**
     * Evalúa los síntomas del paciente seleccionado y muestra las prioridades.
     */
    private void asignarPrioridad() {
        Paciente p = (Paciente) comboPacientes.getSelectedItem();
        if (p == null) {
            areaResultadoIngreso.setText("No hay pacientes. Registre uno primero.");
            return;
        }

        if (p.getSintomas().isEmpty()) {
            areaResultadoIngreso.setText("El paciente no tiene síntomas registrados.");
            return;
        }

        // Usar el primer médico de la lista para evaluar, si existe
        Medico medicoEvaluador = listaMedicos.isEmpty() ? null : listaMedicos.get(0);

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
            if (medicoEvaluador != null) {
                sb.append("Dr. ").append(medicoEvaluador.getNombre()).append(" → ");
            }
            sb.append("Síntoma: '").append(sintoma)
                    .append("' → Prioridad ").append(prioridad).append("\n");
        }
        areaResultadoIngreso.setText(sb.toString());
    }

    // ─── Actualización de combos ─────────────────────────────────────────────────
    private void actualizarCombos() {
        comboCirujanos.removeAllItems();
        for (Medico m : listaMedicos) {
            if (m instanceof MedicoCirujano) {
                comboCirujanos.addItem((MedicoCirujano) m);
            }
        }

        comboPacientes.removeAllItems();
        for (Paciente p : listaPacientes) {
            comboPacientes.addItem(p);
        }
    }

    // ─── Limpieza de campos ──────────────────────────────────────────────────────
    private void limpiarCamposMedico() {
        txtNombreMedico.setText("");
        txtEspecialidad.setText("");
        txtRegistro.setText("");
        txtNroQuirofano.setText("");
        checkEsCirujano.setSelected(false);
        txtNroQuirofano.setEnabled(false);
    }

    private void limpiarCamposPaciente() {
        txtNombrePaciente.setText("");
        txtSaldo.setText("");
    }

    // ─── Utilidad ────────────────────────────────────────────────────────────────
    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error de validación",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Genera un texto con todos los pacientes registrados.
     */
    private String listarPacientes() {
        if (listaPacientes.isEmpty()) return "No hay pacientes registrados.";
        StringBuilder sb = new StringBuilder("=== Pacientes Registrados ===\n");
        for (Paciente p : listaPacientes) {
            sb.append("• ").append(p.getNombre())
                    .append(" | EPS: ").append(p.getEps())
                    .append(" | Saldo: $").append(p.getSaldo())
                    .append(" | Síntomas: ").append(p.getSintomas())
                    .append("\n");
        }
        return sb.toString();
    }

    // ─── main ────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalGUI::new);
    }
}