CREATE TABLE route_updates(
	id INT NOT NULL AUTO_INCREMENT,
    _key VARCHAR(60) NOT NULL,
    route_id INT NOT NULL,
   	method VARCHAR(10) NOT NULL,
    params TEXT,
    delay TEXT,
    description TEXT,
    default_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (route_id) REFERENCES routes(id) ON UPDATE CASCADE ON DELETE CASCADE
);

