DROP DATABASE IF EXISTS receipts_reader;
DROP USER IF EXISTS 'reader'@'localhost';

CREATE DATABASE receipts_reader;

CREATE USER 'reader'@'localhost' IDENTIFIED BY 'vZPammdSmi5gFSe';
GRANT ALL PRIVILEGES ON receipts_reader.* TO 'reader'@'localhost';


/*!
  BEGIN;
  CREATE TABLE table;
  INSERT INTO table;
  COMMIT;
*/