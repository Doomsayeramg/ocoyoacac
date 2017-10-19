/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogspot.ofarukkurt.primeadminbsb.controllers;

import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.IllegalOrphanException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.NonexistentEntityException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.RollbackFailureException;
import com.blogspot.ofarukkurt.primeadminbsb.models.Contribuyente;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.blogspot.ofarukkurt.primeadminbsb.models.Pago;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author kevintron
 */
public class ContribuyenteJpaController implements Serializable {

    public ContribuyenteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Contribuyente contribuyente) throws RollbackFailureException, Exception {
        if (contribuyente.getPagoCollection() == null) {
            contribuyente.setPagoCollection(new ArrayList<Pago>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Pago> attachedPagoCollection = new ArrayList<Pago>();
            for (Pago pagoCollectionPagoToAttach : contribuyente.getPagoCollection()) {
                pagoCollectionPagoToAttach = em.getReference(pagoCollectionPagoToAttach.getClass(), pagoCollectionPagoToAttach.getIdPago());
                attachedPagoCollection.add(pagoCollectionPagoToAttach);
            }
            contribuyente.setPagoCollection(attachedPagoCollection);
            em.persist(contribuyente);
            for (Pago pagoCollectionPago : contribuyente.getPagoCollection()) {
                Contribuyente oldIdContribuyenteOfPagoCollectionPago = pagoCollectionPago.getIdContribuyente();
                pagoCollectionPago.setIdContribuyente(contribuyente);
                pagoCollectionPago = em.merge(pagoCollectionPago);
                if (oldIdContribuyenteOfPagoCollectionPago != null) {
                    oldIdContribuyenteOfPagoCollectionPago.getPagoCollection().remove(pagoCollectionPago);
                    oldIdContribuyenteOfPagoCollectionPago = em.merge(oldIdContribuyenteOfPagoCollectionPago);
                }
            }
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

    public void edit(Contribuyente contribuyente) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Contribuyente persistentContribuyente = em.find(Contribuyente.class, contribuyente.getIdContribuyente());
            Collection<Pago> pagoCollectionOld = persistentContribuyente.getPagoCollection();
            Collection<Pago> pagoCollectionNew = contribuyente.getPagoCollection();
            List<String> illegalOrphanMessages = null;
            for (Pago pagoCollectionOldPago : pagoCollectionOld) {
                if (!pagoCollectionNew.contains(pagoCollectionOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagoCollectionOldPago + " since its idContribuyente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Pago> attachedPagoCollectionNew = new ArrayList<Pago>();
            for (Pago pagoCollectionNewPagoToAttach : pagoCollectionNew) {
                pagoCollectionNewPagoToAttach = em.getReference(pagoCollectionNewPagoToAttach.getClass(), pagoCollectionNewPagoToAttach.getIdPago());
                attachedPagoCollectionNew.add(pagoCollectionNewPagoToAttach);
            }
            pagoCollectionNew = attachedPagoCollectionNew;
            contribuyente.setPagoCollection(pagoCollectionNew);
            contribuyente = em.merge(contribuyente);
            for (Pago pagoCollectionNewPago : pagoCollectionNew) {
                if (!pagoCollectionOld.contains(pagoCollectionNewPago)) {
                    Contribuyente oldIdContribuyenteOfPagoCollectionNewPago = pagoCollectionNewPago.getIdContribuyente();
                    pagoCollectionNewPago.setIdContribuyente(contribuyente);
                    pagoCollectionNewPago = em.merge(pagoCollectionNewPago);
                    if (oldIdContribuyenteOfPagoCollectionNewPago != null && !oldIdContribuyenteOfPagoCollectionNewPago.equals(contribuyente)) {
                        oldIdContribuyenteOfPagoCollectionNewPago.getPagoCollection().remove(pagoCollectionNewPago);
                        oldIdContribuyenteOfPagoCollectionNewPago = em.merge(oldIdContribuyenteOfPagoCollectionNewPago);
                    }
                }
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
                Integer id = contribuyente.getIdContribuyente();
                if (findContribuyente(id) == null) {
                    throw new NonexistentEntityException("The contribuyente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Contribuyente contribuyente;
            try {
                contribuyente = em.getReference(Contribuyente.class, id);
                contribuyente.getIdContribuyente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The contribuyente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Pago> pagoCollectionOrphanCheck = contribuyente.getPagoCollection();
            for (Pago pagoCollectionOrphanCheckPago : pagoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Contribuyente (" + contribuyente + ") cannot be destroyed since the Pago " + pagoCollectionOrphanCheckPago + " in its pagoCollection field has a non-nullable idContribuyente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(contribuyente);
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

    public List<Contribuyente> findContribuyenteEntities() {
        return findContribuyenteEntities(true, -1, -1);
    }

    public List<Contribuyente> findContribuyenteEntities(int maxResults, int firstResult) {
        return findContribuyenteEntities(false, maxResults, firstResult);
    }

    private List<Contribuyente> findContribuyenteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Contribuyente.class));
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

    public Contribuyente findContribuyente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Contribuyente.class, id);
        } finally {
            em.close();
        }
    }

    public int getContribuyenteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Contribuyente> rt = cq.from(Contribuyente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
