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

        // 1. Llegada de procesos en el tick actual
        for (Proceso p : procesos) {
            if (p.getLlegada() == tickActual && p.getEstado() == EstadoProceso.NUEVO) {
                p.setEstado(EstadoProceso.LISTO);
                if (!colaListos.contains(p)) {
                    colaListos.offer(p);
                }
            }
        }

        // 2. Evaluar Expropiación (Solo si hay alguien ejecutando y el algoritmo es expropiativo)
        if (enEjecucion != null && enEjecucion.getEstado() == EstadoProceso.EJECUCION && algoritmo.isExpropiativo()) {
            Proceso elMejorCandidato = obtenerMejorEnListos();
            if (elMejorCandidato != null && debeExpropiar(enEjecucion, elMejorCandidato)) {
                // El proceso actual sufre expropiación y regresa a la cola de listos
                enEjecucion.setEstado(EstadoProceso.LISTO);
                colaListos.offer(enEjecucion);
                enEjecucion = null;
                ticksEjecucionActual = 0;
            }
        }

        // 3. Asignar CPU si está libre
        if (enEjecucion == null || enEjecucion.getEstado() == EstadoProceso.TERMINADO || enEjecucion.getEstado() == EstadoProceso.BLOQUEADO) {
            enEjecucion = seleccionarProceso();
            ticksEjecucionActual = 0;
        }

        // 4. Ejecutar el proceso seleccionado en este tick
        // 4. Ejecutar el proceso seleccionado en este tick
        int procesoEjecutadoId = -1;
        if (enEjecucion != null) {
            if (enEjecucion.getTiempoInicio() == -1) {
                enEjecucion.setTiempoInicio(tickActual);
            }
            enEjecucion.setEstado(EstadoProceso.EJECUCION);

            // !!! CORRECCIÓN 1: Decrementar tiempoRestante, NUNCA la ráfaga original !!!
            enEjecucion.setTiempoRestante(enEjecucion.getTiempoRestante() - 1);
            ticksEjecucionActual++;
            procesoEjecutadoId = enEjecucion.getId();


            // !!! BUSCA ESTE BLOQUE EXACTO EN PLANIFICADORCPU.JAVA Y REEMPLÁZALO !!!
            // === ASEGÚRATE DE QUE EL PASO 4 EN PlanificadorCPU.java ESTÉ ASÍ ===
            if (enEjecucion.getTiempoRestante() <= 0) {
                enEjecucion.setEstado(EstadoProceso.TERMINADO);

                // Como el proceso se ejecuta durante el tick actual, finaliza en el siguiente instante de tiempo
                int finExacto = this.tickActual + 1;
                enEjecucion.setTiempoFin(finExacto);

                // Fórmulas matemáticas estándar de Sistemas Operativos
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
            // Verificar si expiró su Quantum (Round Robin)
            else if (algoritmo == AlgoritmoPlanificacion.ROUND_ROBIN && ticksEjecucionActual >= quantum) {
                enEjecucion.setEstado(EstadoProceso.LISTO);
                colaListos.offer(enEjecucion);
                enEjecucion = null;
                ticksEjecucionActual = 0;
            }
        }


        // 5. Incrementar tiempos de espera de los procesos que se quedaron esperando en LISTO
        for (Proceso p : colaListos) {
            if (p.getEstado() == EstadoProceso.LISTO) {
                p.setTiempoEspera(p.getTiempoEspera() + 1);
            }
        }

        // 6. Registrar en el Historial para el Diagrama de Gantt de la Vista
        HistorialTick tr = new HistorialTick();
        tr.setTick(tickActual);
        tr.setProcesoEjecucion(procesoEjecutadoId);
        for (Proceso p : colaListos) if (p.getEstado() == EstadoProceso.LISTO) tr.getListos().add(p.getId());
        for (Proceso p : colaBloqueados) tr.getBloqueados().add(p.getId());
        for (Proceso p : colaTerminados) tr.getTerminados().add(p.getId());
        historial.add(tr);

        tickActual++;

        // Condición de término de la simulación
        if (colaTerminados.size() == procesos.size()) {
            simulando = false;
            return false;
        }
        return true;
    }

    private Proceso obtenerMejorEnListos() {
        if (colaListos.isEmpty()) return null;

        // Clausura de limpieza preventiva
        colaListos.removeIf(p -> p.getEstado() == EstadoProceso.TERMINADO || p.getEstado() == EstadoProceso.EJECUCION);
        if (colaListos.isEmpty()) return null;

        switch (algoritmo) {
            case FCFS:
            case ROUND_ROBIN:
                return colaListos.peek(); // El primero que llegó
            case SJF_NO_EXPROPIATIVO:
            case SRTF:
                // Menor tiempo restante; si hay empate, el que llegó primero (id o llegada)
                return colaListos.stream()
                        .min(Comparator.comparingInt(Proceso::getTiempoRestante)
                                .thenComparingInt(Proceso::getLlegada))
                        .orElse(null);
            case PRIORIDAD_NO_EXPROPIATIVA:
            case PRIORIDAD_EXPROPIATIVA:
                // Menor valor numérico = Mayor prioridad; desempata por llegada
                return colaListos.stream()
                        .min(Comparator.comparingInt(Proceso::getPrioridad)
                                .thenComparingInt(Proceso::getLlegada))
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