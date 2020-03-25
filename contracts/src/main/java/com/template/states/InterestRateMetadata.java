package com.template.states;

import net.corda.core.serialization.CordaSerializable;

import java.time.LocalDate;
import java.util.Objects;

@CordaSerializable
public class InterestRateMetadata {

    private String name;
    private LocalDate date;

    public InterestRateMetadata(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestRateMetadata that = (InterestRateMetadata) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDate());
    }

    @Override
    public String toString() {
        return "InterestRateMetadata{" +
                "name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
