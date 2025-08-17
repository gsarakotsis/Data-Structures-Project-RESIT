package testing;

import adt.*;
import datastructures.CustomHashMap;
import java.util.*;

public class PerformanceTester {
    private final FlightNetwork network;

    public PerformanceTester(FlightNetwork network) {
        this.network = network;
    }

    public void runAllTests() {
        System.out.println("\n--- PERFORMANCE TESTS ---");
        System.out.println("Testing the performance of key operations...\n");

        testRouteSearchPerformance();
        testHashMapPerformance();
        testReservationPerformance();

        System.out.println("\nAll performance tests completed!");
    }

    private void testRouteSearchPerformance() {
        System.out.println("=== ROUTE SEARCH PERFORMANCE TEST ===");

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
}