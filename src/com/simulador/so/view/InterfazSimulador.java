package com.simulador.so.view;

import com.simulador.so.controller.SimuladorCore;
import com.simulador.so.model.AlgoritmoPlanificacion;
import com.simulador.so.model.AlgoritmoReemplazo;
import com.simulador.so.model.EstadoProceso;
import com.simulador.so.model.Marco;
import com.simulador.so.model.Proceso;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Interfaz gráfica principal del simulador de sistema operativo.
 * Integra las simulaciones de planificación de CPU y memoria virtual.
 * Proporciona visualización en tiempo real del diagrama de Gantt y estado de la RAM.
 * 
 * @author SimuladorSO
 * @version 1.0
 */
public class InterfazSimulador extends JFrame {

    // ==================== COMPONENTES PRINCIPALES ====================
    
    /** Núcleo del simulador que coordina CPU y memoria */
    private final SimuladorCore core = new SimuladorCore();
    
    /** Panel de pestañas para alternar entre CPU y memoria */
    private final JTabbedPane tabs = new JTabbedPane();

    // ==================== CONSTANTES DE ESTILO ====================
    
    /** Color de fondo principal de la aplicación */
    private static final Color BACKGROUND = new Color(15, 15, 35);
    
    /** Color de fondo de paneles secundarios */
    private static final Color PANEL_BG   = new Color(25, 25, 55);
    
    /** Color de fondo de cabeceras */
    private static final Color HEADER_BG  = new Color(35, 35, 75);
    
    /** Color de bordes */
    private static final Color BORDER     = new Color(60, 60, 120);
    
    /** Color de texto estándar */
    private static final Color TEXT       = new Color(220, 220, 255);
    
    /** Color de acento principal (cian) */
    private static final Color ACCENT     = new Color(0, 240, 255);
    
    /** Color de acento secundario (rosa) */
    private static final Color ACCENT2    = new Color(255, 42, 109);
    
    /** Color para éxito/acierto (verde) */
    private static final Color SUCCESS    = new Color(5, 255, 161);
    
    /** Color para advertencia (amarillo) */
    private static final Color WARNING    = new Color(255, 204, 0);
    
    /** Color para error/fallo (rojo) */
    private static final Color DANGER     = new Color(255, 0, 85);

    /** Fuente monoespaciada para datos técnicos */
    private static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);
    
    /** Fuente en negrita para etiquetas */
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    
    /** Fuente de título */
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 16);

    /** Paleta de colores predefinida para los procesos */
    private static final Color[] PROC_COLORS = {
            new Color(0, 240, 255), new Color(255, 42, 109), new Color(5, 255, 161),
            new Color(255, 204, 0), new Color(180, 80, 255), new Color(255, 120, 50),
            new Color(0, 200, 255), new Color(255, 80, 180),
    };

    // ==================== COMPONENTES DE CPU ====================
    
    /** Modelo de tabla para entrada de procesos */
    private DefaultTableModel modeloProcesos;
    
    /** Tabla de entrada de procesos */
    private JTable tablaProcesos;
    
    /** Selector de algoritmo de planificación */
    private JComboBox<AlgoritmoPlanificacion> comboAlgoritmoCPU;
    
    /** Selector de quantum para Round Robin */
    private JSpinner spinnerQuantum;
    
    /** Panel para dibujar el diagrama de Gantt */
    private PanelGantt panelGantt;
    
    /** Panel de leyenda de colores de procesos */
    private JPanel panelLeyenda;

    /** Tabla de estadísticas finales de procesos */
    private JTable tablaEstadisticas;
    
    /** Modelo de la tabla de estadísticas */
    private DefaultTableModel modeloEstadisticas;

    /** Etiquetas de estado en tiempo real */
    private JLabel lblReloj, lblEjecucion, lblListos, lblBloqueados, lblTerminados;
    
    /** Temporizador para simulación automática de CPU */
    private javax.swing.Timer timerCPU;
    
    /** Lista de procesos actuales en simulación */
    private List<Proceso> procesosActuales = new ArrayList<>();

    // ==================== COMPONENTES DE MEMORIA ====================
    
    /** Selector de número de marcos de RAM */
    private JSpinner spinnerMarcos;
    
    /** Selector de tamaño de página (no utilizado actualmente) */
    private JSpinner spinnerPaginaSize;
    
    /** Campo de texto para la secuencia de páginas */
    private JTextField txtSecuencia;
    
    /** Selector de algoritmo de reemplazo */
    private JComboBox<AlgoritmoReemplazo> comboAlgMemoria;
    
    /** Panel visual de marcos de RAM */
    private JPanel panelRAM;
    
    /** Panel visual de secuencia de páginas */
    private JPanel panelSecuencia;
    
    /** Área de texto para el log de eventos de memoria */
    private JTextArea txtLogMemoria;
    
    /** Etiquetas de estadísticas de memoria */
    private JLabel lblFallos, lblHits, lblPasoMemoria;
    
    /** Temporizador para simulación automática de memoria */
    private javax.swing.Timer timerMemoria;

    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructor que inicializa la ventana principal y sus componentes.
     * Configura el look and feel, las pestañas y los paneles de simulación.
     */
    public InterfazSimulador() {
        setTitle("Simulador CPU + Memoria Virtual");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1450, 920);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);

        // Configurar estilo personalizado de las pestañas
        tabs.setFont(FONT_BOLD);
        tabs.setBackground(BACKGROUND);
        tabs.setForeground(TEXT);
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? HEADER_BG : PANEL_BG);
                g.fillRect(x, y, w, h);
                if (isSelected) {
                    g.setColor(ACCENT);
                    g.fillRect(x, y + h - 3, w, 3);
                }
            }
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {}
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
        });

        // Agregar las pestañas principales
        tabs.addTab("   Planificador CPU  ", crearPanelCPU());
        tabs.addTab("   Memoria Virtual  ", crearPanelMemoria());

        add(tabs, BorderLayout.CENTER);

        // Barra de estado inferior
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(HEADER_BG);
        status.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel lblStatus = new JLabel("  Carga los datos de prueba o ingresa valores personalizados.");
        lblStatus.setForeground(TEXT);
        lblStatus.setFont(FONT_MONO);
        status.add(lblStatus, BorderLayout.WEST);
        add(status, BorderLayout.SOUTH);
    }

    // ==================== MÉTODOS DE CONSTRUCCIÓN DE PANELES ====================
    
    /**
     * Crea el panel de simulación de planificación de CPU.
     * Incluye tabla de procesos, controles de algoritmo, visualización Gantt y estadísticas.
     * 
     * @return Panel configurado para la simulación de CPU
     */
    private JPanel crearPanelCPU() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ==================== PANEL DE ENTRADA (IZQUIERDA) ====================
        JPanel panelEntrada = new JPanel(new BorderLayout(5, 5));
        panelEntrada.setBackground(BACKGROUND);
        panelEntrada.setPreferredSize(new Dimension(380, 0));
        panelEntrada.setBorder(crearBorde(" Configuración de Procesos"));

        // Configuración de la tabla de procesos
        String[] cols = {"ID", "Llegada", "Ráfaga", "Prioridad"};
        modeloProcesos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return col != 0; }
            public Class<?> getColumnClass(int col) { return Integer.class; }
        };
        tablaProcesos = new JTable(modeloProcesos);
        estilizarTabla(tablaProcesos);
        JScrollPane scrollTabla = new JScrollPane(tablaProcesos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(BORDER));

        // Botones de gestión de la tabla
        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotonesTabla.setBackground(BACKGROUND);
        JButton btnAdd = crearBoton("+ Proceso", SUCCESS);
        JButton btnDel = crearBoton("- Eliminar", DANGER);
        JButton btnCaso1 = crearBoton("Cargar Caso 1", WARNING);
        JButton btnLimpiar = crearBoton("Limpiar", TEXT);

        btnAdd.addActionListener(e -> {
            int id = modeloProcesos.getRowCount() + 1;
            modeloProcesos.addRow(new Object[]{id, 0, 5, 1});
        });
        btnDel.addActionListener(e -> {
            int row = tablaProcesos.getSelectedRow();
            if (row >= 0) modeloProcesos.removeRow(row);
        });
        btnCaso1.addActionListener(e -> cargarCaso1CPU());
        btnLimpiar.addActionListener(e -> modeloProcesos.setRowCount(0));

        panelBotonesTabla.add(btnAdd);
        panelBotonesTabla.add(btnDel);
        panelBotonesTabla.add(btnCaso1);
        panelBotonesTabla.add(btnLimpiar);

        JPanel panelTablaWrapper = new JPanel(new BorderLayout(5, 5));
        panelTablaWrapper.setBackground(BACKGROUND);
        panelTablaWrapper.add(panelBotonesTabla, BorderLayout.NORTH);
        panelTablaWrapper.add(scrollTabla, BorderLayout.CENTER);

        // Panel de selección de algoritmo
        JPanel panelAlgoritmo = new JPanel(new GridLayout(4, 2, 5, 5));
        panelAlgoritmo.setBackground(BACKGROUND);
        panelAlgoritmo.setBorder(crearBorde(" Algoritmo"));

        comboAlgoritmoCPU = new JComboBox<>(AlgoritmoPlanificacion.values());
        comboAlgoritmoCPU.setFont(FONT_MONO);
        comboAlgoritmoCPU.setBackground(PANEL_BG);
        comboAlgoritmoCPU.setForeground(TEXT);

        spinnerQuantum = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        spinnerQuantum.setFont(FONT_MONO);

        panelAlgoritmo.add(crearLabel("Algoritmo:"));
        panelAlgoritmo.add(comboAlgoritmoCPU);
        panelAlgoritmo.add(crearLabel("Quantum (RR):"));
        panelAlgoritmo.add(spinnerQuantum);
        panelAlgoritmo.add(new JLabel());
        panelAlgoritmo.add(new JLabel());

        // Botones de simulación
        JPanel panelSim = new JPanel(new GridLayout(1, 3, 5, 5));
        panelSim.setBackground(BACKGROUND);
        JButton btnIniciar = crearBoton(" INICIAR", ACCENT);
        JButton btnPaso = crearBoton(" PASO", WARNING);
        JButton btnReset = crearBoton(" RESET", DANGER);

        btnIniciar.addActionListener(e -> iniciarSimulacionCPU(true));
        btnPaso.addActionListener(e -> iniciarSimulacionCPU(false));
        btnReset.addActionListener(e -> resetSimulacionCPU());

        panelSim.add(btnIniciar);
        panelSim.add(btnPaso);
        panelSim.add(btnReset);

        JPanel panelSurEntrada = new JPanel(new BorderLayout(5, 5));
        panelSurEntrada.setBackground(BACKGROUND);
        panelSurEntrada.add(panelAlgoritmo, BorderLayout.CENTER);
        panelSurEntrada.add(panelSim, BorderLayout.SOUTH);

        panelEntrada.add(panelTablaWrapper, BorderLayout.CENTER);
        panelEntrada.add(panelSurEntrada, BorderLayout.SOUTH);

        // ==================== PANEL CENTRAL ====================
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBackground(BACKGROUND);

        // Panel de estado en tiempo real
        JPanel panelEstado = new JPanel(new GridLayout(1, 5, 10, 5));
        panelEstado.setBackground(HEADER_BG);
        panelEstado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblReloj = crearLabelEstado(" Tick: 0", ACCENT);
        lblEjecucion = crearLabelEstado(" Ejecución: -", SUCCESS);
        lblListos = crearLabelEstado(" Listos: -", WARNING);
        lblBloqueados = crearLabelEstado(" Bloqueados: -", DANGER);
        lblTerminados = crearLabelEstado("✓ Terminados: -", TEXT);

        panelEstado.add(lblReloj);
        panelEstado.add(lblEjecucion);
        panelEstado.add(lblListos);
        panelEstado.add(lblBloqueados);
        panelEstado.add(lblTerminados);

        // Panel del diagrama de Gantt (con scroll horizontal)
        panelGantt = new PanelGantt();

        JScrollPane scrollGantt = new JScrollPane(panelGantt);
        scrollGantt.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollGantt.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollGantt.setBorder(crearBorde(" Diagrama de Gantt"));
        scrollGantt.setPreferredSize(new Dimension(0, 195));
        scrollGantt.getHorizontalScrollBar().setBackground(BACKGROUND);
        scrollGantt.getHorizontalScrollBar().setUnitIncrement(16);

        // Panel de leyenda de colores
        panelLeyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelLeyenda.setBackground(BACKGROUND);
        panelLeyenda.setBorder(crearBorde(" Leyenda"));
        panelLeyenda.setPreferredSize(new Dimension(0, 60));

        panelCentro.add(panelEstado, BorderLayout.NORTH);
        panelCentro.add(scrollGantt, BorderLayout.CENTER);
        panelCentro.add(panelLeyenda, BorderLayout.SOUTH);

        // ==================== PANEL DERECHO (ESTADÍSTICAS) ====================
        JPanel panelDer = new JPanel(new BorderLayout(5, 5));
        panelDer.setBackground(BACKGROUND);
        panelDer.setPreferredSize(new Dimension(420, 0));
        panelDer.setBorder(crearBorde(" Estadísticas Finales"));

        String[] colsStats = {"Proc", "Llegada", "Ráfaga", "Fin", "T.Retorno", "T.Espera"};
        modeloEstadisticas = new DefaultTableModel(colsStats, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaEstadisticas = new JTable(modeloEstadisticas);
        estilizarTabla(tablaEstadisticas);

        JScrollPane scrollStats = new JScrollPane(tablaEstadisticas);
        scrollStats.setBorder(BorderFactory.createLineBorder(BORDER));
        panelDer.add(scrollStats, BorderLayout.CENTER);

        // Ensamblar el panel completo
        panel.add(panelEntrada, BorderLayout.WEST);
        panel.add(panelCentro, BorderLayout.CENTER);
        panel.add(panelDer, BorderLayout.EAST);

        return panel;
    }

    /**
     * Carga un caso de prueba predefinido para la simulación de CPU.
     * Configura 4 procesos con diferentes tiempos de llegada y prioridades.
     */
    private void cargarCaso1CPU() {
        modeloProcesos.setRowCount(0);
        modeloProcesos.addRow(new Object[]{1, 0, 8, 3});
        modeloProcesos.addRow(new Object[]{2, 1, 4, 1});
        modeloProcesos.addRow(new Object[]{3, 2, 9, 4});
        modeloProcesos.addRow(new Object[]{4, 3, 5, 2});
        comboAlgoritmoCPU.setSelectedItem(AlgoritmoPlanificacion.SRTF);
    }

    // ==================== MÉTODOS DE CONTROL DE CPU ====================
    
    /**
     * Inicia la simulación de CPU en modo automático o paso a paso.
     * 
     * @param autoPlay true para simulación automática con temporizador, false para paso a paso
     */
    private void iniciarSimulacionCPU(boolean autoPlay) {
        // Lógica de pausa/reanudación al presionar el botón PASO
        if (!autoPlay) {
            if (core.getPlanificadorCPU().isSimulando()) {
                if (timerCPU != null && timerCPU.isRunning()) {
                    timerCPU.stop();
                    return;
                } else if (timerCPU != null) {
                    timerCPU.start();
                    return;
                }
            }
        }

        // Prevenir reinicios accidentales
        if (core.getPlanificadorCPU().isSimulando() && autoPlay) return;

        // Inicializar si es la primera vez
        if (!core.getPlanificadorCPU().isSimulando()) {
            procesosActuales.clear();
            for (int i = 0; i < modeloProcesos.getRowCount(); i++) {
                int id = (Integer) modeloProcesos.getValueAt(i, 0);
                int llegada = (Integer) modeloProcesos.getValueAt(i, 1);
                int rafaga = (Integer) modeloProcesos.getValueAt(i, 2);
                int prioridad = (Integer) modeloProcesos.getValueAt(i, 3);
                Proceso p = new Proceso(id, llegada, rafaga, prioridad);
                p.setColor(PROC_COLORS[(id - 1) % PROC_COLORS.length]);
                procesosActuales.add(p);
            }

            if (procesosActuales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay procesos para simular.");
                return;
            }

            AlgoritmoPlanificacion alg = (AlgoritmoPlanificacion) comboAlgoritmoCPU.getSelectedItem();
            int q = (Integer) spinnerQuantum.getValue();

            core.iniciarCPU(procesosActuales, alg, q);
            modeloEstadisticas.setRowCount(0);
        }

        // Configurar el modo de simulación
        if (autoPlay) {
            if (timerCPU != null) timerCPU.stop();
            timerCPU = new javax.swing.Timer(600, e -> ejecutarTickCPU());
            timerCPU.start();
        } else {
            ejecutarTickCPU();
        }
    }

    /**
     * Reinicia completamente la simulación de CPU.
     * Limpia todos los estados, estadísticas y visualizaciones.
     */
    private void resetSimulacionCPU() {
        if (timerCPU != null) timerCPU.stop();
        core.resetCPU();
        lblReloj.setText(" Tick: 0");
        lblEjecucion.setText(" Ejecución: -");
        lblListos.setText(" Listos: -");
        lblBloqueados.setText(" Bloqueados: -");
        lblTerminados.setText(" Terminados: -");
        modeloEstadisticas.setRowCount(0);
        panelGantt.setDatos(new ArrayList<>(), new ArrayList<>(), 0);
        panelLeyenda.removeAll();
        panelLeyenda.revalidate();
        panelLeyenda.repaint();
    }

    /**
     * Ejecuta un tick de la simulación de CPU y actualiza la interfaz.
     */
    private void ejecutarTickCPU() {
        boolean continua = core.tickCPU();
        actualizarUICPU();
        if (!continua) {
            if (timerCPU != null) timerCPU.stop();
            mostrarEstadisticasCPU();
        }
    }

    /**
     * Actualiza todos los componentes visuales de la interfaz de CPU.
     * Refleja el estado actual de colas, proceso en ejecución y diagrama de Gantt.
     */
    private void actualizarUICPU() {
        var plan = core.getPlanificadorCPU();
        int tick = plan.getTickActual();
        Proceso ejec = plan.getEnEjecucion();

        lblReloj.setText(" Tick: " + tick);
        lblEjecucion.setText(" Ejecución: " + (ejec != null ? ejec.getNombre() : "Idle"));

        // Actualizar cola de listos
        String listosStr = plan.getColaListos().stream()
                .filter(p -> p.getEstado() == EstadoProceso.LISTO)
                .map(Proceso::getNombre)
                .reduce((a, b) -> a + "," + b).orElse("-");
        lblListos.setText(" Listos: " + listosStr);

        // Actualizar cola de bloqueados
        String bloqStr = plan.getColaBloqueados().isEmpty() ? "-" :
                plan.getColaBloqueados().stream().map(Proceso::getNombre)
                        .reduce((a, b) -> a + "," + b).orElse("-");
        lblBloqueados.setText(" Bloqueados: " + bloqStr);

        // Actualizar cola de terminados
        String termStr = plan.getColaTerminados().stream().map(Proceso::getNombre)
                .reduce((a, b) -> a + "," + b).orElse("-");
        lblTerminados.setText(" Terminados: " + termStr);

        // Actualizar diagrama de Gantt
        panelGantt.setDatos(plan.getHistorial(), procesosActuales, tick);

        // Actualizar leyenda de colores
        panelLeyenda.removeAll();
        for (Proceso p : procesosActuales) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            item.setBackground(BACKGROUND);
            JLabel cuadro = new JLabel("  ");
            cuadro.setOpaque(true);
            cuadro.setBackground(p.getColor());
            cuadro.setBorder(BorderFactory.createLineBorder(TEXT));
            item.add(cuadro);
            item.add(crearLabel(p.getNombre() + " (Pri:" + p.getPrioridad() + ")"));
            panelLeyenda.add(item);
        }
        panelLeyenda.revalidate();
        panelLeyenda.repaint();
    }

    /**
     * Muestra las estadísticas finales de todos los procesos completados.
     * Calcula y muestra promedios de tiempo de retorno y espera.
     */
    private void mostrarEstadisticasCPU() {
        modeloEstadisticas.setRowCount(0);
        List<Proceso> fuenteProcesos = core.getPlanificadorCPU().getColaTerminados();

        if (fuenteProcesos == null || fuenteProcesos.isEmpty()) {
            fuenteProcesos = core.getPlanificadorCPU().getProcesos();
        }

        // Eliminar duplicados usando un mapa por ID
        Map<Integer, Proceso> mapaUnicos = new LinkedHashMap<>();
        for (Proceso p : fuenteProcesos) {
            mapaUnicos.put(p.getId(), p);
        }

        // Ordenar por ID
        List<Proceso> procesosFinales = new ArrayList<>(mapaUnicos.values());
        procesosFinales.sort(java.util.Comparator.comparingInt(Proceso::getId));

        double sumEspera = 0.0;
        double sumRetorno = 0.0;

        for (Proceso p : procesosFinales) {
            int retorno = p.getTiempoRetorno();
            int espera = p.getTiempoEspera();

            sumEspera += (double) espera;
            sumRetorno += (double) retorno;

            modeloEstadisticas.addRow(new Object[]{
                    p.getNombre(),
                    p.getLlegada(),
                    p.getRafaga(),
                    p.getTiempoFin(),
                    retorno,
                    espera
            });
        }

        // Fila separadora
        modeloEstadisticas.addRow(new Object[]{"", "", "", "", "", ""});

        // Calcular y mostrar promedios
        double promRetorno = sumRetorno / procesosFinales.size();
        double promEspera = sumEspera / procesosFinales.size();

        modeloEstadisticas.addRow(new Object[]{
                "PROMEDIOS",
                "",
                "",
                "",
                String.format("%.2f", promRetorno),
                String.format("%.2f", promEspera)
        });
    }

    // ==================== PANEL DE MEMORIA ====================
    
    /**
     * Crea el panel de simulación de memoria virtual.
     * Incluye controles de configuración y visualización de marcos y secuencia.
     * 
     * @return Panel configurado para la simulación de memoria
     */
    private JPanel crearPanelMemoria() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de configuración (izquierda)
        JPanel panelConfig = new JPanel(new BorderLayout(5, 5));
        panelConfig.setBackground(BACKGROUND);
        panelConfig.setPreferredSize(new Dimension(350, 0));
        panelConfig.setBorder(crearBorde(" Configuración Memoria"));

        JPanel gridConfig = new JPanel(new GridLayout(5, 2, 8, 8));
        gridConfig.setBackground(BACKGROUND);

        spinnerMarcos = new JSpinner(new SpinnerNumberModel(3, 1, 16, 1));
        spinnerPaginaSize = new JSpinner(new SpinnerNumberModel(4, 1, 64, 1));
        txtSecuencia = new JTextField("7,0,1,2,0,3,0,4,2,3");
        txtSecuencia.setFont(FONT_MONO);
        comboAlgMemoria = new JComboBox<>(AlgoritmoReemplazo.values());
        comboAlgMemoria.setFont(FONT_MONO);
        comboAlgMemoria.setBackground(PANEL_BG);
        comboAlgMemoria.setForeground(TEXT);

        gridConfig.add(crearLabel("Marcos (RAM):"));
        gridConfig.add(spinnerMarcos);
        gridConfig.add(crearLabel("Tamaño Página:"));
        gridConfig.add(spinnerPaginaSize);
        gridConfig.add(crearLabel("Algoritmo:"));
        gridConfig.add(comboAlgMemoria);
        gridConfig.add(crearLabel("Secuencia:"));
        gridConfig.add(txtSecuencia);
        gridConfig.add(new JLabel());

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 5, 5));
        panelBotones.setBackground(BACKGROUND);
        JButton btnCaso2 = crearBoton("Cargar Caso 2", WARNING);
        JButton btnIniciar = crearBoton(" INICIAR", ACCENT);
        JButton btnReset = crearBoton(" RESET", DANGER);

        btnCaso2.addActionListener(e -> {
            spinnerMarcos.setValue(3);
            txtSecuencia.setText("7,0,1,2,0,3,0,4,2,3");
            comboAlgMemoria.setSelectedItem(AlgoritmoReemplazo.FIFO);
        });
        btnIniciar.addActionListener(e -> iniciarSimulacionMemoria());
        btnReset.addActionListener(e -> resetSimulacionMemoria());

        panelBotones.add(btnCaso2);
        panelBotones.add(btnIniciar);
        panelBotones.add(btnReset);

        // Área de log de eventos
        txtLogMemoria = new JTextArea();
        txtLogMemoria.setBackground(PANEL_BG);
        txtLogMemoria.setForeground(TEXT);
        txtLogMemoria.setFont(FONT_MONO);
        txtLogMemoria.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(txtLogMemoria);
        scrollLog.setBorder(crearBorde(" Log de Eventos"));
        scrollLog.setPreferredSize(new Dimension(0, 200));

        JPanel panelSurConfig = new JPanel(new BorderLayout(5, 5));
        panelSurConfig.setBackground(BACKGROUND);
        panelSurConfig.add(gridConfig, BorderLayout.CENTER);
        panelSurConfig.add(panelBotones, BorderLayout.SOUTH);

        panelConfig.add(panelSurConfig, BorderLayout.NORTH);
        panelConfig.add(scrollLog, BorderLayout.CENTER);

        // Panel de visualización (derecha)
        JPanel panelVis = new JPanel(new BorderLayout(5, 5));
        panelVis.setBackground(BACKGROUND);

        // Panel de estado de memoria
        JPanel panelEstadoMem = new JPanel(new GridLayout(1, 4, 10, 5));
        panelEstadoMem.setBackground(HEADER_BG);
        panelEstadoMem.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblFallos = crearLabelEstado(" Fallos: 0", DANGER);
        lblHits = crearLabelEstado(" Hits: 0", SUCCESS);
        lblPasoMemoria = crearLabelEstado(" Paso: 0/0", ACCENT);
        JLabel lblAlgMem = crearLabelEstado("Algoritmo: -", TEXT);
        panelEstadoMem.add(lblFallos);
        panelEstadoMem.add(lblHits);
        panelEstadoMem.add(lblPasoMemoria);
        panelEstadoMem.add(lblAlgMem);

        // Panel de marcos de RAM
        panelRAM = new JPanel();
        panelRAM.setBackground(BACKGROUND);
        panelRAM.setBorder(crearBorde(" Marcos de RAM"));

        // Panel de secuencia de páginas
        panelSecuencia = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelSecuencia.setBackground(BACKGROUND);
        panelSecuencia.setBorder(crearBorde(" Secuencia de Páginas"));
        panelSecuencia.setPreferredSize(new Dimension(0, 100));

        panelVis.add(panelEstadoMem, BorderLayout.NORTH);
        panelVis.add(panelRAM, BorderLayout.CENTER);
        panelVis.add(panelSecuencia, BorderLayout.SOUTH);

        panel.add(panelConfig, BorderLayout.WEST);
        panel.add(panelVis, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Inicia la simulación de memoria con los parámetros configurados.
     * Valida la entrada, configura el core y lanza el temporizador automático.
     */
    private void iniciarSimulacionMemoria() {
        resetSimulacionMemoria();

        int numMarcos = (Integer) spinnerMarcos.getValue();
        String[] parts = txtSecuencia.getText().split(",");
        List<Integer> secuencia = new ArrayList<>();
        for (String p : parts) {
            try { secuencia.add(Integer.parseInt(p.trim())); } catch (Exception ignored) {}
        }

        if (secuencia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Secuencia inválida.");
            return;
        }

        AlgoritmoReemplazo alg = (AlgoritmoReemplazo) comboAlgMemoria.getSelectedItem();
        core.iniciarMemoria(numMarcos, secuencia, alg);

        // Construir visualización de la secuencia
        panelSecuencia.removeAll();
        for (int i = 0; i < secuencia.size(); i++) {
            JLabel lbl = new JLabel(String.valueOf(secuencia.get(i)), SwingConstants.CENTER);
            lbl.setFont(FONT_TITLE);
            lbl.setPreferredSize(new Dimension(45, 45));
            lbl.setOpaque(true);
            lbl.setBackground(PANEL_BG);
            lbl.setForeground(TEXT);
            lbl.setBorder(BorderFactory.createLineBorder(BORDER));
            lbl.setName("seq_" + i);
            panelSecuencia.add(lbl);
        }
        panelSecuencia.revalidate();
        panelSecuencia.repaint();

        reconstruirPanelRAM();

        // Iniciar temporizador automático
        if (timerMemoria != null) timerMemoria.stop();
        timerMemoria = new javax.swing.Timer(800, e -> ejecutarPasoMemoria());
        timerMemoria.start();
    }

    /**
     * Reinicia la simulación de memoria, limpiando todos los estados visuales.
     */
    private void resetSimulacionMemoria() {
        if (timerMemoria != null) timerMemoria.stop();
        core.resetMemoria();
        txtLogMemoria.setText("");
        lblFallos.setText(" Fallos: 0");
        lblHits.setText(" Hits: 0");
        lblPasoMemoria.setText(" Paso: 0/0");
        panelRAM.removeAll();
        panelSecuencia.removeAll();
        panelRAM.revalidate();
        panelRAM.repaint();
        panelSecuencia.revalidate();
        panelSecuencia.repaint();
    }

    /**
     * Reconstruye el panel visual de marcos de RAM basado en el estado actual.
     */
    private void reconstruirPanelRAM() {
        panelRAM.removeAll();
        int numMarcos = core.getMemoriaRAM().getMarcos().size();
        int cols = Math.min(numMarcos, 4);
        int rows = (int) Math.ceil((double) numMarcos / cols);
        panelRAM.setLayout(new GridLayout(rows, cols, 10, 10));

        for (Marco m : core.getMemoriaRAM().getMarcos()) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(PANEL_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            card.setPreferredSize(new Dimension(120, 100));

            JLabel lblMarco = new JLabel("Marco " + m.getId(), SwingConstants.CENTER);
            lblMarco.setFont(FONT_BOLD);
            lblMarco.setForeground(ACCENT);

            JLabel lblPagina = new JLabel("Vacío", SwingConstants.CENTER);
            lblPagina.setFont(FONT_TITLE);
            lblPagina.setForeground(TEXT);
            lblPagina.setName("marco_" + m.getId());

            card.add(lblMarco, BorderLayout.NORTH);
            card.add(lblPagina, BorderLayout.CENTER);
            panelRAM.add(card);
        }
        panelRAM.revalidate();
        panelRAM.repaint();
    }

    /**
     * Ejecuta un paso de la simulación de memoria y actualiza la interfaz.
     */
    private void ejecutarPasoMemoria() {
        String mensaje = core.pasoMemoria();
        if (mensaje == null) {
            timerMemoria.stop();
            txtLogMemoria.append("\n=== SIMULACIÓN FINALIZADA ===\n");
            txtLogMemoria.append("Total Fallos: " + core.getMemoriaRAM().getFallos() + "\n");
            txtLogMemoria.append("Total Hits: " + core.getMemoriaRAM().getHits() + "\n");
            int total = core.getMemoriaRAM().getFallos() + core.getMemoriaRAM().getHits();
            if (total > 0) {
                txtLogMemoria.append("Tasa de Fallos: " + String.format("%.1f%%", 100.0 * core.getMemoriaRAM().getFallos() / total));
            }
            return;
        }

        txtLogMemoria.append(mensaje + "\n");

        // Actualizar contenido de los marcos
        for (Marco m : core.getMemoriaRAM().getMarcos()) {
            for (Component c : panelRAM.getComponents()) {
                if (c instanceof JPanel) {
                    for (Component inner : ((JPanel) c).getComponents()) {
                        if (inner.getName() != null && inner.getName().equals("marco_" + m.getId())) {
                            JLabel lbl = (JLabel) inner;
                            if (m.isOcupado()) {
                                lbl.setText("Pág " + m.getPagina());
                                lbl.setForeground(ACCENT);
                            } else {
                                lbl.setText("Vacío");
                                lbl.setForeground(TEXT);
                            }
                        }
                    }
                }
            }
        }

        // Resaltar página actual en la secuencia
        int tick = core.getMemoriaRAM().getTick();
        for (Component c : panelSecuencia.getComponents()) {
            if (c instanceof JLabel && c.getName() != null) {
                int idx = Integer.parseInt(c.getName().replace("seq_", ""));
                if (idx == tick - 1) {
                    c.setBackground(ACCENT);
                    ((JLabel) c).setForeground(BACKGROUND);
                } else if (idx < tick - 1) {
                    c.setBackground(HEADER_BG);
                    ((JLabel) c).setForeground(TEXT);
                }
            }
        }

        // Actualizar estadísticas
        lblFallos.setText(" Fallos: " + core.getMemoriaRAM().getFallos());
        lblHits.setText(" Hits: " + core.getMemoriaRAM().getHits());
        lblPasoMemoria.setText(" Paso: " + tick + "/" + core.getMemoriaRAM().getSecuencia().size());
    }

    // ==================== MÉTODOS UTILITARIOS DE UI ====================
    
    /**
     * Crea un borde con título estilizado.
     * 
     * @param titulo Título del borde
     * @return Borde compuesto configurado
     */
    private Border crearBorde(String titulo) {
        TitledBorder titled = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                titulo
        );
        titled.setTitleColor(ACCENT);
        titled.setTitleFont(FONT_BOLD);
        return BorderFactory.createCompoundBorder(
                titled,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
    }

    /**
     * Crea un botón estilizado con hover effect.
     * 
     * @param texto Texto del botón
     * @param color Color del texto y borde
     * @return Botón configurado
     */
    private JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(PANEL_BG);
        b.setForeground(color);
        b.setFont(FONT_BOLD);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(color); b.setForeground(BACKGROUND); }
            public void mouseExited(MouseEvent e) { b.setBackground(PANEL_BG); b.setForeground(color); }
        });
        return b;
    }

    /**
     * Crea una etiqueta de texto estilizada.
     * 
     * @param texto Texto de la etiqueta
     * @return Etiqueta configurada
     */
    private JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(TEXT);
        l.setFont(FONT_BOLD);
        return l;
    }

    /**
     * Crea una etiqueta para el panel de estado.
     * 
     * @param texto Texto de la etiqueta
     * @param color Color del texto
     * @return Etiqueta configurada
     */
    private JLabel crearLabelEstado(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setForeground(color);
        l.setFont(FONT_MONO);
        return l;
    }

    /**
     * Aplica estilo uniforme a una tabla JTable.
     * 
     * @param t Tabla a estilizar
     */
    private void estilizarTabla(JTable t) {
        t.setBackground(PANEL_BG);
        t.setForeground(TEXT);
        t.setGridColor(BORDER);
        t.setFont(FONT_MONO);
        t.setRowHeight(24);
        t.getTableHeader().setBackground(HEADER_BG);
        t.getTableHeader().setForeground(TEXT);
        t.getTableHeader().setFont(FONT_BOLD);
    }
}