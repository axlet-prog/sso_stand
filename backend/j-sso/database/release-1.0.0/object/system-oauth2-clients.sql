--liquibase formatted sql

--changeSet axlet:system-oauth2-clients-1
create sequence sso.system_oauth2_clients_sq start 1;

--changeSet axlet:system-oauth2-clients-2
create table sso.system_oauth2_clients
(
    system_client_id              bigint        not null default nextval('sso.system_oauth2_clients_sq'),
    client_id                     varchar(100)  not null,
    client_id_issued_at           timestamp              default current_timestamp not null,
    client_secret                 varchar(200),
    client_secret_expires_at      timestamp,
    client_name                   varchar(200)  not null,
    client_authentication_methods varchar(1000) not null,
    authorization_grant_types     varchar(1000) not null,
    redirect_uris                 varchar(1000),
    scopes                        varchar(1000) not null,
    client_settings               varchar(2000),
    token_settings                varchar(2000),
    constraint system_oauth2_clients_pk primary key (system_client_id)
);

comment on table sso.system_oauth2_clients is 'OAuth2 клиенты системы';
comment on column sso.system_oauth2_clients.client_id is 'ID клиента';
comment on column sso.system_oauth2_clients.client_id_issued_at is 'Дата создания записи';
comment on column sso.system_oauth2_clients.client_secret is 'Пароль';
comment on column sso.system_oauth2_clients.client_secret_expires_at is 'Срок действия пароля';
comment on column sso.system_oauth2_clients.client_name is 'Наименование клиента';
comment on column sso.system_oauth2_clients.client_authentication_methods is 'Доступные методы аутентификации';
comment on column sso.system_oauth2_clients.authorization_grant_types is 'Типы доступа';
comment on column sso.system_oauth2_clients.redirect_uris is 'Доступные URL-ы перенаправления';
comment on column sso.system_oauth2_clients.scopes is 'Области доступа';
comment on column sso.system_oauth2_clients.client_settings is 'Дополнительные настройки клиента';
comment on column sso.system_oauth2_clients.token_settings is 'Дополнительные настройки токена';

--changeset axlet:system-oauth2-clients-3
create unique index idx_system_oauth2_clients_n1 on sso.system_oauth2_clients (client_id);