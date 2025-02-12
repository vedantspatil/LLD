package LibraryManagementSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryExample {

    // ----------------- Book Class -----------------
    public static class Book {
        private int id;
        private String name;
        private String title;
        private boolean isBorrowed;

        public Book(int id, String name, String title) {
            this.id = id;
            this.name = name;
            this.title = title;
            this.isBorrowed = false;
        }

        // Check availability
        public boolean isAvailable() {
            return !isBorrowed;
        }

        // Borrow the book
        public void borrowBook() {
            this.isBorrowed = true;
        }

        // Return the book
        public void returnBook() {
            this.isBorrowed = false;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getTitle() { return title; }
    }

    // ----------------- User Class -----------------
    public static class User {
        private int id;
        private String name;
        private String email;
        // Borrowed books keyed by book ID
        private Map<Integer, Book> borrowedBooks;

        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.borrowedBooks = new HashMap<>();
        }

        public void borrowBook(Book book) {
            borrowedBooks.put(book.getId(), book);
            book.borrowBook();
        }

        public void returnBook(Book book) {
            if (borrowedBooks.containsKey(book.getId())) {
                borrowedBooks.remove(book.getId());
                book.returnBook();
            }
        }

        public boolean hasBook(Book book) {
            return borrowedBooks.containsKey(book.getId());
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    // ----------------- LibrarySystem Class -----------------
    public static class LibrarySystem {
        // For simplicity, store by user ID / book ID
        private Map<Integer, User> users;
        private Map<Integer, Book> books;
        // Reservations: book ID -> list of users who reserved it
        private Map<Integer, List<User>> reservation;
        // Due dates: book ID -> date when it must be returned
        private Map<Integer, LocalDate> dueDates;
        // Fine per day
        private int finePerDay;

        public LibrarySystem() {
            this.users = new HashMap<>();
            this.books = new HashMap<>();
            this.reservation = new HashMap<>();
            this.dueDates = new HashMap<>();
            this.finePerDay = 10; // example default
        }

        // Basic user/book methods:
        public void addUser(User user) {
            users.put(user.getId(), user);
        }

        public void removeUser(int userId) {
            users.remove(userId);
        }

        public void addBook(Book book) {
            books.put(book.getId(), book);
        }

        public void removeBook(int bookId) {
            books.remove(bookId);
        }

        // Lend a book if available
        public boolean lendBook(User user, Book book) {
            if (users.containsKey(user.getId()) && books.containsKey(book.getId()) && book.isAvailable()) {
                user.borrowBook(book);
                return true;
            }
            return false;
        }

        // Helper method that also updates the due date
        public void lendBookHelper(User user, Book book, int days) {
            if (lendBook(user, book)) {
                updateDueDate(user, book, days);
                System.out.println("Book " + book.getName() + " assigned to " + user.getName());
            } else {
                System.out.println("Book unavailable");
            }
        }

        // Return a book if the user actually has it
        public boolean returnBook(User user, Book book) {
            if (users.containsKey(user.getId()) && books.containsKey(book.getId()) && user.hasBook(book)) {
                user.returnBook(book);
                return true;
            }
            return false;
        }

        // Helper method that also checks fines
        public void returnBookHelper(User user, Book book) {
            if (returnBook(user, book)) {
                calculateDues(user, book);
                System.out.println("User " + user.getName() + " returned Book " + book.getName());
            } else {
                System.out.println("Cannot accept a book that does not belong to a user");
            }
        }

        // Reserve a book that is currently borrowed
        public void addReservation(User user, Book book) {
            if (users.containsKey(user.getId()) && books.containsKey(book.getId())) {
                if (book.isAvailable()) {
                    System.out.println("No need to reserve, Book is available");
                } else {
                    // Add user to reservation list for that book
                    reservation.putIfAbsent(book.getId(), new ArrayList<>());
                    reservation.get(book.getId()).add(user);
                    System.out.println("Book reserved by " + user.getName());
                }
            }
        }

        // Update the due date for a borrowed book
        public void updateDueDate(User user, Book book, int days) {
            LocalDate dueDate = LocalDate.now().plusDays(days);
            dueDates.put(book.getId(), dueDate);
        }

        // Calculate dues if user is late
        public void calculateDues(User user, Book book) {
            LocalDate today = LocalDate.now();
            LocalDate dueDate = dueDates.get(book.getId());
            if (dueDate != null && today.isAfter(dueDate)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
                long fine = finePerDay * daysLate;
                System.out.println("You are past your due date. Pay fine " + fine);
            }
        }

        // Extend borrowing period if no one else is waiting
        public void extendBook(User user, Book book, int days) {
            List<User> waitList = reservation.get(book.getId());
            if (waitList != null && !waitList.isEmpty()) {
                System.out.println("Cannot be extended due to pending reservations");
                return;
            }
            updateDueDate(user, book, days);
        }
    }

    // ----------------- Main Example (Optional) -----------------
    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem();

        // Create sample users and books
        User u1 = new User(1, "Alice", "alice@example.com");
        User u2 = new User(2, "Bob", "bob@example.com");

        Book b1 = new Book(101, "Harry Potter", "Fantasy Novel");
        Book b2 = new Book(102, "The Hobbit", "Adventure Novel");

        // Add them to the library
        library.addUser(u1);
        library.addUser(u2);
        library.addBook(b1);
        library.addBook(b2);

        // Borrow and return
        library.lendBookHelper(u1, b1, 7);    // Lend b1 to Alice for 7 days
        library.returnBookHelper(u1, b1);     // Return b1, possibly paying a fine if late

        // Reserve a book
        library.lendBookHelper(u2, b2, 5);    // Bob borrows b2
        library.addReservation(u1, b2);       // Alice tries to reserve b2
    }
}
