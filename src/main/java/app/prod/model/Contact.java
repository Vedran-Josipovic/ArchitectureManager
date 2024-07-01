package app.prod.model;

import java.util.Objects;

public abstract class Contact extends Entity {
    protected String email;

    public String getContactDetails() {
        return getEmail() + " [" + getName() + "]";
    }

    public Contact(Long id, String name, String email) {
        super(id, name);
        this.email = email;
    }

    public Contact(String name, String email) {
        super(name);
        this.email = email;
    }
    public Contact() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(getEmail(), contact.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmail());
    }

    @Override
    public String toString() {
        return "Contact{" +
                "email='" + email + '\'' +
                "} " + super.toString();
    }
}
