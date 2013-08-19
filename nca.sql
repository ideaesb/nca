DROP TABLE IF EXISTS entries;
CREATE TABLE entries
(
  entryKeyHash varchar(32) PRIMARY KEY
)
;
ALTER TABLE entries OWNER TO postgres;
