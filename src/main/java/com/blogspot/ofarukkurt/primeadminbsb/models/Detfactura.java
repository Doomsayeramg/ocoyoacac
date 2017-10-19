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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevintron
 */
@Entity
@Table(name = "detfactura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detfactura.findAll", query = "SELECT d FROM Detfactura d")
    , @NamedQuery(name = "Detfactura.findByIdDetalle", query = "SELECT d FROM Detfactura d WHERE d.idDetalle = :idDetalle")
    , @NamedQuery(name = "Detfactura.findByIdDetServicio", query = "SELECT d FROM Detfactura d WHERE d.idDetServicio = :idDetServicio")
    , @NamedQuery(name = "Detfactura.findByMontoNeto", query = "SELECT d FROM Detfactura d WHERE d.montoNeto = :montoNeto")
    , @NamedQuery(name = "Detfactura.findByNatDetalle", query = "SELECT d FROM Detfactura d WHERE d.natDetalle = :natDetalle")})
public class Detfactura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idDetalle")
    private double idDetalle;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idDetServicio")
    private Double idDetServicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "montoNeto")
    private float montoNeto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "natDetalle")
    private Character natDetalle;
    @JoinColumn(name = "idDetServicio", referencedColumnName = "idDetalle", insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Configservicio configservicio;
    @JoinColumn(name = "idFactura", referencedColumnName = "idFactura")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura idFactura;

    public Detfactura() {
    }

    public Detfactura(Double idDetServicio) {
        this.idDetServicio = idDetServicio;
    }

    public Detfactura(Double idDetServicio, double idDetalle, float montoNeto, Character natDetalle) {
        this.idDetServicio = idDetServicio;
        this.idDetalle = idDetalle;
        this.montoNeto = montoNeto;
        this.natDetalle = natDetalle;
    }

    public double getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(double idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Double getIdDetServicio() {
        return idDetServicio;
    }

    public void setIdDetServicio(Double idDetServicio) {
        this.idDetServicio = idDetServicio;
    }

    public float getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(float montoNeto) {
        this.montoNeto = montoNeto;
    }

    public Character getNatDetalle() {
        return natDetalle;
    }

    public void setNatDetalle(Character natDetalle) {
        this.natDetalle = natDetalle;
    }

    public Configservicio getConfigservicio() {
        return configservicio;
    }

    public void setConfigservicio(Configservicio configservicio) {
        this.configservicio = configservicio;
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
        hash += (idDetServicio != null ? idDetServicio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detfactura)) {
            return false;
        }
        Detfactura other = (Detfactura) object;
        if ((this.idDetServicio == null && other.idDetServicio != null) || (this.idDetServicio != null && !this.idDetServicio.equals(other.idDetServicio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.blogspot.ofarukkurt.primeadminbsb.models.Detfactura[ idDetServicio=" + idDetServicio + " ]";
    }
    
}
