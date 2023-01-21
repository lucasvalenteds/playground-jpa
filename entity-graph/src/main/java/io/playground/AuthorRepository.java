package io.playground;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

    @EntityGraph(attributePaths = "books")
    Optional<Author> findById(Long id);

    @EntityGraph(attributePaths = "books")
    List<Author> findAll();
}
