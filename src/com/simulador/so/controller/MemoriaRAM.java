package com.simulador.so.controller;

import com.simulador.so.model.AlgoritmoReemplazo;
import com.simulador.so.model.Marco;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemoriaRAM {
    private List<Marco> marcos = new ArrayList<>();
    private List<Integer> secuencia = new ArrayList<>();
    private List<int[]> historialRAM = new ArrayList<>();
    private AlgoritmoReemplazo algoritmo = AlgoritmoReemplazo.FIFO;
    private int tick = 0;
    private int fallos = 0;
    private int hits = 0;
    private boolean simulando = false;

    public void inicializar(int numMarcos, List<Integer> secuenciaPaginas, AlgoritmoReemplazo alg) {
        this.marcos.clear();
        for (int i = 0; i < numMarcos; i++) {
            this.marcos.add(new Marco(i));
        }
        this.secuencia = new ArrayList<>(secuenciaPaginas);
        this.algoritmo = alg;
        this.tick = 0;
        this.fallos = 0;
        this.hits = 0;
        this.historialRAM.clear();
        this.simulando = true;
    }

    public String ejecutarPaso() {
        if (!simulando || tick >= secuencia.size()) {
            simulando = false;
            return null;
        }

        int pagina = secuencia.get(tick);
        boolean encontrado = false;
        Marco marcoHit = null;

        for (Marco m : marcos) {
            if (m.isOcupado() && m.getPagina() == pagina) {
                encontrado = true;
                marcoHit = m;
                m.setUltimoUso(tick);
                hits++;
                break;
            }
        }

        String mensaje;
        if (!encontrado) {
            fallos++;
            Marco libre = marcos.stream().filter(m -> !m.isOcupado()).findFirst().orElse(null);
            if (libre != null) {
                libre.setPagina(pagina);
                libre.setOcupado(true);
                libre.setTiempoCarga(tick);
                libre.setUltimoUso(tick);
                mensaje = "[Paso " + tick + "] Página " + pagina + " → FALLO (Cargado en Marco " + libre.getId() + ")";
            } else {
                Marco victima;
                if (algoritmo == AlgoritmoReemplazo.FIFO) {
                    victima = marcos.stream().min(Comparator.comparingInt(Marco::getTiempoCarga)).orElse(null);
                } else {
                    victima = marcos.stream().min(Comparator.comparingInt(Marco::getUltimoUso)).orElse(null);
                }
                if (victima != null) {
                    int paginaReemplazada = victima.getPagina();
                    victima.setPagina(pagina);
                    victima.setTiempoCarga(tick);
                    victima.setUltimoUso(tick);
                    mensaje = "[Paso " + tick + "] Página " + pagina + " → FALLO (Reemplaza P" + paginaReemplazada + " en Marco " + victima.getId() + ")";
                } else {
                    mensaje = "[Paso " + tick + "] Página " + pagina + " → FALLO";
                }
            }
        } else {
            mensaje = "[Paso " + tick + "] Página " + pagina + " → HIT (Marco " + marcoHit.getId() + ")";
        }

        int[] estado = new int[marcos.size()];
        for (int i = 0; i < marcos.size(); i++) {
            estado[i] = marcos.get(i).isOcupado() ? marcos.get(i).getPagina() : -1;
        }
        historialRAM.add(estado);

        tick++;
        return mensaje;
    }

    public void reset() {
        simulando = false;
        tick = 0;
        fallos = 0;
        hits = 0;
        marcos.clear();
        secuencia.clear();
        historialRAM.clear();
    }

    public List<Marco> getMarcos() { return marcos; }
    public List<Integer> getSecuencia() { return secuencia; }
    public List<int[]> getHistorialRAM() { return historialRAM; }
    public int getTick() { return tick; }
    public int getFallos() { return fallos; }
    public int getHits() { return hits; }
    public boolean isSimulando() { return simulando; }
    public AlgoritmoReemplazo getAlgoritmo() { return algoritmo; }
}