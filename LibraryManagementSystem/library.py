from datetime import datetime
class Book:
    def __init__(self, id, name, title):
        self.id = id
        self.name = name
        self.title = title
        self.isBorrowed = False
    
    def isAvailable(self):
        return self.isBorrowed

    def borrowBook(self):
        self.isBorrowed = True

    def returnBook(self):
        self.isBorrowed = False

class User:
    def __init__(self, id, name, email):
        self.id = id
        self.name = name
        self.email = email
        self.borrowedBooks = defaultdict(Book)

    def borrowBook(self, book):
        self.borrowedBooks[book.id] = book
        book.borrowBook()

    def returnBook(self, book):
        if book.id in self.borrowedBooks:
            del self.borrowedBooks[book.id]
            book.returnBook()
    
    def hasBook(self, book):
        return book.id in self.borrowedBooks

class LibrarySystem:
    def __init__(self):
        self.users = defaultdict(User)
        self.books = defaultdict(Book)
        self.reservation = defaultdict(list)
        self.dueDates = defaultdict(datetime)
        self.finePerDay = 10

    def addUser(self):
        #Add user to the map
    
    def removeUser(self):
        #Remove user from the map
    
    def addBook(self):
        #Add book to the map
    
    def removeBook(self):
        #remove book from the map

    def lendBook(self, user, book):
        if user.id in self.users and book.id in self.books and book.isAvailable():
            user.borrowBook(book)
            return True
        return False
    
    def lendBookHelper(self, user, book, days):
        if self.lendBook(user, book):
            self.updateDueDate(user, book, days)
            print("Book {0} assigned to {1}".format(book.name, user.name))
            return
        print("Book unavailable")

    def returnBook(self, user, book):
        if user.id in self.users and book.id in self.books and user.hasBook(book):
            user.returnBook(book)
            return True
        return False

    def returnBookHelper(self, user, book):
        if self.returnBook(user, book):
            self.calculateDues(user, book)
            print("User {0} returned Book {1}".format(user.name, book.name))
            return
        print("Cannot accept a book that does not belong to a user")
    
    def addReservation(self, user, book):
        if user.id in self.users and book.id in self.books:
            if book.isAvailable():
                print("No need to reserve, Book is available")
                return 
            else:
                self.reservation[book.id].append(user)
                print("Book reserved")
                return
    
    def updateDueDate(self, user, book, days):
        dueDate = datetime.now().plusDays(days).date
        self.dueDates[book.id] = dueDate
    
    def calculateDues(self, user, book):
        today = datetime.now().datetime.now().date
        if self.dueDate[book.id] < today:
            fine = self.finePerDay * (today-self.dueDate[book.id])
            print("You are past your due date. Pay fine {0}".format(fine))
        return

    def extendBook(self, user, book, days):
        if len(self.reservation[book.id]) > 0:
            print("Cannot be extended due to pending reservations")
            return
        self.updateDueDate(user, book, days)

#Add reservation
#Calculate Dues
#Extend Book