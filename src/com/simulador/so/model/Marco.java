package com.simulador.so.model;

public class Marco {
    private int id;
    private int pagina = -1;
    private int procesoId = -1;
    private int tiempoCarga = -1;
    private int ultimoUso = -1;
    private boolean ocupado = false;

    public Marco(int id) {
        this.id = id;
    }

    public void limpiar() {
        pagina = -1;
        procesoId = -1;
        tiempoCarga = -1;
        ultimoUso = -1;
        ocupado = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getProcesoId() { return procesoId; }
    public void setProcesoId(int procesoId) { this.procesoId = procesoId; }
    public int getTiempoCarga() { return tiempoCarga; }
    public void setTiempoCarga(int tiempoCarga) { this.tiempoCarga = tiempoCarga; }
    public int getUltimoUso() { return ultimoUso; }
    public void setUltimoUso(int ultimoUso) { this.ultimoUso = ultimoUso; }
    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }
}