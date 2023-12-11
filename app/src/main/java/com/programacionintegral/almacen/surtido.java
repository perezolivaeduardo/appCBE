package com.programacionintegral.almacen;

public class surtido {
    private int id_surtido;
    private String fecha;
    private String almacen;
    private int id_producto;
    private String descripcion;
    private int cantidad;
    private boolean entregado;
    private String comentario;
    private boolean recolectado;

    public surtido (int id_surtido,String fecha,String almacen,int id_producto, String descripcion,int cantidad,boolean entregado,String comentario, boolean recolectado){
        this.id_surtido = id_surtido;
        this.fecha=fecha;
        this.almacen=almacen;
        this.id_producto=id_producto;
        this.descripcion=descripcion;
        this.cantidad=cantidad;
        this.entregado=entregado;
        this.comentario=comentario;
        this.recolectado=recolectado;
    }

    public int getId() {
        return id_surtido;
    }

    public void setId(int id_surtido) {
        this.id_surtido = id_surtido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public boolean isRecolectado() {
        return recolectado;
    }

    public void setRecolectado(boolean recolectado) {
        this.recolectado = recolectado;
    }

}
