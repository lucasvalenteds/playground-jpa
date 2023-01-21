CREATE TABLE book
(
    id    SERIAL PRIMARY KEY,
    title VARCHAR
);

CREATE TABLE author
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE author_book
(
    author_id BIGINT REFERENCES author (id),
    book_id   BIGINT REFERENCES book (id)
);

INSERT INTO book (id, title)
VALUES (1, 'Animal Farm'),
       (2, 'Nineteen Eighty-Four'),
       (3, 'Homage to Catalonia'),
       (4, 'Crime and Punishment'),
       (5, 'The Brothers Karamazov'),
       (6, 'One Hundred Years of Solitude');

INSERT INTO author (id, name)
VALUES (1, 'George Orwell'),
       (2, 'Fyodor Dostoevsky'),
       (3, 'Gabriel García Márquez');

INSERT INTO author_book (author_id, book_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 4),
       (2, 5),
       (3, 6);
