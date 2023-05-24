CREATE TABLE goal (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) NOT NULL,
    description varchar(4096) NOT NULL,
    parent_goal_id bigint,
    status smallint DEFAULT 0 NOT NULL,
    deadline timestamptz,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    mentor_id bigint,

    CONSTRAINT fk_goal_id FOREIGN KEY (parent_goal_id) REFERENCES goal (id),
    CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id)
);

CREATE TABLE goal_invitation (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    goal_id bigint NOT NULL,
    inviter_id bigint NOT NULL,
    invited_id bigint NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_inviter_id FOREIGN KEY (inviter_id) REFERENCES users (id),
    CONSTRAINT fk_invited_id FOREIGN KEY (invited_id) REFERENCES users (id),
    CONSTRAINT fk_goal_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

CREATE TABLE user_goal (
   id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
   user_id bigint NOT NULL,
   goal_id bigint NOT NULL,
   created_at timestamptz DEFAULT current_timestamp,
   updated_at timestamptz DEFAULT current_timestamp,

   CONSTRAINT fk_user_goal_id FOREIGN KEY (user_id) REFERENCES users (id),
   CONSTRAINT fk_goal_user_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

CREATE TABLE goal_skill (
   id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
   goal_id bigint NOT NULL,
   skill_id bigint NOT NULL,
   created_at timestamptz DEFAULT current_timestamp,
   updated_at timestamptz DEFAULT current_timestamp,

   CONSTRAINT fk_goal_skill_id FOREIGN KEY (goal_id) REFERENCES goal (id),
   CONSTRAINT fk_skill_goal_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);