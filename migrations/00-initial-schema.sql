-- Таблица для хранения информации для входа (пароль, email)
CREATE TABLE User_password_info (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
;
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES User_password_info(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Основная информация о пользователе
CREATE TABLE User_info (
    id UUID PRIMARY KEY REFERENCES User_password_info(id) ON DELETE CASCADE,
    name VARCHAR(100),
    surname VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    email VARCHAR(255) UNIQUE NOT NULL,
    education_id INT,
    room_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP DEFAULT NULL,
    role VARCHAR(50)
);
