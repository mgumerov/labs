CREATE TABLE USERS (
USERNAME VARCHAR2,
PASSWORD VARCHAR2,
ISADMIN NUMBER
);

INSERT INTO USERS(USERNAME, PASSWORD, ISADMIN) VALUES ('Admin', 'admin', 1);
INSERT INTO USERS(USERNAME, PASSWORD, ISADMIN) VALUES ('User', 'user', 0);
