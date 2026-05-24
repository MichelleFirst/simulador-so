package com.simulador.so.controller;

import com.simulador.so.model.AlgoritmoPlanificacion;
import com.simulador.so.model.AlgoritmoReemplazo;
import com.simulador.so.model.Proceso;
import java.util.List;

public class SimuladorCore {
    private final PlanificadorCPU planificadorCPU;
    private final MemoriaRAM memoriaRAM;

    public SimuladorCore() {
        this.planificadorCPU = new PlanificadorCPU();
        this.memoriaRAM = new MemoriaRAM();
    }

    public void iniciarCPU(List<Proceso> procesos, AlgoritmoPlanificacion algoritmo, int quantum) {
        planificadorCPU.inicializar(procesos, algoritmo, quantum);
    }

    public boolean tickCPU() {
        return planificadorCPU.ejecutarTick();
    }

    public void resetCPU() {
        planificadorCPU.reset();
    }

    public void iniciarMemoria(int numMarcos, List<Integer> secuencia, AlgoritmoReemplazo algoritmo) {
        memoriaRAM.inicializar(numMarcos, secuencia, algoritmo);
    }

    public String pasoMemoria() {
        return memoriaRAM.ejecutarPaso();
    }

    public void resetMemoria() {
        memoriaRAM.reset();
    }

    public PlanificadorCPU getPlanificadorCPU() { return planificadorCPU; }
    public MemoriaRAM getMemoriaRAM() { return memoriaRAM; }
}