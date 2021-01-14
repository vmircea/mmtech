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

CREATE TABLE payments (
	transaction_no int NOT NULL auto_increment,
    sender_id char(36) NOT NULL,
    receiver_id char(11),
    amount DOUBLE,
    date char(10) NOT NULL,
    description char(150),
    PRIMARY KEY(transaction_no)
);
DROP TABLE payments;

CREATE TABLE projects (
	project_id char(12) NOT NULL,
    name char(20) NOT NULL,
    status char(15),
    description char(150),
    amount DOUBLE,
    PRIMARY KEY(project_id)
);
DROP TABLE projects;

CREATE TABLE ProjectSkills (
	skillName char(50) NOT NULL,
    project_id char(12) NOT NULL,
    KEY `projectFK` (`project_id`),
	CONSTRAINT `projectFK` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE
);
DROP TABLE ProjectSkills;

INSERT INTO projects (project_id, name, status, description, amount) 
VALUES ("PROJECT-0001", "Project No 1", "NEW", "First Project Description", 0);

SELECT project_id, name, status
FROM projects;

CREATE TABLE projectMembers (
	project_id char(12) NOT NULL,
    member_id char(36) NOT NULL,
    KEY `projectForMembersFK` (`project_id`),
	CONSTRAINT `projectForMembersFK` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    KEY `membershipFK` (`member_id`),
    CONSTRAINT `membershipFK` FOREIGN KEY (`member_id`) REFERENCES `MEMBERSHIP` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DELETE FROM projectMembers WHERE project_id = "PROJECT-0003";

