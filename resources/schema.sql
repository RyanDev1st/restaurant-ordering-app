-- Creates the 5 tables. DatabaseSetup runs this once, when restaurant.db does not exist yet.
CREATE TABLE users (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    username  TEXT NOT NULL UNIQUE,
    password  TEXT NOT NULL,
    full_name TEXT NOT NULL,
    role      TEXT NOT NULL,
    phone     TEXT,
    position  TEXT
);

CREATE TABLE menu_items (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    name      TEXT NOT NULL,
    category  TEXT NOT NULL,
    price     INTEGER NOT NULL,
    available INTEGER NOT NULL
);

CREATE TABLE dining_tables (
    number INTEGER PRIMARY KEY,
    seats  INTEGER NOT NULL
);

CREATE TABLE orders (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id  INTEGER NOT NULL REFERENCES users(id),
    table_number INTEGER NOT NULL REFERENCES dining_tables(number),
    status       TEXT NOT NULL,
    created_at   TEXT NOT NULL
);

CREATE TABLE order_items (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id     INTEGER NOT NULL REFERENCES orders(id),
    menu_item_id INTEGER NOT NULL REFERENCES menu_items(id),
    quantity     INTEGER NOT NULL,
    unit_price   INTEGER NOT NULL
);
