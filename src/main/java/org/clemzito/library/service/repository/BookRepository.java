package org.clemzito.library.service.repository;

import org.clemzito.library.service.domain.Book;
import java.util.*;


/**
 * In-memory store for {@link Book} entities, keyed by ISBN.
 *
 * <p>Nothing is persisted: the catalogue lives only for the lifetime of
 * this object. Backed by a {@link HashMap}, so iteration order is not
 * guaranteed.</p>
 */
public class BookRepository {
    private final Map<String, Book> books = new HashMap<>();

    /** Creates an empty catalogue. */
    public BookRepository() {}

    /**
     * Inserts or replaces a book.
     *
     * @param book the book to store, indexed by its ISBN
     */
    public void save(Book book) {
        books.put(book.getIsbn(), book);}

    /**
     * Finds a book by ISBN.
     *
     * @param isbn ISBN to look up
     * @return the book if present, otherwise empty
     */
    public Optional<Book> findByIsbn(String isbn){
        return Optional.ofNullable(books.get(isbn));
    }

    /**
     * Returns all stored books.
     *
     * @return a mutable copy of every book in the catalogue
     */
    public List<Book> findAll(){
        return new ArrayList<>(books.values());
    }

    /**
     * Returns the books that are currently available.
     *
     * @return an unmodifiable list of available books
     */
    public List<Book>findAvailable(){
        return books.values().stream().filter(Book::isAvailable).toList();
    }

    /**
     * Checks whether a book with the given ISBN exists.
     *
     * @param isbn ISBN to test
     * @return {@code true} if the catalogue contains the ISBN
     */
    public boolean existsByIsbn(String isbn){
        return books.containsKey(isbn);
    }
}
