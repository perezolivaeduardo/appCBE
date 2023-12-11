package com.programacionintegral.almacen;

import java.util.Date;

public class solicitud {
    private int idArea;
    private String area;
    private Date fecha;
    private int tipo;
    private int idUsuario;
    private  String solicita;
    private int idProducto;
    private String producto;
    private int cantidad;
    private int surtido;
    private String comentarios;

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getSolicita() {
        return solicita;
    }

    public void setSolicita(String solicita) {
        this.solicita = solicita;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getSurtido() {
        return surtido;
    }

    public void setSurtido(int surtido) {
        this.surtido = surtido;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public solicitud (int idArea, String area, Date fecha, int tipo, int idUsuario, String solicita, int idProducto, String producto, int cantidad, int surtido, String comentarios){

        this.idArea=idArea;
        this.area=area;
        this.fecha=fecha;
        this.tipo=tipo;
        this.idUsuario=idUsuario;
        this.solicita=solicita;
        this.idProducto=idProducto;
        this.cantidad=cantidad;
        this.surtido=surtido;


    }


}
