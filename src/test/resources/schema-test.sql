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
    branch_id char(11) NOT NULL,
    PRIMARY KEY(member_id),
	FOREIGN KEY (contactInfo_id) REFERENCES CONTACTINFO (id)
);

CREATE TABLE branches (
	branch_id char(11) NOT NULL,
    branch_name char(50) NOT NULL,
    admin_id char(36),
    contactInfo_id int NOT NULL,
    totalAmount DOUBLE,
    PRIMARY KEY(branch_id)
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
    1,
    'UK-LDN-5645'
);

INSERT INTO branches (branch_id, branch_name, admin_id, contactInfo_id, totalAmount)
VALUES (
    'XX-TTT-0000',
    '4e48d7a8-64b9-4455-a37f-903fd62def32',
    'Test Name No 3',
    1,
    150.0
);

INSERT INTO branches (branch_id, branch_name, admin_id, contactInfo_id, totalAmount)
VALUES (
    'ZZ-TTT-0000',
    '4e48d7a8-64b9-4455-a37f-903fd62def32',
    'Test Name No 4',
    1,
    0.0
);
