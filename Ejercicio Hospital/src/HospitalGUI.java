import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del Sistema Hospital Lili's Valley.
 * <p>
 * Esta clase maneja toda la interfaz gráfica. Cada pestaña tiene
 * su propia sección con los componentes y botones que la controlan.
 * <p>
 * Pestañas disponibles:
 * 0 - Medicos   -> registrar médicos y cirujanos
 * 1 - Pacientes -> registrar pacientes
 * 2 - Cirugias  -> realizar operaciones
 * 3 - Atencion  -> asignar pacientes a pabellones según EPS
 * 4 - Pagos     -> verificar y cobrar según síntomas
 */
public class HospitalGUI {

    // Paneles principales que IntelliJ conecta con el archivo .form
    private JPanel mainPanel;
    private JPanel SDAW;
    private JTabbedPane tabbedPane1; // el componente de pestañas

    // Campos de la pestaña Medicos
    private JTextField txtNombreMedico;      // nombre del médico a registrar
    private JTextField txtEspecialidad;      // su especialidad (ej: Cardiología)
    private JTextField txtRegistro;          // número de registro profesional
    private JButton verPacientesButton;      // muestra todos los pacientes registrados
    private JButton asignarPrioridadButton;  // evalúa síntomas y asigna prioridad

    // Campos de la pestaña Pacientes
    private JTextField txtNombrePaciente; // nombre del paciente a registrar
    private JComboBox comboBox1;          // para elegir la EPS del paciente
    private JTextField txtSaldo;          // dinero disponible del paciente

    // Campos de la pestaña Cirugias
    private JComboBox comboCirujanos;        // lista de cirujanos disponibles
    private JComboBox comboPacientesCirugia; // paciente que va a ser operado
    private JCheckBox checkDisponible;       // indica si el quirófano está libre
    private JButton operarButton;            // ejecuta la operación
    private JTextArea areaResultado;         // muestra el resultado de la cirugía

    // Campos de la pestaña Atencion
    private JComboBox comboPacientes; // lista de pacientes para asignar pabellón

    // Campos de la pestaña Pagos
    private JComboBox comboPacientesPago; // paciente que va a pagar
    private JTextField txtCosto;          // muestra el costo calculado automáticamente
    private JButton pagarButton;          // ejecuta el cobro
    private JTextArea areaResultadoPago;  // muestra si el pago fue exitoso o no

    // Botón general y área de resultados (están fuera de las pestañas, abajo de todo)
    private JButton procesarIngresoButton;   // botón que actúa según la pestaña activa
    private JTextArea areaResultadoIngreso;  // aquí aparecen los mensajes del sistema

    // Costo fijo por cada tipo de síntoma
    private static final double COSTO_INFARTO = 500.0;
    private static final double COSTO_DOLOR = 200.0;
    private static final double COSTO_FIEBRE = 100.0;
    private static final double COSTO_OTRO = 150.0;

    // Listas donde guardamos los datos en memoria mientras corre el programa
    private List<Medico> listaMedicos;
    private List<Paciente> listaPacientes;
    private Atencion atencion; // objeto que maneja el ingreso a pabellones

    /**
     * Constructor: inicializa las listas vacías y el objeto de atención.
     * IntelliJ llama esto automáticamente al abrir la ventana.
     */
    public HospitalGUI() {
        listaMedicos = new ArrayList<>();
        listaPacientes = new ArrayList<>();
        atencion = new Atencion();
    }

    /**
     * Punto de entrada del programa.
     * Crea la ventana, le pone el panel del .form y la muestra en pantalla.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Hospital Lili's Valley");
            HospitalGUI gui = new HospitalGUI();
            frame.setContentPane(gui.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null); // centrar en pantalla
            gui.inicializarEventos(); // conectar botones DESPUÉS de cargar el panel
            frame.setVisible(true);
        });
    }

    /**
     * Conecta cada botón con lo que debe hacer cuando se presiona.
     * Se llama una sola vez al iniciar, después de cargar la ventana.
     */
    public void inicializarEventos() {

        // El botón "Procesar Ingreso" hace cosas distintas según la pestaña abierta
        procesarIngresoButton.addActionListener(e -> procesarSegunPestana());

        // Botón "Ver Pacientes" (pestaña Medicos)
        // Muestra la lista de todos los pacientes que hay registrados
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

        // Botón "Asignar Prioridad" (pestaña Medicos)
        // El primer médico registrado evalúa los síntomas del paciente seleccionado
        // y les asigna un nivel de urgencia del 1 (crítico) al 4 (normal)
        asignarPrioridadButton.addActionListener(e -> {
            if (listaMedicos.isEmpty()) {
                mostrarResultado("No hay médicos registrados. Registre uno primero.");
                return;
            }
            if (comboPacientes.getSelectedItem() == null) {
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

        // Botón "Operar" (pestaña Cirugias)
        // Verifica si el quirófano está disponible y realiza la operación
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

            cirujano.operar(); // llama al método operar() de MedicoCirujano
            areaResultado.setText(sb.toString());
        });

        // Pestaña Pagos: cuando el usuario cambia el paciente, el costo se calcula solo
        comboPacientesPago.addActionListener(e -> calcularCosto());

        // Botón "Pagar" (pestaña Pagos)
        // Descuenta el costo del saldo del paciente si tiene suficiente dinero
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
            sb.append(exito ? "✔ Pago exitoso." : "✘ Pago rechazado: fondos insuficientes.");
            areaResultadoPago.setText(sb.toString());
        });
    }

    /**
     * Calcula el costo del paciente seleccionado en la pestaña Pagos
     * y lo muestra automáticamente en el campo txtCosto.
     */
    private void calcularCosto() {
        if (comboPacientesPago.getSelectedItem() == null) return;
        Paciente p = buscarPaciente(comboPacientesPago.getSelectedItem().toString());
        if (p == null) return;
        txtCosto.setText("$" + calcularCostoPaciente(p));
    }

    /**
     * Suma el costo de todos los síntomas que tiene el paciente.
     * Infarto = $500, Dolor = $200, Fiebre = $100, Otro = $150
     */
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

    /**
     * Decide qué hacer cuando se presiona "Procesar Ingreso",
     * dependiendo de en qué pestaña está parado el usuario.
     * <p>
     * Pestaña 0 (Medicos)   → registra un médico o cirujano
     * Pestaña 1 (Pacientes) → registra un paciente
     * Pestaña 3 (Atencion)  → asigna pabellón según EPS
     * Otras pestañas        → muestra un aviso
     */
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

    /**
     * Lee los campos de la pestaña Medicos y crea un Medico o MedicoCirujano.
     * Si el usuario dice que es cirujano, también le pide el número de quirófano.
     * Al terminar, limpia los campos para el siguiente registro.
     */
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

        // Pregunta si es cirujano o médico normal
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
            comboCirujanos.addItem(c.getNombre()); // aparece en la pestaña Cirugias
            mostrarResultado("✔ Cirujano registrado: " + c.getNombre()
                    + " | Especialidad: " + especialidad
                    + " | Quirófano: " + nq);
        } else {
            Medico m = new Medico(nombre, "000", 0, "-", especialidad, registro);
            listaMedicos.add(m);
            mostrarResultado("✔ Médico registrado: " + m.getNombre()
                    + " | Especialidad: " + especialidad);
        }

        // Limpiar campos
        txtNombreMedico.setText("");
        txtEspecialidad.setText("");
        txtRegistro.setText("");
    }

    /**
     * Lee los campos de la pestaña Pacientes y crea un Paciente nuevo.
     * Le pide el síntoma principal con un popup y lo agrega a la lista.
     * También actualiza los combos de Cirugias, Atencion y Pagos.
     * Al terminar, limpia los campos para el siguiente registro.
     */
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
        String historial = "H" + (listaPacientes.size() + 1); // genera ID automático
        Paciente p = new Paciente(nombre, "000", 0, "-", historial, eps, saldo);

        // Popup para elegir el síntoma principal
        String[] opciones = {"Infarto", "Fiebre", "Dolor", "Otro"};
        String sintoma = (String) JOptionPane.showInputDialog(null,
                "Síntoma principal:", "Síntoma",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sintoma != null) p.agregarSintoma(sintoma);

        listaPacientes.add(p);

        // Agregar el nombre del paciente a todos los combos que lo necesitan
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

        // Limpiar campos
        txtNombrePaciente.setText("");
        txtSaldo.setText("");
    }

    /**
     * Toma el paciente seleccionado en la pestaña Atencion
     * y lo asigna al pabellón que le corresponde según su EPS.
     * La lógica de asignación está en la clase Atencion.
     */
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

    /**
     * Muestra un texto en el área de resultados (la caja de texto de abajo).
     * Si por alguna razón esa caja no existe, muestra un popup en su lugar.
     */
    private void mostrarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.setText(texto);
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Agrega texto al área de resultados sin borrar lo que ya había.
     * Mueve el scroll al final para que siempre se vea lo último.
     */
    private void agregarResultado(String texto) {
        if (areaResultadoIngreso != null) {
            areaResultadoIngreso.append(texto + "\n");
            areaResultadoIngreso.setCaretPosition(areaResultadoIngreso.getDocument().getLength());
        } else {
            JOptionPane.showMessageDialog(null, texto, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Busca un paciente en la lista por su nombre (sin importar mayúsculas).
     * Devuelve el paciente si lo encuentra, o null si no existe.
     */
    private Paciente buscarPaciente(String nombre) {
        for (Paciente p : listaPacientes)
            if (p.getNombre().equalsIgnoreCase(nombre)) return p;
        return null;
    }

    /**
     * Busca un cirujano dentro de la lista de médicos.
     * Solo devuelve el que sea instancia de MedicoCirujano y tenga ese nombre.
     * Devuelve null si no lo encuentra.
     */
    private MedicoCirujano buscarCirujano(String nombre) {
        for (Medico m : listaMedicos)
            if (m instanceof MedicoCirujano && m.getNombre().equalsIgnoreCase(nombre))
                return (MedicoCirujano) m;
        return null;
    }
}
