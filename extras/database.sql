DROP DATABASE IF EXISTS mock_api;
CREATE DATABASE mock_api;
USE mock_api;

CREATE TABLE projects (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR (10) NOT NULL,
  api_key VARCHAR (10) NOT NULL,
  pass_hash TEXT NOT NULL,
  is_active TINYINT NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY(id),
  UNIQUE KEY (name),
  UNIQUE KEY (api_key)
);

CREATE TABLE jsons(
  id INT NOT NULL AUTO_INCREMENT,
  project_id INT NOT NULL,
  route VARCHAR (50),
  response TEXT,
  is_active TINYINT NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY(id),
  FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE
);