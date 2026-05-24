package com.simulador.so.dto;

import com.simulador.so.model.Marco;
import java.util.ArrayList;
import java.util.List;

public class HistorialTick {
    private int tick;
    private int procesoEjecucion = -1;
    private List<Integer> listos = new ArrayList<>();
    private List<Integer> bloqueados = new ArrayList<>();
    private List<Integer> terminados = new ArrayList<>();
    private List<Marco> marcos = new ArrayList<>();
    private boolean falloPagina = false;
    private int paginaFallida = -1;
    private int procesoFallido = -1;
    private String algoritmoMemoria = "";

    public int getTick() { return tick; }
    public void setTick(int tick) { this.tick = tick; }
    public int getProcesoEjecucion() { return procesoEjecucion; }
    public void setProcesoEjecucion(int procesoEjecucion) { this.procesoEjecucion = procesoEjecucion; }
    public List<Integer> getListos() { return listos; }
    public void setListos(List<Integer> listos) { this.listos = listos; }
    public List<Integer> getBloqueados() { return bloqueados; }
    public void setBloqueados(List<Integer> bloqueados) { this.bloqueados = bloqueados; }
    public List<Integer> getTerminados() { return terminados; }
    public void setTerminados(List<Integer> terminados) { this.terminados = terminados; }
    public List<Marco> getMarcos() { return marcos; }
    public void setMarcos(List<Marco> marcos) { this.marcos = marcos; }
    public boolean isFalloPagina() { return falloPagina; }
    public void setFalloPagina(boolean falloPagina) { this.falloPagina = falloPagina; }
    public int getPaginaFallida() { return paginaFallida; }
    public void setPaginaFallida(int paginaFallida) { this.paginaFallida = paginaFallida; }
    public int getProcesoFallido() { return procesoFallido; }
    public void setProcesoFallido(int procesoFallido) { this.procesoFallido = procesoFallido; }
    public String getAlgoritmoMemoria() { return algoritmoMemoria; }
    public void setAlgoritmoMemoria(String algoritmoMemoria) { this.algoritmoMemoria = algoritmoMemoria; }
}