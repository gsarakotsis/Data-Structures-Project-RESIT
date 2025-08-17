package datastructures;

import adt.Airport;
import adt.Flight;
import adt.Route;

import java.util.*;


public class FlightGraph {
    private CustomHashMap<String, Airport> airports;
    private CustomHashMap<String, List<Flight>> adjacencyList;
    private int totalFlights;


    public FlightGraph() {
        airports = new CustomHashMap<>();
        adjacencyList = new CustomHashMap<>();
        totalFlights = 0;
    }


    public void addAirport(Airport airport) {
        if (airport == null) {
            throw new IllegalArgumentException("Airport cannot be null");
        }

        String code = airport.getCode();
        if (!airports.containsKey(code)) {
            airports.put(code, airport);
            adjacencyList.put(code, new ArrayList<>());
        }
    }

    public void addFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }

        String originCode = flight.getOrigin().getCode();
        String destCode = flight.getDestination().getCode();

        // Ensure airports exist in the graph
        addAirport(flight.getOrigin());
        addAirport(flight.getDestination());

        // Add flight to origin's adjacency list
        List<Flight> originFlights = adjacencyList.get(originCode);
        originFlights.add(flight);
        totalFlights++;
    }


    public Flight findDirectFlight(String originCode, String destCode) {
        List<Flight> flights = adjacencyList.get(originCode);
        if (flights == null) return null;

        return flights.stream()
                .filter(f -> f.getDestination().getCode().equals(destCode) && f.hasAvailableSeats())
                .min(Comparator.comparingDouble(Flight::getCurrentPrice))
                .orElse(null);
    }


    public List<Flight> findAllDirectFlights(String originCode, String destCode) {
        List<Flight> flights = adjacencyList.get(originCode);
        if (flights == null) return new ArrayList<>();

        return flights.stream()
                .filter(f -> f.getDestination().getCode().equals(destCode) && f.hasAvailableSeats())
                .sorted(Comparator.comparingDouble(Flight::getCurrentPrice))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Route> findRoutes(String originCode, String destCode) {
        if (originCode == null || destCode == null) {
            throw new IllegalArgumentException("Airport codes cannot be null");
        }

        List<Route> routes = new ArrayList<>();

        // Find direct flights
        List<Flight> directFlights = findAllDirectFlights(originCode, destCode);
        for (Flight directFlight : directFlights) {
            Route directRoute = new Route(Arrays.asList(directFlight));
            if (directRoute.isValidRoute()) {
                routes.add(directRoute);
            }
        }

        // Find one-stop routes
        routes.addAll(findOneStopRoutes(originCode, destCode));

        // Sort routes by total price
        routes.sort(Route.priceComparator());
        return routes;
    }


    private List<Route> findOneStopRoutes(String originCode, String destCode) {
        List<Route> oneStopRoutes = new ArrayList<>();
        List<Flight> originFlights = adjacencyList.get(originCode);

        if (originFlights != null) {
            for (Flight firstFlight : originFlights) {
                if (!firstFlight.hasAvailableSeats()) continue;

                String intermediateCode = firstFlight.getDestination().getCode();
                if (intermediateCode.equals(destCode)) continue; // Skip direct routes

                // Find connecting flights from intermediate airport
                List<Flight> connectingFlights = findAllDirectFlights(intermediateCode, destCode);
                for (Flight secondFlight : connectingFlights) {
                    try {
                        Route oneStopRoute = new Route(Arrays.asList(firstFlight, secondFlight));
                        if (oneStopRoute.isValidRoute()) {
                            oneStopRoutes.add(oneStopRoute);
                        }
                    } catch (Exception e) {
                        // Skip invalid routes
                        continue;
                    }
                }
            }
        }

        return oneStopRoutes;
    }

    public List<Route> findRoutesWithMaxStops(String originCode, String destCode, int maxStops) {
        if (maxStops < 0) {
            throw new IllegalArgumentException("Max stops cannot be negative");
        }

        if (maxStops == 0) {
            // Only direct flights
            List<Route> routes = new ArrayList<>();
            List<Flight> directFlights = findAllDirectFlights(originCode, destCode);
            for (Flight flight : directFlights) {
                routes.add(new Route(Arrays.asList(flight)));
            }
            return routes;
        } else if (maxStops == 1) {
            return findRoutes(originCode, destCode);
        } else {
            return findRoutes(originCode, destCode); // Limit to 1-stop for simplicity
        }
    }


    public Airport getAirport(String code) {
        return airports.get(code);
    }


    public List<Flight> getFlightsFrom(String airportCode) {
        List<Flight> flights = adjacencyList.get(airportCode);
        return flights != null ? new ArrayList<>(flights) : new ArrayList<>();
    }


    public List<Flight> getFlightsTo(String airportCode) {
        List<Flight> incomingFlights = new ArrayList<>();

        for (String originCode : getAllAirportCodes()) {
            List<Flight> flights = adjacencyList.get(originCode);
            if (flights != null) {
                for (Flight flight : flights) {
                    if (flight.getDestination().getCode().equals(airportCode)) {
                        incomingFlights.add(flight);
                    }
                }
            }
        }

        return incomingFlights;
    }


    public Set<String> getAllAirportCodes() {
        return airports.keySet();
    }

    public Collection<Airport> getAllAirports() {
        return airports.values();
    }


    public boolean hasAirport(String code) {
        return airports.containsKey(code);
    }

    public boolean removeAirport(String code) {
        if (!hasAirport(code)) {
            return false;
        }

        // Remove outgoing flights
        List<Flight> outgoingFlights = adjacencyList.get(code);
        if (outgoingFlights != null) {
            totalFlights -= outgoingFlights.size();
        }

        // Remove incoming flights
        for (String originCode : getAllAirportCodes()) {
            if (!originCode.equals(code)) {
                List<Flight> flights = adjacencyList.get(originCode);
                if (flights != null) {
                    int originalSize = flights.size();
                    flights.removeIf(flight -> flight.getDestination().getCode().equals(code));
                    totalFlights -= (originalSize - flights.size());
                }
            }
        }

        // Remove airport
        airports.remove(code);
        adjacencyList.remove(code);

        return true;
    }


    public String getGraphStatistics() {
        int airportCount = airports.size();
        int totalSeats = 0;
        int availableSeats = 0;
        double totalRevenue = 0.0;

        for (String code : getAllAirportCodes()) {
            List<Flight> flights = getFlightsFrom(code);
            for (Flight flight : flights) {
                totalSeats += flight.getTotalSeats();
                availableSeats += flight.getAvailableSeats();
                totalRevenue += flight.getCurrentPrice() * (flight.getTotalSeats() - flight.getAvailableSeats());
            }
        }

        double occupancyRate = totalSeats > 0 ? (double)(totalSeats - availableSeats) / totalSeats * 100 : 0;

        return String.format("=== Flight Network Statistics ===\n" +
                        "Airports: %d\n" +
                        "Total Flights: %d\n" +
                        "Total Seats: %d\n" +
                        "Available Seats: %d\n" +
                        "Occupancy Rate: %.1f%%\n" +
                        "Estimated Revenue: $%.2f\n" +
                        "Average Flights per Airport: %.1f",
                airportCount, totalFlights, totalSeats, availableSeats,
                occupancyRate, totalRevenue,
                airportCount > 0 ? (double)totalFlights / airportCount : 0);
    }


    public List<Airport> getHubAirports(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }

        return getAllAirports().stream()
                .sorted((a1, a2) -> Integer.compare(
                        getFlightsFrom(a2.getCode()).size(),
                        getFlightsFrom(a1.getCode()).size()))
                .limit(limit)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

 
    public boolean validateGraph() {
        for (String originCode : getAllAirportCodes()) {
            List<Flight> flights = getFlightsFrom(originCode);
            for (Flight flight : flights) {
                if (!hasAirport(flight.getOrigin().getCode()) ||
                        !hasAirport(flight.getDestination().getCode())) {
                    return false;
                }
            }
        }
        return true;
    }


    public List<String> getShortestPath(String originCode, String destCode) {
        if (!hasAirport(originCode) || !hasAirport(destCode)) {
            return new ArrayList<>();
        }

        if (originCode.equals(destCode)) {
            return Arrays.asList(originCode);
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.offer(originCode);
        visited.add(originCode);
        parent.put(originCode, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(destCode)) {
                // Reconstruct path
                List<String> path = new ArrayList<>();
                String node = destCode;
                while (node != null) {
                    path.add(0, node);
                    node = parent.get(node);
                }
                return path;
            }

            List<Flight> flights = getFlightsFrom(current);
            for (Flight flight : flights) {
                String neighbor = flight.getDestination().getCode();
                if (!visited.contains(neighbor) && flight.hasAvailableSeats()) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return new ArrayList<>(); // No path found
    }
}