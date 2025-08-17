package adt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class Reservation {
    private static int nextId = 1000;

    private int reservationId;
    private Route route;
    private LocalDate bookingDate;
    private LocalDateTime bookingTime;
    private int passengerCount;
    private double totalCost;
    private ReservationStatus status;
    private String customerEmail;
    private List<String> passengerNames;


    public enum ReservationStatus {
        PENDING("Pending Confirmation"),
        CONFIRMED("Confirmed"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");

        private final String description;

        ReservationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    public Reservation(Route route, int passengerCount) {
        if (route == null || passengerCount <= 0) {
            throw new IllegalArgumentException("Invalid route or passenger count");
        }

        this.reservationId = nextId++;
        this.route = route;
        this.passengerCount = passengerCount;
        this.bookingDate = LocalDate.now();
        this.bookingTime = LocalDateTime.now();
        this.totalCost = route.getTotalPrice() * passengerCount;
        this.status = ReservationStatus.PENDING;
        this.customerEmail = "";
        this.passengerNames = new ArrayList<>();
    }

    public Reservation(Route route, int passengerCount, String customerEmail, List<String> passengerNames) {
        this(route, passengerCount);
        this.customerEmail = customerEmail != null ? customerEmail : "";
        this.passengerNames = passengerNames != null ? new ArrayList<>(passengerNames) : new ArrayList<>();
    }

    public boolean confirm() {
        if (status != ReservationStatus.PENDING) {
            return false;
        }

        // Check if route still has availability
        if (!route.hasAvailability(passengerCount)) {
            return false;
        }

        // Attempt to book the route
        if (route.bookRoute(passengerCount)) {
            status = ReservationStatus.CONFIRMED;
            // Recalculate cost due to potential dynamic pricing changes
            this.totalCost = route.getTotalPrice() * passengerCount;
            return true;
        }
        return false;
    }


    public boolean cancel() {
        if (status == ReservationStatus.CANCELLED || status == ReservationStatus.COMPLETED) {
            return false;
        }

        if (status == ReservationStatus.CONFIRMED) {
            // Release seats back to flights
            route.cancelRoute(passengerCount);
        }

        status = ReservationStatus.CANCELLED;
        return true;
    }

    public boolean complete() {
        if (status != ReservationStatus.CONFIRMED) {
            return false;
        }

        status = ReservationStatus.COMPLETED;
        return true;
    }

    public boolean canModify() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }

    public boolean updatePassengerCount(int newPassengerCount) {
        if (newPassengerCount <= 0 || !canModify()) {
            return false;
        }

        if (status == ReservationStatus.CONFIRMED) {
            // Check if we can accommodate the change
            int difference = newPassengerCount - this.passengerCount;

            if (difference > 0) {
                // Need more seats
                if (!route.hasAvailability(difference)) {
                    return false;
                }
                // Book additional seats
                if (!route.bookRoute(difference)) {
                    return false;
                }
            } else if (difference < 0) {
                // Release excess seats
                route.cancelRoute(-difference);
            }
        }

        this.passengerCount = newPassengerCount;
        this.totalCost = route.getTotalPrice() * passengerCount;
        return true;
    }

    public boolean addPassengerName(String passengerName) {
        if (passengerName == null || passengerName.trim().isEmpty()) {
            return false;
        }

        if (passengerNames.size() < passengerCount) {
            passengerNames.add(passengerName.trim());
            return true;
        }
        return false;
    }


    public String getReservationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Reservation Summary ===\n");
        summary.append(String.format("Reservation ID: #%d\n", reservationId));
        summary.append(String.format("Status: %s\n", status.getDescription()));
        summary.append(String.format("Booking Date: %s\n", bookingDate));
        summary.append(String.format("Passengers: %d\n", passengerCount));
        summary.append(String.format("Total Cost: $%.2f\n\n", totalCost));

        summary.append("Route Details:\n");
        summary.append(route.toDetailedString());

        if (!customerEmail.isEmpty()) {
            summary.append(String.format("\nCustomer Email: %s\n", customerEmail));
        }

        if (!passengerNames.isEmpty()) {
            summary.append("\nPassengers:\n");
            for (int i = 0; i < passengerNames.size(); i++) {
                summary.append(String.format("  %d. %s\n", i + 1, passengerNames.get(i)));
            }
        }

        return summary.toString();
    }


    public long getDaysUntilDeparture() {
        if (route.getFlights().isEmpty()) {
            return -1;
        }

        LocalDate departureDate = route.getFlights().getFirst().getFlightDate();
        return LocalDate.now().until(departureDate).getDays();
    }


    public boolean isRefundable() {
        return getDaysUntilDeparture() >= 1 && status != ReservationStatus.CANCELLED;
    }

    public double getRefundAmount() {
        if (!isRefundable() || status == ReservationStatus.CANCELLED) {
            return 0.0;
        }

        long daysUntilDeparture = getDaysUntilDeparture();

        if (daysUntilDeparture >= 7) {
            return totalCost; // Full refund
        } else if (daysUntilDeparture >= 3) {
            return totalCost * 0.8; // 80% refund
        } else if (daysUntilDeparture >= 1) {
            return totalCost * 0.5; // 50% refund
        } else {
            return 0.0; // No refund
        }
    }


    public boolean validate() {
        if (route == null || !route.isValidRoute()) {
            return false;
        }

        if (passengerCount <= 0) {
            return false;
        }

        if (totalCost < 0) {
            return false;
        }

        return passengerNames.size() <= passengerCount;
    }

    // Getter methods
    public int getReservationId() {
        return reservationId;
    }

    public Route getRoute() {
        return route;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<String> getPassengerNames() {
        return new ArrayList<>(passengerNames);
    }

    public void setCustomerEmail(String email) {
        this.customerEmail = email != null ? email : "";
    }

    /**
     * Basic string representation of the reservation
     */
    @Override
    public String toString() {
        return String.format("Reservation #%d (%s) - %d passengers - $%.2f - %s",
                reservationId, bookingDate, passengerCount, totalCost, status.getDescription());
    }

    /**
     * Detailed string representation
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Reservation #%d\n", reservationId));
        sb.append(String.format("  Status: %s\n", status.getDescription()));
        sb.append(String.format("  Booked: %s at %s\n", bookingDate,
                bookingTime.toLocalTime().toString()));
        sb.append(String.format("  Passengers: %d\n", passengerCount));
        sb.append(String.format("  Total Cost: $%.2f\n", totalCost));
        sb.append(String.format("  Days until departure: %d\n", getDaysUntilDeparture()));

        if (isRefundable()) {
            sb.append(String.format("  Refund available: $%.2f\n", getRefundAmount()));
        }

        sb.append("  Route: ").append(route.getOrigin().getCode())
                .append(" -> ").append(route.getDestination().getCode())
                .append(" (").append(route.isDirect() ? "Direct" : "1-Stop").append(")\n");

        return sb.toString();
    }

    /**
     * Comparator for sorting reservations by booking date
     */
    public static Comparator<Reservation> bookingDateComparator() {
        return Comparator.comparing(Reservation::getBookingDate)
                .thenComparing(Reservation::getBookingTime);
    }

    /**
     * Comparator for sorting reservations by departure date
     */
    public static Comparator<Reservation> departureDateComparator() {
        return (r1, r2) -> {
            LocalDate date1 = r1.getRoute().getFlights().getFirst().getFlightDate();
            LocalDate date2 = r2.getRoute().getFlights().getFirst().getFlightDate();
            return date1.compareTo(date2);
        };
    }

    /**
     * Comparator for sorting reservations by total cost
     */
    public static Comparator<Reservation> costComparator() {
        return Comparator.comparingDouble(Reservation::getTotalCost).reversed();
    }
}