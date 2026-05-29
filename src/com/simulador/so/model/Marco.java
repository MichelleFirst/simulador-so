package com.simulador.so.model;

/**
 * Clque representa un marco de página en la memoria RAM.
 * Cada marco puede contener una página de un proceso y mantiene métricas
 * necesarias para los algoritmos de reemplazo (FIFO y LRU).
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class Marco {
    // ==================== ATRIBUTOS ====================

    /** Identificador único del marco (normalmente su posición en memoria) */
    private int id;

    /** Número de página almacenada en este marco (-1 si está vacío) */
    private int pagina = -1;

    /** ID del proceso al que pertenece la página almacenada (-1 si está vacío) */
    private int procesoId = -1;

    /** Tick en el que la página fue cargada en este marco (utilizado para FIFO) */
    private int tiempoCarga = -1;

    /** Tick del último acceso a esta página (utilizado para LRU) */
    private int ultimoUso = -1;

    /** Indica si el marco está actualmente ocupado por alguna página */
    private boolean ocupado = false;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor que inicializa un marco con su identificador.
     * El marco comienza vacío (ocupado = false).
     *
     * @param id Identificador único del marco
     */
    public Marco(int id) {
        this.id = id;
    }

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Limpia el marco, reiniciando todos sus atributos al estado inicial.
     * Útil para reiniciar la simulación sin crear nuevos objetos.
     */
    public void limpiar() {
        pagina = -1;
        procesoId = -1;
        tiempoCarga = -1;
        ultimoUso = -1;
        ocupado = false;
    }

    // ==================== MÉTODOS GETTER Y SETTER ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getProcesoId() {
        return procesoId;
    }

    public void setProcesoId(int procesoId) {
        this.procesoId = procesoId;
    }

    public int getTiempoCarga() {
        return tiempoCarga;
    }

    public void setTiempoCarga(int tiempoCarga) {
        this.tiempoCarga = tiempoCarga;
    }

    public int getUltimoUso() {
        return ultimoUso;
    }

    public void setUltimoUso(int ultimoUso) {
        this.ultimoUso = ultimoUso;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
}