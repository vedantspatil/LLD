from abc import ABC, abstractmethod

class Pizza(ABC):
    @abstractmethod
    def get_description(self) -> str:
        pass

    @abstractmethod
    def get_price(self) -> float:
        pass


class BasicPizza(Pizza):
    def get_description(self) -> str:
        return "Basic Pizza"

    def get_price(self) -> float:
        return 5.00


class PizzaDecorator(Pizza, ABC):
    def __init__(self, pizza: Pizza):
        self._pizza = pizza


class BaseAddon(PizzaDecorator, ABC):
    pass


class ThinCrust(BaseAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + ", Thin Crust"

    def get_price(self) -> float:
        return self._pizza.get_price() + 2.00


class PanCrust(BaseAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + ", Pan Crust"

    def get_price(self) -> float:
        return self._pizza.get_price() + 2.50


class CheeseBurst(BaseAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + ", Cheese Burst Crust"

    def get_price(self) -> float:
        return self._pizza.get_price() + 3.00


class SizeAddon(PizzaDecorator, ABC):
    pass


class SmallSize(SizeAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + " (Small)"

    def get_price(self) -> float:
        return self._pizza.get_price() + 0.00 


class MediumSize(SizeAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + " (Medium)"

    def get_price(self) -> float:
        return self._pizza.get_price() + 2.00


class LargeSize(SizeAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + " (Large)"

    def get_price(self) -> float:
        return self._pizza.get_price() + 4.00


class ToppingAddon(PizzaDecorator, ABC):
    pass


class Pepperoni(ToppingAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + ", Pepperoni"

    def get_price(self) -> float:
        return self._pizza.get_price() + 1.50


class Mushroom(ToppingAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + ", Mushroom"

    def get_price(self) -> float:
        return self._pizza.get_price() + 1.00


class Olive(ToppingAddon):
    def get_description(self) -> str:
        return self._pizza.get_description() + ", Olive"

    def get_price(self) -> float:
        return self._pizza.get_price() + 1.25

if __name__ == "__main__":
    
    my_pizza = BasicPizza()

    my_pizza = CheeseBurst(my_pizza)

    my_pizza = LargeSize(my_pizza)

    # Add some toppings
    my_pizza = Pepperoni(my_pizza)
    my_pizza = Mushroom(my_pizza)

    # Print final pizza description and total price
    print("Pizza Description:", my_pizza.get_description())
    print("Total Price: $", my_pizza.get_price())
