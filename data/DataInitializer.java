package data;

import adt.*;
import java.time.LocalDate;
import java.util.*;

public class DataInitializer {

    public static Airport[] createSampleAirports() {
        return new Airport[] {
                new Airport("ATH", "Athens International Airport Eleftherios Venizelos", "Athens, Greece"),
                new Airport("SKG", "Thessaloniki Airport Makedonia", "Thessaloniki, Greece"),
                new Airport("HER", "Heraklion International Airport Nikos Kazantzakis", "Crete, Greece"),
                new Airport("RHO", "Rhodes International Airport Diagoras", "Rhodes, Greece"),
                new Airport("MYK", "Mykonos Airport", "Mykonos, Greece"),
                new Airport("JTR", "Santorini Airport", "Santorini, Greece"),
                new Airport("CFU", "Corfu International Airport", "Corfu, Greece"),
                new Airport("KGS", "Kos Airport", "Kos, Greece"),

                new Airport("LHR", "London Heathrow Airport", "London, United Kingdom"),
                new Airport("CDG", "Charles de Gaulle Airport", "Paris, France"),
                new Airport("FCO", "Leonardo da Vinci Airport", "Rome, Italy"),
                new Airport("MAD", "Madrid-Barajas Airport", "Madrid, Spain"),
                new Airport("FRA", "Frankfurt Airport", "Frankfurt, Germany"),
                new Airport("AMS", "Amsterdam Airport Schiphol", "Amsterdam, Netherlands"),
                new Airport("ZUR", "Zurich Airport", "Zurich, Switzerland"),
                new Airport("VIE", "Vienna International Airport", "Vienna, Austria")
        };
    }

    public static Flight[] createSampleFlights(Airport[] airports) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfter = LocalDate.now().plusDays(2);

        return new Flight[] {
                new Flight(airports[0], airports[1], 150, 80.0, tomorrow, "A3301"),   // ATH -> SKG
                new Flight(airports[1], airports[0], 150, 85.0, tomorrow, "A3302"),   // SKG -> ATH
                new Flight(airports[0], airports[2], 180, 120.0, tomorrow, "A3303"),  // ATH -> HER
                new Flight(airports[2], airports[0], 180, 115.0, tomorrow, "A3304"),  // HER -> ATH
                new Flight(airports[0], airports[3], 160, 140.0, tomorrow, "A3305"),  // ATH -> RHO
                new Flight(airports[3], airports[0], 160, 135.0, tomorrow, "A3306"),  // RHO -> ATH
                new Flight(airports[0], airports[4], 100, 95.0, tomorrow, "A3307"),   // ATH -> MYK
                new Flight(airports[4], airports[0], 100, 90.0, tomorrow, "A3308"),   // MYK -> ATH
                new Flight(airports[0], airports[5], 120, 110.0, tomorrow, "A3309"),  // ATH -> JTR
                new Flight(airports[5], airports[0], 120, 105.0, tomorrow, "A3310"),  // JTR -> ATH
                new Flight(airports[0], airports[6], 140, 100.0, tomorrow, "A3311"),  // ATH -> CFU
                new Flight(airports[6], airports[0], 140, 95.0, tomorrow, "A3312"),   // CFU -> ATH
                new Flight(airports[0], airports[7], 130, 125.0, tomorrow, "A3313"),  // ATH -> KGS
                new Flight(airports[7], airports[0], 130, 120.0, tomorrow, "A3314"),  // KGS -> ATH
                new Flight(airports[0], airports[8], 200, 280.0, tomorrow, "A3401"),  // ATH -> LHR
                new Flight(airports[8], airports[0], 200, 290.0, tomorrow, "BA2801"), // LHR -> ATH
                new Flight(airports[0], airports[9], 180, 260.0, tomorrow, "A3402"),  // ATH -> CDG
                new Flight(airports[9], airports[0], 180, 270.0, tomorrow, "AF1832"), // CDG -> ATH
                new Flight(airports[0], airports[10], 190, 220.0, tomorrow, "A3403"), // ATH -> FCO
                new Flight(airports[10], airports[0], 190, 230.0, tomorrow, "AZ714"), // FCO -> ATH
                new Flight(airports[0], airports[11], 170, 250.0, tomorrow, "A3404"), // ATH -> MAD
                new Flight(airports[11], airports[0], 170, 240.0, tomorrow, "IB3123"),// MAD -> ATH
                new Flight(airports[0], airports[12], 160, 300.0, tomorrow, "A3405"), // ATH -> FRA
                new Flight(airports[12], airports[0], 160, 310.0, tomorrow, "LH1266"),// FRA -> ATH
                new Flight(airports[0], airports[13], 150, 280.0, tomorrow, "A3406"), // ATH -> AMS
                new Flight(airports[13], airports[0], 150, 285.0, tomorrow, "KL1573"),// AMS -> ATH
                new Flight(airports[1], airports[8], 180, 320.0, tomorrow, "A3501"),  // SKG -> LHR
                new Flight(airports[8], airports[1], 180, 330.0, tomorrow, "BA2802"), // LHR -> SKG
                new Flight(airports[1], airports[12], 140, 280.0, tomorrow, "A3502"), // SKG -> FRA
                new Flight(airports[12], airports[1], 140, 290.0, tomorrow, "LH1267"),// FRA -> SKG
                new Flight(airports[1], airports[14], 120, 200.0, tomorrow, "A3503"), // SKG -> ZUR
                new Flight(airports[14], airports[1], 120, 210.0, tomorrow, "LX8392"),// ZUR -> SKG
                new Flight(airports[1], airports[15], 130, 220.0, tomorrow, "A3504"), // SKG -> VIE
                new Flight(airports[15], airports[1], 130, 215.0, tomorrow, "OS542"), // VIE -> SKG
                new Flight(airports[2], airports[8], 200, 350.0, tomorrow, "A3601"),  // HER -> LHR
                new Flight(airports[8], airports[2], 200, 360.0, tomorrow, "BA2803"), // LHR -> HER
                new Flight(airports[3], airports[12], 160, 320.0, tomorrow, "A3602"), // RHO -> FRA
                new Flight(airports[12], airports[3], 160, 315.0, tomorrow, "LH1268"),// FRA -> RHO
                new Flight(airports[4], airports[9], 100, 380.0, tomorrow, "A3603"),  // MYK -> CDG
                new Flight(airports[9], airports[4], 100, 390.0, tomorrow, "AF1833"), // CDG -> MYK
                new Flight(airports[5], airports[10], 120, 340.0, tomorrow, "A3604"), // JTR -> FCO
                new Flight(airports[10], airports[5], 120, 335.0, tomorrow, "AZ715"), // FCO -> JTR
                new Flight(airports[8], airports[9], 300, 180.0, tomorrow, "BA301"),   // LHR -> CDG
                new Flight(airports[9], airports[8], 300, 185.0, tomorrow, "AF1234"), // CDG -> LHR
                new Flight(airports[12], airports[13], 250, 120.0, tomorrow, "LH401"), // FRA -> AMS
                new Flight(airports[13], airports[12], 250, 125.0, tomorrow, "KL1234"),// AMS -> FRA
                new Flight(airports[11], airports[10], 200, 150.0, tomorrow, "IB501"), // MAD -> FCO
                new Flight(airports[10], airports[11], 200, 155.0, tomorrow, "AZ601"), // FCO -> MAD
                new Flight(airports[14], airports[15], 180, 140.0, tomorrow, "LX701"), // ZUR -> VIE
                new Flight(airports[15], airports[14], 180, 145.0, tomorrow, "OS701"), // VIE -> ZUR
                new Flight(airports[8], airports[4], 150, 420.0, tomorrow, "A3701"),  // LHR -> MYK (charter)
                new Flight(airports[9], airports[5], 160, 400.0, tomorrow, "A3702"),  // CDG -> JTR (charter)
                new Flight(airports[12], airports[6], 170, 380.0, tomorrow, "A3703"), // FRA -> CFU (charter)
                new Flight(airports[13], airports[7], 140, 360.0, tomorrow, "A3704"), // AMS -> KGS (charter)
                new Flight(airports[0], airports[8], 200, 300.0, dayAfter, "A3801"),  // ATH -> LHR
                new Flight(airports[1], airports[12], 140, 290.0, dayAfter, "A3802"), // SKG -> FRA
                new Flight(airports[0], airports[9], 180, 280.0, dayAfter, "A3803"),  // ATH -> CDG
                new Flight(airports[2], airports[10], 190, 370.0, dayAfter, "A3804"), // HER -> FCO
        };
    }

    public static void simulateInitialBookings(FlightNetwork network) {
        Random random = new Random();
        Set<String> codes = network.getAllAirportCodes();

        for (String originCode : codes) {
            List<Flight> flights = network.getFlightsFrom(originCode);
            for (Flight flight : flights) {
                int maxBookings = (int)(flight.getTotalSeats() * 0.7);
                int seatsToBook = random.nextInt(maxBookings + 1);
                flight.bookSeats(seatsToBook);
            }
        }
    }
}