package org.crud.data.core;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base class for DAO implementation.
 */
public abstract class AbstractDao {

    @PersistenceContext
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }
}
