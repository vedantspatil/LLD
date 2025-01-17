from abc import ABC, abstractmethod
from enum import Enum

class Size(Enum):
    SMALL = 1
    MEDIUM = 2
    LARGE = 3

class User(ABC):
    def __init__(self, id, name, code):
        self.id = id
        self.name = name
        self.code = code
    
    def validateCode(self, code):
        return self.code == code

class DeliveryUser(User):
    def __init__(self, id, name, code):
        super().__init__(id, name, code)

class EndUser(User):
    def __init__(self, id, name, code):
        super().__init__(id, name, code)
    

class Package(ABC):
    def __init__(self, id, size, user):
        self.id = id
        self.size = size
        self.user = user

class LargePackage(Package):
    def __init__(self, id, user):
        super().__init__(id, Size.LARGE, user)
    
class MediumPackage(Package):
    def __init__(self, id, user):
        super().__init__(id, Size.MEDIUM, user)
        
class SmallPackage(Package):
    def __init__(self, id, user):
        super().__init__(id, Size.SMALL, user)


class Locker(ABC):
    def __init__(self, id, size):
        self.id = id
        self.code = None
        self.size = size
        self.isAvailable = True
        self.package = None
    
    def assignPackage(self, package):
        if self.isAvailable:
            self.isAvailable = False
            self.package = package
            self._assignCode(self.package.user.code)
    
    def emptyLocker(self):
        self.isAvailable = True
        self.package = None
        self._assignCode(None)
    
    def validateCode(self, code):
        return code == self.code

    def _assignCode(self, code):
        self.code = code

    def open(self):
        return self.emptyLocker()
    
    @abstractmethod
    def canFit(self, package):
        pass

class SmallLocker(Locker):
    def __init__(self, id):
        super().__init__(id, Size.SMALL)

    def canFit(self, package):
        return package.size == Size.SMALL

class MediumLocker(Locker):
    def __init__(self, id):
        super().__init__(id, Size.MEDIUM)

    def canFit(self, package):
        return package.size in [Size.MEDIUM, Size.SMALL]

class LargeLocker(Locker):
    def __init__(self, id):
        super().__init__(id, Size.LARGE)

    def canFit(self, package):
        return package.size == Size.LARGE


class LockerSystem:
    def __init__(self):
        self.lockers = []
        self.userPackages = {}
        self.packageLocation = {}

    def addLockers(self, locker):
        self.lockers.append(locker)
    
    def _findLocker(self, package):
        currLocker = None
        for locker in self.lockers:
            if locker.isAvailable and locker.canFit(package):
                if not currLocker or locker.size <= currLocker.size:
                    currLocker = locker
        return currLocker

    def assignLocker(self, user, package):
        if isinstance(user, DeliveryUser):
            locker = self._findLocker(package)
            if locker:
                locker.assignPackage(package)
                self.packageLocation[package.id] = locker
                self.userPackages[package.user.id] = package
                return "Package Assigned to Locker {0}".format(locker.id)
            else:
                return "No Locker Found for the Package"
        return "Only valid Delivery Agents can deliver packages"


    def pickUpPackage(self, user):
        if isinstance(user, EndUser) and user.id in self.userPackages:
            package = self.userPackages[user.id]
            locker = self.packageLocation[package.id]
            if locker.validateCode(user.code):
                locker.open()
                del self.userPackages[user.id]
                del self.packageLocation[package.id]
                return "Package Picked Up"
            else:
                return "Invalid Code"
        return "No Package for User"