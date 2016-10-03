package org.crud.data.api;

import org.crud.data.api.exceptions.ObjectNotFoundException;

import java.util.List;

public interface ICrudService<T, PK> {

    T getById(PK objId) throws ObjectNotFoundException;

    T create(T createdObj);

    T update(T updatedObj) throws ObjectNotFoundException;

    T deleteById(PK objId) throws ObjectNotFoundException;

    List<T> getAll();
}
