/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blogspot.ofarukkurt.primeadminbsb.controllers;

import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.IllegalOrphanException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.NonexistentEntityException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.PreexistingEntityException;
import com.blogspot.ofarukkurt.primeadminbsb.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.blogspot.ofarukkurt.primeadminbsb.models.Factura;
import java.util.ArrayList;
import java.util.Collection;
import com.blogspot.ofarukkurt.primeadminbsb.models.Configservicio;
import com.blogspot.ofarukkurt.primeadminbsb.models.Prodservicio;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author kevintron
 */
public class ProdservicioJpaController implements Serializable {

    public ProdservicioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prodservicio prodservicio) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (prodservicio.getFacturaCollection() == null) {
            prodservicio.setFacturaCollection(new ArrayList<Factura>());
        }
        if (prodservicio.getConfigservicioCollection() == null) {
            prodservicio.setConfigservicioCollection(new ArrayList<Configservicio>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Factura> attachedFacturaCollection = new ArrayList<Factura>();
            for (Factura facturaCollectionFacturaToAttach : prodservicio.getFacturaCollection()) {
                facturaCollectionFacturaToAttach = em.getReference(facturaCollectionFacturaToAttach.getClass(), facturaCollectionFacturaToAttach.getIdFactura());
                attachedFacturaCollection.add(facturaCollectionFacturaToAttach);
            }
            prodservicio.setFacturaCollection(attachedFacturaCollection);
            Collection<Configservicio> attachedConfigservicioCollection = new ArrayList<Configservicio>();
            for (Configservicio configservicioCollectionConfigservicioToAttach : prodservicio.getConfigservicioCollection()) {
                configservicioCollectionConfigservicioToAttach = em.getReference(configservicioCollectionConfigservicioToAttach.getClass(), configservicioCollectionConfigservicioToAttach.getIdDetalle());
                attachedConfigservicioCollection.add(configservicioCollectionConfigservicioToAttach);
            }
            prodservicio.setConfigservicioCollection(attachedConfigservicioCollection);
            em.persist(prodservicio);
            for (Factura facturaCollectionFactura : prodservicio.getFacturaCollection()) {
                Prodservicio oldIdServicioOfFacturaCollectionFactura = facturaCollectionFactura.getIdServicio();
                facturaCollectionFactura.setIdServicio(prodservicio);
                facturaCollectionFactura = em.merge(facturaCollectionFactura);
                if (oldIdServicioOfFacturaCollectionFactura != null) {
                    oldIdServicioOfFacturaCollectionFactura.getFacturaCollection().remove(facturaCollectionFactura);
                    oldIdServicioOfFacturaCollectionFactura = em.merge(oldIdServicioOfFacturaCollectionFactura);
                }
            }
            for (Configservicio configservicioCollectionConfigservicio : prodservicio.getConfigservicioCollection()) {
                Prodservicio oldIdServicioOfConfigservicioCollectionConfigservicio = configservicioCollectionConfigservicio.getIdServicio();
                configservicioCollectionConfigservicio.setIdServicio(prodservicio);
                configservicioCollectionConfigservicio = em.merge(configservicioCollectionConfigservicio);
                if (oldIdServicioOfConfigservicioCollectionConfigservicio != null) {
                    oldIdServicioOfConfigservicioCollectionConfigservicio.getConfigservicioCollection().remove(configservicioCollectionConfigservicio);
                    oldIdServicioOfConfigservicioCollectionConfigservicio = em.merge(oldIdServicioOfConfigservicioCollectionConfigservicio);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findProdservicio(prodservicio.getIdServicio()) != null) {
                throw new PreexistingEntityException("Prodservicio " + prodservicio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prodservicio prodservicio) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Prodservicio persistentProdservicio = em.find(Prodservicio.class, prodservicio.getIdServicio());
            Collection<Factura> facturaCollectionOld = persistentProdservicio.getFacturaCollection();
            Collection<Factura> facturaCollectionNew = prodservicio.getFacturaCollection();
            Collection<Configservicio> configservicioCollectionOld = persistentProdservicio.getConfigservicioCollection();
            Collection<Configservicio> configservicioCollectionNew = prodservicio.getConfigservicioCollection();
            List<String> illegalOrphanMessages = null;
            for (Factura facturaCollectionOldFactura : facturaCollectionOld) {
                if (!facturaCollectionNew.contains(facturaCollectionOldFactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Factura " + facturaCollectionOldFactura + " since its idServicio field is not nullable.");
                }
            }
            for (Configservicio configservicioCollectionOldConfigservicio : configservicioCollectionOld) {
                if (!configservicioCollectionNew.contains(configservicioCollectionOldConfigservicio)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Configservicio " + configservicioCollectionOldConfigservicio + " since its idServicio field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Factura> attachedFacturaCollectionNew = new ArrayList<Factura>();
            for (Factura facturaCollectionNewFacturaToAttach : facturaCollectionNew) {
                facturaCollectionNewFacturaToAttach = em.getReference(facturaCollectionNewFacturaToAttach.getClass(), facturaCollectionNewFacturaToAttach.getIdFactura());
                attachedFacturaCollectionNew.add(facturaCollectionNewFacturaToAttach);
            }
            facturaCollectionNew = attachedFacturaCollectionNew;
            prodservicio.setFacturaCollection(facturaCollectionNew);
            Collection<Configservicio> attachedConfigservicioCollectionNew = new ArrayList<Configservicio>();
            for (Configservicio configservicioCollectionNewConfigservicioToAttach : configservicioCollectionNew) {
                configservicioCollectionNewConfigservicioToAttach = em.getReference(configservicioCollectionNewConfigservicioToAttach.getClass(), configservicioCollectionNewConfigservicioToAttach.getIdDetalle());
                attachedConfigservicioCollectionNew.add(configservicioCollectionNewConfigservicioToAttach);
            }
            configservicioCollectionNew = attachedConfigservicioCollectionNew;
            prodservicio.setConfigservicioCollection(configservicioCollectionNew);
            prodservicio = em.merge(prodservicio);
            for (Factura facturaCollectionNewFactura : facturaCollectionNew) {
                if (!facturaCollectionOld.contains(facturaCollectionNewFactura)) {
                    Prodservicio oldIdServicioOfFacturaCollectionNewFactura = facturaCollectionNewFactura.getIdServicio();
                    facturaCollectionNewFactura.setIdServicio(prodservicio);
                    facturaCollectionNewFactura = em.merge(facturaCollectionNewFactura);
                    if (oldIdServicioOfFacturaCollectionNewFactura != null && !oldIdServicioOfFacturaCollectionNewFactura.equals(prodservicio)) {
                        oldIdServicioOfFacturaCollectionNewFactura.getFacturaCollection().remove(facturaCollectionNewFactura);
                        oldIdServicioOfFacturaCollectionNewFactura = em.merge(oldIdServicioOfFacturaCollectionNewFactura);
                    }
                }
            }
            for (Configservicio configservicioCollectionNewConfigservicio : configservicioCollectionNew) {
                if (!configservicioCollectionOld.contains(configservicioCollectionNewConfigservicio)) {
                    Prodservicio oldIdServicioOfConfigservicioCollectionNewConfigservicio = configservicioCollectionNewConfigservicio.getIdServicio();
                    configservicioCollectionNewConfigservicio.setIdServicio(prodservicio);
                    configservicioCollectionNewConfigservicio = em.merge(configservicioCollectionNewConfigservicio);
                    if (oldIdServicioOfConfigservicioCollectionNewConfigservicio != null && !oldIdServicioOfConfigservicioCollectionNewConfigservicio.equals(prodservicio)) {
                        oldIdServicioOfConfigservicioCollectionNewConfigservicio.getConfigservicioCollection().remove(configservicioCollectionNewConfigservicio);
                        oldIdServicioOfConfigservicioCollectionNewConfigservicio = em.merge(oldIdServicioOfConfigservicioCollectionNewConfigservicio);
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
                Double id = prodservicio.getIdServicio();
                if (findProdservicio(id) == null) {
                    throw new NonexistentEntityException("The prodservicio with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Double id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Prodservicio prodservicio;
            try {
                prodservicio = em.getReference(Prodservicio.class, id);
                prodservicio.getIdServicio();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prodservicio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Factura> facturaCollectionOrphanCheck = prodservicio.getFacturaCollection();
            for (Factura facturaCollectionOrphanCheckFactura : facturaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prodservicio (" + prodservicio + ") cannot be destroyed since the Factura " + facturaCollectionOrphanCheckFactura + " in its facturaCollection field has a non-nullable idServicio field.");
            }
            Collection<Configservicio> configservicioCollectionOrphanCheck = prodservicio.getConfigservicioCollection();
            for (Configservicio configservicioCollectionOrphanCheckConfigservicio : configservicioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prodservicio (" + prodservicio + ") cannot be destroyed since the Configservicio " + configservicioCollectionOrphanCheckConfigservicio + " in its configservicioCollection field has a non-nullable idServicio field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(prodservicio);
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

    public List<Prodservicio> findProdservicioEntities() {
        return findProdservicioEntities(true, -1, -1);
    }

    public List<Prodservicio> findProdservicioEntities(int maxResults, int firstResult) {
        return findProdservicioEntities(false, maxResults, firstResult);
    }

    private List<Prodservicio> findProdservicioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prodservicio.class));
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

    public Prodservicio findProdservicio(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prodservicio.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdservicioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prodservicio> rt = cq.from(Prodservicio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
