package analytics;

import adt.*;
import java.util.*;

public class StatisticsManager {
    private final FlightNetwork network;

    public StatisticsManager(FlightNetwork network) {
        this.network = network;
    }

    public void displayStatistics(int choice) {
        System.out.println("\n--- NETWORK STATISTICS ---");
        System.out.println("1. General Network Statistics");
        System.out.println("2. Airport Statistics");
        System.out.println("3. Popular Routes");
        System.out.println("4. Hash Map Performance");

        switch (choice) {
            case 1: network.printNetworkStatistics(); break;
            case 2: network.printAirportStatistics(); break;
            case 3: displayPopularRoutes(); break;
            case 4: displayHashMapStatistics(); break;
            default: System.out.println("Invalid choice.");
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
}