/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogspot.ofarukkurt.primeadminbsb.controllers;

import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.NonexistentEntityException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.PreexistingEntityException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.blogspot.ofarukkurt.primeadminbsb.models.Contribuyente;
import com.blogspot.ofarukkurt.primeadminbsb.models.Factura;
import com.blogspot.ofarukkurt.primeadminbsb.models.Pago;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author kevintron
 */
public class PagoJpaController implements Serializable {

    public PagoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pago pago) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Contribuyente idContribuyente = pago.getIdContribuyente();
            if (idContribuyente != null) {
                idContribuyente = em.getReference(idContribuyente.getClass(), idContribuyente.getIdContribuyente());
                pago.setIdContribuyente(idContribuyente);
            }
            Factura idFactura = pago.getIdFactura();
            if (idFactura != null) {
                idFactura = em.getReference(idFactura.getClass(), idFactura.getIdFactura());
                pago.setIdFactura(idFactura);
            }
            em.persist(pago);
            if (idContribuyente != null) {
                idContribuyente.getPagoCollection().add(pago);
                idContribuyente = em.merge(idContribuyente);
            }
            if (idFactura != null) {
                idFactura.getPagoCollection().add(pago);
                idFactura = em.merge(idFactura);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPago(pago.getIdPago()) != null) {
                throw new PreexistingEntityException("Pago " + pago + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pago pago) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pago persistentPago = em.find(Pago.class, pago.getIdPago());
            Contribuyente idContribuyenteOld = persistentPago.getIdContribuyente();
            Contribuyente idContribuyenteNew = pago.getIdContribuyente();
            Factura idFacturaOld = persistentPago.getIdFactura();
            Factura idFacturaNew = pago.getIdFactura();
            if (idContribuyenteNew != null) {
                idContribuyenteNew = em.getReference(idContribuyenteNew.getClass(), idContribuyenteNew.getIdContribuyente());
                pago.setIdContribuyente(idContribuyenteNew);
            }
            if (idFacturaNew != null) {
                idFacturaNew = em.getReference(idFacturaNew.getClass(), idFacturaNew.getIdFactura());
                pago.setIdFactura(idFacturaNew);
            }
            pago = em.merge(pago);
            if (idContribuyenteOld != null && !idContribuyenteOld.equals(idContribuyenteNew)) {
                idContribuyenteOld.getPagoCollection().remove(pago);
                idContribuyenteOld = em.merge(idContribuyenteOld);
            }
            if (idContribuyenteNew != null && !idContribuyenteNew.equals(idContribuyenteOld)) {
                idContribuyenteNew.getPagoCollection().add(pago);
                idContribuyenteNew = em.merge(idContribuyenteNew);
            }
            if (idFacturaOld != null && !idFacturaOld.equals(idFacturaNew)) {
                idFacturaOld.getPagoCollection().remove(pago);
                idFacturaOld = em.merge(idFacturaOld);
            }
            if (idFacturaNew != null && !idFacturaNew.equals(idFacturaOld)) {
                idFacturaNew.getPagoCollection().add(pago);
                idFacturaNew = em.merge(idFacturaNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Double id = pago.getIdPago();
                if (findPago(id) == null) {
                    throw new NonexistentEntityException("The pago with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Double id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pago pago;
            try {
                pago = em.getReference(Pago.class, id);
                pago.getIdPago();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pago with id " + id + " no longer exists.", enfe);
            }
            Contribuyente idContribuyente = pago.getIdContribuyente();
            if (idContribuyente != null) {
                idContribuyente.getPagoCollection().remove(pago);
                idContribuyente = em.merge(idContribuyente);
            }
            Factura idFactura = pago.getIdFactura();
            if (idFactura != null) {
                idFactura.getPagoCollection().remove(pago);
                idFactura = em.merge(idFactura);
            }
            em.remove(pago);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pago> findPagoEntities() {
        return findPagoEntities(true, -1, -1);
    }

    public List<Pago> findPagoEntities(int maxResults, int firstResult) {
        return findPagoEntities(false, maxResults, firstResult);
    }

    private List<Pago> findPagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pago.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Pago findPago(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pago.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pago> rt = cq.from(Pago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
