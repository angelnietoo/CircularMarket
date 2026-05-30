package com.circularmarket.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CRid")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CRusuarioid", referencedColumnName = "USid", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    @Column(name = "CRactualizadoen", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public Carrito() {
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}