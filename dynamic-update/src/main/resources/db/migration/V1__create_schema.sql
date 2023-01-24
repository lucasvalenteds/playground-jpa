CREATE TABLE product
(
    id          SERIAL,
    name        VARCHAR,
    description TEXT,
    quantity    INT,

    CONSTRAINT pk_product_id PRIMARY KEY (id)
);

CREATE TABLE vehicle
(
    id           SERIAL,
    brand        VARCHAR,
    model        VARCHAR,
    electric     BOOLEAN,
    release_year SMALLINT,

    CONSTRAINT pk_vehicle_id PRIMARY KEY (id)
);

INSERT INTO product (name, description, quantity)
VALUES ('Regular Notebook', 'Regular Notebook with black cover containing 200 blank pages measuring 21x15', 5);

INSERT INTO vehicle (brand, model, release_year)
VALUES ('Ford', 'F-150 Special Edition', 2021);