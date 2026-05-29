package com.simulador.so.controller;

import com.simulador.so.model.AlgoritmoReemplazo;
import com.simulador.so.model.Marco;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Clase que simula la memoria RAM principal del sistema operativo.
 * Implementa algoritmos de reemplazo de páginas (FIFO y LRU) para la gestión de memoria.
 *
 * @author SimuladorSO
 * @version 1.0
 */
public class MemoriaRAM {
    // ==================== ATRIBUTOS ====================

    /** Lista de marcos de página que componen la memoria RAM */
    private List<Marco> marcos = new ArrayList<>();

    /** Secuencia de páginas que serán referenciadas durante la simulación */
    private List<Integer> secuencia = new ArrayList<>();

    /** Historial de estados de la RAM en cada paso de la simulación */
    private List<int[]> historialRAM = new ArrayList<>();

    /** Algoritmo de reemplazo de páginas actual (FIFO o LRU) */
    private AlgoritmoReemplazo algoritmo = AlgoritmoReemplazo.FIFO;

    /** Contador de pasos/ticks de la simulación */
    private int tick = 0;

    /** Número total de fallos de página ocurridos */
    private int fallos = 0;

    /** Número total de aciertos de página ocurridos */
    private int hits = 0;

    /** Estado de la simulación (activa o inactiva) */
    private boolean simulando = false;

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Inicializa o reinicia la simulación con los parámetros especificados.
     * Limpia todos los estados previos y configura una nueva simulación.
     *
     * @param numMarcos Número de marcos de página disponibles en RAM
     * @param secuenciaPaginas Lista con la secuencia de páginas a referenciar
     * @param alg Algoritmo de reemplazo a utilizar (FIFO o LRU)
     */
    public void inicializar(int numMarcos, List<Integer> secuenciaPaginas, AlgoritmoReemplazo alg) {
        // Limpiar y crear nuevos marcos
        this.marcos.clear();
        for (int i = 0; i < numMarcos; i++) {
            this.marcos.add(new Marco(i));
        }

        // Configurar parámetros de simulación
        this.secuencia = new ArrayList<>(secuenciaPaginas);
        this.algoritmo = alg;
        this.tick = 0;
        this.fallos = 0;
        this.hits = 0;
        this.historialRAM.clear();
        this.simulando = true;
    }

    /**
     * Ejecuta un paso (tick) de la simulación.
     * Procesa una referencia de página, determina si es fallo o acierto,
     * y aplica el algoritmo de reemplazo correspondiente.
     *
     * @return Mensaje descriptivo del paso ejecutado, o null si la simulación ha terminado
     */
    public String ejecutarPaso() {
        // Validar si la simulación debe continuar
        if (!simulando || tick >= secuencia.size()) {
            simulando = false;
            return null;
        }

        int pagina = secuencia.get(tick);
        boolean encontrado = false;
        Marco marcoHit = null;

        // Buscar si la página ya está cargada en algún marco (acierto)
        for (Marco m : marcos) {
            if (m.isOcupado() && m.getPagina() == pagina) {
                encontrado = true;
                marcoHit = m;
                m.setUltimoUso(tick);  // Actualizar timestamp para LRU
                hits++;
                break;
            }
        }

        String mensaje;

        if (!encontrado) {
            // CASO: FALLO DE PÁGINA
            fallos++;

            // Buscar un marco libre disponible
            Marco libre = marcos.stream().filter(m -> !m.isOcupado()).findFirst().orElse(null);

            if (libre != null) {
                // Hay marco libre: cargar página directamente
                libre.setPagina(pagina);
                libre.setOcupado(true);
                libre.setTiempoCarga(tick);
                libre.setUltimoUso(tick);
                mensaje = "[Paso " + tick + "] Página " + pagina + " → FALLO (Cargado en Marco " + libre.getId() + ")";
            } else {
                // No hay marcos libres: aplicar algoritmo de reemplazo
                Marco victima;

                if (algoritmo == AlgoritmoReemplazo.FIFO) {
                    // FIFO: reemplazar la página más antigua (menor tiempo de carga)
                    victima = marcos.stream().min(Comparator.comparingInt(Marco::getTiempoCarga)).orElse(null);
                } else {
                    // LRU: reemplazar la página usada menos recientemente
                    victima = marcos.stream().min(Comparator.comparingInt(Marco::getUltimoUso)).orElse(null);
                }

                if (victima != null) {
                    int paginaReemplazada = victima.getPagina();
                    victima.setPagina(pagina);
                    victima.setTiempoCarga(tick);
                    victima.setUltimoUso(tick);
                    mensaje = "[Paso " + tick + "] Página " + pagina + " → FALLO (Reemplaza P" + paginaReemplazada + " en Marco " + victima.getId() + ")";
                } else {
                    // Caso de seguridad: no debería ocurrir si hay marcos
                    mensaje = "[Paso " + tick + "] Página " + pagina + " → FALLO";
                }
            }
        } else {
            // CASO: ACIERTO DE PÁGINA
            mensaje = "[Paso " + tick + "] Página " + pagina + " → HIT (Marco " + marcoHit.getId() + ")";
        }

        // Registrar el estado actual de la RAM en el historial
        int[] estado = new int[marcos.size()];
        for (int i = 0; i < marcos.size(); i++) {
            estado[i] = marcos.get(i).isOcupado() ? marcos.get(i).getPagina() : -1;
        }
        historialRAM.add(estado);

        tick++;
        return mensaje;
    }

    /**
     * Reinicia completamente el estado de la simulación.
     * Limpia todos los datos y prepara la instancia para una nueva simulación.
     */
    public void reset() {
        simulando = false;
        tick = 0;
        fallos = 0;
        hits = 0;
        marcos.clear();
        secuencia.clear();
        historialRAM.clear();
    }

    // ==================== MÉTODOS GETTER ====================

    /**
     * @return Lista actual de marcos de página
     */
    public List<Marco> getMarcos() {
        return marcos;
    }

    /**
     * @return Secuencia de páginas a referenciar
     */
    public List<Integer> getSecuencia() {
        return secuencia;
    }

    /**
     * @return Historial de estados de la RAM en cada paso
     */
    public List<int[]> getHistorialRAM() {
        return historialRAM;
    }

    /**
     * @return Tick/paso actual de la simulación
     */
    public int getTick() {
        return tick;
    }

    /**
     * @return Número total de fallos de página
     */
    public int getFallos() {
        return fallos;
    }

    /**
     * @return Número total de aciertos de página
     */
    public int getHits() {
        return hits;
    }

    /**
     * @return true si la simulación está activa, false en caso contrario
     */
    public boolean isSimulando() {
        return simulando;
    }

    /**
     * @return Algoritmo de reemplazo actualmente configurado
     */
    public AlgoritmoReemplazo getAlgoritmo() {
        return algoritmo;
    }
}