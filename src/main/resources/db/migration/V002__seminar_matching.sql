CREATE TABLE school (
    id     UUID         PRIMARY KEY,
    name   VARCHAR(255) NOT NULL UNIQUE,
    county VARCHAR(255) NOT NULL,
    state  VARCHAR(100)
);

CREATE TABLE app_user (
    id       UUID         PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    is_admin BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE seminar (
    id           UUID         PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    seminar_date DATE         NOT NULL,
    created_by   VARCHAR(255) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE ambassador (
    id          UUID         PRIMARY KEY,
    seminar_id  UUID         NOT NULL REFERENCES seminar(id) ON DELETE CASCADE,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    school_name VARCHAR(255),
    color       VARCHAR(100),
    group_code  VARCHAR(50),
    county      VARCHAR(255)
);
CREATE INDEX idx_ambassador_seminar ON ambassador(seminar_id);

CREATE TABLE supporter (
    id                UUID         PRIMARY KEY,
    seminar_id        UUID         NOT NULL REFERENCES seminar(id) ON DELETE CASCADE,
    supporter_type    VARCHAR(20)  NOT NULL,
    letter_count      INTEGER      NOT NULL DEFAULT 1,
    street            VARCHAR(500),
    city              VARCHAR(255),
    state             VARCHAR(100),
    zip               VARCHAR(20),
    full_name         VARCHAR(500),
    first_name        VARCHAR(255),
    last_name         VARCHAR(255),
    organization      VARCHAR(500),
    donation_info     VARCHAR(500),
    donor_type        VARCHAR(255),
    beneficiary_first VARCHAR(255),
    beneficiary_last  VARCHAR(255),
    sponsored_school  VARCHAR(255),
    sponsor_county    VARCHAR(255),
    title             VARCHAR(255),
    role              VARCHAR(255),
    color             VARCHAR(100),
    group_code        VARCHAR(50)
);
CREATE INDEX idx_supporter_seminar ON supporter(seminar_id);
CREATE INDEX idx_supporter_type    ON supporter(seminar_id, supporter_type);

CREATE TABLE match_result (
    id            UUID    PRIMARY KEY,
    seminar_id    UUID    NOT NULL REFERENCES seminar(id) ON DELETE CASCADE,
    ambassador_id UUID    NOT NULL REFERENCES ambassador(id) ON DELETE CASCADE,
    supporter_id  UUID    NOT NULL REFERENCES supporter(id) ON DELETE CASCADE,
    mandatory     BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (ambassador_id, supporter_id)
);
CREATE INDEX idx_match_result_seminar    ON match_result(seminar_id);
CREATE INDEX idx_match_result_ambassador ON match_result(ambassador_id);
