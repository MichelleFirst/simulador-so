package com.simulador.so.model;



import java.awt.Color;

import java.util.ArrayList;

import java.util.List;



public class Proceso {

    private int id;

    private int llegada;

    private int rafaga;

    private int prioridad;

    private int tiempoRestante;

    private int tiempoEspera;

    private int tiempoRetorno;

    private int tiempoInicio = -1;

    private int tiempoFin = -1;

    private EstadoProceso estado = EstadoProceso.NUEVO;

    private Color color;

    private String nombre;

    private List<Integer> paginasSolicitadas = new ArrayList<>();

    private int paginaActualIdx = 0;

    private boolean enFalloPagina = false;



    public Proceso(int id, int llegada, int rafaga, int prioridad) {

        this.id = id;

        this.llegada = llegada;

        this.rafaga = rafaga;

        this.prioridad = prioridad;

        this.tiempoRestante = rafaga;

        this.nombre = "P" + id;

    }



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



    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getLlegada() { return llegada; }

    public void setLlegada(int llegada) { this.llegada = llegada; }

    public int getRafaga() { return rafaga; }

    public void setRafaga(int rafaga) { this.rafaga = rafaga; }

    public int getPrioridad() { return prioridad; }

    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public int getTiempoRestante() { return tiempoRestante; }

    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; }

    public int getTiempoEspera() { return tiempoEspera; }

    public void setTiempoEspera(int tiempoEspera) { this.tiempoEspera = tiempoEspera; }

    public int getTiempoRetorno() { return tiempoRetorno; }

    public void setTiempoRetorno(int tiempoRetorno) { this.tiempoRetorno = tiempoRetorno; }

    public int getTiempoInicio() { return tiempoInicio; }

    public void setTiempoInicio(int tiempoInicio) { this.tiempoInicio = tiempoInicio; }

    public int getTiempoFin() { return tiempoFin; }

    public void setTiempoFin(int tiempoFin) { this.tiempoFin = tiempoFin; }

    public EstadoProceso getEstado() { return estado; }

    public void setEstado(EstadoProceso estado) { this.estado = estado; }

    public Color getColor() { return color; }

    public void setColor(Color color) { this.color = color; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Integer> getPaginasSolicitadas() { return paginasSolicitadas; }

    public void setPaginasSolicitadas(List<Integer> paginasSolicitadas) { this.paginasSolicitadas = paginasSolicitadas; }

    public int getPaginaActualIdx() { return paginaActualIdx; }

    public void setPaginaActualIdx(int paginaActualIdx) { this.paginaActualIdx = paginaActualIdx; }

    public boolean isEnFalloPagina() { return enFalloPagina; }

    public void setEnFalloPagina(boolean enFalloPagina) { this.enFalloPagina = enFalloPagina; }

}

