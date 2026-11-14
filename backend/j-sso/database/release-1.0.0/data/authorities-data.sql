--liquibase formatted sql

--changeSet axlet:authorities-data-01
insert into sso.authorities(authority_code, authority_description, system_code)
values ('GET_OWN_DATA', 'Привилегия позволяет получить данные текущего пользователя', 'SSO');

insert into sso.authorities(authority_code, authority_description, system_code)
values ('CHANGE_OWN_DATA', 'Привилегия позволяет изменять данные текущего пользователя', 'SSO');

insert into sso.authorities(authority_code, authority_description, system_code)
values ('CHANGE_OWN_PASSWORD', 'Привилегия позволяет изменять пароль текущего пользователя', 'SSO');

insert into sso.authorities(authority_code, authority_description, system_code)
values ('DELETE_OWN_ACCOUNT', 'Привилегия позволяет удалять аккаунт текущего пользователя', 'SSO');