package com.simulador.so.model;

public enum AlgoritmoPlanificacion {
    FCFS("FCFS — First Come First Served", false),
    SJF_NO_EXPROPIATIVO("SJF No Expropiativo", false),
    SRTF("SRTF — Shortest Remaining Time First", true),
    ROUND_ROBIN("Round Robin", false),
    PRIORIDAD_NO_EXPROPIATIVA("Prioridad No Expropiativa", false),
    PRIORIDAD_EXPROPIATIVA("Prioridad Expropiativa", true);

    private final String descripcion;
    private final boolean expropiativo;

    AlgoritmoPlanificacion(String descripcion, boolean expropiativo) {
        this.descripcion = descripcion;
        this.expropiativo = expropiativo;
    }

    public String getDescripcion() { return descripcion; }
    public boolean isExpropiativo() { return expropiativo; }

    @Override
    public String toString() { return descripcion; }
}