CREATE TABLE education (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    year_from int NOT NULL,
    year_to int,
    institution varchar(255) NOT NULL,
    education_level varchar(255),
    specialization varchar(255),
    user_id bigint NOT NULL,

    CONSTRAINT fk_education_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE career (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    date_from DATE NOT NULL,
    date_to DATE,
    company varchar(255) NOT NULL,
    position varchar(255) NOT NULL,
    user_id bigint NOT NULL,

    CONSTRAINT fk_career_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE work_schedule (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    start_time time NOT NULL,
    end_time time NOT NULL,
    start_lunch time NOT NULL,
    end_lunch time NOT NULL,
    timezone varchar(255) NOT NULL,
    user_id bigint NOT NULL,

    CONSTRAINT fk_work_schedule_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);