--liquibase formatted sql

--changeset axlet:roles-1
create table if not exists sso.roles
(
    role_id
    uuid
    not
    null
    default
    uuid_generate_v4
(
),
    role_code varchar
(
    50
) not null,
    role_description varchar
(
    500
) not null,
    system_code varchar
(
    50
),
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
    constraint roles_pk primary key
(
    role_code
)
    );

comment
on table sso.roles is 'Справочник ролей';
comment
on column sso.roles.role_code is 'Уникальный идентификатор роли';
comment
on column sso.roles.role_code is 'Код роли';
comment
on column sso.roles.role_description is 'Описание роли';
comment
on column sso.roles.system_code is 'Код системы, к которой принадлежит привилегия';
comment
on column sso.roles.active is 'Флаг активности';

comment
on column sso.roles.created_by is 'Логин пользователя, создавшего запись';
comment
on column sso.roles.created_date is 'Дата создания записи';
comment
on column sso.roles.last_updated_by is 'Логин пользователя, изменившего запись';
comment
on column sso.roles.last_updated_date is 'Дата последнего обновления записи';
comment
on column sso.roles.object_version_number is 'Номер версии записи в БД';

--changeset axlet:roles-2
create unique index idx_roles_u1 on sso.roles (role_code, system_code);