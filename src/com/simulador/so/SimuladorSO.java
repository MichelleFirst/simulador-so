package com.simulador.so;

import com.simulador.so.view.InterfazSimulador;
import javax.swing.*;

public class SimuladorSO {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background", new java.awt.Color(15, 15, 35));
        UIManager.put("Table.background", new java.awt.Color(25, 25, 55));
        UIManager.put("Table.foreground", new java.awt.Color(220, 220, 255));
        UIManager.put("Table.gridColor", new java.awt.Color(60, 60, 120));
        UIManager.put("TableHeader.background", new java.awt.Color(35, 35, 75));
        UIManager.put("TableHeader.foreground", new java.awt.Color(220, 220, 255));
        UIManager.put("TextField.background", new java.awt.Color(25, 25, 55));
        UIManager.put("TextField.foreground", new java.awt.Color(220, 220, 255));
        UIManager.put("TextField.caretForeground", new java.awt.Color(0, 240, 255));
        UIManager.put("ComboBox.background", new java.awt.Color(25, 25, 55));
        UIManager.put("ComboBox.foreground", new java.awt.Color(220, 220, 255));
        UIManager.put("Label.foreground", new java.awt.Color(220, 220, 255));
        UIManager.put("TabbedPane.background", new java.awt.Color(15, 15, 35));
        UIManager.put("TabbedPane.foreground", new java.awt.Color(220, 220, 255));

        SwingUtilities.invokeLater(() -> {
            InterfazSimulador ventana = new InterfazSimulador();
            ventana.setVisible(true);
        });
    }
}
