CREATE TABLE EMP(
SSN INT PRIMARY KEY,
EmpName VARCHAR(255)
);

CREATE TABLE CITY(
CityName VARCHAR(255) PRIMARY KEY,
Country VARCHAR(255)
);

CREATE TABLE ASSIGN(
SSN INT REFERENCES EMP(SSN),
CityName VARCHAR(255) REFERENCES CITY(CityName),
StartYear INT,
EndYear INT,
CONSTRAINT pk_assign PRIMARY KEY(SSN, CityName, StartYear)
);

INSERT INTO EMP VALUES (123, 'Jack Black');
INSERT INTO EMP VALUES (555, 'Judy Brown');
INSERT INTO EMP VALUES (302, 'Jim Smith');
INSERT INTO EMP VALUES (510, 'Mike Aarons');
INSERT INTO EMP VALUES (601, 'John Morgon');
INSERT INTO EMP VALUES (622, 'Alice Robbin');
INSERT INTO EMP VALUES (451, 'Jason Doe');
INSERT INTO EMP VALUES (733, 'Cathy Stone');
INSERT INTO CITY VALUES ('Paris', 'France');
INSERT INTO CITY VALUES ('Moscow', 'Russia');
INSERT INTO CITY VALUES ('Berlin', 'Germany');
INSERT INTO CITY VALUES ('Rome', 'Italy');
INSERT INTO CITY VALUES ('Sydney', 'Australia');
INSERT INTO ASSIGN VALUES (123, 'Paris', 2000, 2002);
INSERT INTO ASSIGN VALUES (555, 'Paris', 2000, 2005);
INSERT INTO ASSIGN VALUES (555, 'Moscow', 1997, 2000);
INSERT INTO ASSIGN VALUES (510, 'Moscow', 1995, 1999);
INSERT INTO ASSIGN VALUES (601, 'Moscow', 1990, 1995);
INSERT INTO ASSIGN VALUES (601, 'Paris', 1995, 1999);
INSERT INTO ASSIGN VALUES (302, 'Berlin', 2002, 2012);
INSERT INTO ASSIGN VALUES (302, 'Rome', 1998, 2001);
INSERT INTO ASSIGN VALUES (622, 'Berlin', 1991, 1995);
INSERT INTO ASSIGN VALUES (451, 'Berlin', 2003, 2004);
INSERT INTO ASSIGN VALUES (733, 'Rome', 2001, 2008);
INSERT INTO ASSIGN VALUES (622, 'Sydney', 2001, 2011);
INSERT INTO ASSIGN VALUES (733, 'Berlin', 2009, 2011);

--------------Q 1.1----------------------------------------------------------------------------------

CREATE View Emp_Assignment(EMPNAME, SSN, Startyear, Endyear) AS
Select EMPNAME, SSN, Startyear, Endyear 
FROM EMP NATURAL JOIN Assign;

SELECT EMPNAME, AVG(EndYear - StartYear) AS AvgLength
FROM Emp_Assignment
GROUP BY EMPNAME
ORDER BY AvgLength DESC;
----------------------------------------------------------------------------------------------------

----------------------------------Q1.2---------------------------------------------------------------
CREATE VIEW City_Assignments AS
SELECT CITYNAME, MAX(Endyear-Startyear) AS Max_Assign_Length
FROM ASSIGN
GROUP BY CITYNAME;

CREATE VIEW MaxAssignment(years) AS
SELECT MAX(Endyear-Startyear)
FROM ASSIGN;

SELECT CITYNAME
FROM City_Assignments JOIN MaxAssignment
ON City_Assignments.MAX_ASSIGN_LENGTH = MaxAssignment.years; 

-------------------------------------------------------------------------------

--------------------------------------Q1.3-------------------------------------
CREATE View Emp_Assignment_With_Cities(EMPNAME, CITYNAME, SSN, Startyear, Endyear) AS
Select EMPNAME, CITYNAME, SSN, Startyear, Endyear 
FROM EMP NATURAL JOIN Assign;

SELECT E1.EMPNAME 
FROM Emp_Assignment_With_Cities E1 JOIN Emp_Assignment_With_Cities E2
ON E1.EMPNAME = E2.EMPNAME AND E1.Startyear = E2.Endyear; 


--------------------------------------------------------------------------------------------------------

-------------------------------------Q1.4----------------------------------------------------------------
CREATE VIEW Judy_Brown_Assignments(CITYNAME,Startyear,Endyear) AS
SELECT Cityname, Startyear,Endyear
FROM Emp_Assignment_With_Cities
WHERE EMPNAME = 'Judy Brown';

SELECT DISTINCT EMPNAME
FROM Emp_Assignment_With_Cities E1 JOIN Judy_Brown_Assignments J
ON (E1.EMPNAME <> 'Judy Brown' AND E1.CITYNAME = J.CITYNAME AND E1.Startyear =  J.Startyear AND E1.StartYear<J.Endyear)
OR (E1.EMPNAME <> 'Judy Brown' AND E1.CITYNAME = J.CITYNAME AND J.Startyear < E1.Endyear);

--------------------------------------------------------------------------------------------------------

------------------------------------Q1.5----------------------------------------------------------------
CREATE VIEW JIM_ASSIGNMENT1(CITYNAME) AS
SELECT CITYNAME FROM Jim_Smith_Assignments
MINUS
SELECT J1.CITYNAME 
FROM Jim_Smith_Assignments J1 JOIN Jim_Smith_Assignments J2
ON J1.Startyear>J2.Startyear;

CREATE VIEW JIM_ASSIGNMENT2(CITYNAME) AS
SELECT CITYNAME FROM Jim_Smith_Assignments
MINUS
SELECT J1.CITYNAME 
FROM Jim_Smith_Assignments J1 JOIN Jim_Smith_Assignments J2
ON J1.Startyear<J2.Startyear;

CREATE VIEW Assignment_1 AS
SELECT * 
FROM Emp_Assignment_With_Cities E NATURAL JOIN JIM_ASSIGNMENT1;

CREATE VIEW Assignment_2 AS
SELECT * 
FROM Emp_Assignment_With_Cities E NATURAL JOIN JIM_ASSIGNMENT2;

SELECT A1.EMPNAME
FROM Assignment_1 A1, Assignment_2 A2
WHERE A1.EMPNAME = A2.EMPNAME AND A1.EMPNAME <> 'Jim Smith';

