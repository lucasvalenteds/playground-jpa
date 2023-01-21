package io.playground;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {


    @Override
    @EntityGraph("book-with-authors")
    Optional<Book> findById(Long id);

    @Override
    @EntityGraph("book-with-authors")
    List<Book> findAll(Sort sort);
}
