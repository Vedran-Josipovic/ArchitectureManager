package app.prod.model;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Location, Serializable {
    private Long id;
    private String street, houseNumber, city;

    @Override
    public String getFullLocationDetails() {
        return getStreet() + " " + getHouseNumber() + ", " + getCity();
    }

    public Address(Long id, String street, String houseNumber, String city) {
        this.id = id;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }

    public Address(String street, String houseNumber, String city) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getId(), address.getId()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getHouseNumber(), address.getHouseNumber()) && Objects.equals(getCity(), address.getCity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStreet(), getHouseNumber(), getCity());
    }

    @Override
    public String toString() {
        return "Address{" + "street='" + street + '\'' + ", houseNumber='" + houseNumber + '\'' + ", city='" + city + '\'' + '}';
    }


}
