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
	CONSTRAINT `contactInfo_FK_id` FOREIGN KEY (`contactInfo_id`) REFERENCES `CONTACTINFO` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
	KEY `branch_FK_id` (`branch_id`),
	CONSTRAINT `branch_FK_id` FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
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

CREATE TABLE memberskills (
	skillName char(50) NOT NULL,
    MEMBER_ID char(36) NOT NULL,
	KEY `membership_skills_FK` (`member_id`),
    CONSTRAINT `membership_skills_FK` FOREIGN KEY (`member_id`) REFERENCES `MEMBERSHIP` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE branches (
	branch_id char(11) NOT NULL,
    branch_name char(50) NOT NULL,
    admin_id char(36),
    contactInfo_id int NOT NULL,
    totalAmount DOUBLE,
    PRIMARY KEY(branch_id),
	KEY `contactInfo_branches_FK_id` (`contactInfo_id`),
	CONSTRAINT `contactInfo_branches_FK_id` FOREIGN KEY (`contactInfo_id`) REFERENCES `CONTACTINFO` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE payments (
	transaction_no int NOT NULL auto_increment,
    sender_id char(36) NOT NULL,
    receiver_id char(11),
    amount DOUBLE,
    date char(10) NOT NULL,
    description char(150),
    PRIMARY KEY(transaction_no)
);

CREATE TABLE projects (
	project_id char(12) NOT NULL,
    name char(20) NOT NULL,
    status char(15),
    description char(150),
    amount DOUBLE,
    PRIMARY KEY(project_id)
);

CREATE TABLE ProjectSkills (
	skillName char(50) NOT NULL,
    project_id char(12) NOT NULL,
    KEY `projectFK` (`project_id`),
	CONSTRAINT `projectFK` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE projectMembers (
	project_id char(12) NOT NULL,
    member_id char(36) NOT NULL,
    KEY `projectForMembersFK` (`project_id`),
	CONSTRAINT `projectForMembersFK` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    KEY `membershipFK` (`member_id`),
    CONSTRAINT `membershipFK` FOREIGN KEY (`member_id`) REFERENCES `MEMBERSHIP` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE
);


