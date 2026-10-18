package org.clemzito.library.service.repository;

import org.clemzito.library.service.domain.Loan;
import java.util.*;



/**
 * In-memory store for {@link Loan} records.
 *
 * <p>Loans are append-only: returns update the existing record in place
 * rather than removing it, preserving the full borrowing history.</p>
 */
public class LoanRepository {
    private final List<Loan> loans = new ArrayList<>();

    /** Creates an empty loan history. */
    public LoanRepository() {}

    /**
     * Appends a loan to the history.
     *
     * @param loan the loan record to store
     */
    public void save(Loan loan){
        loans.add(loan);
    }

    /**
     * Returns the open loans for a given ISBN.
     *
     * @param isbn ISBN whose loans are requested
     * @return an unmodifiable list of active loans for that book
     */
    public List<Loan> findActiveByIsbn(String isbn){
        return loans.stream()
                .filter(loan -> loan.getIsbn().equals(isbn) && loan.isActive())
                .toList();
    }

    /**
     * Returns the open loans held by a member.
     *
     * @param memberId member whose loans are requested
     * @return an unmodifiable list of that member's active loans
     */
    public List<Loan> findActiveByMember(String memberId){
        return loans.stream()
                .filter(loan -> loan.getMemberId().equals(memberId) && loan.isActive())
                .toList();
    }

    /**
     * Returns every loan that has not been returned yet.
     *
     * @return an unmodifiable list of all active loans across the library
     */
    public List<Loan> findAllActive(){
        return loans.stream().filter(Loan::isActive).toList();
    }

}
