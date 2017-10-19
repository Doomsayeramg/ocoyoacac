/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogspot.ofarukkurt.primeadminbsb.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevintron
 */
@Entity
@Table(name = "configservicio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Configservicio.findAll", query = "SELECT c FROM Configservicio c")
    , @NamedQuery(name = "Configservicio.findByIdDetalle", query = "SELECT c FROM Configservicio c WHERE c.idDetalle = :idDetalle")
    , @NamedQuery(name = "Configservicio.findByFactor", query = "SELECT c FROM Configservicio c WHERE c.factor = :factor")
    , @NamedQuery(name = "Configservicio.findByFijo", query = "SELECT c FROM Configservicio c WHERE c.fijo = :fijo")})
public class Configservicio implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idDetalle")
    private Double idDetalle;
    @Lob
    @Size(max = 65535)
    @Column(name = "descDetalle")
    private String descDetalle;
    @Column(name = "factor")
    private Float factor;
    @Column(name = "fijo")
    private Character fijo;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "configservicio", fetch = FetchType.LAZY)
    private Detfactura detfactura;
    @JoinColumn(name = "idServicio", referencedColumnName = "idServicio")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Prodservicio idServicio;

    public Configservicio() {
    }

    public Configservicio(Double idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Double getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Double idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getDescDetalle() {
        return descDetalle;
    }

    public void setDescDetalle(String descDetalle) {
        this.descDetalle = descDetalle;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

    public Character getFijo() {
        return fijo;
    }

    public void setFijo(Character fijo) {
        this.fijo = fijo;
    }

    public Detfactura getDetfactura() {
        return detfactura;
    }

    public void setDetfactura(Detfactura detfactura) {
        this.detfactura = detfactura;
    }

    public Prodservicio getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Prodservicio idServicio) {
        this.idServicio = idServicio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetalle != null ? idDetalle.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Configservicio)) {
            return false;
        }
        Configservicio other = (Configservicio) object;
        if ((this.idDetalle == null && other.idDetalle != null) || (this.idDetalle != null && !this.idDetalle.equals(other.idDetalle))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.blogspot.ofarukkurt.primeadminbsb.models.Configservicio[ idDetalle=" + idDetalle + " ]";
    }
    
}
