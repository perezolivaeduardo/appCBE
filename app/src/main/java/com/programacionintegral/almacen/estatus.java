package com.programacionintegral.almacen;

public class estatus {
    private String Almacen;
    private String Comentario;
    private Boolean Cerrado;

    public estatus(String almacen, String comentario, Boolean cerrado) {
        Almacen = almacen;
        Comentario = comentario;
        Cerrado = cerrado;
    }

    public String getAlmacen() {
        return Almacen;
    }

    public void setAlmacen(String almacen) {
        Almacen = almacen;
    }

    public String getComentario() {
        return Comentario;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }

    public Boolean getCerrado() {
        return Cerrado;
    }

    public void setCerrado(Boolean cerrado) {
        Cerrado = cerrado;
    }
}
