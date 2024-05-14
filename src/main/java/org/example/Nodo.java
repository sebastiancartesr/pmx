package org.example;

public class Nodo {
    private Nodo siguiente;
    private Nodo anterior;
    private int posicion;
    private int id;

    // Constructor
    public Nodo(int id, int posicion, Nodo anterior, Nodo siguiente) {
        this.id = id;
        this.posicion = posicion;
        // Verificar y establecer el nodo anterior y siguiente
        if (anterior != null) {
            this.anterior = anterior;
            anterior.setSiguiente(this);
        }
        if (siguiente != null) {
            this.siguiente = siguiente;
            siguiente.setAnterior(this);
        }
    }

    // Getters
    public Nodo getSiguiente() {
        return this.siguiente;
    }

    public Nodo getAnterior() {
        return this.anterior;
    }

    public int getPosicion() {
        return this.posicion;
    }

    public int getId() {
        return this.id;
    }

    // Setters
    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    public void setAnterior(Nodo anterior) {
        this.anterior = anterior;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setId(int id) {
        this.id = id;
    }
}
