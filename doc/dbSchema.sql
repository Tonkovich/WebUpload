CREATE TABLE Users
(
  id INTEGER      NOT NULL AUTO_INCREMENT,
  username   VARCHAR(10)  NOT NULL,
  email 	 VARCHAR(100) NOT NULL,
  password   TEXT 		  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE Files
(
  file_id   INTEGER       NOT NULL AUTO_INCREMENT,
  user_id INTEGER       NOT NULL,
  file      MEDIUMBLOB    NOT NULL,
  file_name VARCHAR(100)  NOT NULL,
  PRIMARY KEY (file_id),
  FOREIGN KEY (user_id) REFERENCES Users (id)
    ON DELETE CASCADE
);