package com.simulador.so.controller;

import com.simulador.so.model.AlgoritmoPlanificacion;
import com.simulador.so.model.AlgoritmoReemplazo;
import com.simulador.so.model.Proceso;
import java.util.List;

/**
 * Clase fachada (Facade) que unifica y orquesta los dos subsistemas principales del simulador:
 * el planificador de CPU y la memoria RAM.
 * Proporciona una interfaz simplificada para que la capa de presentación interactúe con la simulación.
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class SimuladorCore {
    // ==================== ATRIBUTOS ====================

    /** Componente encargado de la planificación de procesos en CPU */
    private final PlanificadorCPU planificadorCPU;

    /** Componente encargado de la simulación de memoria RAM y reemplazo de páginas */
    private final MemoriaRAM memoriaRAM;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor que inicializa los dos subsistemas del simulador.
     * Se instancian nuevos objetos para cada simulación, evitando interferencias entre ejecuciones.
     */
    public SimuladorCore() {
        this.planificadorCPU = new PlanificadorCPU();
        this.memoriaRAM = new MemoriaRAM();
    }

    // ==================== MÉTODOS PARA PLANIFICACIÓN DE CPU ====================

    /**
     * Inicia una nueva simulación de planificación de CPU.
     *
     * @param procesos Lista de procesos a simular
     * @param algoritmo Algoritmo de planificación a utilizar (FCFS, SJF, SRTF, Round Robin, Prioridad)
     * @param quantum Quantum de tiempo para Round Robin (ignorado en otros algoritmos)
     */
    public void iniciarCPU(List<Proceso> procesos, AlgoritmoPlanificacion algoritmo, int quantum) {
        planificadorCPU.inicializar(procesos, algoritmo, quantum);
    }

    /**
     * Ejecuta un tick de la simulación de planificación de CPU.
     *
     * @return true si la simulación continúa activa, false si todos los procesos han terminado
     */
    public boolean tickCPU() {
        return planificadorCPU.ejecutarTick();
    }

    /**
     * Reinicia completamente la simulación de planificación de CPU.
     * Limpia todas las colas, historiales y estados internos.
     */
    public void resetCPU() {
        planificadorCPU.reset();
    }

    // ==================== MÉTODOS PARA MEMORIA Y REEMPLAZO DE PÁGINAS ====================

    /**
     * Inicia una nueva simulación de memoria RAM con reemplazo de páginas.
     *
     * @param numMarcos Número de marcos de página disponibles en memoria
     * @param secuencia Secuencia de páginas a referenciar durante la simulación
     * @param algoritmo Algoritmo de reemplazo a utilizar (FIFO o LRU)
     */
    public void iniciarMemoria(int numMarcos, List<Integer> secuencia, AlgoritmoReemplazo algoritmo) {
        memoriaRAM.inicializar(numMarcos, secuencia, algoritmo);
    }

    /**
     * Ejecuta un paso de la simulación de memoria RAM.
     * Procesa una referencia de página y aplica el algoritmo de reemplazo si es necesario.
     *
     * @return Mensaje descriptivo del paso ejecutado, o null si la simulación ha terminado
     */
    public String pasoMemoria() {
        return memoriaRAM.ejecutarPaso();
    }

    /**
     * Reinicia completamente la simulación de memoria RAM.
     * Limpia los marcos, el historial y reinicia las estadísticas.
     */
    public void resetMemoria() {
        memoriaRAM.reset();
    }

    // ==================== MÉTODOS GETTER ====================

    /**
     * Obtiene el planificador de CPU para acceder a su estado actual.
     *
     * @return Instancia del PlanificadorCPU
     */
    public PlanificadorCPU getPlanificadorCPU() {
        return planificadorCPU;
    }

    /**
     * Obtiene el módulo de memoria RAM para acceder a su estado actual.
     *
     * @return Instancia de MemoriaRAM
     */
    public MemoriaRAM getMemoriaRAM() {
        return memoriaRAM;
    }
}