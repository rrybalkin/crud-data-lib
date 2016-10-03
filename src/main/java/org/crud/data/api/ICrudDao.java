package org.crud.data.api;

import org.crud.data.api.exceptions.ObjectNotFoundException;

import java.util.List;

public interface ICrudDao<T, K> {

    T findById(K id) throws ObjectNotFoundException;

    T create(T object);

    T update(T updatedObject) throws ObjectNotFoundException;

    T deleteById(K objectId) throws ObjectNotFoundException;

    List<T> getAll();
}
