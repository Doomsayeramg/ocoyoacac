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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kevintron
 */
@Entity
@Table(name = "prodservicio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prodservicio.findAll", query = "SELECT p FROM Prodservicio p")
    , @NamedQuery(name = "Prodservicio.findByIdServicio", query = "SELECT p FROM Prodservicio p WHERE p.idServicio = :idServicio")
    , @NamedQuery(name = "Prodservicio.findByStatus", query = "SELECT p FROM Prodservicio p WHERE p.status = :status")})
public class Prodservicio implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idServicio")
    private Double idServicio;
    @Lob
    @Size(max = 65535)
    @Column(name = "descServicio")
    private String descServicio;
    @Lob
    @Size(max = 65535)
    @Column(name = "cuentaIngreso")
    private String cuentaIngreso;
    @Lob
    @Size(max = 65535)
    @Column(name = "cuentaEgreso")
    private String cuentaEgreso;
    @Column(name = "status")
    private Character status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idServicio", fetch = FetchType.LAZY)
    private Collection<Factura> facturaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idServicio", fetch = FetchType.LAZY)
    private Collection<Configservicio> configservicioCollection;

    public Prodservicio() {
    }

    public Prodservicio(Double idServicio) {
        this.idServicio = idServicio;
    }

    public Double getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Double idServicio) {
        this.idServicio = idServicio;
    }

    public String getDescServicio() {
        return descServicio;
    }

    public void setDescServicio(String descServicio) {
        this.descServicio = descServicio;
    }

    public String getCuentaIngreso() {
        return cuentaIngreso;
    }

    public void setCuentaIngreso(String cuentaIngreso) {
        this.cuentaIngreso = cuentaIngreso;
    }

    public String getCuentaEgreso() {
        return cuentaEgreso;
    }

    public void setCuentaEgreso(String cuentaEgreso) {
        this.cuentaEgreso = cuentaEgreso;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    @XmlTransient
    public Collection<Factura> getFacturaCollection() {
        return facturaCollection;
    }

    public void setFacturaCollection(Collection<Factura> facturaCollection) {
        this.facturaCollection = facturaCollection;
    }

    @XmlTransient
    public Collection<Configservicio> getConfigservicioCollection() {
        return configservicioCollection;
    }

    public void setConfigservicioCollection(Collection<Configservicio> configservicioCollection) {
        this.configservicioCollection = configservicioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idServicio != null ? idServicio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prodservicio)) {
            return false;
        }
        Prodservicio other = (Prodservicio) object;
        if ((this.idServicio == null && other.idServicio != null) || (this.idServicio != null && !this.idServicio.equals(other.idServicio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.blogspot.ofarukkurt.primeadminbsb.models.Prodservicio[ idServicio=" + idServicio + " ]";
    }
    
}
