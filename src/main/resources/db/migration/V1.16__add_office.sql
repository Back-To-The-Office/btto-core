create table office(
    id serial primary key,
    address text,
    name varchar(255),
    created_at timestamp default now(),
    modified_at timestamp default now(),
    company_id integer references company(id) not null
);

create index office_company_idx on office(company_id);
