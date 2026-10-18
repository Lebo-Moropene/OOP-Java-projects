
package org.clemzito.library.service;

import org.clemzito.library.service.domain.Book;
import org.clemzito.library.service.domain.Member;
import org.clemzito.library.service.repository.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link LibraryService}, covering the happy paths and the
 * business-rule violations (duplicate ISBN, duplicate member, borrowing an
 * unavailable book, unknown member, returning without a loan).
 *
 * <p>Each test runs against a fresh, empty service thanks to
 * {@code @BeforeEach}.</p>
 */
public class LibraryServiceTest {
    private LibraryService service;

    /**
     * Builds a new {@link LibraryService} backed by empty in-memory
     * repositories before every test.
     */
    @BeforeEach
    void setUp() {
        // Instantiate LibraryService here once
        // we know its constructor parameters.
        service = new LibraryService(
                new BookRepository(),
                new MemberRepository(),
                new LoanRepository());
    }
    @Test
    @DisplayName("Should add a valid book")
    void shouldAddBook() {

        service.addBook("978-0134685991", "Effective Java", "Joshua Bloch");
        assertThat(service.findBook("978-0134685991"))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("Effective Java");


    }
    @Test
    @DisplayName("Should reject duplicate ISBN")
    void shouldRejectDuplicateIsbn() {
        service.addBook("123", "Book A", "Author");
        assertThatThrownBy(() -> service.addBook("123", "Book B", "Author"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should register a member")
    void shouldRegisterMember() {
        service.registerMember("M1", "Alice");
        assertThat(service.findMember("M1")).isPresent();
    }

    @Test
    @DisplayName("Should borrow a book successfully")
    void shouldBorrowBook(){
        service.addBook("111", "Clean Code", "Robert Martin");
        service.registerMember("M1", "Alice");

        service.borrowBook("111", "M1");

        assertThat(service.findBook("111").orElseThrow().isAvailable()).isFalse();
        assertThat(service.activeLoansForMember("M1")).hasSize(1);
    }


    @Test
    @DisplayName("Should reject borrowing a book that is already on loan")
    void shouldRejectUnknownMember() {
        service.addBook("111", "Clean Code", "Robert Martin");
        service.registerMember("M1", "Alice");
        service.registerMember("M2", "Bob");
        service.borrowBook("111", "M1");


        assertThatThrownBy(() -> service.borrowBook("111", "M2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

    @Test
    @DisplayName("Should reject borrowing for unknown member")
    void shoulRejectUknownMember(){
        service.addBook("111", "Clean Code", "Robert Martin");
        assertThatThrownBy(() -> service.borrowBook("111", "UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not found");
    }

    @Test
    @DisplayName("Should return a book")
    void shouldReturnBook() {
        service.addBook("111", "Clean Code", "Robert Martin");
        service.registerMember("M1", "Alice");
        service.borrowBook("111", "M1");


        service.returnBook("111", "M1");

        assertThat(service.findBook("111").orElseThrow().isAvailable()).isTrue();
        assertThat(service.activeLoansForMember("M1")).isEmpty();
    }

    @Test
    @DisplayName("Should reject return when no active loan")
    void shouldRejectReturnWithoutLoan() {
        service.addBook("111", "Clean Code", "Robert Martin");
        service.registerMember("M1", "Alice");

        assertThatThrownBy(() -> service.returnBook("111", "M1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No active loan");
    }
}
