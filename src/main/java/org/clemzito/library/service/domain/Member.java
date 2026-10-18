package org.clemzito.library.service.domain;


import java.util.Objects;

/**
 * A person (or account) allowed to borrow books.
 *
 * <p>The {@code memberId} is immutable and serves as the identity of the
 * member; it must be unique within the library.</p>
 */
public class Member {

    private final String memberId;
    private final String name;


    /**
     * Creates a member.
     *
     * @param memberId unique member identifier
     * @param name     display name
     * @throws IllegalArgumentException if either argument is {@code null} or blank
     */
    public Member(String memberId, String name) {
        if(memberId == null || memberId.isBlank())
            throw new IllegalArgumentException("Member ID cannot be blank");
        if(name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank");
        this.memberId = memberId;
        this.name = name;
    }

    /**
     * Returns the member's identifier.
     *
     * @return the unique member ID
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Returns the member's name.
     *
     * @return the member's display name
     */
    public String getName() {
        return name;
    }


    /** {@inheritDoc} Equality is based solely on the member ID. */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Member member)) return false;
        return memberId.equals(member.memberId);

    }

    /** {@inheritDoc} Hash code derived from the member ID to match {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    /** {@inheritDoc} Renders as {@code name (memberId)}. */
    @Override
    public String toString(){
        return String.format("%s (%s)", name, memberId);
    }
}
