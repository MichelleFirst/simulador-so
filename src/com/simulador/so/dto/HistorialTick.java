package com.simulador.so.dto;

import com.simulador.so.model.Marco;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) que representa el estado completo del sistema en un tick específico.
 * Almacena la información necesaria para generar el diagrama de Gantt y visualizar
 * el estado de CPU, colas y memoria en cada instante de la simulación.
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class HistorialTick {
    // ==================== ATRIBUTOS ====================

    /** Número de tick correspondiente a este registro histórico */
    private int tick;

    /** ID del proceso que está en ejecución en este tick (-1 si no hay proceso) */
    private int procesoEjecucion = -1;

    /** Lista de IDs de procesos en estado LISTO */
    private List<Integer> listos = new ArrayList<>();

    /** Lista de IDs de procesos en estado BLOQUEADO */
    private List<Integer> bloqueados = new ArrayList<>();

    /** Lista de IDs de procesos en estado TERMINADO */
    private List<Integer> terminados = new ArrayList<>();

    /** Estado actual de los marcos de memoria RAM */
    private List<Marco> marcos = new ArrayList<>();

    /** Indica si ocurrió un fallo de página en este tick */
    private boolean falloPagina = false;

    /** Número de página que causó el fallo (-1 si no hubo fallo) */
    private int paginaFallida = -1;

    /** ID del proceso que experimentó el fallo de página (-1 si no hubo fallo) */
    private int procesoFallido = -1;

    /** Nombre del algoritmo de memoria utilizado (FIFO o LRU) */
    private String algoritmoMemoria = "";

    // ==================== MÉTODOS GETTER Y SETTER ====================

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getProcesoEjecucion() {
        return procesoEjecucion;
    }

    public void setProcesoEjecucion(int procesoEjecucion) {
        this.procesoEjecucion = procesoEjecucion;
    }

    public List<Integer> getListos() {
        return listos;
    }

    public void setListos(List<Integer> listos) {
        this.listos = listos;
    }

    public List<Integer> getBloqueados() {
        return bloqueados;
    }

    public void setBloqueados(List<Integer> bloqueados) {
        this.bloqueados = bloqueados;
    }

    public List<Integer> getTerminados() {
        return terminados;
    }

    public void setTerminados(List<Integer> terminados) {
        this.terminados = terminados;
    }

    public List<Marco> getMarcos() {
        return marcos;
    }

    public void setMarcos(List<Marco> marcos) {
        this.marcos = marcos;
    }

    public boolean isFalloPagina() {
        return falloPagina;
    }

    public void setFalloPagina(boolean falloPagina) {
        this.falloPagina = falloPagina;
    }

    public int getPaginaFallida() {
        return paginaFallida;
    }

    public void setPaginaFallida(int paginaFallida) {
        this.paginaFallida = paginaFallida;
    }

    public int getProcesoFallido() {
        return procesoFallido;
    }

    public void setProcesoFallido(int procesoFallido) {
        this.procesoFallido = procesoFallido;
    }

    public String getAlgoritmoMemoria() {
        return algoritmoMemoria;
    }

    public void setAlgoritmoMemoria(String algoritmoMemoria) {
        this.algoritmoMemoria = algoritmoMemoria;
    }
}