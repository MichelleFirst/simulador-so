package com.simulador.so.model;

public enum AlgoritmoReemplazo {
    FIFO("FIFO — First In First Out"),
    LRU("LRU — Least Recently Used");

    private final String descripcion;

    AlgoritmoReemplazo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return descripcion; }
}