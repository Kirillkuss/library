CREATE TABLE lib_users(
    id serial PRIMARY KEY,
    lu_date timestamp(6),
    login VARCHAR( 50 ) NOT NULL,
	password VARCHAR( 250 ) NOT NULL,
    first_name varchar(255) NOT NULL,
	second_name varchar(255) NOT NULL,
	middle_name varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	is_open bool NOT NULL,
	phone varchar(255) NOT NULL,
    secret varchar(255) NULL
);

CREATE TABLE lib_roles(
    id serial PRIMARY KEY,
    name_role varchar(255) NOT NULL,
    lu_date timestamp(6)
);

CREATE TABLE lib_users_roles(
    user_id int8 NOT NULL REFERENCES public.lib_users(id),
	role_id int8 NOT NULL REFERENCES public.lib_roles(id)
);

CREATE TABLE lib_authors(
    id serial PRIMARY KEY,
    lu_date timestamp(6),
    first_name varchar(255) NOT NULL,
	second_name varchar(255) NOT NULL,
	middle_name varchar(255) NOT NULL,
    country varchar(255) NOT NULL
);

CREATE TABLE lib_books(
    id serial PRIMARY KEY,
    lu_date timestamp(6),
    name_book varchar(50) NOT NULL,
    description_book varchar(50) NOT NULL,
    author_id int8,
    book_number int8 NOT NULL UNIQUE,
	page_book int8 NOT NULL,
    FOREIGN KEY (author_id) REFERENCES public.lib_authors(id)
);

CREATE TABLE lib_cards(
    id serial PRIMARY KEY,
    lu_date timestamp(6),
	user_id int8 NULL,
	create_date timestamp(6) NULL,
	finish_date timestamp(6) NULL,
	isopen bool NULL,
    FOREIGN KEY (user_id) REFERENCES public.lib_users(id)
);

CREATE TABLE lib_cards_record(
    id serial PRIMARY KEY,
    create_date timestamp(6) NOT NULL,
    finish_date timestamp(6) NOT NULL,
    book_id int8,
    card_id int8,
    FOREIGN KEY (book_id) REFERENCES public.lib_books(id),
    FOREIGN KEY (card_id) REFERENCES public.lib_cards(id)
);

CREATE TABLE lib_logs (
    id BIGSERIAL PRIMARY KEY,
    server_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    log_level VARCHAR(10) NOT NULL,  -- INFO, WARN, ERROR, DEBUG
    logger VARCHAR(255),          -- Название класса/компонента
    log_message TEXT NOT NULL,        -- Текст сообщения
    exception TEXT,               -- Stacktrace (если есть)
    username VARCHAR(100),       -- Пользователь (если есть)
    request_method VARCHAR(10),  -- GET, POST, PUT, DELETE
    request_uri VARCHAR(255),    -- URL запроса
    response_status INT,         -- HTTP-статус (200, 404, 500)
    execute_time INT             -- Время выполнения
);


CREATE INDEX idx_logs_timestamp ON lib_logs(server_time);
CREATE INDEX idx_logs_level ON lib_logs(log_level);
CREATE INDEX idx_logs_username ON lib_logs(username);