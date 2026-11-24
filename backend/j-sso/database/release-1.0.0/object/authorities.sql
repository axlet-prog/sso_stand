--liquibase formatted sql

--changeset axlet:authorities-1
create table if not exists sso.authorities
(
    authority_id
    uuid
    not
    null
    default
    uuid_generate_v4
(
),
    authority_code varchar
(
    100
) not null,
    authority_description varchar
(
    500
) not null,
    system_code varchar
(
    50
) not null,
    active boolean not null default true,

    created_by varchar
(
    50
) not null default 'system',
    created_date timestamp without time zone not null default current_timestamp,
    last_updated_by varchar
(
    50
) not null default 'system',
    last_updated_date timestamp
                           without time zone not null default current_timestamp,
    object_version_number integer not null default 0,
    constraint authorities_pk primary key
(
    authority_id
)
    );

comment
on table sso.authorities is 'Справочник привилегий';
comment
on column sso.authorities.authority_id is 'Уникальный идентификатор привилегии';
comment
on column sso.authorities.authority_code is 'Код привилегии';
comment
on column sso.authorities.authority_description is 'Описание привилегии';
comment
on column sso.authorities.system_code is 'Код системы, к которой принадлежит привилегия';
comment
on column sso.authorities.active is 'Флаг активности';

comment
on column sso.authorities.created_by is 'Логин пользователя, создавшего запись';
comment
on column sso.authorities.created_date is 'Дата создания записи';
comment
on column sso.authorities.last_updated_by is 'Логин пользователя, изменившего запись';
comment
on column sso.authorities.last_updated_date is 'Дата последнего обновления записи';
comment
on column sso.authorities.object_version_number is 'Номер версии записи в БД';

--changeset axlet:authorities-2
create unique index idx_authorities_u1 on sso.authorities (authority_code, system_code);