CREATE TABLE criteria_car_origin (CAR_ID VARCHAR(255), CAR_VER INT, component VARCHAR(255) NOT NULL, origin VARCHAR(255));
CREATE TABLE SimpleEntityOLGH10515 (CAR_ID VARCHAR(255) NOT NULL, CAR_VER INT NOT NULL, PRIMARY KEY (CAR_ID, CAR_VER));
CREATE INDEX I_CRTRRGN_CAR_ID ON criteria_car_origin (CAR_ID, CAR_VER);