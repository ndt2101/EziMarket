insert into role (created_by, created_time, description) values (1, now() , 'ROLE_USER');

insert into permission (created_by, created_time, description) values (1, now() , 'READ_PERMISSION');

insert into granted_permission (role_id, permission_id) values (1, 1);

insert into email_validation_status (created_by, created_time, status_description) values (1, now(), 'valid');

insert into email_validation_status (created_by, created_time, status_description) values (2, now(), 'invalid')