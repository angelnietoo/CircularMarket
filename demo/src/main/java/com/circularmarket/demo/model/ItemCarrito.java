package com.circularmarket.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "itemscarrito",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_itemscarrito_carrito_producto",
                        columnNames = {"ICcarritoid", "ICproductoid"}
                )
        }
)
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ICid")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ICcarritoid", referencedColumnName = "CRid", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ICproductoid", referencedColumnName = "PRid", nullable = false)
    private Producto producto;

    @Column(name = "ICcantidad")
    private Integer cantidad = 1;

    @Column(name = "ICagregadoen", insertable = false, updatable = false)
    private LocalDateTime agregadoEn;

    public ItemCarrito() {
    }

    public ItemCarrito(Carrito carrito, Producto producto, Integer cantidad) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad != null ? cantidad : 1;
    }

    @PrePersist
    @PreUpdate
    public void validarCantidad() {
        if (cantidad == null || cantidad < 1) {
            cantidad = 1;
        }
    }

    public Long getId() {
        return id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getAgregadoEn() {
        return agregadoEn;
    }
}