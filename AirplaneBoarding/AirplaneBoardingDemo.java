package AirplaneBoarding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ------------------------- DOMAIN CLASSES -------------------------
class Seat {
    private String seatId;        // e.g., "12A"
    private boolean isOccupied;

    public Seat(String seatId) {
        this.seatId = seatId;
        this.isOccupied = false;
    }

    public String getSeatId() {
        return seatId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void occupySeat() {
        this.isOccupied = true;
    }

    public void freeSeat() {
        this.isOccupied = false;
    }
}

class Passenger {
    private String name;
    private String boardingPass;  // e.g., flight number + seat info
    private String seatId;        // The seat assigned to them (if any)

    public Passenger(String name, String boardingPass) {
        this.name = name;
        this.boardingPass = boardingPass;
    }

    public String getName() {
        return name;
    }

    public String getBoardingPass() {
        return boardingPass;
    }

    public String getSeatId() {
        return seatId;
    }

    public void assignSeatId(String seatId) {
        this.seatId = seatId;
    }
}

class Flight {
    private String flightNumber;
    private Map<String, Seat> seats; // seatId -> Seat

    public Flight(String flightNumber) {
        this.flightNumber = flightNumber;
        seats = new HashMap<>();
        // For simplicity, let's say we have 30 seats: Rows 1-5, seats A-F
        for (int row = 1; row <= 5; row++) {
            for (char col = 'A'; col <= 'F'; col++) {
                String seatId = row + String.valueOf(col); // e.g., "1A"
                seats.put(seatId, new Seat(seatId));
            }
        }
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Seat getSeat(String seatId) {
        return seats.get(seatId);
    }

    public List<Seat> getAllSeats() {
        return new ArrayList<>(seats.values());
    }
}

// ------------------------- BOARDING & DISEMBARKING -------------------------
class BoardingSystem {
    // In a real scenario, we'd integrate with a gate scanner or reservation system
    // For simplicity, store passengers in a list
    private Flight flight;
    private List<Passenger> passengers;

    public BoardingSystem(Flight flight) {
        this.flight = flight;
        this.passengers = new ArrayList<>();
    }

    // Register passenger, possibly assigned a seat
    public void addPassenger(Passenger passenger, String seatId) {
        Seat seat = flight.getSeat(seatId);
        if (seat != null && !seat.isOccupied()) {
            passenger.assignSeatId(seatId);
            seat.occupySeat();
            passengers.add(passenger);
            System.out.println("Assigned seat " + seatId + " to " + passenger.getName());
        } else {
            System.out.println("Seat " + seatId + " is unavailable.");
        }
    }

    // A simpler approach: passengers can board as long as seat is assigned
    public void boardPassenger(Passenger passenger) {
        if (!passengers.contains(passenger)) {
            System.out.println("Passenger not registered in this flight!");
            return;
        }
        if (passenger.getSeatId() == null) {
            System.out.println("Passenger has no seat assignment!");
        } else {
            System.out.println("Passenger " + passenger.getName() + " boarded, seat " + passenger.getSeatId());
        }
    }

    public void disembarkAll() {
        System.out.println("Disembarking flight " + flight.getFlightNumber() + " in row order...");
        // Typically from front to back or back to front; let's say front to back
        for (int row = 1; row <= 5; row++) {
            for (char col = 'A'; col <= 'F'; col++) {
                String seatId = row + String.valueOf(col);
                Seat seat = flight.getSeat(seatId);
                if (seat != null && seat.isOccupied()) {
                    // find which passenger is in that seat
                    Passenger occupant = findPassengerBySeat(seatId);
                    if (occupant != null) {
                        System.out.println("Passenger " + occupant.getName() + " leaves seat " + seatId);
                    }
                    seat.freeSeat();
                }
            }
        }
    }

    private Passenger findPassengerBySeat(String seatId) {
        for (Passenger p : passengers) {
            if (seatId.equals(p.getSeatId())) {
                return p;
            }
        }
        return null;
    }
}

public class AirplaneBoardingDemo {
    public static void main(String[] args) {
        Flight flight = new Flight("AB123");
        BoardingSystem boardingSystem = new BoardingSystem(flight);

        Passenger p1 = new Passenger("Alice", "AB123-Seat2A");
        Passenger p2 = new Passenger("Bob", "AB123-Seat2B");
        Passenger p3 = new Passenger("Charlie", "AB123-Seat3C");

        // Assign seats
        boardingSystem.addPassenger(p1, "2A"); // seat for Alice
        boardingSystem.addPassenger(p2, "2B"); // seat for Bob
        boardingSystem.addPassenger(p3, "3C"); // seat for Charlie

        // Board them (in any order)
        boardingSystem.boardPassenger(p1);
        boardingSystem.boardPassenger(p2);
        boardingSystem.boardPassenger(p3);

        // After flight lands:
        boardingSystem.disembarkAll();
    }
}

