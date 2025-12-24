--liquibase formatted sql

--changeSet axlet:users-data-01
insert into sso.users (email, password_hash, first_name, last_name, middle_name, birthday, avatar_url, active)
values ('admin@example.com', '$2a$10$VUqrcPxSpEhmYjIZ5zbygu3bEf1KHw8A8Vm4agZwh061SVFGr2OUG', 'Иван', 'Иванов',
        'Иванович', '1978-03-12', null, true);

--changeSet axlet:users-data-02
insert into sso.users (email, password_hash, first_name, last_name, middle_name, birthday, avatar_url, active)
values ('admin2@example.com', '{noop}1234', 'Иван', 'Иванов', 'Иванович', '1978-03-12', null, true);