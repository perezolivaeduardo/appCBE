package com.programacionintegral.almacen;

public class inventariodiario {
    private Integer id_area;
    private Integer id_producto;
    private String fecha;
    private String turno;
    private String descripcion;
    private Integer cantidad;
    private Integer id_usuario;
    private String hora;
    private boolean contado;
    private Integer stock;
    private Integer consumidos;
    private Integer libros;


    public inventariodiario(Integer id_area, Integer id_producto, String fecha, String turno, String descripcion, Integer cantidad, Integer id_usuario, String hora, boolean contado, Integer stock,Integer consumidos, Integer libros) {
        this.id_area = id_area;
        this.id_producto = id_producto;
        this.fecha = fecha;
        this.turno = turno;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.id_usuario = id_usuario;
        this.hora = hora;
        this.contado = contado;
        this.stock=stock;
        this.consumidos=consumidos;
        this.libros=libros;
    }

    public Integer getId_area() {
        return id_area;
    }

    public void setId_area(Integer id_area) {
        this.id_area = id_area;
    }

    public Integer getId_producto() {
        return id_producto;
    }

    public void setId_producto(Integer id_producto) {
        this.id_producto = id_producto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isContado() {
        return contado;
    }

    public void setContado(boolean contado) {
        this.contado = contado;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getConsumidos() {
        return consumidos;
    }

    public void setConsumidos(Integer consumidos) {
        this.consumidos = consumidos;
    }

    public Integer getLibros() {
        return libros;
    }

    public void setLibros(Integer libros) {
        this.libros = libros;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
