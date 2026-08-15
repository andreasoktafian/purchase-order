CREATE DATABASE IF NOT EXISTS andreas_oktafian;

CREATE TABLE users (
    id INT UNSIGNED PRIMARY KEY,
    first_name VARCHAR(500) NOT NULL,
    last_name VARCHAR(500),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_datetime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE item (
    id INT UNSIGNED PRIMARY KEY,
    name VARCHAR(500) UNIQUE ,
    description VARCHAR(500),
    price INT NOT NULL,
    cost INT NOT NULL,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_datetime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE po_h (
    id INT AUTO_INCREMENT PRIMARY KEY,
    datetime DATETIME,
    description VARCHAR(500),
    total_price INT,
    total_cost INT,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    created_datetime DATETIME,
    updated_datetime DATETIME
);

CREATE TABLE po_d (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poh_id INT,
    item_id INT UNSIGNED,
    item_qty INT,
    item_cost INT,
    item_price INT,
    FOREIGN KEY (poh_id) REFERENCES po_h(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(id)
);