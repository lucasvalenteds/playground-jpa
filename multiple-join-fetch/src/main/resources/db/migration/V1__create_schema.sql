CREATE TABLE customer
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE product
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE product_image
(
    id         SERIAL PRIMARY KEY,
    url        VARCHAR,
    product_id BIGINT REFERENCES product (id)
);

CREATE TABLE product_order
(
    id          SERIAL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    customer_id BIGINT REFERENCES customer (id)
);

CREATE TABLE product_order_item
(
    id         SERIAL PRIMARY KEY,
    order_id   BIGINT REFERENCES product_order (id),
    product_id BIGINT REFERENCES product (id)
);

INSERT INTO customer (name)
VALUES ('John Smith'),
       ('Mary Jane');

INSERT INTO product (name)
VALUES ('Notebook'),
       ('Pencil');

INSERT INTO product_image (product_id, url)
VALUES (1, 'https://playground.io/images/products/1/image-1.jpg'),
       (1, 'https://playground.io/images/products/1/image-2.jpg'),
       (2, 'https://playground.io/images/products/2/image-1.jpg'),
       (2, 'https://playground.io/images/products/2/image-2.jpg');

INSERT INTO product_order (created_at, customer_id)
VALUES ('2023-01-25 16:12:33.983962 +00:00', 1),
       ('2023-01-25 16:12:33.983962 +00:00', 2);

INSERT INTO product_order_item (order_id, product_id)
VALUES (1, 1),
       (1, 2),
       (2, 1);
