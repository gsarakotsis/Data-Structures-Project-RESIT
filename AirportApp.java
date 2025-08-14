import adt.*;
import data.DataInitializer;
import testing.PerformanceTester;
import ui.MenuHandler;
import ui.InputHandler;
import analytics.StatisticsManager;
import java.util.Scanner;

public class AirportApp {
    private final FlightNetwork network;
    private final Scanner scanner;
    private final MenuHandler menuHandler;
    private final InputHandler inputHandler;
    private final StatisticsManager statisticsManager;
    private final PerformanceTester performanceTester;

    public AirportApp() {
        network = new FlightNetwork();
        scanner = new Scanner(System.in);
        inputHandler = new InputHandler(scanner);
        menuHandler = new MenuHandler(network, inputHandler);
        statisticsManager = new StatisticsManager(network);
        performanceTester = new PerformanceTester(network);

        initializeSampleData();
    }

    private void initializeSampleData() {
        Airport[] airports = DataInitializer.createSampleAirports();
        network.addAirports(airports);

        Flight[] flights = DataInitializer.createSampleFlights(airports);
        network.addFlights(flights);

        DataInitializer.simulateInitialBookings(network);

        System.out.println("Sample data initialized successfully!");
    }

    public void run() {
        while (true) {
            menuHandler.displayMenu();
            int choice = inputHandler.getIntInput("Enter your choice (1-9): ");

            try {
                if (choice == 6) {
                    statisticsManager.displayStatistics(0);
                    int statChoice = inputHandler.getIntInput("Select statistics type (1-4): ");
                    statisticsManager.displayStatistics(statChoice);
                } else if (choice == 8) {
                    performanceTester.runAllTests();
                } else if (choice == 9) {
                    System.out.println("Thank you for using the Flight Pricing System!");
                    return;
                } else {
                    menuHandler.handleMenuChoice(choice);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            inputHandler.getStringInput("");
        }
    }

    public static void main(String[] args) {
        try {
            AirportApp app = new AirportApp();
            app.run();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
        }
    }
}