/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogspot.ofarukkurt.primeadminbsb.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevintron
 */
@Entity
@Table(name = "pago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pago.findAll", query = "SELECT p FROM Pago p")
    , @NamedQuery(name = "Pago.findByIdPago", query = "SELECT p FROM Pago p WHERE p.idPago = :idPago")
    , @NamedQuery(name = "Pago.findByMontoCobro", query = "SELECT p FROM Pago p WHERE p.montoCobro = :montoCobro")
    , @NamedQuery(name = "Pago.findByMontoPago", query = "SELECT p FROM Pago p WHERE p.montoPago = :montoPago")
    , @NamedQuery(name = "Pago.findByStatus", query = "SELECT p FROM Pago p WHERE p.status = :status")})
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idPago")
    private Double idPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "montoCobro")
    private float montoCobro;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MontoPago")
    private float montoPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private Character status;
    @JoinColumn(name = "idContribuyente", referencedColumnName = "idContribuyente")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Contribuyente idContribuyente;
    @JoinColumn(name = "idFactura", referencedColumnName = "idFactura")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura idFactura;

    public Pago() {
    }

    public Pago(Double idPago) {
        this.idPago = idPago;
    }

    public Pago(Double idPago, float montoCobro, float montoPago, Character status) {
        this.idPago = idPago;
        this.montoCobro = montoCobro;
        this.montoPago = montoPago;
        this.status = status;
    }

    public Double getIdPago() {
        return idPago;
    }

    public void setIdPago(Double idPago) {
        this.idPago = idPago;
    }

    public float getMontoCobro() {
        return montoCobro;
    }

    public void setMontoCobro(float montoCobro) {
        this.montoCobro = montoCobro;
    }

    public float getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(float montoPago) {
        this.montoPago = montoPago;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Contribuyente getIdContribuyente() {
        return idContribuyente;
    }

    public void setIdContribuyente(Contribuyente idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public Factura getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Factura idFactura) {
        this.idFactura = idFactura;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPago != null ? idPago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pago)) {
            return false;
        }
        Pago other = (Pago) object;
        if ((this.idPago == null && other.idPago != null) || (this.idPago != null && !this.idPago.equals(other.idPago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.blogspot.ofarukkurt.primeadminbsb.models.Pago[ idPago=" + idPago + " ]";
    }
    
}
