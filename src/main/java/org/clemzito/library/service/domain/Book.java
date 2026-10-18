package org.clemzito.library.service.domain;

import java.util.Objects;

/**
 * A catalogue entry for a single physical book.
 *
 * <p>Identity is the {@code isbn}; two books with the same ISBN are
 * considered equal. A book starts out available and flips between the
 * available/borrowed states through {@link #markBorrowed()} and
 * {@link #markReturned()}.</p>
 */
public class Book {
    private final String isbn;
    private String title;
    private String author;
    private boolean available;

    /**
     * Creates an available book.
     *
     * @param isbn   unique ISBN
     * @param title  title of the book
     * @param author author of the book
     * @throws IllegalArgumentException if any argument is {@code null} or blank
     */
    public Book(String isbn, String title, String author){
        if(isbn == null || isbn.isBlank())
            throw new IllegalArgumentException("ISBN cannot be blank");
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be blank");
        if(author == null || author.isBlank())
            throw new IllegalArgumentException("Author cannot be blank");

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    /**
     * Returns the ISBN of the book.
     *
     * @return the ISBN identifying this book
     */
    public String getIsbn(){
        return isbn;
    }

    /**
     * Returns the title of the book.
     *
     * @return the book's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the author of the book.
     *
     * @return the book's author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Reports the shelf status of the book.
     *
     * @return {@code true} when the book is on the shelf
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Updates the title.
     *
     * @param title new title
     * @throws IllegalArgumentException if the title is {@code null} or blank
     */
    public void setTitle(String title) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be blank");
        this.title = title;
    }

    /**
     * Updates the author.
     *
     * @param author new author
     * @throws IllegalArgumentException if the author is {@code null} or blank
     */
    public void setAuthor(String author) {
        if(author == null || author.isBlank())
            throw new IllegalArgumentException("Author cannot be blank");
        this.author = author;
    }

    /**
     * Marks the book as taken out of the library.
     *
     * @throws IllegalArgumentException if the book is already borrowed
     */
    public void markBorrowed() {
        if (!available)
            throw new IllegalArgumentException("Book already borrowed");
        this.available = false; }

    /**
     * Marks the book as back on the shelf.
     *
     * @throws IllegalArgumentException if the book is not currently borrowed
     */
    public void markReturned(){
        if(available)
            throw new IllegalArgumentException("Book is not borrowed");
        this.available = true;
    }


    /** {@inheritDoc} Equality is based solely on the ISBN. */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Book book)) return false;
        return isbn.equals(book.isbn);
    }

    /** {@inheritDoc} Hash code derived from the ISBN to match {@link #equals(Object)}. */
    @Override
    public int hashCode(){
        return Objects.hash(isbn);
    }

    /**
     * {@inheritDoc} Renders as {@code [isbn] title by author (AVAILABLE|BORROWED)}.
     */
    @Override
    public String toString(){
        return String.format("[%s] %s by %s (%s)",
                isbn, title, author, available ? "AVAILABLE" : "BORROWED");
    }
}
