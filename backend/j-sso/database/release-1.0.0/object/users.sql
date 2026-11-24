--liquibase formatted sql

--changeset axlet:users-1
create table if not exists sso.users
(
    user_id
    uuid
    not
    null
    default
    uuid_generate_v4
(
),
    email varchar
(
    100
) not null,
    password_hash varchar
(
    500
),
    first_name varchar
(
    100
) not null,
    last_name varchar
(
    100
) not null,
    middle_name varchar
(
    100
),
    birthday date,
    avatar_url varchar
(
    255
),
    active boolean not null default false,

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
    constraint users_pk primary key
(
    user_id
)
    );

comment
on table sso.users is 'Пользователи';
comment
on column sso.users.user_id is 'УИ пользователя';
comment
on column sso.users.email is 'Логин пользователя';
comment
on column sso.users.password_hash is 'Хэш пароля';
comment
on column sso.users.first_name is 'Начало действия учетной записи';
comment
on column sso.users.last_name is 'Окончание действия учетной записи';
comment
on column sso.users.middle_name is 'Статус пользователя, активен или неактивен';
comment
on column sso.users.birthday is 'Пользователь, создавший текущую запись';
comment
on column sso.users.avatar_url is 'Ссылка на аватар';

comment
on column sso.users.created_by is 'Логин пользователя, создавшего запись';
comment
on column sso.users.created_date is 'Дата создания записи';
comment
on column sso.users.last_updated_by is 'Логин пользователя, изменившего запись';
comment
on column sso.users.last_updated_date is 'Дата последнего обновления записи';
comment
on column sso.users.object_version_number is 'Номер версии записи в БД';

--changeset axlet:users-2
create unique index if not exists idx_user_u1 on sso.users (email);