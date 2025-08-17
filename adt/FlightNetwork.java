package adt;

import datastructures.FlightGraph;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class FlightNetwork {
    private final FlightGraph flightGraph;
    private final List<Reservation> reservations;
    private final Map<String, List<Reservation>> customerReservations;


    public FlightNetwork() {
        flightGraph = new FlightGraph();
        reservations = new ArrayList<>();
        customerReservations = new HashMap<>();
    }


    public void addAirport(Airport airport) {
        if (airport == null) {
            throw new IllegalArgumentException("Airport cannot be null");
        }
        flightGraph.addAirport(airport);
    }


    public void addAirports(Airport[] airports) {
        if (airports == null) {
            throw new IllegalArgumentException("Airports array cannot be null");
        }

        for (Airport airport : airports) {
            if (airport != null) {
                addAirport(airport);
            }
        }
    }

    public void addFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        flightGraph.addFlight(flight);
    }

    public void addFlights(Flight[] flights) {
        if (flights == null) {
            throw new IllegalArgumentException("Flights array cannot be null");
        }

        for (Flight flight : flights) {
            if (flight != null) {
                addFlight(flight);
            }
        }
    }


    public List<Route> searchRoutes(String originCode, String destCode) {
        if (originCode == null || destCode == null) {
            throw new IllegalArgumentException("Airport codes cannot be null");
        }

        if (!flightGraph.hasAirport(originCode)) {
            throw new IllegalArgumentException("Origin airport not found: " + originCode);
        }

        if (!flightGraph.hasAirport(destCode)) {
            throw new IllegalArgumentException("Destination airport not found: " + destCode);
        }

        return flightGraph.findRoutes(originCode, destCode);
    }

    public List<Route> searchRoutes(String originCode, String destCode, int passengerCount) {
        if (passengerCount <= 0) {
            throw new IllegalArgumentException("Passenger count must be positive");
        }

        List<Route> allRoutes = searchRoutes(originCode, destCode);
        return allRoutes.stream()
                .filter(route -> route.hasAvailability(passengerCount))
                .collect(Collectors.toList());
    }


    public Reservation makeReservation(Route route, int passengerCount) {
        if (route == null || passengerCount <= 0) {
            throw new IllegalArgumentException("Invalid route or passenger count");
        }

        Reservation reservation = new Reservation(route, passengerCount);

        if (reservation.confirm()) {
            reservations.add(reservation);
            System.out.println("Reservation confirmed: #" + reservation.getReservationId());
            return reservation;
        } else {
            System.out.println("Reservation failed: Insufficient seats available");
            return null;
        }
    }

    public Reservation makeReservation(Route route, int passengerCount, String customerEmail, List<String> passengerNames) {
        if (customerEmail == null) {
            throw new IllegalArgumentException("Customer email cannot be null");
        }

        Reservation reservation = new Reservation(route, passengerCount, customerEmail, passengerNames);

        if (reservation.confirm()) {
            reservations.add(reservation);

            // Track by customer email
            customerReservations.computeIfAbsent(customerEmail, k -> new ArrayList<>()).add(reservation);

            System.out.println("Reservation confirmed: #" + reservation.getReservationId() + " for " + customerEmail);
            return reservation;
        } else {
            System.out.println("Reservation failed: Insufficient seats available");
            return null;
        }
    }

    public boolean cancelReservation(int reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId() == reservationId) {
                if (reservation.cancel()) {
                    System.out.println("Reservation cancelled: #" + reservationId);
                    return true;
                } else {
                    System.out.println("Cannot cancel reservation #" + reservationId + " - Status: " + reservation.getStatus());
                    return false;
                }
            }
        }
        System.out.println("Reservation not found: #" + reservationId);
        return false;
    }


    public Reservation getReservation(int reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId() == reservationId)
                .findFirst()
                .orElse(null);
    }

    public List<Reservation> getCustomerReservations(String customerEmail) {
        if (customerEmail == null) {
            return new ArrayList<>();
        }

        return customerReservations.getOrDefault(customerEmail, new ArrayList<>());
    }

    public void printNetworkStatistics() {
        Set<String> airportCodes = flightGraph.getAllAirportCodes();
        int totalFlights = 0;
        int totalSeats = 0;
        int bookedSeats = 0;
        double totalRevenue = 0.0;

        // Calculate flight statistics
        for (String code : airportCodes) {
            List<Flight> flights = flightGraph.getFlightsFrom(code);
            totalFlights += flights.size();
            for (Flight flight : flights) {
                totalSeats += flight.getTotalSeats();
                bookedSeats += (flight.getTotalSeats() - flight.getAvailableSeats());
                totalRevenue += flight.getCurrentPrice() * (flight.getTotalSeats() - flight.getAvailableSeats());
            }
        }

        // Calculate reservation statistics
        long confirmedReservations = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .count();

        double totalReservationValue = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .mapToDouble(Reservation::getTotalCost)
                .sum();

        double occupancyRate = totalSeats > 0 ? (double)bookedSeats / totalSeats * 100 : 0;

        System.out.println("\n" + "=".repeat(40));
        System.out.println("       NETWORK STATISTICS");
        System.out.println("=".repeat(40));
        System.out.printf("Airports: %d\n", airportCodes.size());
        System.out.printf("Total Flights: %d\n", totalFlights);
        System.out.printf("Total Seats: %d\n", totalSeats);
        System.out.printf("Booked Seats: %d\n", bookedSeats);
        System.out.printf("Occupancy Rate: %.1f%%\n", occupancyRate);
        System.out.printf("Average Flights per Airport: %.1f\n",
                !airportCodes.isEmpty() ? (double)totalFlights / airportCodes.size() : 0);
        System.out.println();
        System.out.printf("Total Reservations: %d\n", reservations.size());
        System.out.printf("Confirmed Reservations: %d\n", confirmedReservations);
        System.out.printf("Total Reservation Value: $%.2f\n", totalReservationValue);
        System.out.printf("Estimated Revenue: $%.2f\n", totalRevenue);
        System.out.println("=".repeat(40));
    }


    public void printAirportStatistics() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           AIRPORT STATISTICS");
        System.out.println("=".repeat(50));

        List<Airport> hubAirports = flightGraph.getHubAirports(10);

        for (Airport airport : hubAirports) {
            String code = airport.getCode();
            List<Flight> outgoing = flightGraph.getFlightsFrom(code);
            List<Flight> incoming = flightGraph.getFlightsTo(code);

            int outgoingSeats = outgoing.stream().mapToInt(Flight::getTotalSeats).sum();
            int incomingSeats = incoming.stream().mapToInt(Flight::getTotalSeats).sum();
            int outgoingBooked = outgoing.stream().mapToInt(f -> f.getTotalSeats() - f.getAvailableSeats()).sum();
            int incomingBooked = incoming.stream().mapToInt(f -> f.getTotalSeats() - f.getAvailableSeats()).sum();

            System.out.printf("%s (%s)\n", airport.getName(), code);
            System.out.printf("  Outgoing: %d flights, %d/%d seats (%.1f%% full)\n",
                    outgoing.size(), outgoingBooked, outgoingSeats,
                    outgoingSeats > 0 ? (double)outgoingBooked / outgoingSeats * 100 : 0);
            System.out.printf("  Incoming: %d flights, %d/%d seats (%.1f%% full)\n",
                    incoming.size(), incomingBooked, incomingSeats,
                    incomingSeats > 0 ? (double)incomingBooked / incomingSeats * 100 : 0);
            System.out.println();
        }
    }


    public List<String> getPopularRoutes(int limit) {
        Map<String, Integer> routeBookings = new HashMap<>();

        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == Reservation.ReservationStatus.CONFIRMED ||
                    reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {

                Route route = reservation.getRoute();
                String routeKey = route.getOrigin().getCode() + " -> " + route.getDestination().getCode();
                routeBookings.put(routeKey, routeBookings.getOrDefault(routeKey, 0) + 1);
            }
        }

        return routeBookings.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> String.format("%s (%d bookings)", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public void analyzePricing(String originCode, String destCode) {
        List<Route> routes = searchRoutes(originCode, destCode);

        if (routes.isEmpty()) {
            System.out.println("No routes found between " + originCode + " and " + destCode);
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.printf("    PRICING ANALYSIS: %s -> %s\n", originCode, destCode);
        System.out.println("=".repeat(50));

        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            System.out.printf("\nOption %d: %s Route\n", i + 1, route.isDirect() ? "Direct" : "1-Stop");
            System.out.printf("Total Price: $%.2f\n", route.getTotalPrice());
            System.out.printf("Duration: %s\n", route.getFormattedDuration());

            for (Flight flight : route.getFlights()) {
                double occupancy = flight.getOccupancyRate() * 100;
                System.out.printf("  %s: $%.2f (%.1f%% full, base: $%.2f)\n",
                        flight.getFlightNumber(), flight.getCurrentPrice(),
                        occupancy, flight.getBasePrice());
            }
        }

        // Price comparison
        double minPrice = routes.stream().mapToDouble(Route::getTotalPrice).min().orElse(0);
        double maxPrice = routes.stream().mapToDouble(Route::getTotalPrice).max().orElse(0);
        double avgPrice = routes.stream().mapToDouble(Route::getTotalPrice).average().orElse(0);

        System.out.println("\nPrice Summary:");
        System.out.printf("  Cheapest: $%.2f\n", minPrice);
        System.out.printf("  Most Expensive: $%.2f\n", maxPrice);
        System.out.printf("  Average: $%.2f\n", avgPrice);
        System.out.printf("  Price Range: $%.2f\n", maxPrice - minPrice);
    }

    public void simulateTimeProgression() {
        Random random = new Random();
        Set<String> airportCodes = flightGraph.getAllAirportCodes();

        System.out.println("Simulating passenger bookings...");

        for (String code : airportCodes) {
            List<Flight> flights = flightGraph.getFlightsFrom(code);
            for (Flight flight : flights) {
                // Randomly book additional seats (0-20% of available seats)
                int availableSeats = flight.getAvailableSeats();
                if (availableSeats > 0) {
                    int additionalBookings = random.nextInt(Math.max(1, (int)(availableSeats * 0.2)));
                    flight.bookSeats(additionalBookings);
                }
            }
        }

        System.out.println("Time progression simulation complete!");
    }

    public Airport getAirport(String code) {
        return flightGraph.getAirport(code);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Flight> getFlightsFrom(String airportCode) {
        return flightGraph.getFlightsFrom(airportCode);
    }

    public List<Flight> getFlightsTo(String airportCode) {
        return flightGraph.getFlightsTo(airportCode);
    }

    public Set<String> getAllAirportCodes() {
        return flightGraph.getAllAirportCodes();
    }

    public Collection<Airport> getAllAirports() {
        return flightGraph.getAllAirports();
    }

    public boolean validateNetwork() {
        // Validate graph integrity
        if (!flightGraph.validateGraph()) {
            return false;
        }

        // Validate reservations
        for (Reservation reservation : reservations) {
            if (!reservation.validate()) {
                return false;
            }
        }

        return true;
    }

    public String exportNetworkSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("FLIGHT NETWORK SUMMARY\n");
        summary.append("Generated: ").append(LocalDate.now()).append("\n\n");

        summary.append("AIRPORTS:\n");
        for (Airport airport : getAllAirports()) {
            summary.append(String.format("- %s\n", airport.toString()));
        }

        summary.append("\nFLIGHTS:\n");
        for (String code : getAllAirportCodes()) {
            List<Flight> flights = getFlightsFrom(code);
            for (Flight flight : flights) {
                summary.append(String.format("- %s\n", flight.toString()));
            }
        }

        summary.append("\nRESERVATIONS:\n");
        for (Reservation reservation : reservations) {
            summary.append(String.format("- %s\n", reservation.toString()));
        }

        return summary.toString();
    }
}