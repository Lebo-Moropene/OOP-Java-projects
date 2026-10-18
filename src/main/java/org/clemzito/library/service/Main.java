package org.clemzito.library.service;

import org.clemzito.library.service.domain.Loan;
import org.clemzito.library.service.repository.BookRepository;
import org.clemzito.library.service.repository.LoanRepository;
import org.clemzito.library.service.repository.MemberRepository;

import java.util.Scanner;

/**
 * Entry point of the Library Management System.
 *
 * <p>Runs a small text menu on {@code System.in} and forwards every user
 * action to a {@link LibraryService}. Domain errors raised by the service
 * are caught per iteration and printed without terminating the loop.</p>
 *
 * <p>Menu options: 1 add book, 2 register member, 3 borrow, 4 return,
 * 5 list books, 6 active loans for a member, 0 exit.</p>
 */
public class Main {

    /** Utility class: only static entry points, never instantiated. */
    private Main() {}

    /**
     * Starts the interactive CLI session.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        LibraryService service = new LibraryService(
                new BookRepository(),
                new MemberRepository(),
                new LoanRepository());

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> addBook(scanner, service);
                        case "2" -> registerMember(scanner, service);
                        case "3" -> borrow(scanner, service);
                        case "4" -> returnBook(scanner, service);
                        case "5" -> listBooks(service);
                        case "6" -> listActiveLoans(scanner, service);
                        case "0" -> running = false;
                        default -> System.out.println("Invalid option.");
                    }
                } catch (RuntimeException e) {
                    System.out.println("Error: " + e.getMessage());
                }

            }
        }
        System.out.println("Goodbye");
    }

    /** Prints the numbered menu of available actions. */
    private static void printMenu() {
        System.out.println("""
                \n== Library Management ==
                1. Add book
                2. Register member
                3. Borrow book
                4. Return book
                5. List books
                6. Active loans for member
                0. Exit
                Choose an option: """);
    }

    /**
     * Reads ISBN, title and author from the console and adds the book.
     *
     * @param scanner source of user input
     * @param service service performing the registration
     */
    private static void addBook(Scanner scanner, LibraryService service) {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        service.addBook(isbn, title, author);
        System.out.println("Book added.");
    }

    /**
     * Reads a member ID and name from the console and registers the member.
     *
     * @param scanner source of user input
     * @param service service performing the registration
     */
    private static void registerMember(Scanner scanner, LibraryService service) {
        System.out.print("Member ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        service.registerMember(id, name);
        System.out.println("Member registered.");
    }

    /**
     * Reads an ISBN and member ID from the console and borrows the book.
     *
     * @param scanner source of user input
     * @param service service performing the loan
     */
    private static void borrow(Scanner scanner, LibraryService service) {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        service.borrowBook(isbn, memberId);
        System.out.println("Book borrowed.");
    }

    /**
     * Reads an ISBN and member ID from the console and returns the book.
     *
     * @param scanner source of user input
     * @param service service ending the loan
     */
    private static void returnBook(Scanner scanner, LibraryService service) {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        service.returnBook(isbn, memberId);
        System.out.println("Book returned.");
    }

    /**
     * Prints every book in the catalogue, or a notice when it is empty.
     *
     * @param service service exposing the catalogue
     */
    private static void listBooks(LibraryService service) {
        var books = service.allBooks();
        if (books.isEmpty()) {
            System.out.println("No books.");
            return;
        }
        books.forEach(System.out::println);
    }

    /**
     * Prints the member's currently open loans.
     *
     * @param scanner source of user input
     * @param service service exposing the loan history
     */
    private static void listActiveLoans(Scanner scanner, LibraryService service) {
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        var loans = service.activeLoansForMember(memberId);
        if (loans.isEmpty()) {
            System.out.println("No active loans.");
            return;
        }
        for (Loan loan : loans) {
            System.out.printf("%s borrowed %s on %s%n",
                    loan.getMemberId(), loan.getIsbn(), loan.getBorrowedAt());
        }
    }
}
