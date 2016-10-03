package org.crud.data.core;

import org.crud.data.api.ICrudDao;
import org.crud.data.api.Identifiable;
import org.crud.data.api.exceptions.ObjectNotFoundException;
import org.crud.data.utils.EntityUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Roman Rybalkin
 * 24.08.2015
 */
public abstract class AbstractCrudDao<D extends Identifiable, E, PK> extends AbstractDao implements ICrudDao<D, PK> {
    private final Class<E> entityType = (Class<E>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
    private final Class<D> dataType = (Class<D>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];

    protected AbstractCrudDao() {
        Objects.requireNonNull(getDInstance(), "Method 'getDInstance' must return correct objects");
        Objects.requireNonNull(getEInstance(), "Method 'getEInstance' must return correct objects");
    }

    /**
     * Creates and gets instance of Data Object type.
     * @return Data Object instance
     */
    protected D getDInstance() {
        return (D) instantiateObjectByClass(dataType);
    }

    /**
     * Creates and gets instance of Entity Object type.
     * @return Entity Object instance
     */
    protected E getEInstance() {
        return (E) instantiateObjectByClass(entityType);
    }

    /**
     * Method instantiates object by using reflection and based on
     * target object class.
     * For success instantiation objectClass must have default
     * (public, without argument) constructor.
     * In any specific case you can override {@link #getDInstance()}
     * and {@link #getEInstance()} methods.
     *
     * @param objectClass target object class
     * @return target object instance or null in case of problems while instantiation
     */
    private Object instantiateObjectByClass(Class<?> objectClass) {
        try {
            Constructor constructor = objectClass.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Converts entity object to data object by using blind converting method,
     * which based on matching filed names for both types.
     * @param entityObject entity object
     * @param dataObject data object
     * @return converted data object with filled fields
     */
    protected D convertFromEntity(E entityObject, D dataObject) {
        return EntityUtil.fillBlindly(entityObject, dataObject);
    }

    /**
     * Converts data object to entity object by using blind converting method,
     * with based on matching field names for both types.
     * @param dataObject data object
     * @param entityObject entity object
     * @return converted entity object with filled fields
     */
    protected E convertToEntity(D dataObject, E entityObject) {
        return EntityUtil.fillBlindly(dataObject, entityObject);
    }

    @Override
    public D findById(PK id) {
        Objects.requireNonNull(id, "ID must not be null");
        E entity = findEntityByPK(id);
        return convertFromEntity(entity, getDInstance());
    }

    @Override
    public D create(D createdObject) {
        Objects.requireNonNull(createdObject, "createdObject must not be null");
        E entity = convertToEntity(createdObject, getEInstance());
        EntityManager em = getEntityManager();
        em.persist(entity);
        em.flush();

        return convertFromEntity(entity, getDInstance());
    }

    @Override
    public D update(D updatedObject) {
        Objects.requireNonNull(updatedObject, "updatedObject must not be null");
        EntityManager em = getEntityManager();
        Object primaryKey = updatedObject.getId();
        E origin = findEntityByPK(primaryKey);
        E updatedEntity = convertToEntity(updatedObject, origin);
        E merged = em.merge(updatedEntity);
        return convertFromEntity(merged, updatedObject);
    }

    @Override
    public D deleteById(PK objectId) {
        Objects.requireNonNull(objectId, "ID must not be null");
        EntityManager em = getEntityManager();
        E origin = findEntityByPK(objectId);
        em.remove(origin);
        return convertFromEntity(origin, getDInstance());
    }

    @Override
    public List<D> getAll() {
        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery(entityType);
        Root<E> root = criteria.<E>from(entityType);
        criteria.select(root);
        List foundEntities = em.createQuery(criteria).getResultList();
        return convertFromEntities(foundEntities);
    }

    private E findEntityByPK(Object pk) {
        E origin = (E) getEntityManager().find(entityType, pk);
        if (origin == null) {
            throw new ObjectNotFoundException(entityType.getName(), pk);
        }
        return origin;
    }

    protected List<D> convertFromEntities(List<E> entities) {
        List<D> dataObjects = new ArrayList<>(entities.size());
        for (E entity : entities) {
            dataObjects.add(convertFromEntity(entity, getDInstance()));
        }
        return dataObjects;
    }

    /**
     * Util method to find entities with one specific field value.
     * @param fieldName interested field of entity
     * @param fieldValue interested field value
     * @return list of entities with interested field value
     */
    protected List<D> findByFieldEquals(String fieldName, Object fieldValue) {
        Objects.requireNonNull(fieldName, "Field name must not be null");
        Objects.requireNonNull(fieldValue, "Field value must not be null");

        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery(entityType);
        Root root = criteria.from(entityType);
        criteria.where(builder.equal(root.get(fieldName), fieldValue));
        List<E> foundEntities = em.createQuery(criteria).getResultList();
        return convertFromEntities(foundEntities);
    }
}
