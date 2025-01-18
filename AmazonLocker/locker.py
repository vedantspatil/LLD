from abc import ABC, abstractmethod
from enum import Enum

class Size(Enum):
    SMALL = 1
    MEDIUM = 2
    LARGE = 3

class User:
    def __init__(self, id, name, code):
        self.id = id
        self.name = name
        self.code = code

    def validate_code(self, code):
        return self.code == code

class DeliveryUser(User):
    pass

class EndUser(User):
    pass

class Package:
    def __init__(self, id, size, user):
        self.id = id
        self.size = size
        self.user = user

class Locker(ABC):
    def __init__(self, id, size):
        self.id = id
        self.size = size
        self.is_available = True
        self.package = None
        self.code = None

    def assign_package(self, package):
        if self.is_available:
            self.is_available = False
            self.package = package
            self.code = package.user.code

    def empty_locker(self):
        self.is_available = True
        self.package = None
        self.code = None

    def validate_code(self, code):
        return self.code == code

    def open_locker(self):
        self.empty_locker()

    @abstractmethod
    def can_fit(self, package):
        pass

class SmallLocker(Locker):
    def __init__(self, id):
        super().__init__(id, Size.SMALL)

    def can_fit(self, package):
        return package.size == Size.SMALL

class MediumLocker(Locker):
    def __init__(self, id):
        super().__init__(id, Size.MEDIUM)

    def can_fit(self, package):
        return package.size in [Size.MEDIUM, Size.SMALL]

class LargeLocker(Locker):
    def __init__(self, id):
        super().__init__(id, Size.LARGE)

    def can_fit(self, package):
        return package.size in [Size.LARGE, Size.MEDIUM, Size.SMALL]

class LockerSystem:
    def __init__(self):
        self.lockers = []
        self.user_packages = {}
        self.package_location = {}

    def add_locker(self, locker):
        self.lockers.append(locker)

    def _find_locker(self, package):
        currLocker = None
        for locker in self.lockers:
            if locker.is_available and locker.can_fit(package):
                if not currLocker or locker.size <= currLocker.size:
                    currLocker = locker
        return currLocker

    def assign_locker(self, user, package):
        if isinstance(user, DeliveryUser):
            locker = self._find_locker(package)
            if locker:
                locker.assign_package(package)
                self.package_location[package.id] = locker
                self.user_packages[package.user.id] = package
                return f"Package Assigned to Locker {locker.id}"
            return "No Locker Found for the Package"
        return "Only valid Delivery Agents can deliver packages"

    def pick_up_package(self, user):
        if isinstance(user, EndUser) and user.id in self.user_packages:
            package = self.user_packages[user.id]
            locker = self.package_location[package.id]
            if locker.validate_code(user.code):
                locker.open_locker()
                del self.user_packages[user.id]
                del self.package_location[package.id]
                return "Package Picked Up"
            return "Invalid Code"
        return "No Package for User"
