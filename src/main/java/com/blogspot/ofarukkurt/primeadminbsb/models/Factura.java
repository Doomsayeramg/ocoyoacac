/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogspot.ofarukkurt.primeadminbsb.models;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kevintron
 */
@Entity
@Table(name = "factura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f")
    , @NamedQuery(name = "Factura.findByIdFactura", query = "SELECT f FROM Factura f WHERE f.idFactura = :idFactura")
    , @NamedQuery(name = "Factura.findByIdContribuyente", query = "SELECT f FROM Factura f WHERE f.idContribuyente = :idContribuyente")
    , @NamedQuery(name = "Factura.findByMontoCobro", query = "SELECT f FROM Factura f WHERE f.montoCobro = :montoCobro")
    , @NamedQuery(name = "Factura.findByStatus", query = "SELECT f FROM Factura f WHERE f.status = :status")})
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idFactura")
    private Double idFactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idContribuyente")
    private double idContribuyente;
    @Basic(optional = false)
    @NotNull
    @Column(name = "montoCobro")
    private float montoCobro;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private Character status;
    @JoinColumn(name = "idServicio", referencedColumnName = "idServicio")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Prodservicio idServicio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idFactura", fetch = FetchType.LAZY)
    private Collection<Detfactura> detfacturaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idFactura", fetch = FetchType.LAZY)
    private Collection<Pago> pagoCollection;

    public Factura() {
    }

    public Factura(Double idFactura) {
        this.idFactura = idFactura;
    }

    public Factura(Double idFactura, double idContribuyente, float montoCobro, Character status) {
        this.idFactura = idFactura;
        this.idContribuyente = idContribuyente;
        this.montoCobro = montoCobro;
        this.status = status;
    }

    public Double getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Double idFactura) {
        this.idFactura = idFactura;
    }

    public double getIdContribuyente() {
        return idContribuyente;
    }

    public void setIdContribuyente(double idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public float getMontoCobro() {
        return montoCobro;
    }

    public void setMontoCobro(float montoCobro) {
        this.montoCobro = montoCobro;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Prodservicio getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Prodservicio idServicio) {
        this.idServicio = idServicio;
    }

    @XmlTransient
    public Collection<Detfactura> getDetfacturaCollection() {
        return detfacturaCollection;
    }

    public void setDetfacturaCollection(Collection<Detfactura> detfacturaCollection) {
        this.detfacturaCollection = detfacturaCollection;
    }

    @XmlTransient
    public Collection<Pago> getPagoCollection() {
        return pagoCollection;
    }

    public void setPagoCollection(Collection<Pago> pagoCollection) {
        this.pagoCollection = pagoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFactura != null ? idFactura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Factura)) {
            return false;
        }
        Factura other = (Factura) object;
        if ((this.idFactura == null && other.idFactura != null) || (this.idFactura != null && !this.idFactura.equals(other.idFactura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.blogspot.ofarukkurt.primeadminbsb.models.Factura[ idFactura=" + idFactura + " ]";
    }
    
}
