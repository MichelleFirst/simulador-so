package com.simulador.so.view;

import com.simulador.so.dto.HistorialTick;
import com.simulador.so.model.Proceso;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel personalizado para dibujar el diagrama de Gantt de la simulación.
 * Muestra gráficamente qué proceso estuvo en ejecución en cada tick,
 * permitiendo visualizar la planificación de CPU de manera intuitiva.
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class PanelGantt extends JPanel {

    // ==================== ATRIBUTOS ====================

    /** Historial de ticks con información de qué proceso se ejecutó */
    private List<HistorialTick> historial;

    /** Lista de procesos para obtener colores y nombres */
    private List<Proceso> procesos;

    /** Tick actual de la simulación (para resaltar la posición) */
    private int tickActual = 0;

    // ==================== CONSTANTES DE ESTILO ====================

    /** Color de fondo del panel */
    private static final Color BACKGROUND = new Color(15, 15, 35);

    /** Color para los bloques de tiempo inactivo (Idle) */
    private static final Color GRID_EMPTY = new Color(40, 40, 70);

    /** Color de bordes y líneas de cuadrícula */
    private static final Color BORDER = new Color(60, 60, 120);

    /** Color de texto estándar */
    private static final Color TEXT = new Color(220, 220, 255);

    /** Color de acento para el indicador de tick actual */
    private static final Color ACCENT2 = new Color(255, 42, 109);

    /** Fuente monoespaciada para números de tick */
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    // ==================== CONSTANTES DE DIMENSIONES ====================

    /** Ancho en píxeles para representar cada tick de simulación */
    private static final int TICK_WIDTH = 45;

    /** Margen izquierdo antes del primer tick */
    private static final int MARGIN_LEFT = 30;

    /** Margen superior para la barra de procesos */
    private static final int MARGIN_TOP = 25;

    /** Altura de cada barra de proceso en píxeles */
    private static final int BAR_HEIGHT = 35;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor que inicializa el panel con tamaño y fondo predeterminados.
     * Se establece una altura suficiente para mostrar correctamente las barras y etiquetas.
     */
    public PanelGantt() {
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(0, 180)); // Altura fija, ancho dinámico
    }

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Actualiza los datos del diagrama de Gantt.
     * Recalcula el ancho necesario basado en la cantidad de ticks y refresca el panel.
     *
     * @param historial Lista de estados por tick
     * @param procesos Lista de procesos (para colores y nombres)
     * @param tickActual Tick actual para resaltar la posición
     */
    public void setDatos(List<HistorialTick> historial, List<Proceso> procesos, int tickActual) {
        this.historial = historial;
        this.procesos = procesos;
        this.tickActual = tickActual;

        if (historial != null && !historial.isEmpty()) {
            // Calcular ancho total: margen izquierdo + espacio para cada tick + margen derecho
            int anchoTotal = MARGIN_LEFT + (historial.size() * TICK_WIDTH) + 50;
            this.setPreferredSize(new Dimension(anchoTotal, 180));
            this.revalidate(); // Notificar al JScrollPane que el tamaño cambió
        } else {
            this.setPreferredSize(new Dimension(0, 180));
            this.revalidate();
        }
        repaint();
    }

    // ==================== MÉTODOS DE DIBUJO ====================

    /**
     * Dibuja el diagrama de Gantt completo.
     * Incluye:
     * - Barras de ejecución por tick (color del proceso o gris para Idle)
     * - Números de tick en la parte inferior
     * - Línea vertical indicadora del tick actual
     * - Cuadrícula de separación entre ticks
     *
     * @param g Contexto gráfico para dibujar
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Validar que haya datos para dibujar
        if (historial == null || historial.isEmpty() || procesos == null || procesos.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dimensiones del área de dibujo
        int anchoDinamico = getPreferredSize().width;
        int alto = getHeight();

        // Limpiar fondo
        g2.setColor(BACKGROUND);
        g2.fillRect(0, 0, anchoDinamico, alto);

        // Dibujar línea base horizontal del eje temporal
        g2.setColor(TEXT);
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP + BAR_HEIGHT + 10, anchoDinamico - 10, MARGIN_TOP + BAR_HEIGHT + 10);

        // Dibujar cada tick del historial
        for (int i = 0; i < historial.size(); i++) {
            int x = MARGIN_LEFT + i * TICK_WIDTH;
            HistorialTick registro = historial.get(i);

            // Dibujar número de tick debajo de la línea base
            g2.setColor(TEXT);
            g2.setFont(FONT_MONO);
            g2.drawString(String.valueOf(registro.getTick()), x + 2, MARGIN_TOP + BAR_HEIGHT + 25);

            // Dibujar la barra de ejecución
            if (registro.getProcesoEjecucion() >= 0) {
                // Hay proceso en ejecución: dibujar con su color
                Proceso p = buscarProceso(registro.getProcesoEjecucion());
                if (p != null && p.getColor() != null) {
                    g2.setColor(p.getColor());
                    g2.fillRoundRect(x + 1, MARGIN_TOP, TICK_WIDTH - 2, BAR_HEIGHT, 4, 4);
                    g2.setColor(Color.WHITE);

                    // Mostrar nombre del proceso (recortado si es muy largo)
                    String nombre = p.getNombre();
                    if (nombre.length() > 4) {
                        nombre = nombre.substring(0, 4);
                    }
                    g2.drawString(nombre, x + 4, MARGIN_TOP + 22);
                }
            } else {
                // No hay proceso en ejecución (Idle): dibujar en gris
                g2.setColor(GRID_EMPTY);
                g2.fillRoundRect(x + 1, MARGIN_TOP, TICK_WIDTH - 2, BAR_HEIGHT, 4, 4);
                g2.setColor(TEXT);
                g2.drawString("Idle", x + 4, MARGIN_TOP + 22);
            }

            // Dibujar línea vertical separadora entre ticks
            g2.setColor(BORDER);
            g2.drawLine(x, MARGIN_TOP, x, MARGIN_TOP + BAR_HEIGHT + 10);
        }

        // Dibujar indicador del tick actual (línea vertical discontinua)
        if (tickActual > 0) {
            int xIndicador = MARGIN_LEFT + (tickActual - 1) * TICK_WIDTH + TICK_WIDTH / 2;
            g2.setColor(ACCENT2);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0));
            g2.drawLine(xIndicador, MARGIN_TOP - 10, xIndicador, MARGIN_TOP + BAR_HEIGHT + 10);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Busca un proceso por su identificador en la lista de procesos.
     *
     * @param id Identificador del proceso a buscar
     * @return El proceso encontrado, o null si no existe
     */
    private Proceso buscarProceso(int id) {
        for (Proceso p : procesos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}