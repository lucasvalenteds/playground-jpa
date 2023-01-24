CREATE TABLE account
(
    id       SERIAL,
    username VARCHAR,

    CONSTRAINT pk_account_id PRIMARY KEY (id)
);

CREATE TABLE issue
(
    id         SERIAL,
    title      VARCHAR,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    account_id BIGINT,

    CONSTRAINT pk_issue_id PRIMARY KEY (id),
    CONSTRAINT uk_account_id UNIQUE (account_id)
);

ALTER TABLE issue
    ADD CONSTRAINT fk_account_id
        FOREIGN KEY (account_id)
            REFERENCES account (id);

INSERT INTO account (username)
VALUES ('john.smith'),
       ('mary.jane');

INSERT INTO issue (title, created_at)
VALUES ('Call the customer ABC to make sure everything is OK', now()),
       ('Send invoice to the customer XYZ', now());
