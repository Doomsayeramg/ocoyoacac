package com.blogspot.ofarukkurt.primeadminbsb.services;

import com.blogspot.ofarukkurt.primeadminbsb.controllers.util.JsfUtil;
import com.blogspot.ofarukkurt.primeadminbsb.models.Contribuyente;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Faruk
 */
@Stateless
public class ContribuyenteFacade extends AbstractFacade<Contribuyente> {

    @PersistenceContext(unitName = "ADMINBSB_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContribuyenteFacade() {
        super(Contribuyente.class);
    }
    
    public List<Contribuyente> getTopContribuyenteList() {
        return getEntityManager().createNamedQuery("Contribuyente.findByContribuyenteType").getResultList();
    }

    public List<Contribuyente> searchContribuyenteList(String text) {
        try {
            return getEntityManager().createNamedQuery("Contribuyente.findByContribuyenteName").setParameter("menuName", "%"+text+"%").getResultList();
        } catch (NoResultException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/messages").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public Contribuyente getTopContribuyente(Integer topContribuyenteId) {
         try {
            return (Contribuyente) getEntityManager().createNamedQuery("Contribuyente.findByContribuyenteId").setParameter("menuId", topContribuyenteId).getSingleResult();
        } catch (NoResultException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/messages").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    @Override
    public List<Contribuyente> findAll() {
        return getEntityManager().createNamedQuery("Contribuyente.findAll").getResultList();
    }
}
