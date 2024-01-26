--**********Admin********
CREATE SEQUENCE employeesP_seq;
CREATE TABLE employeesP (
    employeeID NUMBER GENERATED BY DEFAULT AS IDENTITY,
    firstName VARCHAR2(50),
    lastName VARCHAR2(50),
    gender VARCHAR2(10),
    cnic VARCHAR2(15),
    fatherName VARCHAR2(50),
    fatherCnic VARCHAR2(15),
    dateOfBirth DATE,
    age NUMBER,
    postalAddress VARCHAR2(255),
    permanentAddress VARCHAR2(255),
    contactNo VARCHAR2(15),
    email VARCHAR2(100),
    password VARCHAR2(50),
    designation VARCHAR2(50),
    PRIMARY KEY (employeeID),
    UNIQUE (email)
);
select * from employeesP;
ALTER TABLE employeesP
ADD  salary NUMBER (30);
ALTER TABLE employeesP
ADD registrationDate DATE DEFAULT SYSDATE;

ALTER TABLE employeesP
ADD employeeImage BLOB;
-- Create trigger for generating email and password
CREATE OR REPLACE TRIGGER employeesP_email_password_trigger
BEFORE INSERT ON employeesP
FOR EACH ROW
BEGIN
    :NEW.email := INITCAP(:NEW.firstName) || '.' || INITCAP(:NEW.lastName) || '.' || TO_CHAR(:NEW.employeeID) || '@CozyHaven.com';
    :NEW.password := INITCAP(:NEW.firstName) || '@123';
END;
--getting generated email and passsword
CREATE OR REPLACE PROCEDURE GET_GENERATED_INFO(
    p_firstName IN VARCHAR2,
    p_lastName IN VARCHAR2,
    p_email OUT VARCHAR2,
    p_password OUT VARCHAR2,
    p_employeeID OUT NUMBER
) AS
BEGIN
    SELECT INITCAP(p_firstName) || '.' || INITCAP(p_lastName) || '.' || TO_CHAR(employeesP_seq.NEXTVAL) || '@CozyHaven.com',
           INITCAP(p_firstName) || '@123',
           employeesP_seq.CURRVAL -- Retrieve the current value of the sequence (auto-generated employeeID)
    INTO p_email, p_password, p_employeeID
    FROM DUAL;
END GET_GENERATED_INFO;
/
commit;
--*****manager******
--view salries
CREATE OR REPLACE PROCEDURE get_salaries_and_dues (
    result_cursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN result_cursor FOR
        SELECT 'Employee' AS type, employeeID AS id, firstName || ' ' || lastName AS name, salary AS amount
        FROM employeesP

        UNION ALL

        SELECT 'Resident' AS type, d.resident_id AS id, r.first_name || ' ' || r.last_name AS name, d.due_amount AS amount
        FROM Dues d
        JOIN Residents r ON d.resident_id = r.resident_id;
END;
--view reports
SELECT r.resident_id, r.first_name, r.last_name, r.gender, r.contact_num, r.age,
       r.emergency_cont, r.address, r.address2, r.room_number,
       c.check_in_date, cd.check_out_date
FROM Residents r
LEFT JOIN check_in c ON r.resident_id = c.resident_id
LEFT JOIN check_out cd ON r.resident_id = cd.resident_id;
--room occupancy

CREATE OR REPLACE VIEW CurrentResidentsView AS
SELECT
    resident_id,
    first_name,
    last_name,
    contact_num,
    age,
    gender,
    emergency_cont,
    address,
    address2,
    room_number
FROM
    Residents
WHERE
    room_number <> 0
ORDER BY 
    resident_id;

--view facilities

CREATE TABLE Facility (
    FACILITY_ID NUMBER DEFAULT facility_seq.NEXTVAL PRIMARY KEY,
    FACILITY_NAME VARCHAR2(255),
    FACILITY_DETAIL VARCHAR2(255)
);
CREATE SEQUENCE facility_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
    
    
--    expenses table
CREATE TABLE Expenses (
    expense_id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR2(255),
    amount NUMBER,
    expense_date DATE
);



--********receptionist*******
-- Create Residents table
CREATE TABLE Residents (
    resident_id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    first_name VARCHAR2(50),
    last_name VARCHAR2(50),
    contact_num NUMBER(11),
    age NUMBER,
    gender VARCHAR2(50),
    emergency_cont NUMBER(20),
    address VARCHAR2(50),
    address2 VARCHAR2(50),
    room_number NUMBER
);

alter table Residents
add cnic varchar(50);
ALTER TABLE Residents
ADD checkout_status CHAR(1) DEFAULT 'F' CHECK (checkout_status IN ('T', 'F'));

-- Add a new student
CREATE OR REPLACE TRIGGER AfterInsertResident
FOR INSERT ON Residents
COMPOUND TRIGGER
    -- Declare a collection to store relevant data
    TYPE room_data_type IS RECORD (
        room_number INT,
        residents_count INT
    );

    TYPE room_data_collection_type IS TABLE OF room_data_type;
    
    room_data_collection room_data_collection_type := room_data_collection_type();

    BEFORE STATEMENT IS
    BEGIN
        -- Initialization logic before the statement
        -- Clear the collection before each statement
        room_data_collection := room_data_collection_type();
    END BEFORE STATEMENT;

    BEFORE EACH ROW IS
    BEGIN
        -- Per-row logic before the row is processed
        -- Collect relevant data without querying the Residents table directly
        room_data_collection.EXTEND;
        room_data_collection(room_data_collection.LAST) :=
            room_data_type(:NEW.room_number, NULL);
        -- No need to count residents here; it will be done in the AFTER STATEMENT phase
    END BEFORE EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        -- Statement-level logic after all rows are processed
        FOR i IN 1..room_data_collection.COUNT LOOP
            DECLARE
                v_residents_count NUMBER;
            BEGIN
                -- Count residents for the specific room_number
                SELECT COUNT(*) INTO v_residents_count
                FROM Residents
                WHERE room_number = room_data_collection(i).room_number;

                -- Update the residents_count in the collection
                room_data_collection(i).residents_count := v_residents_count;

                -- Check conditions for room numbers
                IF room_data_collection(i).room_number BETWEEN 1 AND 10 AND v_residents_count > 2 THEN
                    RAISE_APPLICATION_ERROR(-20001, 'Room numbers 1 to 10 can have a maximum of 2 residents.');
                ELSIF room_data_collection(i).room_number BETWEEN 11 AND 30 AND v_residents_count > 4 THEN
                    RAISE_APPLICATION_ERROR(-20002, 'Room numbers 11 to 30 can have a maximum of 4 residents.');
                ELSIF room_data_collection(i).room_number < 1 OR room_data_collection(i).room_number > 30 THEN
                    RAISE_APPLICATION_ERROR(-20003, 'Invalid room number. Room number must be between 1 and 30.');
                END IF;
            END;
        END LOOP;
    END AFTER STATEMENT;
    
END ;
CREATE TABLE check_out (
    resident_id INT,
    check_out_date DATE,
    FOREIGN KEY (resident_id) REFERENCES Residents(resident_id)
);
CREATE TABLE check_in (
    resident_id INT,
    check_in_date DATE,
    FOREIGN KEY (resident_id) REFERENCES Residents(resident_id)
);

CREATE OR REPLACE TRIGGER resident_after_insert
AFTER INSERT
 ON Residents
 FOR EACH ROW
BEGIN

 INSERT INTO check_in
 ( resident_id,
 check_in_date 
 )
 VALUES
 ( :new.resident_id,
 sysdate);
END;



--*******accountant******
--fetchsalary
SELECT employeeID, firstName, lastName, salary, TRUNC(MONTHS_BETWEEN(SYSDATE, registrationDate) / 12) AS workYears FROM employeesP;
--fetchDues
CREATE TABLE Dues (
    resident_id NUMBER,
    due_amount NUMBER,
    due_date DATE DEFAULT sysdate,
    payment_status VARCHAR2(20),
    FOREIGN KEY (resident_id) REFERENCES Residents(resident_id)
);
SELECT resident_id, due_amount, payment_status FROM Dues;
select * from Dues;
--update payment
CREATE OR REPLACE PROCEDURE update_payment_status(
    p_resident_id IN NUMBER,
    p_new_payment_status IN VARCHAR2
) AS
BEGIN
    UPDATE Dues SET payment_status = p_new_payment_status WHERE resident_id = p_resident_id;
    COMMIT;
END;


-- Create a trigger to automatically allocate dues
CREATE OR REPLACE TRIGGER AutoAllocateDues
AFTER INSERT ON Residents   
FOR EACH ROW
DECLARE
    vDueAmount NUMBER;
BEGIN
    -- Set due amount based on room number
    IF :NEW.room_number BETWEEN 1 AND 10 THEN
        vDueAmount := 40000;  -- Set your desired due amount for rooms 1 to 10
    ELSIF :NEW.room_number BETWEEN 11 AND 30 THEN
        vDueAmount := 20000;   -- Set your desired due amount for rooms 11 to 30
    ELSE
        vDueAmount := 0;    -- Set default due amount for other rooms
    END IF;

    -- Insert dues record for the new resident
    INSERT INTO Dues (resident_id, due_amount, due_date, payment_status)
    VALUES (:NEW.resident_id, vDueAmount, SYSDATE, 'Pending');
END;
/
--automatically setting salaries
CREATE OR REPLACE TRIGGER set_salary_trigger
BEFORE INSERT ON employeesP
FOR EACH ROW
BEGIN
    IF UPPER(:NEW.designation) = 'MANAGER' THEN
        :NEW.salary := 80000;
    ELSIF UPPER(:NEW.designation) = 'ADMIN' THEN
        :NEW.salary := 60000;
    ELSIF UPPER(:NEW.designation) = 'RECEPTIONIST' THEN
        :NEW.salary := 45000;
     ELSIF UPPER(:NEW.designation) = 'ACCOUNTANT' THEN
        :NEW.salary := 55000; -- Default salary for other designations
    END IF;
END;
/

--*******accountant end******
select * from Residents;
select * from employeesP;