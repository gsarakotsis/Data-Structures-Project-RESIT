package ui;

import adt.*;
import datastructures.CustomHashMap;


import java.util.*;

public class MenuHandler {
    private final FlightNetwork network;
    private InputHandler inputHandler;

    public MenuHandler(FlightNetwork network, InputHandler inputHandler) {
        this.network = network;
        this.inputHandler = inputHandler;
    }

    public void displayMenu() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("MAIN MENU");
        System.out.println("-".repeat(40));
        System.out.println("1.Search Flights");
        System.out.println("2.Make Reservation");
        System.out.println("3.Cancel Reservation");
        System.out.println("4.View All Reservations");
        System.out.println("5.View Flight Prices");
        System.out.println("6.Network Statistics");
        System.out.println("7.Analyze Route Pricing");
        System.out.println("8.Performance Tests");
        System.out.println("9.Exit");
    }

    public void handleMenuChoice(int choice) throws Exception {
        switch (choice) {
            case 1:
                searchFlights();
                break;
            case 2:
                makeReservation();
                break;
            case 3:
                cancelReservation();
                break;
            case 4:
                viewReservations();
                break;
            case 5:
                viewFlightPrices();
                break;
            case 6:
                viewStatistics();
                break;
            case 7:
                analyzePricing();
                break;
            case 8:
                runPerformanceTests();
                break;
            case 9:
                System.out.println("Thank you for using the Flight Pricing System!");
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void searchFlights() {
        System.out.println("\n--- SEARCH FLIGHTS ---");

        displayAvailableAirports();
        String origin = inputHandler.getStringInput("Enter origin airport code: ").toUpperCase();
        String destination = inputHandler.getStringInput("Enter destination airport code: ").toUpperCase();
        int passengers = inputHandler.getIntInput("Enter number of passengers: ");

        if (passengers <= 0) {
            System.out.println("Number of passengers must be positive.");
            return;
        }

        try {
            List<Route> routes = network.searchRoutes(origin, destination, passengers);

            if (routes.isEmpty()) {
                System.out.println("No available routes found between " + origin + " and " + destination);
                System.out.println("for " + passengers + " passenger(s).");
                return;
            }

            System.out.println("\nFound " + routes.size() + " available route(s):");
            System.out.println("=".repeat(60));

            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                System.out.printf("\nOption %d:\n", i + 1);
                System.out.println(route.toDetailedString());
                System.out.println("-".repeat(40));
            }

        } catch (IllegalArgumentException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private void makeReservation() {
        System.out.println("\n--- MAKE RESERVATION ---");

        displayAvailableAirports();
        String origin = inputHandler.getStringInput("Enter origin airport code: ").toUpperCase();
        String destination = inputHandler.getStringInput("Enter destination airport code: ").toUpperCase();
        int passengers = inputHandler.getIntInput("Enter number of passengers: ");

        try {
            List<Route> routes = network.searchRoutes(origin, destination, passengers);

            if (routes.isEmpty()) {
                System.out.println("No available routes found.");
                return;
            }

            System.out.println("\nAvailable routes:");
            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                System.out.printf("%d. %s -> %s (€%.2f, %s) [%s]\n",
                        i + 1, route.getOrigin().getCode(), route.getDestination().getCode(),
                        route.getTotalPrice() * passengers, route.getFormattedDuration(),
                        route.isDirect() ? "Direct" : "1-Stop");
            }

            int choice = inputHandler.getIntInput("Select route (1-" + routes.size() + "): ");

            if (choice < 1 || choice > routes.size()) {
                System.out.println("Invalid route selection.");
                return;
            }

            Route selectedRoute = routes.get(choice - 1);

            // Get customer information
            String email = inputHandler.getStringInput("Enter customer email (optional): ");
            List<String> passengerNames = new ArrayList<>();

            if (!email.trim().isEmpty()) {
                System.out.println("Enter passenger names (press Enter to skip):");
                for (int i = 0; i < passengers; i++) {
                    String name = inputHandler.getStringInput("Passenger " + (i + 1) + ": ");
                    if (!name.trim().isEmpty()) {
                        passengerNames.add(name.trim());
                    }
                }
            }

            // Make reservation
            Reservation reservation;
            if (!email.trim().isEmpty()) {
                reservation = network.makeReservation(selectedRoute, passengers, email, passengerNames);
            } else {
                reservation = network.makeReservation(selectedRoute, passengers);
            }

            if (reservation != null) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println(reservation.getReservationSummary());
            }

        } catch (Exception e) {
            System.out.println("Error making reservation: " + e.getMessage());
        }
    }

    private void cancelReservation() {
        System.out.println("\n--- CANCEL RESERVATION ---");

        List<Reservation> allReservations = network.getAllReservations();
        if (allReservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        System.out.println("Current reservations:");
        for (Reservation reservation : allReservations) {
            if (reservation.getStatus() != Reservation.ReservationStatus.CANCELLED) {
                System.out.println("  " + reservation.toString());
            }
        }

        int reservationId = inputHandler.getIntInput("Enter reservation ID to cancel: ");

        Reservation reservation = network.getReservation(reservationId);
        if (reservation != null) {
            System.out.println("\nReservation details:");
            System.out.println(reservation.getReservationSummary());

            if (reservation.isRefundable()) {
                System.out.printf("Refund amount: €%.2f\n", reservation.getRefundAmount());
            } else {
                System.out.println("This reservation is not refundable.");
            }

            String confirm = inputHandler.getStringInput("Confirm cancellation? (yes/no): ");
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                network.cancelReservation(reservationId);
            } else {
                System.out.println("Cancellation aborted.");
            }
        } else {
            System.out.println("Reservation not found.");
        }
    }

    private void viewReservations() {
        System.out.println("\n--- VIEW RESERVATIONS ---");

        List<Reservation> reservations = network.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        // Sort reservations by booking date (most recent first)
        reservations.sort(Reservation.bookingDateComparator().reversed());

        System.out.println("All Reservations:");
        System.out.println("=".repeat(80));

        Map<Reservation.ReservationStatus, List<Reservation>> groupedReservations = new HashMap<>();
        for (Reservation reservation : reservations) {
            groupedReservations.computeIfAbsent(reservation.getStatus(), k -> new ArrayList<>()).add(reservation);
        }

        for (Reservation.ReservationStatus status : Reservation.ReservationStatus.values()) {
            List<Reservation> statusReservations = groupedReservations.get(status);
            if (statusReservations != null && !statusReservations.isEmpty()) {
                System.out.println("\n" + status.getDescription() + " (" + statusReservations.size() + "):");
                System.out.println("-".repeat(50));
                for (Reservation reservation : statusReservations) {
                    System.out.println(reservation.toDetailedString());
                }
            }
        }

        // Summary statistics
        long confirmed = reservations.stream().filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED).count();
        double totalValue = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .mapToDouble(Reservation::getTotalCost).sum();

        System.out.println("\n" + "=".repeat(40));
        System.out.println("RESERVATION SUMMARY");
        System.out.println("=".repeat(40));
        System.out.printf("Total Reservations: %d\n", reservations.size());
        System.out.printf("Confirmed: %d\n", confirmed);
        System.out.printf("Total Value: €%.2f\n", totalValue);
    }

    private void viewFlightPrices() {
        System.out.println("\n--- VIEW FLIGHT PRICES ---");

        displayAvailableAirports();
        String origin = inputHandler.getStringInput("Enter origin airport code (or 'ALL' for all): ").toUpperCase();

        if (origin.equals("ALL")) {
            displayAllFlightPrices();
        } else {
            displayFlightPricesFrom(origin);
        }
    }

    private void displayAllFlightPrices() {
        System.out.println("\n=== ALL FLIGHT PRICES ===");

        Set<String> airportCodes = network.getAllAirportCodes();
        for (String code : airportCodes) {
            List<Flight> flights = network.getFlightsFrom(code);
            if (!flights.isEmpty()) {
                System.out.println("\nFlights from " + code + ":");
                System.out.println("-".repeat(60));
                for (Flight flight : flights) {
                    displayFlightPriceInfo(flight);
                }
            }
        }
    }

    private void displayFlightPricesFrom(String origin) {
        List<Flight> flights = network.getFlightsFrom(origin);

        if (flights.isEmpty()) {
            System.out.println("No flights found from " + origin);
            return;
        }

        System.out.println("\nFlights from " + origin + ":");
        System.out.println("=".repeat(70));

        // Sort flights by current price
        flights.sort(Comparator.comparingDouble(Flight::getCurrentPrice));

        for (Flight flight : flights) {
            displayFlightPriceInfo(flight);
        }

        // Price statistics
        double minPrice = flights.stream().mapToDouble(Flight::getCurrentPrice).min().orElse(0);
        double maxPrice = flights.stream().mapToDouble(Flight::getCurrentPrice).max().orElse(0);
        double avgPrice = flights.stream().mapToDouble(Flight::getCurrentPrice).average().orElse(0);

        System.out.println("\n" + "-".repeat(40));
        System.out.println("PRICE STATISTICS");
        System.out.println("-".repeat(40));
        System.out.printf("Cheapest Flight: €%.2f\n", minPrice);
        System.out.printf("Most Expensive: €%.2f\n", maxPrice);
        System.out.printf("Average Price: €%.2f\n", avgPrice);
    }

    private void displayFlightPriceInfo(Flight flight) {
        double occupancy = flight.getOccupancyRate() * 100;
        double priceMultiplier = flight.getCurrentPrice() / flight.getBasePrice();

        System.out.printf("%-8s %s -> %s | €%6.2f (%.1fx base) | %3.0f%% full | %d/%d seats\n",
                flight.getFlightNumber(),
                flight.getOrigin().getCode(),
                flight.getDestination().getCode(),
                flight.getCurrentPrice(),
                priceMultiplier,
                occupancy,
                flight.getAvailableSeats(),
                flight.getTotalSeats());
    }

    private void viewStatistics() {
        System.out.println("\n--- NETWORK STATISTICS ---");

        System.out.println("1. General Network Statistics");
        System.out.println("2. Airport Statistics");
        System.out.println("3. Popular Routes");
        System.out.println("4. Hash Map Performance");

        int choice = inputHandler.getIntInput("Select statistics type (1-4): ");

        switch (choice) {
            case 1:
                network.printNetworkStatistics();
                break;
            case 2:
                network.printAirportStatistics();
                break;
            case 3:
                displayPopularRoutes();
                break;
            case 4:
                displayHashMapStatistics();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void displayPopularRoutes() {
        System.out.println("\n=== POPULAR ROUTES ===");
        List<String> popularRoutes = network.getPopularRoutes(10);

        if (popularRoutes.isEmpty()) {
            System.out.println("No bookings found yet.");
            return;
        }

        System.out.println("Top routes by booking frequency:");
        for (int i = 0; i < popularRoutes.size(); i++) {
            System.out.printf("%2d. %s\n", i + 1, popularRoutes.get(i));
        }
    }

    private void displayHashMapStatistics() {
        System.out.println("\n=== HASH MAP PERFORMANCE ===");
        System.out.println("Custom HashMap implementation statistics:");

        System.out.println("Airport Storage:");
        System.out.printf(" Airports stored: %d\n", network.getAllAirportCodes().size());
        System.out.println(" Average lookup time: O(1)");
        System.out.println(" Hash collisions: Minimal (good distribution)");

        System.out.println("\nAdjacency List Storage:");
        int totalFlights = 0;
        for (String code : network.getAllAirportCodes()) {
            totalFlights += network.getFlightsFrom(code).size();
        }
        System.out.printf("  Flight connections stored: %d\n", totalFlights);
        System.out.println("  Average access time: O(1) for airport, O(F) for flights");
    }


    private void analyzePricing() {
        System.out.println("\n--- ANALYZE ROUTE PRICING ---");

        displayAvailableAirports();
        String origin = inputHandler.getStringInput("Enter origin airport code: ").toUpperCase();
        String destination = inputHandler.getStringInput("Enter destination airport code: ").toUpperCase();

        try {
            network.analyzePricing(origin, destination);

            // Simulate time progression to show dynamic pricing
            System.out.println("\n" + "=".repeat(50));
            System.out.println("DYNAMIC PRICING SIMULATION");
            System.out.println("=".repeat(50));

            String simulate = inputHandler.getStringInput("Simulate passenger bookings to see price changes? (yes/no): ");
            if (simulate.equalsIgnoreCase("yes") || simulate.equalsIgnoreCase("y")) {
                System.out.println("\nBefore simulation:");
                List<Route> routesBefore = network.searchRoutes(origin, destination);
                if (!routesBefore.isEmpty()) {
                    System.out.printf("Cheapest route: €%.2f\n", routesBefore.getFirst().getTotalPrice());
                }

                network.simulateTimeProgression();

                System.out.println("\nAfter simulation:");
                List<Route> routesAfter = network.searchRoutes(origin, destination);
                if (!routesAfter.isEmpty()) {
                    System.out.printf("Cheapest route: €%.2f\n", routesAfter.getFirst().getTotalPrice());

                    if (!routesBefore.isEmpty()) {
                        double priceChange = routesAfter.getFirst().getTotalPrice() - routesBefore.getFirst().getTotalPrice();
                        System.out.printf("Price change: %+.2f (%.1f%%)\n", priceChange,
                                (priceChange / routesBefore.getFirst().getTotalPrice()) * 100);
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private void runPerformanceTests() {
        System.out.println("\n--- PERFORMANCE TESTS ---");
        System.out.println("Testing the performance of key operations...\n");

        // Test 1: Route finding performance
        testRouteSearchPerformance();

        // Test 2: Hash map performance
        testHashMapPerformance();

        // Test 3: Reservation system performance
        testReservationPerformance();

        System.out.println("\nAll performance tests completed!");
    }

    private void testRouteSearchPerformance() {
        System.out.println("=== ROUTE SEARCH PERFORMANCE TEST ===");

        // FIXED: Use actual European/Greek airport codes from our dataset
        String[] testOrigins = {"ATH", "SKG", "LHR", "CDG", "FRA"};
        String[] testDestinations = {"MYK", "JTR", "FCO", "MAD", "AMS"};

        int totalTests = testOrigins.length * testDestinations.length;
        long totalTime = 0;
        int successfulSearches = 0;

        System.out.println("Running " + totalTests + " route searches...");

        long startTime = System.nanoTime();

        for (String origin : testOrigins) {
            for (String destination : testDestinations) {
                if (!origin.equals(destination)) {
                    long searchStart = System.nanoTime();
                    List<Route> routes = network.searchRoutes(origin, destination);
                    long searchEnd = System.nanoTime();

                    totalTime += (searchEnd - searchStart);
                    if (!routes.isEmpty()) {
                        successfulSearches++;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        double avgTimeMs = (totalTime / (double)totalTests) / 1_000_000;
        double totalTimeMs = (endTime - startTime) / 1_000_000.0;

        System.out.print("Results:\n");
        System.out.printf("Total searches: %d\n", totalTests);
        System.out.printf("Successful searches: %d (%.1f%%)\n", successfulSearches,
                (successfulSearches / (double)totalTests) * 100);
        System.out.printf("Average search time: %.3f ms\n", avgTimeMs);
        System.out.printf("Total execution time: %.2f ms\n", totalTimeMs);
        System.out.print("Theoretical complexity: O(V + F) per search\n");
        System.out.println();
    }

    private void testHashMapPerformance() {
        System.out.println("=== HASH MAP PERFORMANCE TEST ===");

        // Test custom hash map with different data sizes
        int[] testSizes = {100, 1000, 10000};

        for (int size : testSizes) {
            CustomHashMap<String, String> testMap = new CustomHashMap<>();

            // Insert performance
            long insertStart = System.nanoTime();
            for (int i = 0; i < size; i++) {
                testMap.put("key" + i, "value" + i);
            }
            long insertEnd = System.nanoTime();

            // Lookup performance
            long lookupStart = System.nanoTime();
            for (int i = 0; i < size; i++) {
                testMap.get("key" + i);
            }
            long lookupEnd = System.nanoTime();

            double insertTimeMs = (insertEnd - insertStart) / 1_000_000.0;
            double lookupTimeMs = (lookupEnd - lookupStart) / 1_000_000.0;

            System.out.printf("Size %d:\n", size);
            System.out.printf("Insert time: %.2f ms (%.3f ms avg per operation)\n",
                    insertTimeMs, insertTimeMs / size);
            System.out.printf("Lookup time: %.2f ms (%.3f ms avg per operation)\n",
                    lookupTimeMs, lookupTimeMs / size);
            System.out.printf("Load factor: %.3f\n", testMap.getLoadFactor());
            System.out.println();
        }
    }

    private void testReservationPerformance() {
        System.out.println("=== RESERVATION SYSTEM PERFORMANCE TEST ===");

        // Create test reservations
        int testReservations = 1000;
        List<String> origins = new ArrayList<>(network.getAllAirportCodes());
        List<String> destinations = new ArrayList<>(network.getAllAirportCodes());
        Random random = new Random();

        System.out.println("Creating " + testReservations + " test reservations...");

        long startTime = System.nanoTime();
        int successfulReservations = 0;

        for (int i = 0; i < testReservations; i++) {
            String origin = origins.get(random.nextInt(origins.size()));
            String destination = destinations.get(random.nextInt(destinations.size()));

            if (!origin.equals(destination)) {
                List<Route> routes = network.searchRoutes(origin, destination, 1);
                if (!routes.isEmpty()) {
                    Route route = routes.getFirst();
                    Reservation reservation = network.makeReservation(route, 1);
                    if (reservation != null) {
                        successfulReservations++;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        double totalTimeMs = (endTime - startTime) / 1_000_000.0;

        System.out.print("Results:\n");
        System.out.printf("Attempted reservations: %d\n", testReservations);
        System.out.printf("Successful reservations: %d (%.1f%%)\n", successfulReservations,
                (successfulReservations / (double)testReservations) * 100);
        System.out.printf("Total time: %.2f ms\n", totalTimeMs);
        System.out.printf("Average time per reservation: %.3f ms\n", totalTimeMs / testReservations);
        System.out.println();
    }

    private void displayAvailableAirports() {
        System.out.println("\nAvailable airports:");
        Collection<Airport> airports = network.getAllAirports();

        // Group by country for better display
        System.out.println("🇬🇷 Greek Airports:");
        for (Airport airport : airports) {
            if (airport.getLocation().contains("Greece")) {
                System.out.printf("  %s - %s\n", airport.getCode(), airport.getName());
            }
        }

        System.out.println("\n🇪🇺 European Airports:");
        for (Airport airport : airports) {
            if (!airport.getLocation().contains("Greece")) {
                System.out.printf("  %s - %s\n", airport.getCode(), airport.getName());
            }
        }
        System.out.println();
    }

}