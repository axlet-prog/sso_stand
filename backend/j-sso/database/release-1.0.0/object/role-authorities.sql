--liquibase formatted sql

--changeset axlet:role-authorities-1
create table if not exists sso.role_authorities
(
    role_authority_id uuid                        not null default uuid_generate_v4(),
    role_id           uuid                        not null,
    authority_id      uuid                        not null,

    created_by        varchar(50)                 not null default 'system',
    created_date      timestamp without time zone not null default current_timestamp,
    constraint role_authorities_pk primary key (role_authority_id)
);

comment on table sso.role_authorities is 'Маппинг ролей и привилегий';
comment on column sso.role_authorities.role_authority_id is 'Уникальный идентификатор записи';
comment on column sso.role_authorities.role_id is 'Уникальный код привилегии';
comment on column sso.role_authorities.authority_id is 'Уникальный код привилегии';

comment on column sso.role_authorities.created_by is 'Логин пользователя, создавшего запись';
comment on column sso.role_authorities.created_date is 'Дата создания записи';

--changeset axlet:role-authorities-2
create unique index idx_role_authorities_u1 on sso.role_authorities (role_id, authority_id);