package com.simulador.so.view;

import com.simulador.so.dto.HistorialTick;
import com.simulador.so.model.Proceso;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelGantt extends JPanel {
    private List<HistorialTick> historial;
    private List<Proceso> procesos;
    private int tickActual = 0;

    private static final Color BACKGROUND = new Color(15, 15, 35);
    private static final Color GRID_EMPTY = new Color(40, 40, 70);
    private static final Color BORDER = new Color(60, 60, 120);
    private static final Color TEXT = new Color(220, 220, 255);
    private static final Color ACCENT2 = new Color(255, 42, 109);
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    // Ancho constante en píxeles para cada tick de la simulación
    private static final int TICK_WIDTH = 45;
    private static final int MARGIN_LEFT = 30;
    private static final int MARGIN_TOP = 25;
    private static final int BAR_HEIGHT = 35;

    public PanelGantt() {
        setBackground(BACKGROUND);
        // CORRECCIÓN: Subimos el alto de 120 a 180 para dar suficiente espacio vertical
        setPreferredSize(new Dimension(0, 180));
    }

    public void setDatos(List<HistorialTick> historial, List<Proceso> procesos, int tickActual) {
        this.historial = historial;
        this.procesos = procesos;
        this.tickActual = tickActual;

        if (historial != null && !historial.isEmpty()) {
            // Calcular el ancho total real sumando todos los ticks
            int anchoTotal = MARGIN_LEFT + (historial.size() * TICK_WIDTH) + 50;

            // CORRECCIÓN: Notificamos la nueva dimensión expandida también con 180 de alto
            this.setPreferredSize(new Dimension(anchoTotal, 180));
            this.revalidate(); // Re-calcula la barra de scroll horizontal inmediatamente
        } else {
            // CORRECCIÓN: Mantenemos el estándar de 180 aquí también
            this.setPreferredSize(new Dimension(0, 180));
            this.revalidate();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (historial == null || historial.isEmpty() || procesos == null || procesos.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // El ancho de dibujo ya no depende de la ventana, crece horizontalmente
        int anchoDinamico = getPreferredSize().width;
        int h = getHeight();

        g2.setColor(BACKGROUND);
        g2.fillRect(0, 0, anchoDinamico, h);

        // Línea base del tiempo extendida
        g2.setColor(TEXT);
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP + BAR_HEIGHT + 10, anchoDinamico - 10, MARGIN_TOP + BAR_HEIGHT + 10);

        for (int i = 0; i < historial.size(); i++) {
            // Calcular la posición x estricta multiplicando por el ancho estático del tick
            int x = MARGIN_LEFT + i * TICK_WIDTH;
            HistorialTick tr = historial.get(i);

            g2.setColor(TEXT);
            g2.setFont(FONT_MONO);
            g2.drawString(String.valueOf(tr.getTick()), x + 2, MARGIN_TOP + BAR_HEIGHT + 25);

            if (tr.getProcesoEjecucion() >= 0) {
                Proceso p = buscarProceso(tr.getProcesoEjecucion());
                if (p != null && p.getColor() != null) {
                    g2.setColor(p.getColor());
                    g2.fillRoundRect(x + 1, MARGIN_TOP, TICK_WIDTH - 2, BAR_HEIGHT, 4, 4);
                    g2.setColor(Color.WHITE);
                    // Si el nombre no cabe completo por el tamaño del tick, recortarlo de forma segura
                    String nombre = p.getNombre();
                    if (nombre.length() > 4) nombre = nombre.substring(0, 4);
                    g2.drawString(nombre, x + 4, MARGIN_TOP + 22);
                }
            } else {
                g2.setColor(GRID_EMPTY);
                g2.fillRoundRect(x + 1, MARGIN_TOP, TICK_WIDTH - 2, BAR_HEIGHT, 4, 4);
                g2.setColor(TEXT);
                g2.drawString("Idle", x + 4, MARGIN_TOP + 22);
            }

            g2.setColor(BORDER);
            g2.drawLine(x, MARGIN_TOP, x, MARGIN_TOP + BAR_HEIGHT + 10);
        }

        // Línea indicadora del tick actual en ejecución
        if (tickActual > 0) {
            int indX = MARGIN_LEFT + (tickActual - 1) * TICK_WIDTH + TICK_WIDTH / 2;
            g2.setColor(ACCENT2);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0));
            g2.drawLine(indX, MARGIN_TOP - 10, indX, MARGIN_TOP + BAR_HEIGHT + 10);
        }
    }

    private Proceso buscarProceso(int id) {
        for (Proceso p : procesos) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}