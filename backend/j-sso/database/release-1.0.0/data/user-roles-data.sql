--liquibase formatted sql

--changeSet axlet:users-role-data-01
insert into sso.user_roles(user_id, role_id)
select user_id, (select role_id from sso.roles where role_code = 'USER_SSO')
from sso.users
on conflict do nothing;