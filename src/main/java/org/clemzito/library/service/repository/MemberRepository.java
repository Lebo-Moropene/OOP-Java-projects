package org.clemzito.library.service.repository;

import org.clemzito.library.service.domain.Member;

import java.util.*;




/**
 * In-memory store for {@link Member} entities, keyed by member ID.
 *
 * <p>Data is not persisted between runs.</p>
 */
public class MemberRepository {

    private final Map<String, Member> members = new HashMap<>();

    /** Creates an empty member registry. */
    public MemberRepository() {}

    /**
     * Inserts or replaces a member.
     *
     * @param member the member to store, indexed by their ID
     */
    public void save(Member member){
        members.put(member.getMemberId(), member); }

    /**
     * Finds a member by ID.
     *
     * @param id member ID to look up
     * @return the member if present, otherwise empty
     */
    public Optional<Member> findById(String id){
        return Optional.ofNullable(members.get(id));
    }

    /**
     * Returns all registered members.
     *
     * @return a mutable copy of every member
     */
    public List<Member>findAll() { return new ArrayList<>(members.values()); }

    /**
     * Checks whether a member with the given ID exists.
     *
     * @param id member ID to test
     * @return {@code true} if the member is registered
     */
    public boolean existsById(String id) { return members.containsKey(id); }

}
