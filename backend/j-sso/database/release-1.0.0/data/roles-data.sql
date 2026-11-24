--liquibase formatted sql

--changeSet axlet:roles-data-01
insert into sso.roles(role_code, role_description)
values ('USER_SSO', 'Роль обычного пользователя DL-SSO');