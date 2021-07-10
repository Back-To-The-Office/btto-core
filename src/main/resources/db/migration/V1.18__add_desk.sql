create table desk(
    id serial primary key,
    name varchar(255),
    capacity integer,
    created_at timestamp default now(),
    modified_at timestamp default now(),
    room_id integer references room(id) not null
);

create index desk_room_idx on desk(room_id);
