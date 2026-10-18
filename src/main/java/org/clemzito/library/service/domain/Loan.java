package org.clemzito.library.service.domain;

import java.time.LocalDate;

/**
 * A single borrowing event: which member took which book and when.
 *
 * <p>A loan is <em>active</em> while {@code returnedAt} is {@code null}
 * and becomes closed once {@link #markReturned(LocalDate)} is called.
 * The library keeps the full loan history, so returned loans are retained
 * rather than deleted.</p>
 */
public class Loan {

    private final String isbn;
    private final String memberId;
    private final LocalDate borrowedAt;
    private LocalDate returnedAt;

    /**
     * Creates an open loan.
     *
     * @param isbn       ISBN of the borrowed book
     * @param memberId   ID of the borrowing member
     * @param borrowedAt date the book was taken out
     */
    public Loan(String isbn, String memberId, LocalDate borrowedAt) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowedAt = borrowedAt;
    }

    /**
     * Returns the ISBN of the borrowed book.
     *
     * @return the ISBN of the borrowed book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns the borrowing member's identifier.
     *
     * @return the ID of the member who borrowed the book
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Returns the date the book left the library.
     *
     * @return the date the book was borrowed
     */
    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    /**
     * Returns the date the book came back.
     *
     * @return the date the book was returned, or {@code null} while the loan is open
     */
    public LocalDate getReturnedAt() {
        return returnedAt;
    }

    /**
     * Tells whether the loan is still open.
     *
     * @return {@code true} if the book has not been returned yet
     */
    public boolean isActive() {
        return returnedAt == null;
    }

    /**
     * Closes the loan.
     *
     * <p>Only the service layer changes loan state, so callers cannot
     * accidentally forge returns while building the model.</p>
     *
     * @param date return date to record
     */
    public void markReturned(LocalDate date){
        this.returnedAt = date;
    }

}
