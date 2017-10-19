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
import com.blogspot.ofarukkurt.primeadminbsb.models.Prodservicio;
import com.blogspot.ofarukkurt.primeadminbsb.models.Detfactura;
import com.blogspot.ofarukkurt.primeadminbsb.models.Factura;
import java.util.ArrayList;
import java.util.Collection;
import com.blogspot.ofarukkurt.primeadminbsb.models.Pago;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author kevintron
 */
public class FacturaJpaController implements Serializable {

    public FacturaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Factura factura) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (factura.getDetfacturaCollection() == null) {
            factura.setDetfacturaCollection(new ArrayList<Detfactura>());
        }
        if (factura.getPagoCollection() == null) {
            factura.setPagoCollection(new ArrayList<Pago>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Prodservicio idServicio = factura.getIdServicio();
            if (idServicio != null) {
                idServicio = em.getReference(idServicio.getClass(), idServicio.getIdServicio());
                factura.setIdServicio(idServicio);
            }
            Collection<Detfactura> attachedDetfacturaCollection = new ArrayList<Detfactura>();
            for (Detfactura detfacturaCollectionDetfacturaToAttach : factura.getDetfacturaCollection()) {
                detfacturaCollectionDetfacturaToAttach = em.getReference(detfacturaCollectionDetfacturaToAttach.getClass(), detfacturaCollectionDetfacturaToAttach.getIdDetServicio());
                attachedDetfacturaCollection.add(detfacturaCollectionDetfacturaToAttach);
            }
            factura.setDetfacturaCollection(attachedDetfacturaCollection);
            Collection<Pago> attachedPagoCollection = new ArrayList<Pago>();
            for (Pago pagoCollectionPagoToAttach : factura.getPagoCollection()) {
                pagoCollectionPagoToAttach = em.getReference(pagoCollectionPagoToAttach.getClass(), pagoCollectionPagoToAttach.getIdPago());
                attachedPagoCollection.add(pagoCollectionPagoToAttach);
            }
            factura.setPagoCollection(attachedPagoCollection);
            em.persist(factura);
            if (idServicio != null) {
                idServicio.getFacturaCollection().add(factura);
                idServicio = em.merge(idServicio);
            }
            for (Detfactura detfacturaCollectionDetfactura : factura.getDetfacturaCollection()) {
                Factura oldIdFacturaOfDetfacturaCollectionDetfactura = detfacturaCollectionDetfactura.getIdFactura();
                detfacturaCollectionDetfactura.setIdFactura(factura);
                detfacturaCollectionDetfactura = em.merge(detfacturaCollectionDetfactura);
                if (oldIdFacturaOfDetfacturaCollectionDetfactura != null) {
                    oldIdFacturaOfDetfacturaCollectionDetfactura.getDetfacturaCollection().remove(detfacturaCollectionDetfactura);
                    oldIdFacturaOfDetfacturaCollectionDetfactura = em.merge(oldIdFacturaOfDetfacturaCollectionDetfactura);
                }
            }
            for (Pago pagoCollectionPago : factura.getPagoCollection()) {
                Factura oldIdFacturaOfPagoCollectionPago = pagoCollectionPago.getIdFactura();
                pagoCollectionPago.setIdFactura(factura);
                pagoCollectionPago = em.merge(pagoCollectionPago);
                if (oldIdFacturaOfPagoCollectionPago != null) {
                    oldIdFacturaOfPagoCollectionPago.getPagoCollection().remove(pagoCollectionPago);
                    oldIdFacturaOfPagoCollectionPago = em.merge(oldIdFacturaOfPagoCollectionPago);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findFactura(factura.getIdFactura()) != null) {
                throw new PreexistingEntityException("Factura " + factura + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Factura factura) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Factura persistentFactura = em.find(Factura.class, factura.getIdFactura());
            Prodservicio idServicioOld = persistentFactura.getIdServicio();
            Prodservicio idServicioNew = factura.getIdServicio();
            Collection<Detfactura> detfacturaCollectionOld = persistentFactura.getDetfacturaCollection();
            Collection<Detfactura> detfacturaCollectionNew = factura.getDetfacturaCollection();
            Collection<Pago> pagoCollectionOld = persistentFactura.getPagoCollection();
            Collection<Pago> pagoCollectionNew = factura.getPagoCollection();
            List<String> illegalOrphanMessages = null;
            for (Detfactura detfacturaCollectionOldDetfactura : detfacturaCollectionOld) {
                if (!detfacturaCollectionNew.contains(detfacturaCollectionOldDetfactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detfactura " + detfacturaCollectionOldDetfactura + " since its idFactura field is not nullable.");
                }
            }
            for (Pago pagoCollectionOldPago : pagoCollectionOld) {
                if (!pagoCollectionNew.contains(pagoCollectionOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagoCollectionOldPago + " since its idFactura field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idServicioNew != null) {
                idServicioNew = em.getReference(idServicioNew.getClass(), idServicioNew.getIdServicio());
                factura.setIdServicio(idServicioNew);
            }
            Collection<Detfactura> attachedDetfacturaCollectionNew = new ArrayList<Detfactura>();
            for (Detfactura detfacturaCollectionNewDetfacturaToAttach : detfacturaCollectionNew) {
                detfacturaCollectionNewDetfacturaToAttach = em.getReference(detfacturaCollectionNewDetfacturaToAttach.getClass(), detfacturaCollectionNewDetfacturaToAttach.getIdDetServicio());
                attachedDetfacturaCollectionNew.add(detfacturaCollectionNewDetfacturaToAttach);
            }
            detfacturaCollectionNew = attachedDetfacturaCollectionNew;
            factura.setDetfacturaCollection(detfacturaCollectionNew);
            Collection<Pago> attachedPagoCollectionNew = new ArrayList<Pago>();
            for (Pago pagoCollectionNewPagoToAttach : pagoCollectionNew) {
                pagoCollectionNewPagoToAttach = em.getReference(pagoCollectionNewPagoToAttach.getClass(), pagoCollectionNewPagoToAttach.getIdPago());
                attachedPagoCollectionNew.add(pagoCollectionNewPagoToAttach);
            }
            pagoCollectionNew = attachedPagoCollectionNew;
            factura.setPagoCollection(pagoCollectionNew);
            factura = em.merge(factura);
            if (idServicioOld != null && !idServicioOld.equals(idServicioNew)) {
                idServicioOld.getFacturaCollection().remove(factura);
                idServicioOld = em.merge(idServicioOld);
            }
            if (idServicioNew != null && !idServicioNew.equals(idServicioOld)) {
                idServicioNew.getFacturaCollection().add(factura);
                idServicioNew = em.merge(idServicioNew);
            }
            for (Detfactura detfacturaCollectionNewDetfactura : detfacturaCollectionNew) {
                if (!detfacturaCollectionOld.contains(detfacturaCollectionNewDetfactura)) {
                    Factura oldIdFacturaOfDetfacturaCollectionNewDetfactura = detfacturaCollectionNewDetfactura.getIdFactura();
                    detfacturaCollectionNewDetfactura.setIdFactura(factura);
                    detfacturaCollectionNewDetfactura = em.merge(detfacturaCollectionNewDetfactura);
                    if (oldIdFacturaOfDetfacturaCollectionNewDetfactura != null && !oldIdFacturaOfDetfacturaCollectionNewDetfactura.equals(factura)) {
                        oldIdFacturaOfDetfacturaCollectionNewDetfactura.getDetfacturaCollection().remove(detfacturaCollectionNewDetfactura);
                        oldIdFacturaOfDetfacturaCollectionNewDetfactura = em.merge(oldIdFacturaOfDetfacturaCollectionNewDetfactura);
                    }
                }
            }
            for (Pago pagoCollectionNewPago : pagoCollectionNew) {
                if (!pagoCollectionOld.contains(pagoCollectionNewPago)) {
                    Factura oldIdFacturaOfPagoCollectionNewPago = pagoCollectionNewPago.getIdFactura();
                    pagoCollectionNewPago.setIdFactura(factura);
                    pagoCollectionNewPago = em.merge(pagoCollectionNewPago);
                    if (oldIdFacturaOfPagoCollectionNewPago != null && !oldIdFacturaOfPagoCollectionNewPago.equals(factura)) {
                        oldIdFacturaOfPagoCollectionNewPago.getPagoCollection().remove(pagoCollectionNewPago);
                        oldIdFacturaOfPagoCollectionNewPago = em.merge(oldIdFacturaOfPagoCollectionNewPago);
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
                Double id = factura.getIdFactura();
                if (findFactura(id) == null) {
                    throw new NonexistentEntityException("The factura with id " + id + " no longer exists.");
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
            Factura factura;
            try {
                factura = em.getReference(Factura.class, id);
                factura.getIdFactura();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The factura with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Detfactura> detfacturaCollectionOrphanCheck = factura.getDetfacturaCollection();
            for (Detfactura detfacturaCollectionOrphanCheckDetfactura : detfacturaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Factura (" + factura + ") cannot be destroyed since the Detfactura " + detfacturaCollectionOrphanCheckDetfactura + " in its detfacturaCollection field has a non-nullable idFactura field.");
            }
            Collection<Pago> pagoCollectionOrphanCheck = factura.getPagoCollection();
            for (Pago pagoCollectionOrphanCheckPago : pagoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Factura (" + factura + ") cannot be destroyed since the Pago " + pagoCollectionOrphanCheckPago + " in its pagoCollection field has a non-nullable idFactura field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Prodservicio idServicio = factura.getIdServicio();
            if (idServicio != null) {
                idServicio.getFacturaCollection().remove(factura);
                idServicio = em.merge(idServicio);
            }
            em.remove(factura);
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

    public List<Factura> findFacturaEntities() {
        return findFacturaEntities(true, -1, -1);
    }

    public List<Factura> findFacturaEntities(int maxResults, int firstResult) {
        return findFacturaEntities(false, maxResults, firstResult);
    }

    private List<Factura> findFacturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Factura.class));
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

    public Factura findFactura(Double id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Factura.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Factura> rt = cq.from(Factura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
