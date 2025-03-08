CREATE DATABASE securityDb;

USE securityDb;


--
-- Create table `users`
--
CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  password VARCHAR(255) DEFAULT NULL,
  username VARCHAR(255) DEFAULT NULL,
  account_no_expired BIT(1) DEFAULT NULL,
  account_no_locked BIT(1) DEFAULT NULL,
  creadential_no_expired BIT(1) DEFAULT NULL,
  is_enabled BIT(1) DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AUTO_INCREMENT = 5,
AVG_ROW_LENGTH = 4096,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_0900_ai_ci,
ROW_FORMAT = DYNAMIC;

--
-- Create table `roles`
--
CREATE TABLE roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_name ENUM('ADMIN','DEVELOPER','INVITED','USER') DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AUTO_INCREMENT = 5,
AVG_ROW_LENGTH = 4096,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_0900_ai_ci,
ROW_FORMAT = DYNAMIC;

--
-- Create table `user_roles`
--
CREATE TABLE user_roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 4096,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_0900_ai_ci,
ROW_FORMAT = DYNAMIC;

--
-- Create foreign key
--
ALTER TABLE user_roles 
  ADD CONSTRAINT FKh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id)
    REFERENCES roles(id);

--
-- Create foreign key
--
ALTER TABLE user_roles 
  ADD CONSTRAINT FKhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id)
    REFERENCES users(id);

--
-- Create table `permissions`
--
CREATE TABLE permissions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AUTO_INCREMENT = 6,
AVG_ROW_LENGTH = 3276,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_0900_ai_ci,
ROW_FORMAT = DYNAMIC;

--
-- Create index `UKpnvtwliis6p05pn6i3ndjrqt2` on table `permissions`
--
ALTER TABLE permissions 
  ADD UNIQUE INDEX UKpnvtwliis6p05pn6i3ndjrqt2(name);

--
-- Create table `role_permissions`
--
CREATE TABLE role_permissions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  permission_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 1365,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_0900_ai_ci,
ROW_FORMAT = DYNAMIC;

--
-- Create foreign key
--
ALTER TABLE role_permissions 
  ADD CONSTRAINT FKegdk29eiy7mdtefy5c7eirr6e FOREIGN KEY (permission_id)
    REFERENCES permissions(id);

--
-- Create foreign key
--
ALTER TABLE role_permissions 
  ADD CONSTRAINT FKn5fotdgk8d1xvo8nav9uv3muc FOREIGN KEY (role_id)
    REFERENCES roles(id);




-- select * from roles;
-- select * from users;
-- select * from user_roles;
-- select * from permissions;
-- select * from role_permissions;


-- delete from permissions

insert into permissions(name)value('CREATE');
insert into permissions(name)value('DELETE');
insert into permissions(name)value('UPDATE');
insert into permissions(name)value('READ');
insert into permissions(name)value('REFACTOR');


insert into roles(role_name) value('ADMIN');
SET @id_insertRolAdm = LAST_INSERT_ID();
insert into roles(role_name) value('USER');
SET @id_insertRolUser = LAST_INSERT_ID();
insert into roles(role_name) value('INVITED');
SET @id_insertRolInv = LAST_INSERT_ID();
insert into roles(role_name) value('DEVELOPER');
SET @id_insertRolDev = LAST_INSERT_ID();

insert into users(username,password,account_no_expired,account_no_locked,creadential_no_expired,is_enabled)
values('william','$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6',True,True,True,True);
SET @id_insertUser = LAST_INSERT_ID();


insert into user_roles(role_id,user_id)value(@id_insertRolAdm,@id_insertUser);


insert into role_permissions(permission_id,role_id)
select id,@id_insertRolAdm from permissions where name in ('CREATE','READ','DELETE','UPDATE');


insert into role_permissions(permission_id,role_id)
select id,@id_insertRolUser from permissions where name in ('CREATE','READ');

insert into role_permissions(permission_id,role_id)
select id,@id_insertRolInv from permissions where name in ('READ');

insert into role_permissions(permission_id,role_id)
select id,@id_insertRolDev from permissions where name in ('CREATE','READ','DELETE','UPDATE','REFACTOR');




