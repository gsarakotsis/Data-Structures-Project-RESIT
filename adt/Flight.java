package adt;

import java.time.LocalDate;

public class Flight {
    private Airport origin;
    private Airport destination;
    private int totalSeats;
    private int availableSeats;
    private double basePrice;
    private LocalDate flightDate;
    private String flightNumber;


    public Flight(Airport origin, Airport destination, int totalSeats, double basePrice,
                  LocalDate flightDate, String flightNumber) {
        if (origin == null || destination == null)
            throw new IllegalArgumentException("Origin and destination cannot be null");
        if (totalSeats <= 0 || basePrice <= 0)
            throw new IllegalArgumentException("Total seats and base price cannot be negative");
        if (origin.equals(destination))
            throw new IllegalArgumentException("Origin and destination cannot be the same");

        this.origin = origin;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.basePrice = basePrice;
        this.flightDate = flightDate;
        this.flightNumber = flightNumber;
        this.availableSeats = totalSeats;
    }

    public boolean bookSeats(int seatCount) {
        if (seatCount <= 0 || seatCount > availableSeats)
            return false;
        availableSeats -= seatCount;
        return true;
    }

    public void releaseSeats(int seatCount) {
        if (seatCount > 0 && (availableSeats + seatCount) <= totalSeats)
            availableSeats += seatCount;
    }

    public double getCurrentPrice() {
        double occupancyRate = (double) (totalSeats - availableSeats) / totalSeats;

        if (occupancyRate  >= 0.9)
            return basePrice*2;
        else if (occupancyRate <= 0.1)
            return basePrice*0.5;
        else
            return basePrice*(0.5 + 1.5*occupancyRate);
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public double getOccupancyRate() {
        return (double)(totalSeats - availableSeats)/totalSeats;
    }

    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(LocalDate flightDate) {
        this.flightDate = flightDate;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Override
    public String toString() {
        return String.format("%s: %s -> %s\n" +
                "Date: %s\n" +
                "Seats: %d available / %d total (%.1f%% full)\n" +
                "Price : %.2f (Base: %.2f)",
                flightNumber,origin.getCode(),destination.getCode(),
                flightDate, availableSeats , totalSeats , getOccupancyRate()*100,
                getCurrentPrice() , basePrice);
    }
}
