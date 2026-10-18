# Library Management System

A small, dependency-free Java console application that manages a library's
catalogue: books, members, and loans. It demonstrates a clean separation
between business logic, persistence, and presentation using plain Java 21
(no framework, no database — everything lives in memory).

---

## What it does

| # | Action | Description |
|---|--------|-------------|
| 1 | Add book | Registers a new book, rejected if the ISBN already exists |
| 2 | Register member | Registers a borrower, rejected if the ID already exists |
| 3 | Borrow book | Marks a book as borrowed and opens a loan record |
| 4 | Return book | Closes the member's active loan and frees the book |
| 5 | List books | Prints the whole catalogue with availability status |
| 6 | Active loans | Shows the books a member currently has out |
| 0 | Exit | Ends the session |

**Business rules enforced**

- An ISBN can only be added once; a member ID can only be registered once.
- A book must exist and a member must be registered before it can be borrowed.
- A book that is already on loan cannot be borrowed again.
- A book can only be returned by the member who holds the active loan, and
  only when such a loan exists.
- Blank/`null` identifiers, titles, authors and names are rejected.

Failures are reported as exceptions (`IllegalStateException`,
`IllegalArgumentException`) and printed by the CLI without crashing the
session.

---

## Requirements

- **JDK 21 or newer** (the project uses records-era syntax, `switch` arrows,
  text blocks and `Stream.toList()`)
- **Maven 3.8+**
- Optional: an IDE (IntelliJ IDEA, Eclipse, VS Code) — the repo ships with an
  `.idea/` folder for IntelliJ

---

## Building and running

```bash
# compile + run all tests
mvn test

# compile only
mvn compile

# run the interactive CLI
mvn -q compile exec:java -Dexec.mainClass="org.clemzito.library.service.Main"
```

If you prefer to run the compiled classes directly:

```bash
mvn -q compile
java -cp target/classes org.clemzito.library.service.Main
```

### Sample session

```
== Library Management ==
1. Add book
2. Register member
3. Borrow book
4. Return book
5. List books
6. Active loans for member
0. Exit
Choose an option: 1
ISBN: 978-0134685991
Title: Effective Java
Author: Joshua Bloch
Book added.
...
Choose an option: 5
[978-0134685991] Effective Java by Joshua Bloch (AVAILABLE)
Choose an option: 0
Goodbye
```

---

## Usage of the API (for programmatic callers)

```java
LibraryService service = new LibraryService(
        new BookRepository(),
        new MemberRepository(),
        new LoanRepository());

service.addBook("978-0134685991", "Effective Java", "Joshua Bloch");
service.registerMember("M1", "Alice");

service.borrowBook("978-0134685991", "M1");   // book now BORROWED
service.activeLoansForMember("M1");            // 1 open loan
service.returnBook("978-0134685991", "M1");    // book back to AVAILABLE

Optional<Book> book = service.findBook("978-0134685991");
List<Book> onShelf = service.availableBooks();
```

---

## Architecture

The code follows a classic three-layer design. Dependencies point downward
only: **CLI → Service → Repository → Domain**.

```
org.clemzito.library.service
├── Main.java                  Presentation layer: console menu, I/O, error display
├── LibraryService.java        Application/service layer: business rules & use cases
├── domain/                    Domain layer: pure entities, no dependencies upward
│   ├── Book.java              Catalogue item, availability state machine
│   ├── Member.java            Borrower identified by memberId
│   └── Loan.java              Borrowing event (isbn, memberId, dates, active flag)
└── repository/                Persistence layer: in-memory collections
    ├── BookRepository.java    Map<String, Book>   keyed by ISBN
    ├── MemberRepository.java  Map<String, Member> keyed by memberId
    └── LoanRepository.java    List<Loan>          append-only history
```

### Layer responsibilities

1. **Presentation (`Main`)** — reads input, prints the menu and results, and
   catches `RuntimeException` so a bad input never terminates the loop. It
   contains *no* business logic.
2. **Application (`LibraryService`)** — the single façade over the system.
   It validates uniqueness, resolves entities, applies the borrow/return
   rules, and keeps `Book.available` consistent with open `Loan` records.
   All use cases are transactional *in spirit*: a failure happens before any
   state is mutated.
3. **Domain (`domain`)** — small, framework-free entities that own their own
   invariants (`Book.markBorrowed()` refuses a double borrow, constructors
   reject blank values). Equality is identity-based: `Book` by ISBN,
   `Member` by member ID.
4. **Repository (`repository`)** — thin collections wrappers exposing
   `save` / `findBy…` / `find…` query methods. Swapping them for a JDBC or
   JPA implementation requires no change to the layers above, because
   `LibraryService` only talks to them through their own types (constructor
   injection).

### Key design notes

- **Constructor injection** — `LibraryService` receives its repositories,
  which keeps it testable and lets tests start from empty stores.
- **State**: books flip `available → borrowed → available`; loans are
  append-only and closed by setting `returnedAt`, so history is preserved.
- **Dates** — loans are stamped with `LocalDate.now()` at borrow/return time.
- **Data lifetime** — everything is in memory; restarting the program resets
  the library to empty.

### Typical borrow flow

```
User → Main.borrow()
     → LibraryService.borrowBook(isbn, memberId)
          1. BookRepository.findByIsbn   → orElseThrow "Book not found"
          2. MemberRepository.findById   → isEmpty  "Member not found"
          3. book.isAvailable()          → false    "not available"
          4. book.markBorrowed()         (domain state change)
          5. LoanRepository.save(Loan)   (open loan, dated today)
```

---

## Testing

Tests live in `src/test/java` and use **JUnit 5** + **AssertJ**:

```bash
mvn test
```

`LibraryServiceTest` covers 8 scenarios: adding a book, duplicate ISBN
rejection, member registration, a successful borrow, borrowing an
unavailable book, borrowing with an unknown member, a successful return, and
returning without an active loan. Each test gets a fresh service via
`@BeforeEach`.

---

## Possible extensions

- Persist books/members/loans (JDBC, JPA/Hibernate, or JSON files).
- Due dates, overdue fines, and loan history reports.
- Multiple copies of the same title (copy IDs instead of ISBN-only identity).
- Search by title/author and pagination of listings.
