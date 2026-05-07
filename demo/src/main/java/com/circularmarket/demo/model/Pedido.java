package com.circularmarket.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PEid")
    private Long id;

    @Column(name = "PEcompradorid", nullable = false)
    private Integer compradorId;

    @Column(name = "PEdireccionenvioid", nullable = false)
    private Integer direccionEnvioId;

    @Column(name = "PEimportetotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal importeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "PEestado", nullable = false)
    private EstadoPedido estado;

    @Column(name = "PEfechapedido", insertable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "PEactualizadoen", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(Integer compradorId) {
        this.compradorId = compradorId;
    }

    public Integer getDireccionEnvioId() {
        return direccionEnvioId;
    }

    public void setDireccionEnvioId(Integer direccionEnvioId) {
        this.direccionEnvioId = direccionEnvioId;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}