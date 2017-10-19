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
import com.blogspot.ofarukkurt.primeadminbsb.models.Configservicio;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.blogspot.ofarukkurt.primeadminbsb.models.Detfactura;
import com.blogspot.ofarukkurt.primeadminbsb.models.Prodservicio;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author kevintron
 */
public class ConfigservicioJpaController implements Serializable {

    public ConfigservicioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Configservicio configservicio) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detfactura detfactura = configservicio.getDetfactura();
            if (detfactura != null) {
                detfactura = em.getReference(detfactura.getClass(), detfactura.getIdDetServicio());
                configservicio.setDetfactura(detfactura);
            }
            Prodservicio idServicio = configservicio.getIdServicio();
            if (idServicio != null) {
                idServicio = em.getReference(idServicio.getClass(), idServicio.getIdServicio());
                configservicio.setIdServicio(idServicio);
            }
            em.persist(configservicio);
            if (detfactura != null) {
                Configservicio oldConfigservicioOfDetfactura = detfactura.getConfigservicio();
                if (oldConfigservicioOfDetfactura != null) {
                    oldConfigservicioOfDetfactura.setDetfactura(null);
                    oldConfigservicioOfDetfactura = em.merge(oldConfigservicioOfDetfactura);
                }
                detfactura.setConfigservicio(configservicio);
                detfactura = em.merge(detfactura);
            }
            if (idServicio != null) {
                idServicio.getConfigservicioCollection().add(configservicio);
                idServicio = em.merge(idServicio);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findConfigservicio(configservicio.getIdDetalle()) != null) {
                throw new PreexistingEntityException("Configservicio " + configservicio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Configservicio configservicio) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Configservicio persistentConfigservicio = em.find(Configservicio.class, configservicio.getIdDetalle());
            Detfactura detfacturaOld = persistentConfigservicio.getDetfactura();
            Detfactura detfacturaNew = configservicio.getDetfactura();
            Prodservicio idServicioOld = persistentConfigservicio.getIdServicio();
            Prodservicio idServicioNew = configservicio.getIdServicio();
            List<String> illegalOrphanMessages = null;
            if (detfacturaOld != null && !detfacturaOld.equals(detfacturaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Detfactura " + detfacturaOld + " since its configservicio field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (detfacturaNew != null) {
                detfacturaNew = em.getReference(detfacturaNew.getClass(), detfacturaNew.getIdDetServicio());
                configservicio.setDetfactura(detfacturaNew);
            }
            if (idServicioNew != null) {
                idServicioNew = em.getReference(idServicioNew.getClass(), idServicioNew.getIdServicio());
                configservicio.setIdServicio(idServicioNew);
            }
            configservicio = em.merge(configservicio);
            if (detfacturaNew != null && !detfacturaNew.equals(detfacturaOld)) {
                Configservicio oldConfigservicioOfDetfactura = detfacturaNew.getConfigservicio();
                if (oldConfigservicioOfDetfactura != null) {
                    oldConfigservicioOfDetfactura.setDetfactura(null);
                    oldConfigservicioOfDetfactura = em.merge(oldConfigservicioOfDetfactura);
                }
                detfacturaNew.setConfigservicio(configservicio);
                detfacturaNew = em.merge(detfacturaNew);
            }
            if (idServicioOld != null && !idServicioOld.equals(idServicioNew)) {
                idServicioOld.getConfigservicioCollection().remove(configservicio);
                idServicioOld = em.merge(idServicioOld);
            }
            if (idServicioNew != null && !idServicioNew.equals(idServicioOld)) {
                idServicioNew.getConfigservicioCollection().add(configservicio);
                idServicioNew = em.merge(idServicioNew);
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
                Double id = configservicio.getIdDetalle();
                if (findConfigservicio(id) == null) {
                    throw new NonexistentEntityException("The configservicio with id " + id + " no longer exists.");
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
            Configservicio configservicio;
            try {
                configservicio = em.getReference(Configservicio.class, id);
                configservicio.getIdDetalle();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The configservicio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Detfactura detfacturaOrphanCheck = configservicio.getDetfactura();
            if (detfacturaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Configservicio (" + configservicio + ") cannot be destroyed since the Detfactura " + detfacturaOrphanCheck + " in its detfactura field has a non-nullable configservicio field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Prodservicio idServicio = configservicio.getIdServicio();
            if (idServicio != null) {
                idServicio.getConfigservicioCollection().remove(configservicio);
                idServicio = em.merge(idServicio);
            }
            em.remove(configservicio);
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

    public List<Configservicio> findConfigservicioEntities() {
        return findConfigservicioEntities(true, -1, -1);
    }

    public List<Configservicio> findConfigservicioEntities(int maxResults, int firstResult) {
        return findConfigservicioEntities(false, maxResults, firstResult);
    }

    private List<Configservicio> findConfigservicioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Configservicio.class));
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

    public Configservicio findConfigservicio(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Configservicio.class, id);
        } finally {
            em.close();
        }
    }

    public int getConfigservicioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Configservicio> rt = cq.from(Configservicio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
