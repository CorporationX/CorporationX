CREATE TABLE country (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) UNIQUE NOT NULL
);

CREATE TABLE users (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    username varchar(64) UNIQUE NOT NULL,
    password varchar(128) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    phone varchar(32) UNIQUE,
    about_me varchar(4096),
    active boolean DEFAULT true NOT NULL,
    city varchar(64),
    country_id bigint NOT NULL,
    experience int,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE subscription (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    follower_id bigint NOT NULL,
    followee_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id),
    CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id)
);

CREATE TABLE mentorship (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    mentor_id bigint NOT NULL,
    mentee_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id),
    CONSTRAINT fk_mentee_id FOREIGN KEY (mentee_id) REFERENCES users (id)
);

CREATE TABLE mentorship_request (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    description varchar(4096) NOT NULL,
    requester_id bigint NOT NULL,
    receiver_id bigint NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    rejection_reason varchar(4096),
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_mentee_req_id FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_mentor_req_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE skill (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) UNIQUE NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE user_skill (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    skill_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_user_skill_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_skill_user_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_skill_guarantee (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
  user_id BIGINT NOT NULL,
  skill_id BIGINT NOT NULL,
  guarantor_id BIGINT NOT NULL,

  CONSTRAINT fk_user_skill_guarantee_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_user_skill_guarantee_skill FOREIGN KEY (skill_id) REFERENCES skill (id),
  CONSTRAINT fk_user_skill_guarantee_guarantor FOREIGN KEY (guarantor_id) REFERENCES users (id)
);

CREATE TABLE recommendation (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content varchar(4096) NOT NULL,
    author_id bigint NOT NULL,
    receiver_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_recommender_id FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE skill_offer (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    skill_id bigint NOT NULL,
    recommendation_id bigint NOT NULL,

    CONSTRAINT fk_skill_offered_id FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE CASCADE,
    CONSTRAINT fk_recommendation_skill_id FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) ON DELETE CASCADE
);

CREATE TABLE recommendation_request (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    message varchar(4096) NOT NULL,
    requester_id bigint NOT NULL,
    receiver_id bigint NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    rejection_reason varchar(4096),
    recommendation_id bigint,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_requester_recommendation_id FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_receiver_recommendation_id FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT fk_recommendation_req_id FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) ON DELETE CASCADE
);

CREATE TABLE skill_request (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    request_id bigint NOT NULL,
    skill_id bigint NOT NULL,

    CONSTRAINT fk_request_skill_id FOREIGN KEY (request_id) REFERENCES recommendation_request (id) ON DELETE CASCADE,
    CONSTRAINT fk_skill_request_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE contact (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    contact varchar(128) NOT NULL UNIQUE,
    type smallint NOT NULL,

    CONSTRAINT fk_contact_owner_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE project_subscription (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    project_id bigint NOT NULL,
    follower_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_project_follower_id FOREIGN KEY (follower_id) REFERENCES users (id)
);

CREATE TABLE event (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) NOT NULL,
    description varchar(4096) NOT NULL,
    start_date timestamptz NOT NULL,
    end_date timestamptz NOT NULL,
    location varchar(128) NOT NULL,
    max_attendees int,
    user_id bigint NOT NULL,
    type smallint NOT NULL,
    status smallint NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_event_owner_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_skill (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    event_id bigint NOT NULL,
    skill_id bigint NOT NULL,

    CONSTRAINT fk_event_skill_id FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE,
    CONSTRAINT fk_skill_event_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_event (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    event_id bigint NOT NULL,

    CONSTRAINT fk_user_event_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event_user_id FOREIGN KEY (event_id) REFERENCES event (id)
);

CREATE TABLE rating (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    event_id bigint NOT NULL,
    rate smallint NOT NULL,
    comment varchar(4096),
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_rater_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event_rated_id FOREIGN KEY (event_id) REFERENCES event (id)
);