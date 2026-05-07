package com.circularmarket.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRid")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PRcategoriaid", referencedColumnName = "CPid", nullable = false)
    private Categoria categoria;

    @Column(name = "PRtitulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "PRdescripcion", length = 255)
    private String descripcion;

    @Column(name = "PRstock")
    private Integer stock = 0;

    @Column(name = "PRprecio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "PRactivo")
    private Boolean activo = true;

    @Lob
    @Column(name = "PRimagen", columnDefinition = "MEDIUMBLOB")
    private byte[] imagen;

    @Column(name = "PRcreadoen", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "PRactualizadoen")
    private LocalDateTime actualizadoEn;

    public Producto() {
    }

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();

        if (this.stock == null) {
            this.stock = 0;
        }
        if (this.activo == null) {
            this.activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo != null ? titulo.trim() : null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock != null ? stock : 0;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}