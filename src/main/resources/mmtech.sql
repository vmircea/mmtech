use mmtech;

CREATE TABLE MEMBERSHIP (
	MEMBER_ID char(36) NOT NULL,
    name char(30) NOT NULL,
    age TINYINT,
    status char(15),
    profession char(30),
    contactInfo_id int NOT NULL,
    branch_id char(11) NOT NULL,
    PRIMARY KEY(member_id),
	KEY `contactInfo_FK_id` (`contactInfo_id`),
	CONSTRAINT `contactInfo_FK_id` FOREIGN KEY (`contactInfo_id`) REFERENCES `CONTACTINFO` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
);
DROP TABLE MEMBERSHIP;
ALTER TABLE MEMBERSHIP MODIFY COLUMN contactInfo_id int NOT NULL AUTO_INCREMENT;
DESC Membership;

INSERT INTO membership VALUES (
	"4e48d7a8-64b9-4455-a37f-903fd62def32",
    "John Doe",
    34,
    "ACTIVE",
    "DOCTOR",
    8,
    "UK-LDN-5645"
);
INSERT INTO membership VALUES (
	"67f30725-3993-432c-a778-d29fd3cf2d91",
    "Dennis Brown",
    21,
    "INACTIVE",
    "LAWYER",
    9,
    "UK-LDN-5645"
);

CREATE TABLE CONTACTINFO (
	id int NOT NULL auto_increment,
	phoneNumber char(15) NOT NULL,
    emailAddress char(30),
    country char(15),
    city char(15),
    street char(50),
    building char(15),
    PRIMARY KEY(id)
);
DROP TABLE CONTACTINFO;
ALTER TABLE contactinfo MODIFY COLUMN id int NOT NULL AUTO_INCREMENT;
DELETE FROM contactinfo where id > 0;

INSERT INTO CONTACTINFO VALUES (
	null,
	"+40759698745",
    "john.doe@gmail.com",
    "United Kingdom",
    "London",
    "ParkWay 16",
    "24"
);
INSERT INTO CONTACTINFO VALUES (
	null,
	"+40798574875",
    "denis.brown@gmail.com",
    "Romania",
    "Bucharest",
    "Independetei 50",
    "29 C"
);

-- FIND BY ID 
SELECT m.member_id as id, m.name, m.age, m.status, m.profession, m.branch_id, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building
FROM membership m
JOIN contactinfo c ON (m.contactInfo_id = c.id)
WHERE member_id = "4e48d7a8-64b9-4455-a37f-903fd62def32";

-- FIND ALL 
SELECT m.member_id as id, m.name, m.age, m.status, m.profession, m.branch_id, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building
FROM membership m
JOIN contactinfo c ON (m.contactInfo_id = c.id);


CREATE TABLE skills (
	skillName char(50) NOT NULL,
    MEMBER_ID char(36) NOT NULL
);

INSERT INTO skills VALUES (
	"QUALITYCONTROL",
    "4e48d7a8-64b9-4455-a37f-903fd62def32"
);
INSERT INTO skills VALUES (
	"DIGITALMEDIA",
    "4e48d7a8-64b9-4455-a37f-903fd62def32"
);

SELECT name, skillName
FROM membership
JOIN skills USING (MEMBER_ID);	

CREATE TABLE fees (
	paidInDate char(10) NOT NULL,
    paidInAmount DOUBLE NOT NULL,
    member_id char(36) NOT NULL
);

DROP TABLE fees;

SELECT paidInDate, paidInAmount
FROM fees
WHERE member_id = "4e48d7a8-64b9-4455-a37f-903fd62def32";

CREATE TABLE branches (
	branch_id char(11) NOT NULL,
    branch_name char(50) NOT NULL,
    admin_id char(36),
    contactInfo_id int NOT NULL,
    totalAmount DOUBLE,
    PRIMARY KEY(branch_id)
);
DROP TABLE branches;
DESC branches;



