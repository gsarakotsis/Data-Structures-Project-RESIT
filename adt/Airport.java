package adt;

import java.util.Objects;


public class Airport {
    private String code;
    private String name;
    private String location;

    public Airport(String code, String name, String location) {
        if (code == null || code.isEmpty() || name == null)
            throw new IllegalArgumentException("Airports code and name cannot be empty");

        this.code = code;
        this.name = name;
        this.location = location;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return Objects.equals(code, airport.code) && Objects.equals(name, airport.name) && Objects.equals(location, airport.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, location);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s" , name , code, location);
    }
}
