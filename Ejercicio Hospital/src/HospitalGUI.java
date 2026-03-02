import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // ── Paleta de colores ──────────────────────────────────────────────────────
    private static final Color C_FONDO = new Color(245, 248, 252);
    private static final Color C_HEADER_BG = new Color(26, 86, 149);
    private static final Color C_HEADER_TEXT = Color.WHITE;
    private static final Color C_BTN_PRIMARY = new Color(26, 86, 149);
    private static final Color C_BTN_SUCCESS = new Color(34, 139, 34);
    private static final Color C_BTN_DANGER = new Color(180, 30, 30);
    private static final Color C_BTN_WARN = new Color(200, 130, 0);
    private static final Color C_FIELD_BG = Color.WHITE;
    private static final Color C_FIELD_BORDER = new Color(180, 200, 220);
    private static final Color C_AREA_BG = new Color(250, 253, 255);
    private static final Color C_AREA_BORDER = new Color(180, 200, 220);
    private static final Color C_LABEL = new Color(40, 60, 90);
    private static final Color C_TEXT_OK = new Color(20, 120, 40);
    private static final Color C_TEXT_ERR = new Color(160, 20, 20);

    // ── Fuentes ────────────────────────────────────────────────────────────────
    private static final Font F_HEADER = new Font("Segoe UI", Font.BOLD, 17);
    private static final Font F_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_AREA = new Font("Consolas", Font.PLAIN, 12);

    // ── Costos ─────────────────────────────────────────────────────────────────
    private static final double COSTO_INFARTO = 500.0;
    private static final double COSTO_DOLOR = 200.0;
    private static final double COSTO_FIEBRE = 100.0;
    private static final double COSTO_OTRO = 150.0;

    // ── Datos ──────────────────────────────────────────────────────────────────
    private List<Medico> listaMedicos;
    private List<Paciente> listaPacientes;
    private Atencion atencion;

    // ── Control de flujo: pacientes que ya pasaron por Atencion ───────────────
    // Un paciente debe ser procesado en Atencion antes de poder operar o pagar.
    private Set<String> pacientesAtendidos;   // nombres con pabellon asignado
    private Set<String> pacientesOperados;    // nombres que ya fueron operados

    // ══════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════════════
    public HospitalGUI() {
        listaMedicos = new ArrayList<>();
        listaPacientes = new ArrayList<>();
        atencion = new Atencion();
        pacientesAtendidos = new HashSet<>();
        pacientesOperados = new HashSet<>();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored2) {
            }
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Hospital Lili's Valley");
            HospitalGUI gui = new HospitalGUI();
            frame.setContentPane(gui.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(640, 520));
            frame.pack();
            frame.setLocationRelativeTo(null);
            gui.inicializarEventos();
            frame.setVisible(true);
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INICIALIZAR EVENTOS + ESTILOS
    // ══════════════════════════════════════════════════════════════════════════
    public void inicializarEventos() {

        aplicarEstilos();

        // Boton global segun pestana activa
        procesarIngresoButton.addActionListener(e -> procesarSegunPestana());

        // ── Tab Medicos: ver pacientes ─────────────────────────────────────────
        verPacientesButton.addActionListener(e -> {
            if (listaPacientes.isEmpty()) {
                mostrarResultado("No hay pacientes registrados.", false);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("LISTA DE PACIENTES\n");
            sb.append("--------------------------------------------------\n");
            int i = 1;
            for (Paciente p : listaPacientes) {
                String estado = pacientesAtendidos.contains(p.getNombre()) ? "Atendido" : "Pendiente";
                sb.append(String.format("  %d. %s\n", i++, p.getNombre()));
                sb.append(String.format("     EPS      : %s\n", p.getEps()));
                sb.append(String.format("     Saldo    : $%.2f\n", p.getSaldo()));
                sb.append(String.format("     Sintomas : %s\n", p.getSintomas()));
                sb.append(String.format("     Estado   : %s\n", estado));
                sb.append("     - - - - - - - - - - - - - - - - - - -\n");
            }
            mostrarResultado(sb.toString(), true);
        });

        // ── Tab Medicos: asignar prioridad ────────────────────────────────────
        asignarPrioridadButton.addActionListener(e -> {
            if (listaMedicos.isEmpty()) {
                mostrarResultado("No hay medicos registrados. Registre uno primero.", false);
                return;
            }
            if (comboPacientes.getSelectedItem() == null) {
                mostrarResultado("No hay pacientes. Registre uno primero.", false);
                return;
            }

            Medico medicoEvaluador = listaMedicos.get(0);
            Paciente p = buscarPaciente(comboPacientes.getSelectedItem().toString());

            if (p == null || p.getSintomas().isEmpty()) {
                mostrarResultado("El paciente no tiene sintomas registrados.", false);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("EVALUACION DE PRIORIDAD\n");
            sb.append("--------------------------------------------------\n");
            sb.append(String.format("  Medico   : Dr. %s\n", medicoEvaluador.getNombre()));
            sb.append(String.format("  Paciente : %s\n\n", p.getNombre()));

            for (String sintoma : p.getSintomas()) {
                int prioridad;
                String nivel;
                switch (sintoma) {
                    case "Infarto":
                        prioridad = 1;
                        nivel = "URGENTE";
                        break;
                    case "Dolor":
                        prioridad = 2;
                        nivel = "MODERADO";
                        break;
                    case "Fiebre":
                        prioridad = 3;
                        nivel = "LEVE";
                        break;
                    default:
                        prioridad = 4;
                        nivel = "NORMAL";
                }
                sb.append(String.format("  %-10s  Prioridad %d  [%s]\n",
                        sintoma, prioridad, nivel));
            }
            mostrarResultado(sb.toString(), true);
        });

        // ── Tab Cirugias: operar ──────────────────────────────────────────────
        operarButton.addActionListener(e -> {
            if (comboCirujanos.getSelectedItem() == null) {
                setAreaText(areaResultado,
                        "No hay cirujanos registrados.\nRegistre un MedicoCirujano primero.", false);
                return;
            }
            if (comboPacientesCirugia.getSelectedItem() == null) {
                setAreaText(areaResultado, "Seleccione un paciente para operar.", false);
                return;
            }

            String nombrePaciente = comboPacientesCirugia.getSelectedItem().toString();

            // VALIDACION DE FLUJO: el paciente debe haber pasado por Atencion primero
            if (!pacientesAtendidos.contains(nombrePaciente)) {
                setAreaText(areaResultado,
                        "El paciente \"" + nombrePaciente + "\" aun no ha sido procesado en Atencion.\n\n" +
                                "Pasos requeridos:\n" +
                                "  1. Ir a la pestana Atencion\n" +
                                "  2. Seleccionar el paciente y presionar Procesar/Registrar\n" +
                                "  3. Volver aqui para operar.", false);
                return;
            }

            MedicoCirujano cirujano = buscarCirujano(comboCirujanos.getSelectedItem().toString());
            Paciente paciente = buscarPaciente(nombrePaciente);

            if (cirujano == null || paciente == null) {
                setAreaText(areaResultado, "Cirujano o paciente no encontrado.", false);
                return;
            }

            boolean disponible = checkDisponible.isSelected();
            cirujano.setQuirofanoDisponible(disponible);

            StringBuilder sb = new StringBuilder();
            sb.append("RESULTADO DE CIRUGIA\n");
            sb.append("--------------------------------------------------\n");
            sb.append(String.format("  Cirujano     : %s\n", cirujano.getNombre()));
            sb.append(String.format("  Especialidad : %s\n", cirujano.getEspecialidad()));
            sb.append(String.format("  Quirofano N. : %d\n", cirujano.getNumeroQuirofano()));
            sb.append(String.format("  Paciente     : %s\n", paciente.getNombre()));
            sb.append(String.format("  Estado       : %s\n\n",
                    disponible ? "Disponible" : "No disponible"));

            if (disponible) {
                sb.append("  Operacion realizada exitosamente.");
                pacientesOperados.add(nombrePaciente);  // marcar como operado para habilitar pago
                cirujano.operar();
            } else {
                sb.append("  No se puede operar: quirofano no disponible.");
            }

            setAreaText(areaResultado, sb.toString(), disponible);
        });

        // ── Tab Pagos: calcular costo al seleccionar paciente ─────────────────
        comboPacientesPago.addActionListener(e -> calcularCosto());

        // ── Tab Pagos: pagar ──────────────────────────────────────────────────
        pagarButton.addActionListener(e -> {
            if (comboPacientesPago.getSelectedItem() == null) {
                setAreaText(areaResultadoPago, "Seleccione un paciente.", false);
                return;
            }

            String nombrePaciente = comboPacientesPago.getSelectedItem().toString();

            // VALIDACION DE FLUJO: el paciente debe haber pasado por Atencion
            if (!pacientesAtendidos.contains(nombrePaciente)) {
                setAreaText(areaResultadoPago,
                        "El paciente \"" + nombrePaciente + "\" aun no ha sido procesado en Atencion.\n\n" +
                                "Pasos requeridos:\n" +
                                "  1. Ir a la pestana Atencion\n" +
                                "  2. Seleccionar el paciente y presionar Procesar/Registrar\n" +
                                "  3. Volver aqui para procesar el pago.", false);
                return;
            }

            Paciente p = buscarPaciente(nombrePaciente);
            if (p == null) {
                setAreaText(areaResultadoPago, "Paciente no encontrado.", false);
                return;
            }

            double costo = calcularCostoPaciente(p);
            boolean exito = p.verificarPresupuesto(costo);

            StringBuilder sb = new StringBuilder();
            sb.append("RESULTADO DE PAGO\n");
            sb.append("--------------------------------------------------\n");
            sb.append(String.format("  Paciente : %s\n", p.getNombre()));
            sb.append(String.format("  EPS      : %s\n", p.getEps()));
            sb.append(String.format("  Sintomas : %s\n", p.getSintomas()));
            sb.append(String.format("  Costo    : $%.2f\n", costo));
            sb.append(String.format("  Saldo    : $%.2f\n\n", p.getSaldo()));
            sb.append(exito ? "  Pago procesado exitosamente."
                    : "  Pago rechazado: fondos insuficientes.");

            setAreaText(areaResultadoPago, sb.toString(), exito);
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ESTILOS (aplicados despues de que el .form crea los componentes)
    // ══════════════════════════════════════════════════════════════════════════
    private void aplicarEstilos() {

        mainPanel.setBackground(C_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        // Encabezado
        SDAW.setBackground(C_HEADER_BG);
        SDAW.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 3, 0, new Color(15, 60, 110)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        for (Component c : SDAW.getComponents()) {
            if (c instanceof JLabel lbl) {
                lbl.setFont(F_HEADER);
                lbl.setForeground(C_HEADER_TEXT);
            }
        }

        tabbedPane1.setBackground(C_FONDO);
        tabbedPane1.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        // Boton global
        styleButton(procesarIngresoButton, C_BTN_PRIMARY, Color.WHITE);
        procesarIngresoButton.setText("Procesar / Registrar");
        procesarIngresoButton.setToolTipText(
                "Registra el elemento de la pestana activa (Medico, Paciente o Atencion)");

        styleTextArea(areaResultadoIngreso);

        // Tab Medicos
        styleField(txtNombreMedico, "Ej: Carlos Ramirez");
        styleField(txtEspecialidad, "Ej: Cardiologia");
        styleField(txtRegistro, "Ej: REG-2024-001");
        styleButton(verPacientesButton, C_BTN_PRIMARY, Color.WHITE);
        styleButton(asignarPrioridadButton, C_BTN_WARN, Color.WHITE);
        styleLabelsInParent(txtNombreMedico.getParent());

        // Tab Pacientes
        styleField(txtNombrePaciente, "Ej: Ana Torres");
        styleField(txtSaldo, "Ej: 1500.00");
        styleCombo(comboBox1);
        styleLabelsInParent(txtNombrePaciente.getParent());

        // Tab Cirugias
        styleCombo(comboCirujanos);
        styleCombo(comboPacientesCirugia);
        styleCheckBox(checkDisponible, "Quirofano disponible");
        styleButton(operarButton, C_BTN_DANGER, Color.WHITE);
        styleTextArea(areaResultado);
        styleLabelsInParent(comboCirujanos.getParent());

        // Tab Atencion
        styleCombo(comboPacientes);
        styleLabelsInParent(comboPacientes.getParent());

        // Tab Pagos
        styleCombo(comboPacientesPago);
        styleField(txtCosto, "Se calcula automaticamente");
        txtCosto.setEditable(false);
        txtCosto.setBackground(new Color(235, 243, 255));
        txtCosto.setFont(new Font("Consolas", Font.BOLD, 13));
        styleButton(pagarButton, C_BTN_SUCCESS, Color.WHITE);
        styleTextArea(areaResultadoPago);
        styleLabelsInParent(comboPacientesPago.getParent());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════════════════

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(F_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        Color hover = bg.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    private void styleField(JTextField field, String placeholder) {
        field.setFont(F_INPUT);
        field.setBackground(C_FIELD_BG);
        field.setForeground(Color.GRAY);
        field.setCaretColor(C_BTN_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_FIELD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        field.setToolTipText(placeholder);
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(C_LABEL);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void styleCombo(JComboBox combo) {
        combo.setFont(F_INPUT);
        combo.setBackground(C_FIELD_BG);
        combo.setForeground(C_LABEL);
        combo.setBorder(BorderFactory.createLineBorder(C_FIELD_BORDER, 1));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 30));
    }

    private void styleCheckBox(JCheckBox cb, String newText) {
        cb.setFont(F_LABEL);
        cb.setForeground(C_LABEL);
        cb.setBackground(C_FONDO);
        cb.setFocusPainted(false);
        if (newText != null) cb.setText(newText);
    }

    private void styleTextArea(JTextArea area) {
        if (area == null) return;
        area.setFont(F_AREA);
        area.setBackground(C_AREA_BG);
        area.setForeground(C_LABEL);
        area.setCaretColor(C_BTN_PRIMARY);
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        Container parent = area.getParent();
        if (parent instanceof JViewport) {
            Container sp = parent.getParent();
            if (sp instanceof JScrollPane) {
                ((JScrollPane) sp).setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C_AREA_BORDER, 1, true),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            }
        }
    }

    private void styleLabelsInParent(Container parent) {
        if (parent == null) return;
        for (Component c : parent.getComponents()) {
            if (c instanceof JLabel lbl) {
                lbl.setFont(F_LABEL);
                lbl.setForeground(C_LABEL);
            }
        }
    }

    private void setAreaText(JTextArea area, String texto, boolean ok) {
        if (area == null) return;
        area.setText(texto);
        area.setForeground(ok ? C_TEXT_OK : C_TEXT_ERR);
        area.setCaretPosition(0);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOGICA DE NEGOCIO
    // ══════════════════════════════════════════════════════════════════════════

    private void calcularCosto() {
        if (comboPacientesPago.getSelectedItem() == null) return;
        Paciente p = buscarPaciente(comboPacientesPago.getSelectedItem().toString());
        if (p == null) return;
        txtCosto.setForeground(C_LABEL);
        txtCosto.setText(String.format("$%.2f", calcularCostoPaciente(p)));
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
            }
        }
        return total;
    }

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
                mostrarResultado("Use los botones propios de la pestana activa.", true);
        }
    }

    private void registrarMedico() {
        String nombre = getRealText(txtNombreMedico);
        String especialidad = getRealText(txtEspecialidad);
        String registro = getRealText(txtRegistro);

        if (nombre.isEmpty() || especialidad.isEmpty() || registro.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Complete Nombre, Especialidad y Registro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(null,
                "Registrar como MedicoCirujano?",
                "Tipo de medico", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            String nqStr = JOptionPane.showInputDialog(null, "Numero de quirofano:");
            if (nqStr == null || nqStr.trim().isEmpty()) return;
            int nq;
            try {
                nq = Integer.parseInt(nqStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Numero de quirofano invalido.");
                return;
            }
            MedicoCirujano c = new MedicoCirujano(
                    nombre, "000", 0, "-", especialidad, registro, nq, false);
            listaMedicos.add(c);
            comboCirujanos.addItem(c.getNombre());
            mostrarResultado(
                    "CIRUJANO REGISTRADO\n" +
                            "--------------------------------------------------\n" +
                            String.format("  Nombre       : %s\n", c.getNombre()) +
                            String.format("  Especialidad : %s\n", especialidad) +
                            String.format("  Registro     : %s\n", registro) +
                            String.format("  Quirofano N. : %d\n", nq), true);
        } else {
            Medico m = new Medico(nombre, "000", 0, "-", especialidad, registro);
            listaMedicos.add(m);
            mostrarResultado(
                    "MEDICO REGISTRADO\n" +
                            "--------------------------------------------------\n" +
                            String.format("  Nombre       : %s\n", m.getNombre()) +
                            String.format("  Especialidad : %s\n", especialidad) +
                            String.format("  Registro     : %s\n", registro), true);
        }

        clearField(txtNombreMedico);
        clearField(txtEspecialidad);
        clearField(txtRegistro);
    }

    private void registrarPaciente() {
        String nombre = getRealText(txtNombrePaciente);
        String saldoStr = getRealText(txtSaldo);

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
            JOptionPane.showMessageDialog(null, "El saldo debe ser un numero valido.");
            return;
        }

        String eps = comboBox1.getSelectedItem().toString();
        String historial = "H" + (listaPacientes.size() + 1);
        Paciente p = new Paciente(nombre, "000", 0, "-", historial, eps, saldo);

        String[] opciones = {"Infarto", "Fiebre", "Dolor", "Otro"};
        String sintoma = (String) JOptionPane.showInputDialog(null,
                "Sintoma principal:", "Sintoma",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sintoma != null) p.agregarSintoma(sintoma);

        listaPacientes.add(p);
        // Los combos de Cirugia y Pagos NO se actualizan aqui.
        // El paciente solo aparecera en esos combos despues de ser atendido.
        comboPacientes.addItem(p.getNombre());

        mostrarResultado(
                "PACIENTE REGISTRADO\n" +
                        "--------------------------------------------------\n" +
                        String.format("  Nombre    : %s\n", p.getNombre()) +
                        String.format("  EPS       : %s\n", eps) +
                        String.format("  Saldo     : $%.2f\n", saldo) +
                        String.format("  Historial : %s\n", historial) +
                        String.format("  Sintoma   : %s\n\n", sintoma != null ? sintoma : "(ninguno)") +
                        "  Siguiente paso: ir a la pestana Atencion\n" +
                        "  para asignar pabellon antes de operar o pagar.\n\n" +
                        "  Tabla de costos de referencia:\n" +
                        String.format("    Infarto  $%.2f\n", COSTO_INFARTO) +
                        String.format("    Dolor    $%.2f\n", COSTO_DOLOR) +
                        String.format("    Fiebre   $%.2f\n", COSTO_FIEBRE) +
                        String.format("    Otro     $%.2f\n", COSTO_OTRO), true);

        clearField(txtNombrePaciente);
        clearField(txtSaldo);
    }

    /**
     * Procesa el ingreso del paciente en Atencion.
     * Este es el paso obligatorio que habilita Cirugia y Pagos para ese paciente.
     */
    private void procesarIngreso() {
        if (comboPacientes.getSelectedItem() == null) {
            mostrarResultado("No hay pacientes registrados. Registre uno primero.", false);
            return;
        }
        Paciente p = buscarPaciente(comboPacientes.getSelectedItem().toString());
        if (p == null) {
            mostrarResultado("Paciente no encontrado.", false);
            return;
        }

        // Llamar a la logica de negocio de Atencion
        String resultado = atencion.procesarIngreso(p);

        // Registrar que este paciente ya fue atendido
        pacientesAtendidos.add(p.getNombre());

        // Ahora habilitarlo en Cirugia y Pagos
        comboPacientesCirugia.addItem(p.getNombre());
        comboPacientesPago.addItem(p.getNombre());

        mostrarResultado(
                "INGRESO PROCESADO - PABELLON ASIGNADO\n" +
                        "--------------------------------------------------\n" +
                        resultado + "\n" +
                        String.format("  EPS      : %s\n", p.getEps()) +
                        String.format("  Saldo    : $%.2f\n", p.getSaldo()) +
                        String.format("  Sintomas : %s\n\n", p.getSintomas()) +
                        "  El paciente ya puede ser operado o procesar pago.", true);
    }

    // ── Mostrar en consola general ─────────────────────────────────────────────
    private void mostrarResultado(String texto, boolean ok) {
        if (areaResultadoIngreso != null) {
            setAreaText(areaResultadoIngreso, texto, ok);
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarResultado(String texto) {
        mostrarResultado(texto, true);
    }

    private void agregarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.append(texto + "\n");
            areaResultadoIngreso.setCaretPosition(
                    areaResultadoIngreso.getDocument().getLength());
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Placeholder helpers ────────────────────────────────────────────────────
    private String getRealText(JTextField field) {
        if (field.getForeground().equals(Color.GRAY)) return "";
        return field.getText().trim();
    }

    private void clearField(JTextField field) {
        field.setText("");
        field.dispatchEvent(new java.awt.event.FocusEvent(
                field, java.awt.event.FocusEvent.FOCUS_LOST));
    }

    // ── Busquedas ──────────────────────────────────────────────────────────────
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