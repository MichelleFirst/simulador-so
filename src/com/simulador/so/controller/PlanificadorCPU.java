package com.simulador.so.controller;

import com.simulador.so.dto.HistorialTick;
import com.simulador.so.model.AlgoritmoPlanificacion;
import com.simulador.so.model.EstadoProceso;
import com.simulador.so.model.Proceso;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase que simula el planificador de CPU del sistema operativo.
 * Implementa múltiples algoritmos de planificación (FCFS, SJF, SRTF, Round Robin, Prioridad).
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class PlanificadorCPU {
    // ==================== ATRIBUTOS ====================

    /** Lista maestra de todos los procesos de la simulación */
    private List<Proceso> procesos = new ArrayList<>();

    /** Cola de procesos listos para ejecutarse (estructura FIFO) */
    private LinkedList<Proceso> colaListos = new LinkedList<>();

    /** Cola de procesos bloqueados (en espera de E/S o por fallo de página) */
    private List<Proceso> colaBloqueados = new ArrayList<>();

    /** Cola de procesos que han completado su ejecución */
    private List<Proceso> colaTerminados = new ArrayList<>();

    /** Historial de estados en cada tick para generar el diagrama de Gantt */
    private List<HistorialTick> historial = new ArrayList<>();

    /** Proceso que actualmente está utilizando la CPU */
    private Proceso enEjecucion = null;

    /** Tick actual de la simulación */
    private int tickActual = 0;

    /** Quantum de tiempo para el algoritmo Round Robin */
    private int quantum = 2;

    /** Ticks que lleva ejecutándose el proceso actual */
    private int ticksEjecucionActual = 0;

    /** Algoritmo de planificación actualmente configurado */
    private AlgoritmoPlanificacion algoritmo = AlgoritmoPlanificacion.FCFS;

    /** Estado de la simulación (activa o inactiva) */
    private boolean simulando = false;

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Inicializa o reinicia la simulación con los parámetros especificados.
     * Realiza una clonación profunda de los procesos para no modificar los originales.
     *
     * @param listaProcesos Lista de procesos a simular
     * @param alg Algoritmo de planificación a utilizar
     * @param q Quantum de tiempo (solo utilizado para Round Robin)
     */
    public void inicializar(List<Proceso> listaProcesos, AlgoritmoPlanificacion alg, int q) {
        // Limpiar estructuras existentes
        this.procesos.clear();

        // Clonar procesos para mantener los originales intactos
        for (Proceso p : listaProcesos) {
            this.procesos.add(p.clonar());
        }

        // Configurar parámetros de simulación
        this.algoritmo = alg;
        this.quantum = q;
        this.tickActual = 0;
        this.historial.clear();
        this.colaListos.clear();
        this.colaBloqueados.clear();
        this.colaTerminados.clear();
        this.enEjecucion = null;
        this.ticksEjecucionActual = 0;
        this.simulando = true;

        // Reinicializar el estado de cada proceso clonado para la nueva simulación
        for (Proceso p : this.procesos) {
            p.setEstado(EstadoProceso.NUEVO);
            p.setTiempoRestante(p.getRafaga()); // Restablecer tiempo restante con la ráfaga original
            p.setTiempoEspera(0);
            p.setTiempoRetorno(0);
            p.setTiempoInicio(-1);  // -1 indica que aún no ha comenzado
            p.setTiempoFin(-1);      // -1 indica que aún no ha terminado
            p.setEnFalloPagina(false);
        }
    }

    /**
     * Ejecuta un tick de la simulación del planificador.
     * Implementa la lógica completa de planificación según el algoritmo configurado.
     *
     * @return true si la simulación continúa, false si ha terminado
     */
    public boolean ejecutarTick() {
        if (!simulando) return false;

        // ==========================================
        // PASO 1: LLEGADA DE NUEVOS PROCESOS
        // ==========================================
        // Los procesos nuevos se insertan en la cola de listos en el orden exacto de llegada
        for (Proceso p : procesos) {
            if (p.getLlegada() == tickActual && p.getEstado() == EstadoProceso.NUEVO) {
                p.setEstado(EstadoProceso.LISTO);
                if (!colaListos.contains(p)) {
                    colaListos.offer(p);  // offer respeta el orden FIFO
                }
            }
        }

        // ==========================================
        // PASO 2: LÓGICA EXCLUSIVA DE PLANIFICACIÓN
        // ==========================================
        switch (algoritmo) {

            case FCFS:
                // FIFO (First Come, First Served): El primero en llegar es el primero en ejecutarse.
                // No es expropiativo.
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    // Limpiar procesos en estados incorrectos antes de obtener el siguiente
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.poll();  // Toma el frente de la cola
                    ticksEjecucionActual = 0;
                }
                break;

            case SJF_NO_EXPROPIATIVO:
                // Shortest Job First (No Expropiativo): Selecciona la ráfaga más pequeña.
                // Una vez que un proceso comienza, se ejecuta hasta completarse.
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getRafaga)  // Menor ráfaga primero
                                    .thenComparingInt(p -> colaListos.indexOf(p)))  // Desempate por orden de llegada
                            .orElse(null);
                    if (enEjecucion != null) {
                        colaListos.remove(enEjecucion);
                    }
                    ticksEjecucionActual = 0;
                }
                break;

            case SRTF:
                // Shortest Remaining Time First (Expropiativo): Ejecuta siempre el proceso con menor tiempo restante.
                // Si llega un proceso con tiempo restante menor, interrumpe al actual.
                colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);

                // Evaluar si el proceso en ejecución debe ser expropiado
                if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION) {
                    Proceso mejorCandidato = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getTiempoRestante)  // Menor tiempo restante
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);

                    // Expropiar si el candidato tiene estrictamente menos tiempo restante
                    if (mejorCandidato != null && mejorCandidato.getTiempoRestante() < enEjecucion.getTiempoRestante()) {
                        enEjecucion.setEstado(EstadoProceso.LISTO);
                        colaListos.offer(enEjecucion);  // Regresa al final de la cola
                        enEjecucion = null;
                        ticksEjecucionActual = 0;
                    }
                }

                // Si no hay proceso en ejecución o terminó, seleccionar el mejor candidato
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    enEjecucion = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getTiempoRestante)
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);
                    if (enEjecucion != null) {
                        colaListos.remove(enEjecucion);
                    }
                    ticksEjecucionActual = 0;
                }
                break;

            case ROUND_ROBIN:
                // Round Robin: Cada proceso recibe un quantum de tiempo.
                // Si no termina dentro del quantum, vuelve al final de la cola.
                if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION) {
                    if (ticksEjecucionActual >= quantum) {
                        // El quantum expiró, el proceso vuelve al final de la cola
                        enEjecucion.setEstado(EstadoProceso.LISTO);
                        colaListos.offer(enEjecucion);
                        enEjecucion = null;
                        ticksEjecucionActual = 0;
                    }
                }

                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.poll();  // Toma el siguiente en orden FIFO
                    ticksEjecucionActual = 0;
                }
                break;

            case PRIORIDAD_NO_EXPROPIATIVA:
                // Prioridad (No Expropiativo): Mayor prioridad ejecuta primero (menor número = mayor prioridad).
                // No interrumpe al proceso actual.
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getPrioridad)  // Menor número = mayor prioridad
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);
                    if (enEjecucion != null) {
                        colaListos.remove(enEjecucion);
                    }
                    ticksEjecucionActual = 0;
                }
                break;

            case PRIORIDAD_EXPROPIATIVA:
                // Prioridad (Expropiativo): Interrumpe si llega un proceso con mayor prioridad.
                colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);

                // Verificar si el proceso actual debe ser expropiado
                if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION) {
                    Proceso mejorCandidato = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getPrioridad)
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);

                    // Expropiar si el candidato tiene mayor prioridad (menor número)
                    if (mejorCandidato != null && mejorCandidato.getPrioridad() < enEjecucion.getPrioridad()) {
                        enEjecucion.setEstado(EstadoProceso.LISTO);
                        colaListos.offer(enEjecucion);
                        enEjecucion = null;
                        ticksEjecucionActual = 0;
                    }
                }

                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    enEjecucion = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getPrioridad)
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);
                    if (enEjecucion != null) {
                        colaListos.remove(enEjecucion);
                    }
                    ticksEjecucionActual = 0;
                }
                break;
        }

        // ==========================================
        // PASO 3: COMPUTACIÓN EN CPU
        // ==========================================
        int procesoEjecutadoId = -1;
        if (enEjecucion != null) {
            // Registrar tiempo de inicio si es la primera vez que se ejecuta
            if (enEjecucion.getTiempoInicio() == -1) {
                enEjecucion.setTiempoInicio(tickActual);
            }
            enEjecucion.setEstado(EstadoProceso.EJECUCION);

            // Ejecutar un tick de ráfaga
            enEjecucion.setTiempoRestante(enEjecucion.getTiempoRestante() - 1);
            ticksEjecucionActual++;
            procesoEjecutadoId = enEjecucion.getId();

            // Verificar si el proceso ha completado su ejecución
            if (enEjecucion.getTiempoRestante() <= 0) {
                enEjecucion.setEstado(EstadoProceso.TERMINADO);
                int finExacto = this.tickActual + 1;
                enEjecucion.setTiempoFin(finExacto);

                // Calcular métricas estándar de Sistemas Operativos
                int tRetorno = finExacto - enEjecucion.getLlegada();  // Turnaround Time
                int tEspera = tRetorno - enEjecucion.getRafaga();      // Waiting Time
                enEjecucion.setTiempoRetorno(tRetorno);
                enEjecucion.setTiempoEspera(tEspera);

                // Mover a la cola de terminados
                if (!colaTerminados.contains(enEjecucion)) {
                    colaTerminados.add(enEjecucion);
                }
                enEjecucion = null;
                ticksEjecucionActual = 0;
            }
        }

        // ==========================================
        // PASO 4: CÁLCULO DE TIEMPOS DE ESPERA
        // ==========================================
        // Solo acumulan tiempo de espera los procesos legítimamente en la cola de listos
        for (Proceso p : colaListos) {
            if (p.getEstado() == EstadoProceso.LISTO && p.getId() != procesoEjecutadoId) {
                p.setTiempoEspera(p.getTiempoEspera() + 1);
            }
        }

        // ==========================================
        // PASO 5: REGISTRO EN EL DIAGRAMA DE GANTT
        // ==========================================
        HistorialTick tr = new HistorialTick();
        tr.setTick(tickActual);
        tr.setProcesoEjecucion(procesoEjecutadoId);

        // Registrar procesos en cada estado para el tick actual
        for (Proceso p : colaListos)
            if (p.getEstado() == EstadoProceso.LISTO)
                tr.getListos().add(p.getId());
        for (Proceso p : colaBloqueados)
            tr.getBloqueados().add(p.getId());
        for (Proceso p : colaTerminados)
            tr.getTerminados().add(p.getId());

        historial.add(tr);

        tickActual++;

        // Verificar condición de parada: todos los procesos han terminado
        if (colaTerminados.size() == procesos.size()) {
            simulando = false;
            return false;
        }
        return true;
    }

    /**
     * Obtiene el mejor proceso de la cola de listos según el algoritmo actual.
     * Método auxiliar para la lógica de selección de procesos.
     *
     * @return El mejor proceso según el algoritmo, o null si la cola está vacía
     */
    private Proceso obtenerMejorEnListos() {
        if (colaListos.isEmpty()) return null;

        // Limpieza preventiva de estados inconsistentes en la cola
        colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
        if (colaListos.isEmpty()) return null;

        switch (algoritmo) {
            case FCFS:
            case ROUND_ROBIN:
                // Algoritmos FIFO: tomar el primero de la cola
                return colaListos.peek();

            case SJF_NO_EXPROPIATIVO:
            case SRTF:
                // Seleccionar por menor tiempo restante, desempatar por orden de llegada
                return colaListos.stream()
                        .min(Comparator.comparingInt(Proceso::getTiempoRestante)
                                .thenComparingInt(p -> colaListos.indexOf(p)))
                        .orElse(null);

            case PRIORIDAD_NO_EXPROPIATIVA:
            case PRIORIDAD_EXPROPIATIVA:
                // Seleccionar por mayor prioridad (menor número), desempatar por orden de llegada
                return colaListos.stream()
                        .min(Comparator.comparingInt(Proceso::getPrioridad)
                                .thenComparingInt(p -> colaListos.indexOf(p)))
                        .orElse(null);

            default:
                return colaListos.peek();
        }
    }

    /**
     * Selecciona y remueve el mejor proceso de la cola de listos.
     *
     * @return El proceso seleccionado, o null si no hay procesos disponibles
     */
    private Proceso seleccionarProceso() {
        Proceso seleccionado = obtenerMejorEnListos();
        if (seleccionado != null) {
            colaListos.remove(seleccionado);
        }
        return seleccionado;
    }

    /**
     * Determina si el proceso actual debe ser expropiado por un candidato.
     *
     * @param actual Proceso actualmente en ejecución
     * @param candidato Proceso candidato para tomar la CPU
     * @return true si debe ocurrir expropiación, false en caso contrario
     */
    private boolean debeExpropiar(Proceso actual, Proceso candidato) {
        if (algoritmo == AlgoritmoPlanificacion.SRTF) {
            // Expropiar si el candidato tiene MENOS tiempo restante
            return candidato.getTiempoRestante() < actual.getTiempoRestante();
        } else if (algoritmo == AlgoritmoPlanificacion.PRIORIDAD_EXPROPIATIVA) {
            // Expropiar si el candidato tiene MAYOR prioridad (menor número)
            return candidato.getPrioridad() < actual.getPrioridad();
        }
        return false;
    }

    /**
     * Reinicia completamente el estado del planificador.
     * Limpia todas las estructuras y prepara para una nueva simulación.
     */
    public void reset() {
        simulando = false;
        tickActual = 0;
        historial.clear();
        enEjecucion = null;
        colaListos.clear();
        colaBloqueados.clear();
        colaTerminados.clear();
    }

    // ==================== MÉTODOS GETTER ====================

    public List<Proceso> getProcesos() {
        return procesos;
    }

    public List<Proceso> getColaListos() {
        return colaListos;
    }

    public List<Proceso> getColaBloqueados() {
        return colaBloqueados;
    }

    public List<Proceso> getColaTerminados() {
        return colaTerminados;
    }

    public List<HistorialTick> getHistorial() {
        return historial;
    }

    public Proceso getEnEjecucion() {
        return enEjecucion;
    }

    public int getTickActual() {
        return tickActual;
    }

    public boolean isSimulando() {
        return simulando;
    }
}