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

    public PanelGantt() {
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(0, 280));
    }

    public void setDatos(List<HistorialTick> historial, List<Proceso> procesos, int tickActual) {
        this.historial = historial;
        this.procesos = procesos;
        this.tickActual = tickActual;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (historial == null || historial.isEmpty() || procesos == null || procesos.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int marginLeft = 50;
        int marginTop = 30;
        int barHeight = 35;
        int tickWidth = Math.max(30, (w - marginLeft - 20) / Math.max(historial.size(), 20));

        g2.setColor(BACKGROUND);
        g2.fillRect(0, 0, w, h);

        g2.setColor(TEXT);
        g2.drawLine(marginLeft, marginTop + barHeight + 10, w - 10, marginTop + barHeight + 10);

        for (int i = 0; i < historial.size(); i++) {
            int x = marginLeft + i * tickWidth;
            HistorialTick tr = historial.get(i);

            g2.setColor(TEXT);
            g2.setFont(FONT_MONO);
            g2.drawString(String.valueOf(tr.getTick()), x + 2, marginTop + barHeight + 25);

            if (tr.getProcesoEjecucion() >= 0) {
                Proceso p = buscarProceso(tr.getProcesoEjecucion());
                if (p != null && p.getColor() != null) {
                    g2.setColor(p.getColor());
                    g2.fillRoundRect(x + 1, marginTop, tickWidth - 2, barHeight, 4, 4);
                    g2.setColor(Color.WHITE);
                    g2.drawString(p.getNombre(), x + 4, marginTop + 22);
                }
            } else {
                g2.setColor(GRID_EMPTY);
                g2.fillRoundRect(x + 1, marginTop, tickWidth - 2, barHeight, 4, 4);
                g2.setColor(TEXT);
                g2.drawString("Idle", x + 4, marginTop + 22);
            }

            g2.setColor(BORDER);
            g2.drawLine(x, marginTop, x, marginTop + barHeight + 10);
        }

        if (tickActual > 0) {
            int indX = marginLeft + (tickActual - 1) * tickWidth + tickWidth / 2;
            g2.setColor(ACCENT2);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0));
            g2.drawLine(indX, marginTop - 10, indX, marginTop + barHeight + 10);
        }
    }

    private Proceso buscarProceso(int id) {
        for (Proceso p : procesos) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}