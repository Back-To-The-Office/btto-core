create table room(
    id serial primary key,
    name varchar(255),
    level varchar(255),
    created_at timestamp default now(),
    modified_at timestamp default now(),
    office_id integer references office(id) not null
);

create index room_office_idx on room(office_id);
