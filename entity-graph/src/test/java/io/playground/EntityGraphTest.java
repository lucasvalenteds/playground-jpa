package io.playground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigurePostgresDatabase
class EntityGraphTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findingAuthorWithBooks() {
        /*
            select a1_0.id, b1_0.author_id, b1_1.id, b1_1.title, a1_0.name
            from author a1_0
            left join (author_book b1_0 join book b1_1 on b1_1.id=b1_0.book_id) on a1_0.id=b1_0.author_id
            where a1_0.id=?
         */
        final var author = authorRepository.findById(1L).orElseThrow();

        assertThat(author.getName()).isEqualTo("George Orwell");
        assertThat(author.getBooks())
                .hasSize(3)
                .extracting(Book::getTitle)
                .containsOnly("Animal Farm", "Nineteen Eighty-Four", "Homage to Catalonia");
    }

    @Test
    void findingAuthorsWithBooks() {
        /*
            select a1_0.id, b1_0.author_id, b1_1.id, b1_1.title, a1_0.name
            from author a1_0
            left join (author_book b1_0 join book b1_1 on b1_1.id=b1_0.book_id) on a1_0.id=b1_0.author_id
         */
        final var authors = authorRepository.findAll();

        assertThat(authors).hasSize(3);
        assertThat(authors).element(0).satisfies(author -> {
            assertThat(author.getId()).isEqualTo(1L);
            assertThat(author.getName()).isEqualTo("George Orwell");
            assertThat(author.getBooks())
                    .extracting(Book::getTitle)
                    .containsOnly("Animal Farm", "Nineteen Eighty-Four", "Homage to Catalonia");
        });
        assertThat(authors).element(1).satisfies(author -> {
            assertThat(author.getId()).isEqualTo(2L);
            assertThat(author.getName()).isEqualTo("Fyodor Dostoevsky");
            assertThat(author.getBooks())
                    .extracting(Book::getTitle)
                    .containsOnly("Crime and Punishment", "The Brothers Karamazov");
        });
        assertThat(authors).element(2).satisfies(author -> {
            assertThat(author.getId()).isEqualTo(3L);
            assertThat(author.getName()).isEqualTo("Gabriel García Márquez");
            assertThat(author.getBooks())
                    .extracting(Book::getTitle)
                    .containsOnly("One Hundred Years of Solitude");
        });
    }

    @Test
    void findingBookWithAuthors() {
        /*
            select b1_0.id,a1_0.book_id,a1_1.id,a1_1.name,b1_0.title
            from book b1_0
            left join (author_book a1_0 join author a1_1 on a1_1.id=a1_0.author_id) on b1_0.id=a1_0.book_id
            where b1_0.id=?
         */
        final var book = bookRepository.findById(6L).orElseThrow();

        assertThat(book.getTitle()).isEqualTo("One Hundred Years of Solitude");
        assertThat(book.getAuthors())
                .hasSize(1)
                .extracting(Author::getName)
                .containsOnly("Gabriel García Márquez");
    }

    @Test
    void findingBooksWithAuthors() {
        /*
            select b1_0.id,a1_0.book_id,a1_1.id,a1_1.name,b1_0.title
            from book b1_0
            left join (author_book a1_0 join author a1_1 on a1_1.id=a1_0.author_id) on b1_0.id=a1_0.book_id
            order by b1_0.id asc
         */
        final var books = bookRepository.findAll(Sort.by("id").ascending());

        assertThat(books).hasSize(6);
        assertThat(books).element(0).satisfies(book -> {
            assertThat(book.getId()).isEqualTo(1L);
            assertThat(book.getTitle()).isEqualTo("Animal Farm");
            assertThat(book.getAuthors())
                    .extracting(Author::getName)
                    .containsOnly("George Orwell");
        });
        assertThat(books).element(1).satisfies(book -> {
            assertThat(book.getId()).isEqualTo(2L);
            assertThat(book.getTitle()).isEqualTo("Nineteen Eighty-Four");
            assertThat(book.getAuthors())
                    .extracting(Author::getName)
                    .containsOnly("George Orwell");
        });
        assertThat(books).element(2).satisfies(book -> {
            assertThat(book.getId()).isEqualTo(3L);
            assertThat(book.getTitle()).isEqualTo("Homage to Catalonia");
            assertThat(book.getAuthors())
                    .extracting(Author::getName)
                    .containsOnly("George Orwell");
        });
        assertThat(books).element(3).satisfies(book -> {
            assertThat(book.getId()).isEqualTo(4L);
            assertThat(book.getTitle()).isEqualTo("Crime and Punishment");
            assertThat(book.getAuthors())
                    .extracting(Author::getName)
                    .containsOnly("Fyodor Dostoevsky");
        });
        assertThat(books).element(4).satisfies(book -> {
            assertThat(book.getId()).isEqualTo(5L);
            assertThat(book.getTitle()).isEqualTo("The Brothers Karamazov");
            assertThat(book.getAuthors())
                    .extracting(Author::getName)
                    .containsOnly("Fyodor Dostoevsky");
        });
        assertThat(books).element(5).satisfies(book -> {
            assertThat(book.getId()).isEqualTo(6L);
            assertThat(book.getTitle()).isEqualTo("One Hundred Years of Solitude");
            assertThat(book.getAuthors())
                    .extracting(Author::getName)
                    .containsOnly("Gabriel García Márquez");
        });
    }
}
