package org.crud.data.core;

import org.crud.data.api.ICrudDao;
import org.crud.data.api.ICrudService;
import org.crud.data.api.Identifiable;

import java.util.List;

/**
 * Created by Roman Rybalkin
 * 21.08.2015
 *
 * Service provides methods for CRUD operations + getting all entities out of the box.
 * Client just has to provide DAO object which implements ICrudDao interface.
 */
public abstract class AbstractCrudService<T extends Identifiable<K>, K> implements ICrudService<T, K> {

    protected abstract ICrudDao<T, K> getDao();

    @Override
    public T getById(K objId) {
        return getDao().findById(objId);
    }

    @Override
    public T create(T toCreate) {
        if (toCreate == null) {
            throw new IllegalArgumentException("toCreate argument is null");
        }
        onBeforeCreate(toCreate);
        T created = getDao().create(toCreate);
        onAfterCreate(created);
        return created;
    }

    @Override
    public T update(T toUpdate) {
        if (toUpdate == null) {
            throw new IllegalArgumentException("toUpdate argument is null");
        }
        T previous = getById(toUpdate.getId());
        onBeforeUpdate(previous, toUpdate);
        T updated = getDao().update(toUpdate);
        onAfterUpdate(previous, updated);
        return updated;
    }

    @Override
    public T deleteById(K objId) {
        T toDelete = getById(objId);
        onBeforeDelete(toDelete);
        T delete = getDao().deleteById(objId);
        onAfterDelete(delete);
        return delete;
    }

    @Override
    public List<T> getAll() {
        return getDao().getAll();
    }

    protected void onBeforeCreate(T toCreateObj) {
        // by default do nothing
    }

    protected void onBeforeUpdate(T beforeUpdateObj, T toUpdateObj) {
        // by default do nothing
    }

    protected void onBeforeDelete(T toDeleteObj) {
        // by default do nothing
    }

    protected void onAfterCreate(T createdObj) {
        // by default do nothing
    }

    protected void onAfterUpdate(T beforeUpdateObj, T updatedObj) {
        // by default do nothing
    }

    protected void onAfterDelete(T deletedObj) {
        // by default do nothing
    }
}
