package com.circularmarket.demo.dto;

import java.math.BigDecimal;

public class CarritoItem {

    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precio;
    private Integer stock;
    private Integer cantidad;
    private BigDecimal total;

    public CarritoItem() {
    }

    public CarritoItem(Long id,
                       String nombre,
                       String descripcion,
                       String imagenUrl,
                       BigDecimal precio,
                       Integer stock,
                       Integer cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.stock = stock;
        this.cantidad = cantidad;
        this.total = precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }
}