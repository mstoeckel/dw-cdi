alter table example.user_profile alter column created_by set null;
alter table example.user_profile alter column last_modified_by set null;
alter table example.users alter column created_by set null;
alter table example.users alter column last_modified_by set null;

insert into example.user_profile(id, created_by, created_at, last_modified_by, last_modified_at,first_name,last_name, email) values('c924fa3e-85d6-4955-a852-ccdbebac4510', null, current_timestamp(), null, current_timestamp(), 'Admin', 'User', 'admin@cognodyne.com');
insert into example.users (id, created_by, created_at, last_modified_by, last_modified_at,username, salt,password, account_status,password_exp_ts,retries,user_profile_id) values ('c924fa3e-85d6-4955-a852-ccdbebac4511', null, current_timestamp(), null, current_timestamp(),'admin', 'fasdfasdf', 'adminadmin', 'Active',current_timestamp(), 0, 'c924fa3e-85d6-4955-a852-ccdbebac4510');

update example.user_profile set created_by='c924fa3e-85d6-4955-a852-ccdbebac4511', last_modified_by='c924fa3e-85d6-4955-a852-ccdbebac4511' where id='c924fa3e-85d6-4955-a852-ccdbebac4510';
update example.users set created_by='c924fa3e-85d6-4955-a852-ccdbebac4511', last_modified_by='c924fa3e-85d6-4955-a852-ccdbebac4511' where id='c924fa3e-85d6-4955-a852-ccdbebac4511';

alter table example.user_profile alter column created_by set not null;
alter table example.user_profile alter column last_modified_by set not null;
alter table example.users alter column created_by set not null;
alter table example.users alter column last_modified_by set not null;
