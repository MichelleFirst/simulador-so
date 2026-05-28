package com.simulador.so.controller;

import com.simulador.so.dto.HistorialTick;
import com.simulador.so.model.AlgoritmoPlanificacion;
import com.simulador.so.model.EstadoProceso;
import com.simulador.so.model.Proceso;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PlanificadorCPU {
    private List<Proceso> procesos = new ArrayList<>();
    private LinkedList<Proceso> colaListos = new LinkedList<>();
    private List<Proceso> colaBloqueados = new ArrayList<>();
    private List<Proceso> colaTerminados = new ArrayList<>();
    private List<HistorialTick> historial = new ArrayList<>();
    private Proceso enEjecucion = null;
    private int tickActual = 0;
    private int quantum = 2;
    private int ticksEjecucionActual = 0;
    private AlgoritmoPlanificacion algoritmo = AlgoritmoPlanificacion.FCFS;
    private boolean simulando = false;

    public void inicializar(List<Proceso> listaProcesos, AlgoritmoPlanificacion alg, int q) {
        this.procesos.clear();
        for (Proceso p : listaProcesos) {
            this.procesos.add(p.clonar());
        }
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

        // Aquí se preparan los procesos clonados para la nueva ejecución
        for (Proceso p : this.procesos) {
            p.setEstado(EstadoProceso.NUEVO);
            p.setTiempoRestante(p.getRafaga()); // <-- Importante: restablece el tiempo restante con la ráfaga estática original
            p.setTiempoEspera(0);
            p.setTiempoRetorno(0);
            p.setTiempoInicio(-1);
            p.setTiempoFin(-1);
            p.setEnFalloPagina(false);
        }
    }
    public boolean ejecutarTick() {
        if (!simulando) return false;

        // ==========================================
        // PASO 1: LLEGADA DE NUEVOS PROCESOS
        // ==========================================
        // Se insertan en la cola en el orden exacto de aparición
        for (Proceso p : procesos) {
            if (p.getLlegada() == tickActual && p.getEstado() == EstadoProceso.NUEVO) {
                p.setEstado(EstadoProceso.LISTO);
                if (!colaListos.contains(p)) {
                    colaListos.offer(p);
                }
            }
        }

        // ==========================================
        // PASO 2: LÓGICA EXCLUSIVA DE PLANIFICACIÓN
        // ==========================================
        switch (algoritmo) {

            case FCFS:
                // "El primero en llegar es el primero en ejecutarse. No expropiativo."
                // Si la CPU está libre, toma estrictamente el frente de la cola (FIFO puro)
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.poll();
                    ticksEjecucionActual = 0;
                }
                break;

            case SJF_NO_EXPROPIATIVO:
                // "Escoge la ráfaga más pequeña. Cuando empieza, termina completo."
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getRafaga)
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);
                    if (enEjecucion != null) {
                        colaListos.remove(enEjecucion);
                    }
                    ticksEjecucionActual = 0;
                }
                break;

            case SRTF:
                // "Expropiativo. Siempre ejecuta el menor tiempo restante. Si el nuevo es menor, interrumpe."
                colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);

                // Evaluar interrupción/expropiación antes de procesar el tick actual
                if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION) {
                    Proceso mejorCandidato = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getTiempoRestante)
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);

                    // Si hay alguien listo con un tiempo restante estrictamente menor, se le quita la CPU
                    if (mejorCandidato != null && mejorCandidato.getTiempoRestante() < enEjecucion.getTiempoRestante()) {
                        enEjecucion.setEstado(EstadoProceso.LISTO);
                        colaListos.offer(enEjecucion); // Regresa al final de la cola de espera
                        enEjecucion = null;
                        ticksEjecucionActual = 0;
                    }
                }

                // Si quedó vacío por expropiación o el proceso previo terminó, asignamos el óptimo
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
                // "Cada proceso recibe un tiempo pequeño llamado quantum. Si no termina, vuelve al final de la cola."
                if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION) {
                    if (ticksEjecucionActual >= quantum) {
                        enEjecucion.setEstado(EstadoProceso.LISTO);
                        colaListos.offer(enEjecucion); // Expira Quantum: se forma al final de la fila
                        enEjecucion = null;
                        ticksEjecucionActual = 0;
                    }
                }

                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
                    enEjecucion = colaListos.poll(); // Toma el siguiente disponible en orden FIFO
                    ticksEjecucionActual = 0;
                }
                break;

            case PRIORIDAD_NO_EXPROPIATIVA:
                // "Mayor prioridad ejecuta primero (menor número = mayor prioridad). No interrumpe."
                if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO) {
                    colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
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

            case PRIORIDAD_EXPROPIATIVA:
                // "Expropiativo. Sí interrumpe si llega un proceso con mayor prioridad (menor número)."
                colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);

                if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION) {
                    Proceso mejorCandidato = colaListos.stream()
                            .min(Comparator.comparingInt(Proceso::getPrioridad)
                                    .thenComparingInt(p -> colaListos.indexOf(p)))
                            .orElse(null);

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
        // PASO 3: COMPUTACIÓN EN CPU (BLOQUE UNIFICADO)
        // ==========================================
        int procesoEjecutadoId = -1;
        if (enEjecucion != null) {
            if (enEjecucion.getTiempoInicio() == -1) {
                enEjecucion.setTiempoInicio(tickActual);
            }
            enEjecucion.setEstado(EstadoProceso.EJECUCION);

            // Ejecución de la unidad de ráfaga
            enEjecucion.setTiempoRestante(enEjecucion.getTiempoRestante() - 1);
            ticksEjecucionActual++;
            procesoEjecutadoId = enEjecucion.getId();

            // Si el proceso agota su tiempo restante en este tick, se destruye y sale de CPU
            if (enEjecucion.getTiempoRestante() <= 0) {
                enEjecucion.setEstado(EstadoProceso.TERMINADO);
                int finExacto = this.tickActual + 1;
                enEjecucion.setTiempoFin(finExacto);

                // Fórmulas estándar de Sistemas Operativos
                int tRetorno = finExacto - enEjecucion.getLlegada();
                int tEspera = tRetorno - enEjecucion.getRafaga();
                enEjecucion.setTiempoRetorno(tRetorno);
                enEjecucion.setTiempoEspera(tEspera);

                if (!colaTerminados.contains(enEjecucion)) {
                    colaTerminados.add(enEjecucion);
                }
                enEjecucion = null;
                ticksEjecucionActual = 0;
            }
        }

        // ==========================================
        // PASO 4: CALCULO DE TIEMPOS DE ESPERA REALES
        // ==========================================
        // Solo acumulan espera aquellos procesos legítimamente parados en colaListos
        for (Proceso p : colaListos) {
            if (p.getEstado() == EstadoProceso.LISTO && p.getId() != procesoEjecutadoId) {
                p.setTiempoEspera(p.getTiempoEspera() + 1);
            }
        }

        // ==========================================
        // PASO 5: VOLCADO AL DIAGRAMA DE GANTT
        // ==========================================
        HistorialTick tr = new HistorialTick();
        tr.setTick(tickActual);
        tr.setProcesoEjecucion(procesoEjecutadoId);
        for (Proceso p : colaListos) if (p.getEstado() == EstadoProceso.LISTO) tr.getListos().add(p.getId());
        for (Proceso p : colaBloqueados) tr.getBloqueados().add(p.getId());
        for (Proceso p : colaTerminados) tr.getTerminados().add(p.getId());
        historial.add(tr);

        tickActual++;

        // Criterio de parada del simulador
        if (colaTerminados.size() == procesos.size()) {
            simulando = false;
            return false;
        }
        return true;
    }

    private Proceso obtenerMejorEnListos() {
        if (colaListos.isEmpty()) return null;

        // Limpieza preventiva de estados en la cola
        colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
        if (colaListos.isEmpty()) return null;

        switch (algoritmo) {
            case FCFS:
            case ROUND_ROBIN:
                return colaListos.peek();

            case SJF_NO_EXPROPIATIVO:
            case SRTF:
                // Menor tiempo restante. Si hay empate, desempata el que esté primero en la cola (menor índice)
                return colaListos.stream()
                        .min(Comparator.comparingInt(Proceso::getTiempoRestante)
                                .thenComparingInt(p -> colaListos.indexOf(p)))
                        .orElse(null);

            case PRIORIDAD_NO_EXPROPIATIVA:
            case PRIORIDAD_EXPROPIATIVA:
                // Menor valor numérico = Mayor prioridad. Si hay empate, por orden en la cola
                return colaListos.stream()
                        .min(Comparator.comparingInt(Proceso::getPrioridad)
                                .thenComparingInt(p -> colaListos.indexOf(p)))
                        .orElse(null);

            default:
                return colaListos.peek();
        }
    }

    private Proceso seleccionarProceso() {
        Proceso seleccionado = obtenerMejorEnListos();
        if (seleccionado != null) {
            colaListos.remove(seleccionado);
        }
        return seleccionado;
    }

    private boolean debeExpropiar(Proceso actual, Proceso candidato) {
        if (algoritmo == AlgoritmoPlanificacion.SRTF) {
            // Expropia si el candidato tiene estrictamente MENOS tiempo restante
            return candidato.getTiempoRestante() < actual.getTiempoRestante();
        } else if (algoritmo == AlgoritmoPlanificacion.PRIORIDAD_EXPROPIATIVA) {
            // Expropia si el candidato tiene MENOR número de prioridad (Prioridad más alta, Ej: 1 es mayor que 3)
            return candidato.getPrioridad() < actual.getPrioridad();
        }
        return false;
    }

    public void reset() {
        simulando = false;
        tickActual = 0;
        historial.clear();
        enEjecucion = null;
        colaListos.clear();
        colaBloqueados.clear();
        colaTerminados.clear();
    }

    public List<Proceso> getProcesos() { return procesos; }
    public List<Proceso> getColaListos() { return colaListos; }
    public List<Proceso> getColaBloqueados() { return colaBloqueados; }
    public List<Proceso> getColaTerminados() { return colaTerminados; }
    public List<HistorialTick> getHistorial() { return historial; }
    public Proceso getEnEjecucion() { return enEjecucion; }
    public int getTickActual() { return tickActual; }
    public boolean isSimulando() { return simulando; }
}