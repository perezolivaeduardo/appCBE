package com.programacionintegral.almacen;

public class inventario {

    private String fecha;
    private Integer id_producto;
    private String categoria;
    private String descripcion;
    private Integer existencia;
    private Integer fisico;
    private String comentario;
   
public inventario (String fecha, int id_producto,String categoria,String descripcion,int existencia,int fisico,String comentario) {
    this.fecha=fecha;
    this.id_producto=id_producto;
    this.categoria=categoria;
    this.descripcion=descripcion;
    this.existencia=existencia;
    this.fisico=fisico;
    this.comentario=comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getId_producto() {
        return id_producto;
    }

    public void setId_producto(Integer id_producto) {
        this.id_producto = id_producto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia(Integer existencia) {
        this.existencia = existencia;
    }

    public Integer getFisico() {
        return fisico;
    }

    public void setFisico(Integer fisico) {
        this.fisico = fisico;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
