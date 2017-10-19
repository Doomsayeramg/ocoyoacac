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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "contribuyente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Contribuyente.findAll", query = "SELECT c FROM Contribuyente c")
    , @NamedQuery(name = "Contribuyente.findByIdContribuyente", query = "SELECT c FROM Contribuyente c WHERE c.idContribuyente = :idContribuyente")
    , @NamedQuery(name = "Contribuyente.findByNombre", query = "SELECT c FROM Contribuyente c WHERE c.nombre = :nombre")
    , @NamedQuery(name = "Contribuyente.findByApellidopat", query = "SELECT c FROM Contribuyente c WHERE c.apellidopat = :apellidopat")
    , @NamedQuery(name = "Contribuyente.findByApellidomat", query = "SELECT c FROM Contribuyente c WHERE c.apellidomat = :apellidomat")
    , @NamedQuery(name = "Contribuyente.findByRfc", query = "SELECT c FROM Contribuyente c WHERE c.rfc = :rfc")
    , @NamedQuery(name = "Contribuyente.findByCurp", query = "SELECT c FROM Contribuyente c WHERE c.curp = :curp")
    , @NamedQuery(name = "Contribuyente.findByCalle", query = "SELECT c FROM Contribuyente c WHERE c.calle = :calle")
    , @NamedQuery(name = "Contribuyente.findByNumero", query = "SELECT c FROM Contribuyente c WHERE c.numero = :numero")
    , @NamedQuery(name = "Contribuyente.findByCp", query = "SELECT c FROM Contribuyente c WHERE c.cp = :cp")
    , @NamedQuery(name = "Contribuyente.findByEmail", query = "SELECT c FROM Contribuyente c WHERE c.email = :email")
    , @NamedQuery(name = "Contribuyente.findByTelefono", query = "SELECT c FROM Contribuyente c WHERE c.telefono = :telefono")})
public class Contribuyente implements Serializable {

   
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idContribuyente", fetch = FetchType.LAZY)
    private Collection<Pago> pagoCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idContribuyente")
    private Integer idContribuyente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "apellidopat")
    private String apellidopat;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "apellidomat")
    private String apellidomat;
    @Size(max = 20)
    @Column(name = "RFC")
    private String rfc;
    @Size(max = 45)
    @Column(name = "CURP")
    private String curp;
    @Size(max = 45)
    @Column(name = "calle")
    private String calle;
    @Column(name = "Numero")
    private Integer numero;
    @Size(max = 6)
    @Column(name = "CP")
    private String cp;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 450)
    @Column(name = "email")
    private String email;
    @Size(max = 10)
    @Column(name = "telefono")
    private String telefono;
    @Size(max = 10)
    @Column(name = "telefonomovil")
    private String telefonomovil;

    public Contribuyente() {
    }

    public Contribuyente(Integer idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public Contribuyente(Integer idContribuyente, String nombre, String apellidopat, String apellidomat) {
        this.idContribuyente = idContribuyente;
        this.nombre = nombre;
        this.apellidopat = apellidopat;
        this.apellidomat = apellidomat;
    }

    public Integer getIdContribuyente() {
        return idContribuyente;
    }

    public void setIdContribuyente(Integer idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidopat() {
        return apellidopat;
    }

    public void setApellidopat(String apellidopat) {
        this.apellidopat = apellidopat;
    }

    public String getApellidomat() {
        return apellidomat;
    }

    public void setApellidomat(String apellidomat) {
        this.apellidomat = apellidomat;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idContribuyente != null ? idContribuyente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contribuyente)) {
            return false;
        }
        Contribuyente other = (Contribuyente) object;
        return !((this.idContribuyente == null && other.idContribuyente != null) || (this.idContribuyente != null && !this.idContribuyente.equals(other.idContribuyente)));
    }

    @Override
    public String toString() {
        return "com.blogspot.ofarukkurt.primeadminbsb.models.Contribuyente[ idContribuyente=" + idContribuyente + " ]";
    }

    public String getTelefonomovil() {
        return telefonomovil;
    }

    public void setTelefonomovil(String telefonomovil) {
        this.telefonomovil = telefonomovil;
    }

    @XmlTransient
    public Collection<Pago> getPagoCollection() {
        return pagoCollection;
    }

    public void setPagoCollection(Collection<Pago> pagoCollection) {
        this.pagoCollection = pagoCollection;
    }
    
}
