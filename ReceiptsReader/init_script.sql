DROP DATABASE IF EXISTS receipts_reader;
DROP USER IF EXISTS 'reader'@'localhost';

CREATE DATABASE receipts_reader;

CREATE USER 'reader'@'localhost' IDENTIFIED BY 'vZPammdSmi5gFSe';
GRANT ALL PRIVILEGES ON receipts_reader.* TO 'reader'@'localhost';

BEGIN;

DROP TABLE IF EXIST t_market;
DROP TABLE IF EXIST t_tax;
DROP TABLE IF EXIST t_receipt;
DROP TABLE IF EXIST t_receipt_taxes;
DROP TABLE IF EXIST t_payment;
DROP TABLE IF EXIST t_auchan_item;
DROP TABLE IF EXIST t_auchan_receipt_items;

CREATE TABLE t_market (
	id SERIAL PRIMARY KEY,	
	name VARCHAR(128) NOT NULL,
	street VARCHAR(64) NOT NULL,
	zip VARCHAR(10) NOT NULL,
	city VARCHAR(64) NOT NULL
);

CREATE TABLE t_tax (
	id SERIAL PRIMARY KEY,	
	sign CHAR(1) NOT NULL,
	rate DECIMAL(5,4) NOT NULL
);

CREATE TABLE t_receipt (
	id SERIAL PRIMARY KEY,
	market_id INT NOT NULL REFERENCES t_market(id),
	sell_date TIMESTAMP,
	tax_sum DECIMAL(10,2),
	price_sum DECIMAL(10,2)
);

CREATE TABLE t_receipt_taxes (
	receipt_id INT REFERENCES t_receipt(id),
	tax_id INT REFERENCES t_tax(id),
	price_sum_by_tax DECIMAL(10,2),
	tax_sum DECIMAL(10,2),
	PRIMARY KEY (receipt_id, tax_id) 
);

CREATE TABLE t_payment (
	receipt_id INT PRIMARY KEY,
	card DECIMAL(10,2),
	cash DECIMAL(10,2),
	cash_change DECIMAL(10,2),
	bonuses DECIMAL(10,2),
	FOREIGN KEY(receipt_id) REFERENCES t_receipt(id)
);

CREATE TABLE t_auchan_item (
	id SERIAL PRIMARY KEY,
	name VARCHAR(128) NOT NULL,
	price DECIMAL(10,2) NOT NULL,
	discount DECIMAL(5,4) NOT NULL,
	tax_id INT NOT NULL REFERENCES t_tax(id)
);

CREATE TABLE t_auchan_receipt_items (
	id SERIAL PRIMARY KEY,
	receipt_id INT NOT NULL REFERENCES t_receipt(id),
	item_id INT NOT NULL REFERENCES t_auchan_item(id),
	amount INT NOT NULL default 0,
	price_sum DECIMAL(10,2) NOT NULL
);

COMMIT;