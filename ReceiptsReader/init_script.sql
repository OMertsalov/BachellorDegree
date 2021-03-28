DROP DATABASE IF EXISTS receipts_reader;
DROP USER IF EXISTS 'reader'@'localhost';

CREATE DATABASE receipts_reader;

CREATE USER 'reader'@'localhost' IDENTIFIED BY 'v&ZPammdSmi5gFSe';
GRANT ALL PRIVILEGES ON receipts_reader.* TO 'reader'@'localhost';

BEGIN;

USE receipts_reader;

DROP TABLE IF EXISTS t_market;
DROP TABLE IF EXISTS t_tax;
DROP TABLE IF EXISTS t_receipt;
DROP TABLE IF EXISTS t_receipt_taxes;
DROP TABLE IF EXISTS t_payment;
DROP TABLE IF EXISTS t_auchan_item;
DROP TABLE IF EXISTS t_auchan_temp_item;
DROP TABLE IF EXISTS t_auchan_receipt_items;

CREATE TABLE t_market (
	id SERIAL PRIMARY KEY,	
	name VARCHAR(128) NOT NULL,
	street VARCHAR(64) NOT NULL,
	zip VARCHAR(10) UNIQUE NOT NULL,
	city VARCHAR(64) NOT NULL
);

CREATE TABLE t_tax (
	id SERIAL PRIMARY KEY,	
	sign CHAR(1) NOT NULL,
	rate DECIMAL(5,4) NOT NULL
);

CREATE TABLE t_receipt (
	id SERIAL PRIMARY KEY,
	market_id BIGINT UNSIGNED NOT NULL,
	sell_date TIMESTAMP,
	tax_sum DECIMAL(10,2),
	price_sum DECIMAL(10,2),
	FOREIGN KEY(market_id) REFERENCES t_market(id)
);

CREATE TABLE t_receipt_taxes (
	receipt_id BIGINT UNSIGNED NOT NULL,
	tax_id BIGINT UNSIGNED NOT NULL,
	price_sum_by_tax DECIMAL(10,2),
	tax_sum DECIMAL(10,2),
	PRIMARY KEY (receipt_id, tax_id),
	FOREIGN KEY(receipt_id) REFERENCES t_receipt(id),
	FOREIGN KEY(tax_id) REFERENCES t_tax(id)
);

CREATE TABLE t_payment (
	receipt_id BIGINT UNSIGNED PRIMARY KEY,
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
	tax_id BIGINT UNSIGNED NOT NULL,
	is_temporary BOOLEAN NOT NULL default false,
	FOREIGN KEY(tax_id) REFERENCES t_tax(id)
);

CREATE TABLE t_auchan_temp_item(
	auchan_item_id BIGINT UNSIGNED PRIMARY KEY,
	last_access_date TIMESTAMP NOT NULL,
	access_counter INT NOT NULL default 1,
	FOREIGN KEY(auchan_item_id) REFERENCES t_auchan_item(id)
);

CREATE TABLE t_auchan_receipt_items (
	id SERIAL PRIMARY KEY,
	receipt_id BIGINT UNSIGNED NOT NULL,
	item_id BIGINT UNSIGNED NOT NULL,
	amount INT NOT NULL default 0,
	price_sum DECIMAL(10,2) NOT NULL,
	FOREIGN KEY(receipt_id) REFERENCES t_receipt(id),
	FOREIGN KEY(item_id) REFERENCES t_auchan_item(id)
);

INSERT INTO t_market VALUES (default,'HIPERMARKET AUCHAN','STAWOWA 61','31-346','KRAKÓW');

INSERT INTO t_tax VALUES (default,'A',0.23);
INSERT INTO t_tax VALUES (default,'B',0.08);
INSERT INTO t_tax VALUES (default,'C',0.05);

COMMIT;