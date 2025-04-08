package com.function.model;

import java.time.LocalDateTime;

public class Factura {
    private Long idFactura;
    private String numeroFactura;
    private Long idCliente;
    private LocalDateTime fechaEmision;
    private Double montoSubtotal;
    private Double montoIva;
    private Double montoTotal;
    private String estado;
    private String formaPago;
    private String observaciones;
    
    // Getters y setters
    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Double getMontoSubtotal() {
        return montoSubtotal;
    }

    public void setMontoSubtotal(Double montoSubtotal) {
        this.montoSubtotal = montoSubtotal;
    }

    public Double getMontoIva() {
        return montoIva;
    }

    public void setMontoIva(Double montoIva) {
        this.montoIva = montoIva;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}