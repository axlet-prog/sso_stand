--liquibase formatted sql

--changeSet axlet:role-authorities-data-01
insert into sso.role_authorities(role_id, authority_id)
values ((select role_id from sso.roles where role_code = 'USER_SSO'),
        (select authority_id from sso.authorities where authority_code = 'GET_OWN_DATA'));

insert into sso.role_authorities(role_id, authority_id)
values ((select role_id from sso.roles where role_code = 'USER_SSO'),
        (select authority_id from sso.authorities where authority_code = 'CHANGE_OWN_DATA'));

insert into sso.role_authorities(role_id, authority_id)
values ((select role_id from sso.roles where role_code = 'USER_SSO'),
        (select authority_id from sso.authorities where authority_code = 'CHANGE_OWN_PASSWORD'));

insert into sso.role_authorities(role_id, authority_id)
values ((select role_id from sso.roles where role_code = 'USER_SSO'),
        (select authority_id from sso.authorities where authority_code = 'DELETE_OWN_ACCOUNT'));