CREATE TABLE CONTACTINFO (
	id int NOT NULL AUTO_INCREMENT,
	phoneNumber char(15) NOT NULL,
    emailAddress char(30),
    country char(15),
    city char(15),
    street char(50),
    building char(15),
    PRIMARY KEY(id)
);

CREATE TABLE MEMBERSHIP (
	MEMBER_ID char(36) NOT NULL,
    name char(30) NOT NULL,
    age TINYINT,
    status char(15),
    profession char(30),
    contactInfo_id int NOT NULL,
    PRIMARY KEY(member_id),
	FOREIGN KEY (contactInfo_id) REFERENCES CONTACTINFO (id)
);

INSERT INTO CONTACTINFO VALUES (
	1,
	'+40759698745',
    'john.doe@gmail.com',
    'United Kingdom',
    'London',
    'ParkWay 16',
    '24'
);

INSERT INTO membership VALUES (
	'4e48d7a8-64b9-4455-a37f-903fd62def32',
    'John Doe',
    34,
    'ACTIVE',
    'DOCTOR',
    1
);
