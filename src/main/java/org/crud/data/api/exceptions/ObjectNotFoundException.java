package org.crud.data.api.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String objectType, Object objectId) {
        super("Object with type = '" + objectType + "' by id = '" + objectId + "' is not found");
    }
}
