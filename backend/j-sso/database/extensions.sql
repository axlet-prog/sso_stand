--liquibase formatted sql

--changeset axlet:extensions-1
create
extension if not exists "uuid-ossp";