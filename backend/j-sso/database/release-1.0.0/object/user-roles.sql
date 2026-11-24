--liquibase formatted sql

--changeset axlet:user-roles-1
create sequence if not exists sso.user_roles_sq start 1;

create table if not exists sso.user_roles
(
    user_role_id uuid                        not null default uuid_generate_v4(),
    user_id      uuid                        not null,
    role_id      uuid                        not null,

    created_by   varchar(50)                 not null default 'system',
    created_date timestamp without time zone not null default current_timestamp,
    constraint user_roles_pk primary key (user_role_id)
);

comment on table sso.user_roles is 'Маппинг пользователей и ролей';
comment on column sso.user_roles.user_role_id is 'УИ записи';
comment on column sso.user_roles.user_id is 'Уникальный идентификатор пользователя';
comment on column sso.user_roles.role_id is 'Уникальный идентификатор роли';

comment on column sso.user_roles.created_by is 'Логин пользователя, создавшего запись';
comment on column sso.user_roles.created_date is 'Дата создания записи';

--changeset axlet:user-roles-2
create unique index idx_user_roles_u1 on sso.user_roles (user_id, role_id);