package com.simulador.so.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un proceso dentro del simulador del sistema operativo.
 * Contiene toda la información necesaria para la planificación de CPU,
 * incluyendo métricas de tiempo, estado actual y gestión de páginas de memoria.
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class Proceso {
    // ==================== ATRIBUTOS ====================

    /** Identificador único del proceso */
    private int id;

    /** Tiempo de llegada al sistema (cuando está listo para ejecutarse) */
    private int llegada;

    /** Ráfaga de CPU total necesaria para completar el proceso */
    private int rafaga;

    /** Prioridad del proceso (menor número = mayor prioridad) */
    private int prioridad;

    /** Tiempo restante de ejecución (se decrementa durante la simulación) */
    private int tiempoRestante;

    /** Tiempo total que el proceso ha estado esperando en cola de listos */
    private int tiempoEspera;

    /** Tiempo de retorno = tiempoFin - tiempoLlegada */
    private int tiempoRetorno;

    /** Tick en el que el proceso comenzó su ejecución por primera vez (-1 si no ha comenzado) */
    private int tiempoInicio = -1;

    /** Tick en el que el proceso completó su ejecución (-1 si no ha terminado) */
    private int tiempoFin = -1;

    /** Estado actual del proceso (NUEVO, LISTO, EJECUCION, BLOQUEADO, TERMINADO) */
    private EstadoProceso estado = EstadoProceso.NUEVO;

    /** Color asociado al proceso para visualización en la interfaz gráfica */
    private Color color;

    /** Nombre descriptivo del proceso (por defecto "P" + id) */
    private String nombre;

    /** Lista de páginas que este proceso solicitará durante su ejecución */
    private List<Integer> paginasSolicitadas = new ArrayList<>();

    /** Índice actual dentro de la lista de páginas solicitadas */
    private int paginaActualIdx = 0;

    /** Indica si el proceso está actualmente en estado de fallo de página (bloqueado) */
    private boolean enFalloPagina = false;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor principal para crear un nuevo proceso.
     * Inicializa el tiempo restante con la ráfaga proporcionada.
     *
     * @param id Identificador único del proceso
     * @param llegada Tick en el que el proceso llega al sistema
     * @param rafaga Ráfaga de CPU total necesaria
     * @param prioridad Prioridad del proceso (menor número = mayor prioridad)
     */
    public Proceso(int id, int llegada, int rafaga, int prioridad) {
        this.id = id;
        this.llegada = llegada;
        this.rafaga = rafaga;
        this.prioridad = prioridad;
        this.tiempoRestante = rafaga;
        this.nombre = "P" + id;
    }

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Crea una copia profunda (clon) del proceso actual.
     * Útil para reiniciar simulaciones sin modificar los procesos originales.
     *
     * @return Una nueva instancia de Proceso con los mismos valores
     */
    public Proceso clonar() {
        Proceso p = new Proceso(id, llegada, rafaga, prioridad);
        p.setTiempoRestante(this.tiempoRestante);
        p.setTiempoEspera(this.tiempoEspera);
        p.setTiempoRetorno(this.tiempoRetorno);
        p.setTiempoInicio(this.tiempoInicio);
        p.setTiempoFin(this.tiempoFin);
        p.setEstado(this.estado);
        p.setColor(this.color);
        p.setNombre(this.nombre);
        p.setPaginaActualIdx(this.paginaActualIdx);
        p.setEnFalloPagina(this.enFalloPagina);
        p.paginasSolicitadas = new ArrayList<>(this.paginasSolicitadas);
        return p;
    }

    // ==================== MÉTODOS GETTER Y SETTER ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLlegada() {
        return llegada;
    }

    public void setLlegada(int llegada) {
        this.llegada = llegada;
    }

    public int getRafaga() {
        return rafaga;
    }

    public void setRafaga(int rafaga) {
        this.rafaga = rafaga;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public int getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(int tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

    public int getTiempoRetorno() {
        return tiempoRetorno;
    }

    public void setTiempoRetorno(int tiempoRetorno) {
        this.tiempoRetorno = tiempoRetorno;
    }

    public int getTiempoInicio() {
        return tiempoInicio;
    }

    public void setTiempoInicio(int tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    public int getTiempoFin() {
        return tiempoFin;
    }

    public void setTiempoFin(int tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Integer> getPaginasSolicitadas() {
        return paginasSolicitadas;
    }

    public void setPaginasSolicitadas(List<Integer> paginasSolicitadas) {
        this.paginasSolicitadas = paginasSolicitadas;
    }

    public int getPaginaActualIdx() {
        return paginaActualIdx;
    }

    public void setPaginaActualIdx(int paginaActualIdx) {
        this.paginaActualIdx = paginaActualIdx;
    }

    public boolean isEnFalloPagina() {
        return enFalloPagina;
    }

    public void setEnFalloPagina(boolean enFalloPagina) {
        this.enFalloPagina = enFalloPagina;
    }
}