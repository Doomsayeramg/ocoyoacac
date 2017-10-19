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
import com.blogspot.ofarukkurt.primeadminbsb.models.Configservicio;
import com.blogspot.ofarukkurt.primeadminbsb.models.Detfactura;
import com.blogspot.ofarukkurt.primeadminbsb.models.Factura;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author kevintron
 */
public class DetfacturaJpaController implements Serializable {

    public DetfacturaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detfactura detfactura) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Configservicio configservicioOrphanCheck = detfactura.getConfigservicio();
        if (configservicioOrphanCheck != null) {
            Detfactura oldDetfacturaOfConfigservicio = configservicioOrphanCheck.getDetfactura();
            if (oldDetfacturaOfConfigservicio != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Configservicio " + configservicioOrphanCheck + " already has an item of type Detfactura whose configservicio column cannot be null. Please make another selection for the configservicio field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Configservicio configservicio = detfactura.getConfigservicio();
            if (configservicio != null) {
                configservicio = em.getReference(configservicio.getClass(), configservicio.getIdDetalle());
                detfactura.setConfigservicio(configservicio);
            }
            Factura idFactura = detfactura.getIdFactura();
            if (idFactura != null) {
                idFactura = em.getReference(idFactura.getClass(), idFactura.getIdFactura());
                detfactura.setIdFactura(idFactura);
            }
            em.persist(detfactura);
            if (configservicio != null) {
                configservicio.setDetfactura(detfactura);
                configservicio = em.merge(configservicio);
            }
            if (idFactura != null) {
                idFactura.getDetfacturaCollection().add(detfactura);
                idFactura = em.merge(idFactura);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDetfactura(detfactura.getIdDetServicio()) != null) {
                throw new PreexistingEntityException("Detfactura " + detfactura + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detfactura detfactura) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detfactura persistentDetfactura = em.find(Detfactura.class, detfactura.getIdDetServicio());
            Configservicio configservicioOld = persistentDetfactura.getConfigservicio();
            Configservicio configservicioNew = detfactura.getConfigservicio();
            Factura idFacturaOld = persistentDetfactura.getIdFactura();
            Factura idFacturaNew = detfactura.getIdFactura();
            List<String> illegalOrphanMessages = null;
            if (configservicioNew != null && !configservicioNew.equals(configservicioOld)) {
                Detfactura oldDetfacturaOfConfigservicio = configservicioNew.getDetfactura();
                if (oldDetfacturaOfConfigservicio != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Configservicio " + configservicioNew + " already has an item of type Detfactura whose configservicio column cannot be null. Please make another selection for the configservicio field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (configservicioNew != null) {
                configservicioNew = em.getReference(configservicioNew.getClass(), configservicioNew.getIdDetalle());
                detfactura.setConfigservicio(configservicioNew);
            }
            if (idFacturaNew != null) {
                idFacturaNew = em.getReference(idFacturaNew.getClass(), idFacturaNew.getIdFactura());
                detfactura.setIdFactura(idFacturaNew);
            }
            detfactura = em.merge(detfactura);
            if (configservicioOld != null && !configservicioOld.equals(configservicioNew)) {
                configservicioOld.setDetfactura(null);
                configservicioOld = em.merge(configservicioOld);
            }
            if (configservicioNew != null && !configservicioNew.equals(configservicioOld)) {
                configservicioNew.setDetfactura(detfactura);
                configservicioNew = em.merge(configservicioNew);
            }
            if (idFacturaOld != null && !idFacturaOld.equals(idFacturaNew)) {
                idFacturaOld.getDetfacturaCollection().remove(detfactura);
                idFacturaOld = em.merge(idFacturaOld);
            }
            if (idFacturaNew != null && !idFacturaNew.equals(idFacturaOld)) {
                idFacturaNew.getDetfacturaCollection().add(detfactura);
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
                Double id = detfactura.getIdDetServicio();
                if (findDetfactura(id) == null) {
                    throw new NonexistentEntityException("The detfactura with id " + id + " no longer exists.");
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
            Detfactura detfactura;
            try {
                detfactura = em.getReference(Detfactura.class, id);
                detfactura.getIdDetServicio();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detfactura with id " + id + " no longer exists.", enfe);
            }
            Configservicio configservicio = detfactura.getConfigservicio();
            if (configservicio != null) {
                configservicio.setDetfactura(null);
                configservicio = em.merge(configservicio);
            }
            Factura idFactura = detfactura.getIdFactura();
            if (idFactura != null) {
                idFactura.getDetfacturaCollection().remove(detfactura);
                idFactura = em.merge(idFactura);
            }
            em.remove(detfactura);
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

    public List<Detfactura> findDetfacturaEntities() {
        return findDetfacturaEntities(true, -1, -1);
    }

    public List<Detfactura> findDetfacturaEntities(int maxResults, int firstResult) {
        return findDetfacturaEntities(false, maxResults, firstResult);
    }

    private List<Detfactura> findDetfacturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detfactura.class));
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

    public Detfactura findDetfactura(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detfactura.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetfacturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detfactura> rt = cq.from(Detfactura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
