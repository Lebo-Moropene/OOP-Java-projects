package org.clemzito.library.service;

import org.clemzito.library.service.domain.*;
import org.clemzito.library.service.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;




/**
 * Application service that coordinates the library's business rules.
 *
 * <p>It sits between the entry points (CLI {@link Main}, tests) and the
 * in-memory repositories, validating input, enforcing invariants
 * (unique ISBNs, unique member IDs, one borrower per available copy) and
 * keeping {@link Book} availability in sync with {@link Loan} records.</p>
 *
 * <p>State is held entirely in memory: every call against a fresh
 * {@code LibraryService} starts from an empty library.</p>
 */
public class LibraryService {
    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;

    /**
     * Creates a service backed by the given repositories.
     *
     * @param bookRepo   storage for {@link Book} entities
     * @param memberRepo storage for {@link Member} entities
     * @param loanRepo   storage for {@link Loan} entities
     */
    public LibraryService(BookRepository bookRepo, MemberRepository memberRepo, LoanRepository loanRepo){
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.loanRepo = loanRepo;
    }

    /**
     * Adds a new, initially available book to the catalogue.
     *
     * @param isbn   unique ISBN of the book
     * @param title  book title
     * @param author book author
     * @throws IllegalStateException if a book with the same ISBN already exists
     * @throws IllegalArgumentException if {@code isbn}, {@code title} or {@code author} is blank
     */
    public void addBook(String isbn, String title, String author){
        if (bookRepo.existsByIsbn(isbn))
            throw new IllegalStateException("Book with ISBN " + isbn + " already exists");
        bookRepo.save(new Book(isbn, title, author));
    }

    /**
     * Looks up a book by ISBN.
     *
     * @param isbn ISBN to search for
     * @return the book if present, otherwise an empty {@link Optional}
     */
    public Optional<Book> findBook(String isbn){return bookRepo.findByIsbn(isbn);}

    /**
     * Returns every book in the catalogue, borrowed or not.
     *
     * @return all stored books in insertion order
     */
    public List<Book> allBooks(){return bookRepo.findAll();}

    /**
     * Returns the books that are currently on the shelf.
     *
     * @return books whose {@link Book#isAvailable()} flag is {@code true}
     */
    public List<Book> availableBooks(){return bookRepo.findAvailable(); }

    /**
     * Registers a new library member.
     *
     * @param memberId unique member identifier
     * @param name     display name of the member
     * @throws IllegalStateException if the member ID is already registered
     * @throws IllegalArgumentException if {@code memberId} or {@code name} is blank
     */
    public void registerMember(String memberId, String name){
        if (memberRepo.existsById(memberId))
            throw new IllegalStateException("Member " + memberId + " already exists");
        memberRepo.save(new Member(memberId, name));

    }

    /**
     * Looks up a member by ID.
     *
     * @param id member ID to search for
     * @return the member if present, otherwise an empty {@link Optional}
     */
    public Optional<Member> findMember(String id){ return memberRepo.findById(id);}

    /**
     * Lends an available book to a registered member.
     *
     * <p>On success the book is flagged as borrowed and an open
     * {@link Loan} dated with today's date is recorded.</p>
     *
     * @param isbn     ISBN of the book to borrow
     * @param memberId ID of the borrowing member
     * @throws IllegalArgumentException if the book or the member does not exist
     * @throws IllegalStateException    if the book is already on loan
     */
    public void borrowBook(String isbn, String memberId){
        Book book = bookRepo.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + isbn));
        if (memberRepo.findById(memberId).isEmpty())
            throw new IllegalArgumentException("Member not found: " + memberId);
        if (!book.isAvailable())
            throw new IllegalStateException("Book " + isbn + " is not available");

        book.markBorrowed();
        loanRepo.save(new Loan(isbn, memberId, LocalDate.now()));
    }

    /**
     * Ends the member's active loan for the given book.
     *
     * <p>The oldest open loan held by that member is closed with today's
     * date and the book is flagged as available again.</p>
     *
     * @param isbn     ISBN of the book being returned
     * @param memberId ID of the member returning the book
     * @throws IllegalStateException if the member has no active loan for that ISBN
     * @throws NoSuchElementException if the book is no longer in the catalogue
     */
    public void returnBook(String isbn, String memberId){
        List<Loan> active = loanRepo.findActiveByIsbn(isbn).stream()
                .filter(loan -> loan.getMemberId().equals(memberId))
                .toList();
        if (active.isEmpty())
            throw new IllegalStateException("No active loan for ISBN " + isbn + " by " + memberId);

        active.get(0).markReturned(LocalDate.now());
        bookRepo.findByIsbn(isbn).orElseThrow().markReturned();
    }
    /**
     * Lists all books currently on loan to a member.
     *
     * @param memberId member whose loans are requested
     * @return the member's open (not yet returned) loans
     */
    public List<Loan> activeLoansForMember(String memberId){
        return loanRepo.findActiveByMember(memberId);
    }

}
