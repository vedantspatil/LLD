package ParkingLot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// --- Enums for Vehicle Types ---
enum VehicleType { SMALL, MEDIUM, LARGE; }

// --- Abstract Vehicle and Concrete Vehicles ---
abstract class Vehicle {
    private String licensePlate;
    private VehicleType type;
    
    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public VehicleType getType() {
        return type;
    }
}

class SmallVehicle extends Vehicle {
    public SmallVehicle(String licensePlate) {
        super(licensePlate, VehicleType.SMALL);
    }
}

class MediumVehicle extends Vehicle {
    public MediumVehicle(String licensePlate) {
        super(licensePlate, VehicleType.MEDIUM);
    }
}

class LargeVehicle extends Vehicle {
    public LargeVehicle(String licensePlate) {
        super(licensePlate, VehicleType.LARGE);
    }
}

// --- Abstract Parking Space and Concrete Implementations ---
abstract class ParkingSpace {
    protected String id;
    protected boolean isAvailable;
    protected Vehicle parkedVehicle;
    
    public ParkingSpace(String id) {
        this.id = id;
        this.isAvailable = true;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void assignVehicle(Vehicle v) {
        parkedVehicle = v;
        isAvailable = false;
    }
    
    public void freeSpace() {
        parkedVehicle = null;
        isAvailable = true;
    }
    
    // Returns true if the parking space can accommodate the given vehicle.
    public abstract boolean canFitVehicle(Vehicle v);
}

class SmallParkingSpot extends ParkingSpace {
    public SmallParkingSpot(String id) {
        super(id);
    }
    
    @Override
    public boolean canFitVehicle(Vehicle v) {
        return isAvailable() && v.getType() == VehicleType.SMALL;
    }
}

class MediumParkingSpot extends ParkingSpace {
    public MediumParkingSpot(String id) {
        super(id);
    }
    
    @Override
    public boolean canFitVehicle(Vehicle v) {
        return isAvailable() && (v.getType() == VehicleType.SMALL || v.getType() == VehicleType.MEDIUM);
    }
}

class LargeParkingSpot extends ParkingSpace {
    public LargeParkingSpot(String id) {
        super(id);
    }
    
    @Override
    public boolean canFitVehicle(Vehicle v) {
        // A large spot can accommodate any vehicle.
        return isAvailable();
    }
}

// --- Floor: A collection of ParkingSpaces ---
class Floor {
    private String id;
    private List<ParkingSpace> parkingSpaces;
    
    public Floor(String id) {
        this.id = id;
        this.parkingSpaces = new ArrayList<>();
    }
    
    public void addParkingSpace(ParkingSpace space) {
        parkingSpaces.add(space);
    }
    
    // Returns the first available spot that can fit the vehicle.
    public ParkingSpace findParkingSpace(Vehicle v) {
        for (ParkingSpace space : parkingSpaces) {
            if (space.canFitVehicle(v)) {
                return space;
            }
        }
        return null;
    }
}

// --- Ticket: A record for a parked vehicle ---
class Ticket {
    private int id;
    private Vehicle vehicle;
    private ParkingSpace space;
    private Date timestamp;
    
    public Ticket(int id, Vehicle vehicle, ParkingSpace space) {
        this.id = id;
        this.vehicle = vehicle;
        this.space = space;
        this.timestamp = new Date();
    }
    
    public int getId() {
        return id;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public ParkingSpace getSpace() {
        return space;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
}

// --- ParkingLot: Aggregates Floors and Manages Parking ---
class ParkingLot {
    private String name;
    private List<Floor> floors;
    private Map<String, Ticket> activeTickets; // Keyed by vehicle license plate.
    private int ticketCounter;
    
    public ParkingLot(String name) {
        this.name = name;
        this.floors = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.ticketCounter = 0;
    }
    
    public void addFloor(Floor floor) {
        floors.add(floor);
    }
    
    // Assigns a parking space for the vehicle, creates a ticket, and returns it.
    public Ticket assignParkingSpace(Vehicle vehicle) {
        for (Floor floor : floors) {
            ParkingSpace space = floor.findParkingSpace(vehicle);
            if (space != null) {
                space.assignVehicle(vehicle);
                Ticket ticket = new Ticket(++ticketCounter, vehicle, space);
                activeTickets.put(vehicle.getLicensePlate(), ticket);
                return ticket;
            }
        }
        return null;
    }
    
    // Processes vehicle exit by freeing the space and removing the ticket.
    public boolean processExit(String licensePlate) {
        Ticket ticket = activeTickets.get(licensePlate);
        if (ticket != null) {
            ticket.getSpace().freeSpace();
            activeTickets.remove(licensePlate);
            return true;
        }
        return false;
    }
}

// --- Example Usage ---
public class ParkingGarageDemo {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot("Main Parking Lot");
        
        // Create a floor and add parking spots.
        Floor floor1 = new Floor("F1");
        floor1.addParkingSpace(new SmallParkingSpot("S1"));   // Only SMALL vehicles.
        floor1.addParkingSpace(new MediumParkingSpot("M1"));  // SMALL and MEDIUM.
        floor1.addParkingSpace(new LargeParkingSpot("L1"));   // SMALL, MEDIUM, LARGE.
        lot.addFloor(floor1);
        
        // Create some vehicles.
        Vehicle smallCar = new SmallVehicle("AAA111");
        Vehicle mediumCar = new MediumVehicle("BBB222");
        Vehicle largeCar = new LargeVehicle("CCC333");
        
        // Assign parking spaces.
        Ticket t1 = lot.assignParkingSpace(smallCar);
        System.out.println("Small car parked at: " + (t1 != null ? t1.getSpace().getId() : "No spot available"));
        
        Ticket t2 = lot.assignParkingSpace(mediumCar);
        System.out.println("Medium car parked at: " + (t2 != null ? t2.getSpace().getId() : "No spot available"));
        
        Ticket t3 = lot.assignParkingSpace(largeCar);
        System.out.println("Large car parked at: " + (t3 != null ? t3.getSpace().getId() : "No spot available"));
        
        // Process exit for one vehicle.
        boolean exited = lot.processExit("AAA111");
        System.out.println("Exit processed for AAA111: " + exited);
    }
}
