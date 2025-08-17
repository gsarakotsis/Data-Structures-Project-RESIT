package adt;

import java.util.*;


public class Route {
    private final List<Flight> flights;
    private double totalPrice;
    private int totalDuration; // in minutes
    private final boolean isValid;


    public Route(List<Flight> flights) {
        if (flights == null || flights.isEmpty()) {
            throw new IllegalArgumentException("Route must contain at least one flight");
        }
        this.flights = new ArrayList<>(flights);
        this.isValid = validateRoute();
        if (isValid) {
            calculateTotalPrice();
            calculateTotalDuration();
        } else {
            this.totalPrice = 0.0;
            this.totalDuration = 0;
        }
    }


    private boolean validateRoute() {
        for (int i = 0; i < flights.size() - 1; i++) {
            if (!flights.get(i).getDestination().equals(flights.get(i + 1).getOrigin())) {
                return false;
            }
        }
        return true;
    }


    private void calculateTotalPrice() {
        totalPrice = flights.stream()
                .mapToDouble(Flight::getCurrentPrice)
                .sum();
    }


    private void calculateTotalDuration() {
        totalDuration = flights.size() * 120; // 2 hours per flight
        if (flights.size() > 1) {
            totalDuration += (flights.size() - 1) * 60; // 1 hour layover
        }
    }


    public boolean isValidRoute() {
        return isValid;
    }


    public boolean bookRoute(int passengerCount) {
        if (passengerCount <= 0 || !isValid) {
            return false;
        }

        // Check availability first
        for (Flight flight : flights) {
            if (flight.getAvailableSeats() < passengerCount) {
                return false;
            }
        }

        // Book all flights (with rollback on failure)
        List<Flight> bookedFlights = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.bookSeats(passengerCount)) {
                bookedFlights.add(flight);
            } else {
                // Rollback previous bookings
                for (Flight bookedFlight : bookedFlights) {
                    bookedFlight.releaseSeats(passengerCount);
                }
                return false;
            }
        }

        // Recalculate price after booking (dynamic pricing change)
        calculateTotalPrice();
        return true;
    }


    public void cancelRoute(int passengerCount) {
        if (passengerCount > 0 && isValid) {
            for (Flight flight : flights) {
                flight.releaseSeats(passengerCount);
            }
            // Recalculate price after cancellation
            calculateTotalPrice();
        }
    }

    public boolean hasAvailability(int passengerCount) {
        if (passengerCount <= 0 || !isValid) {
            return false;
        }

        return flights.stream()
                .allMatch(flight -> flight.getAvailableSeats() >= passengerCount);
    }

    public Airport getOrigin() {
        return flights.getFirst().getOrigin();
    }


    public Airport getDestination() {
        return flights.getLast().getDestination();
    }

    public List<Airport> getLayoverAirports() {
        List<Airport> layovers = new ArrayList<>();
        for (int i = 0; i < flights.size() - 1; i++) {
            layovers.add(flights.get(i).getDestination());
        }
        return layovers;
    }


    public double getComplexityScore() {
        return flights.size() * 1.0 + (totalDuration / 60.0) * 0.1;
    }

    // Getter methods
    public List<Flight> getFlights() {
        return new ArrayList<>(flights);
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public boolean isDirect() {
        return flights.size() == 1;
    }

    public int getFlightCount() {
        return flights.size();
    }


    public String getFormattedDuration() {
        int hours = totalDuration / 60;
        int minutes = totalDuration % 60;
        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        if (!isValid) {
            return "Invalid Route";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Route: %s -> %s ($%.2f, %s) [%s]\n",
                getOrigin().getCode(), getDestination().getCode(),
                totalPrice, getFormattedDuration(),
                isDirect() ? "Direct" : (flights.size() - 1) + "-Stop"));

        for (int i = 0; i < flights.size(); i++) {
            sb.append(String.format("  %d. %s\n", i + 1, flights.get(i)));
        }
        return sb.toString();
    }


    public String toDetailedString() {
        if (!isValid) {
            return "Invalid Route - Flights are not properly connected";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Route Details ===\n");
        sb.append(String.format("From: %s\n", getOrigin()));
        sb.append(String.format("To: %s\n", getDestination()));
        sb.append(String.format("Type: %s\n", isDirect() ? "Direct Flight" :
                (flights.size() - 1) + "-Stop Connection"));
        sb.append(String.format("Total Price: $%.2f\n", totalPrice));
        sb.append(String.format("Total Duration: %s\n", getFormattedDuration()));
        sb.append(String.format("Flights: %d\n\n", flights.size()));

        for (int i = 0; i < flights.size(); i++) {
            sb.append(String.format("Flight %d:\n", i + 1));
            sb.append(String.format("  %s\n", flights.get(i)));

            if (i < flights.size() - 1) {
                Airport layover = flights.get(i).getDestination();
                sb.append(String.format("  Layover at: %s\n", layover));
            }
            sb.append("\n");
        }

        return sb.toString();
    }


    public static Comparator<Route> priceComparator() {
        return Comparator.comparingDouble(Route::getTotalPrice)
                .thenComparingInt(Route::getTotalDuration);
    }


    public static Comparator<Route> durationComparator() {
        return Comparator.comparingInt(Route::getTotalDuration)
                .thenComparingDouble(Route::getTotalPrice);
    }
}