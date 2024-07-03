package app.prod.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChangeLogEntry<T> implements Serializable {
    private LocalDateTime timestamp;
    private String entity, changeType;
    private T oldValue, newValue;
    private String userRole;

    public ChangeLogEntry() {
    }

    public ChangeLogEntry(LocalDateTime timestamp, String entity, String changeType, T oldValue, T newValue, String userRole) {
        this.timestamp = timestamp;
        this.entity = entity;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userRole = userRole;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public T getOldValue() {
        return oldValue;
    }

    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "ChangeLogEntry{" +
                "timestamp=" + timestamp +
                ", entity='" + entity + '\'' +
                ", changeType='" + changeType + '\'' +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
