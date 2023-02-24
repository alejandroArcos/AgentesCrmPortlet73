package com.tokio.crm.agentes73.model;

import java.util.Date;

public class AgenteLegalResponse {
    long agenteId;
    String rfc;
    long contrato;
    String clave;
    long estatusVentas;
    long estatusLC;
    String nombre;
    long ejecutivo;
    long oficina;
    Date fechaRegistro;
    Date fechaSeguimiento;
    Date fechaAlta;
    long tipoCedula;
    Date vencimientoCedula;
    String comprobante;
    Date vencimientoComprobante;
    String polizaRC;
    Date vencimientoPoliza;

    public long getAgenteId() {
        return agenteId;
    }

    public void setAgenteId(long agenteId) {
        this.agenteId = agenteId;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public long getContrato() {
        return contrato;
    }

    public void setContrato(long contrato) {
        this.contrato = contrato;
    }

    public long getEstatusVentas() {
        return estatusVentas;
    }

    public void setEstatusVentas(long estatusVentas) {
        this.estatusVentas = estatusVentas;
    }

    public long getEstatusLC() {
        return estatusLC;
    }

    public void setEstatusLC(long estatusLC) {
        this.estatusLC = estatusLC;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getEjecutivo() {
        return ejecutivo;
    }

    public void setEjecutivo(long ejecutivo) {
        this.ejecutivo = ejecutivo;
    }

    public long getOficina() {
        return oficina;
    }

    public void setOficina(long oficina) {
        this.oficina = oficina;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaSeguimiento() {
        return fechaSeguimiento;
    }

    public void setFechaSeguimiento(Date fechaSeguimiento) {
        this.fechaSeguimiento = fechaSeguimiento;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public long getTipoCedula() {
        return tipoCedula;
    }

    public void setTipoCedula(long tipoCedula) {
        this.tipoCedula = tipoCedula;
    }

    public Date getVencimientoCedula() {
        return vencimientoCedula;
    }

    public void setVencimientoCedula(Date vencimientoCedula) {
        this.vencimientoCedula = vencimientoCedula;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public Date getVencimientoComprobante() {
        return vencimientoComprobante;
    }

    public void setVencimientoComprobante(Date vencimientoComprobante) {
        this.vencimientoComprobante = vencimientoComprobante;
    }

    public String getPolizaRC() {
        return polizaRC;
    }

    public void setPolizaRC(String polizaRC) {
        this.polizaRC = polizaRC;
    }

    public Date getVencimientoPoliza() {
        return vencimientoPoliza;
    }

    public void setVencimientoPoliza(Date vencimientoPoliza) {
        this.vencimientoPoliza = vencimientoPoliza;
    }

    @Override
    public String toString() {
        return "AgenteLegalResponse{" +
                "agenteId=" + agenteId +
                ", rfc='" + rfc + '\'' +
                ", contrato=" + contrato +
                ", clave='" + clave + '\'' +
                ", estatusVentas=" + estatusVentas +
                ", estatusLC=" + estatusLC +
                ", nombre='" + nombre + '\'' +
                ", ejecutivo=" + ejecutivo +
                ", oficina=" + oficina +
                ", fechaRegistro=" + fechaRegistro +
                ", fechaSeguimiento=" + fechaSeguimiento +
                ", fechaAlta=" + fechaAlta +
                ", tipoCedula=" + tipoCedula +
                ", vencimientoCedula=" + vencimientoCedula +
                ", comprobante='" + comprobante + '\'' +
                ", vencimientoComprobante=" + vencimientoComprobante +
                ", polizaRC='" + polizaRC + '\'' +
                ", vencimientoPoliza=" + vencimientoPoliza +
                '}';
    }
}
