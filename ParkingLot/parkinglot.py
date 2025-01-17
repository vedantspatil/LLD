from abs import ABC, abstractmethod
from enum import Enum

class VehicleEnum(Enum):
    MOTORCYCLE = 1
    CAR = 2
    SUV = 3

class ParkingSpaceEnum(Enum):
    SMALL = 1
    COMPACT = 2
    LARGE = 3

class Vehicle(ABC):
    def __init__(self, licensePlate, typeOfVehicle):
        self.licensePlate = licensePlate
        self.type = typeOfVehicle
    
class MotorCycleVehicle(Vehicle):
    def __init__(self, licensePlate):
        super().__init__(licensePlate,VehicleEnum.MOTORCYCLE)

class CarVehicle(Vehicle):
    def __init__(self, licensePlate):
        super().__init__(licensePlate,VehicleEnum.CAR)

class SUVVehicle(Vehicle):
    def __init__(self, licensePlate):
        super().__init__(licensePlate,VehicleEnum.SUV)
    

class ParkingSpace(ABC):
    def __init__(self, id):
        self.id = id
        self.isAvailable = True
        self.vehicle = None
    
    @abstractmethod
    def canFitVehicle(self, vehicle):
        pass

    def assignSpace(self, vehicle):
        self.isAvailable = False
        self.vehicle = vehicle
    
    def emptySpace(self)
        self.isAvailable = True
        self.vehicle = None
    
class SmallParkingSpot(ParkingSpace):
    def __init__(self, id):
        super().__init__(id)
    
    def canFitVehicle(self, vehicle):
        return vehicle.type in [ParkingSpaceEnum.SMALL]

class CompactParkingSpot(ParkingSpace):
    def __init__(self, id):
        super().__init__(id)
    
    def canFitVehicle(self, vehicle):
        return vehicle.type in [ParkingSpaceEnum.SMALL, ParkingSpaceEnum.COMPACT]
    

class LargeParkingSpot(ParkingSpace):
    def __init__(self, id):
        super().__init__(id)
    
    def canFitVehicle(self, vehicle):
        return vehicle.type in [ParkingSpaceEnum.SMALL, ParkingSpaceEnum.COMPACT, ParkingSpaceEnum.LARGE]

class Floor:
    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.parkingSpaces = []

    def addSpace(self, parkingSpace):
        self.parkingSpace.append(parkingSpace)

    def findParkingSpace(self, vehicle):
        for parking in self.parkingSpaces:
            if parking.isAvailable and parking.canFitVehicle(vehicle):
                return parking
        return None

class Ticket:
    def __init__(self, id, vehicle, spot):
        self.id = id
        self.vehicle = vehicle
        self.spot = spot
        self.timestamp = datetime.now()
        self.parkingFare = 10

    def calculateFare(self):
        return (datetime.now() - self.timestamp).hours * self.parkingFare
    
        

class ParkingLot:
    def __init__(self, name):
        self.name = name
        self.floors = []
        self.tickets = {}

    def addfloors(self, floor):
        self.floor.add(floor)

    def createTicket(self, vehicle, spot):
        self.tickets[vehicle.license] = Ticket(len(tickets)+1, vehicle, spot)

    def assignParkingSpace(self, vehicle):
        for floor in self.floors:
            parkingSpot = floor.findParkingSpace(vehicle):
            if parkingSpot:
                self.createTicket(vehcile, parkingSpot)
                parkingSpot.assignSpace(vehicle)
                return "Parking space {parkingSpot.id} assigned to {vehicle.license}"
        return "Cannot find parkingSpace"
    
    def processExit(self, ticket):
        vehicle = ticket.vehicle
        parkingSpot = ticket.spot
        parkingSpot.emptySpace()
        fare = ticket.calculateFare()
        print("Fare for {vehicle.license} is {fare}")
        del self.tickets[vehicle.license]

