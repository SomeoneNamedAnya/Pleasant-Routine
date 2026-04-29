----------------------------
-- Инфраструктура
----------------------------
CREATE TABLE university (
    id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE dormitory (
    id SERIAL PRIMARY KEY,
	code TEXT,
    name TEXT,
	region TEXT,
	city TEXT,
    university_id INT REFERENCES university(id)
);

CREATE TABLE room (
    id SERIAL PRIMARY KEY,
    number TEXT,
    dormitory_id INT REFERENCES dormitory(id),
	public_info TEXT,
	private_info TEXT,
	public_photo_link TEXT
);


CREATE TABLE education_program (
    id SERIAL PRIMARY KEY,
	code TEXT,
    name TEXT,
	qualification TEXT,
	department TEXT,
	level TEXT
);

----------------------------
-- Пользователь
----------------------------

CREATE TABLE user_info (
    id SERIAL PRIMARY KEY,
    name TEXT,
    surname TEXT,
    last_name TEXT,
    date_of_birth DATE,
    email TEXT UNIQUE,
    education_id INT REFERENCES education_program(id),
    room_id INT REFERENCES room(id),
    created_at TIMESTAMP,
    deleted_at TIMESTAMP,
	photo_link TEXT,
	about TEXT,
    role TEXT
);

CREATE TABLE user_password_info (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES user_info(id) ON DELETE CASCADE,
    email VARCHAR(255) UNIQUE NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    has_to_change BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES user_password_info(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_living (
	id SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_info(id),
    dormitory_id INT REFERENCES dormitory(id),
	started_at TIMESTAMP,
	ended_at TIMESTAMP
);

----------------------------
-- Чат
----------------------------

CREATE TABLE chat (
    id SERIAL PRIMARY KEY,
	start_at TIMESTAMP,
	ended_at TIMESTAMP,
	title TEXT,
	room_id INT REFERENCES room(id),
	creator INT REFERENCES user_info(id)
);

CREATE TABLE chat_participant (
	id SERIAL PRIMARY KEY,
    chat_id INT REFERENCES chat(id),
    user_id INT REFERENCES user_info(id)

);

CREATE TABLE message_in_chat (
	id SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_info(id),
    chat_id INT REFERENCES chat(id),
    start_dt TIMESTAMP,
    message TEXT
);

----------------------------
-- Заметки
----------------------------

CREATE TABLE personal_note (
    id SERIAL PRIMARY KEY,
	title TEXT,
    content TEXT,
	creator_id INT REFERENCES user_info(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    edited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE room_note (
    id SERIAL PRIMARY KEY,
	title TEXT,
    content TEXT,
    is_public BOOLEAN,
    room_id INT REFERENCES room(id),
	creator_id INT REFERENCES user_info(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    edited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE photo_room_note (
    id SERIAL PRIMARY KEY,
    note_id INT REFERENCES room_note(id),
    photo_link TEXT
);

CREATE TABLE photo_personal_note (
    id SERIAL PRIMARY KEY,
    note_id INT REFERENCES personal_note(id),
    photo_link TEXT
);

CREATE TABLE tag_room_note (
    id SERIAL PRIMARY KEY,
    note_id INT REFERENCES room_note(id),
    tag TEXT
);

CREATE TABLE tag_personal_note (
    id SERIAL PRIMARY KEY,
    note_id INT REFERENCES personal_note(id),
    tag TEXT
);

----------------------------
-- Задачи
----------------------------

CREATE TABLE task (
    id SERIAL PRIMARY KEY,
    create_at TIMESTAMP,
    deadline TIMESTAMP,
    title TEXT,
    description TEXT,
    type TEXT,
    status TEXT,
    room_id INT REFERENCES room(id)
);

CREATE TABLE task_creator (
	id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id),
    user_id INT REFERENCES user_info(id)

);

CREATE TABLE task_performer (
	id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id),
    user_id INT REFERENCES user_info(id)

);

CREATE TABLE task_watcher (
	id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id),
    user_id INT REFERENCES user_info(id)

);

CREATE TABLE task_comment (
	id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id),
    user_id INT REFERENCES user_info(id),
    comment TEXT,
    created_at TIMESTAMP

);

CREATE TABLE task_approval (
    id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id) ON DELETE CASCADE,
    watcher_id INT REFERENCES user_info(id),
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


---------------------------------------
-- sharing
---------------------------------------

CREATE TABLE sharing_card (
    id          SERIAL PRIMARY KEY,
    title       TEXT NOT NULL,
    description TEXT,
    photo_link  TEXT,
    creator_id  INT REFERENCES user_info(id),
    room_id     INT REFERENCES room(id),
    dormitory_id INT REFERENCES dormitory(id),
    claimed_by  INT REFERENCES user_info(id),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
